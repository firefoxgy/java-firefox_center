package com.firefox.center.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.utils.sm4.SM4Util;
import com.firefox.center.gateway.properties.MyGatewayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @program: java-firefox_center
 * @description: metabase接口请求处理过滤器
 * @author: yungeng
 * @created: 2021/08/06 10:24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SMDecryptGatewayFilterFactory extends AbstractGatewayFilterFactory<SMDecryptGatewayFilterFactory.Config> {

    private final MyGatewayProperties myGatewayProperties;
    private final Class<Config> configClass = Config.class;

    @Override
    public String name() {
        return "SMDecryptGatewayFilter";
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
            exchange.getAttributes().put("startTime", System.currentTimeMillis());
            if (exchange.getRequest().getMethod().equals(HttpMethod.POST)) {
                ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
                MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
                Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
                    if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) || MediaType.APPLICATION_JSON_UTF8.isCompatibleWith(mediaType)) {
                        JSONObject jsonObject = JSONObject.parseObject(body);
                        if(jsonObject != null && jsonObject.containsKey("encrypted_data")) {
                            String encryptedData=jsonObject.getString("encrypted_data");
                            String newBody="";
                            try{
                                newBody= SM4Util.decrypt(encryptedData, myGatewayProperties.getSm4().getKey());
                                newBody=newBody.replaceAll("\\f", "");
                                exchange.getAttributes().put(LoggerGlobalFilter.CACHE_BODY_KEY, newBody);
                            }catch (Exception e){
                                e.printStackTrace();
                                return processError(e.getMessage());
                            }
                            return Mono.just(newBody);
                        }
                    }
                    return Mono.empty();
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

}

