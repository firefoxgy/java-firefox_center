package com.firefox.center.gateway.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.firefox.center.gateway.properties.MyGatewayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: java-firefox_center
 * @description: webase front接口请求处理过滤器
 * @author: yungeng
 * @created: 2021/08/06 10:24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebaseFrontFilterFactory extends AbstractGatewayFilterFactory<WebaseFrontFilterFactory.Config> {

    private final MyGatewayProperties myGatewayProperties;
    private final Class<Config> configClass = Config.class;

    @Override
    public String name() {
        return "WebaseFrontFilter";
    }

    @Override
    public Class<Config> getConfigClass() {
        return configClass;
    }

    @Override
    public Config newConfig() {
        return BeanUtils.instantiateClass(this.configClass);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new InnerFilter(config);
    }

    public final static String CACHE_APP_GROUP_PRE = "com:firefox:center:baas:app_group_";

    public final static String CACHE_CONTRACT_PRE = "com:firefox:center:baas:contract_";

    public final static String PARAM_CONTRACT_ABI = "contractAbi";

    public final static String PARAM_GROUP_ID = "groupId";

    public final static String PARAM_FUNC_NAME = "funcName";

    public final static String PARAM_CONTRACT_ADDRESS = "contractAddress";

    public final static String JSON_NAME = "name";

    public final static String ATTRIBUTE_FUNC = "funcAbi";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 创建一个内部类，来实现2个接口，指定顺序
     */
    private class InnerFilter implements GatewayFilter, Ordered {

        private Config config;

        InnerFilter(Config config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            if (exchange.getRequest().getMethod().equals(HttpMethod.POST)) {

                ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
                MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
                String appId = exchange.getRequest().getHeaders().getFirst(AuthenticatorTokenFilter.HEADER_MANAGE_APPS);

                //测试初始化
                /*
                List<String[]> gIds = new ArrayList<>();
                gIds.add(new String[]{"1","dev01"});
                redisTemplate.opsForValue().set(CACHE_APP_GROUP_PRE + appId, gIds);
                redisTemplate.opsForValue().set(CACHE_CONTRACT_PRE + "0x5e03fb13fc9297b1b937be68e4dd14e287146254", contractAbiStr);
                   */
                //

                Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
                    String newBody = body;
                    if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) || MediaType.APPLICATION_JSON_UTF8.isCompatibleWith(mediaType)) {
                        JSONObject jsonObject = JSONObject.parseObject(body);
                        if(jsonObject == null)
                            jsonObject = new JSONObject();
                        if(!jsonObject.containsKey(PARAM_GROUP_ID)) {
                            List<String[]> groupIds = (List<String[]>)redisTemplate.opsForValue().get(CACHE_APP_GROUP_PRE + appId);
                            if(groupIds != null && groupIds.size() > 0 && groupIds.get(0).length > 0) {
                                String groupId = groupIds.get(0)[0];
                                jsonObject.put(PARAM_GROUP_ID, groupId);
                            }
                        }


                        if(jsonObject.containsKey(PARAM_FUNC_NAME)) {
                            String contractAbi = (String)redisTemplate.opsForValue().get(CACHE_CONTRACT_PRE + jsonObject.get(PARAM_CONTRACT_ADDRESS));
                            if(!StringUtils.isEmpty(contractAbi)) {
                                JSONArray contractAbiJson = JSONArray.parseArray(contractAbi);
                                for(Object item : contractAbiJson) {
                                    JSONObject func = (JSONObject)item;

                                    if(func.getString(JSON_NAME) != null && func.getString(JSON_NAME).equals(jsonObject.getString(PARAM_FUNC_NAME))) {

                                        jsonObject.put(PARAM_CONTRACT_ABI, JSONArray.parse("[" + func.toJSONString() + "]"));
                                        exchange.getAttributes().put(ATTRIBUTE_FUNC, func);

                                    }
                                }

                            }
                        }

                        newBody = jsonObject.toJSONString();
                        log.info("newbody:" + newBody);
                    }
                    return Mono.just(newBody);
                });
                BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
                HttpHeaders headers = new HttpHeaders();
                headers.putAll(exchange.getRequest().getHeaders());
                headers.remove("Content-Length");
                CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
                return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                    ServerHttpRequest decorator = this.decorate(exchange, headers, outputMessage);
                    return returnMono(chain, exchange.mutate().request(decorator).build());
                }));
            }
            return returnMono(chain, exchange);
        }

        private Mono<Void> returnMono(GatewayFilterChain chain,ServerWebExchange exchange){
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                Long startTime = exchange.getAttribute("startTime");
                if (startTime != null){
                    long executeTime = (System.currentTimeMillis() - startTime);
                    log.info("请求接口:{}\t耗时:{}ms\t状态码：{}" , exchange.getRequest().getURI(), executeTime, Objects.requireNonNull(exchange.getResponse().getStatusCode()).value());
                }
            }));
        }

        private Mono processError(String message) {
        /*exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();*/
            log.error(message);
            return Mono.error(new Exception(message));
        }

        ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
            return new ServerHttpRequestDecorator(exchange.getRequest()) {
                public HttpHeaders getHeaders() {
                    long contentLength = headers.getContentLength();
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.putAll(super.getHeaders());
                    if (contentLength > 0L) {
                        httpHeaders.setContentLength(contentLength);
                    } else {
                        httpHeaders.set("Transfer-Encoding", "chunked");
                    }
                    return httpHeaders;
                }
                public Flux<DataBuffer> getBody() {
                    return outputMessage.getBody();
                }
            };
        }

        @Override
        public int getOrder() {
            return 10;
        }
    }

    public static class Config {

    }

    String contractAbiStr = "[{\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"constant\":false,\"payable\":false,\"inputs\":[{\"name\":\"value\",\"type\":\"int256\"}],\"name\":\"register\",\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

}

