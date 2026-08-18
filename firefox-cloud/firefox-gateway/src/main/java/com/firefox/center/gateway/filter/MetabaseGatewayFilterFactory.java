package com.firefox.center.gateway.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.R;
import com.firefox.center.common.constants.BusinessConstants;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.lang.StringUtils;
import com.firefox.center.common.utils.sm4.SM4Util;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: java-firefox_center
 * @description: metabase接口请求处理过滤器
 * @author: yungeng
 * @created: 2021/08/06 10:24
 */
@Component
@Slf4j
public class MetabaseGatewayFilterFactory extends AbstractGatewayFilterFactory<MetabaseGatewayFilterFactory.Config> {

    private static String token;

    public static final String KEY_APP_ID = "appId";
    public static final String KEY_SELF_APP_ID = "selfAppId";


    private final Class<Config> configClass = Config.class;

    @Override
    public Class<Config> getConfigClass() {
        return configClass;
    }

    @Override
    public Config newConfig() {
        return BeanUtils.instantiateClass(this.configClass);
    }

    public final static String paramTemplate = "{"
        + "\"type\": \"${type}}\","
        + "\"value\": \"${value}\","
        + "\"target\": ["
        + "\"variable\","
        + "["
        + "\"template-tag\","
        + "\"${name}\""
        + "]"
        + "]"
        + "}";

    public final static String inParamTemplate = "{\"type\": \"category\"," + "\"value\": [${value}],"
        + "\"target\": [\"dimension\", [\"template-tag\", \"${name}\"]]" + "}";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    private static String TOKEN_KEY = "X-Metabase-Session";

    @Override public GatewayFilter apply(Config config) {
        return new MetabaseGatewayFilterFactory.InnerFilter(config);
    }

    protected DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }

    public static String  resolveBodyFromRequest(ServerHttpRequest serverHttpRequest){

        Flux<DataBuffer> body = serverHttpRequest.getBody();

        AtomicReference<String> bodyRef = new AtomicReference<>();

        body.subscribe(buffer -> {

            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());

            DataBufferUtils.release(buffer);

            bodyRef.set(charBuffer.toString());

        });

        return bodyRef.get();

    }

    /**
     * 创建一个内部类，来实现2个接口，指定顺序
     */
    private class InnerFilter implements GatewayFilter, Ordered {

        private MetabaseGatewayFilterFactory.Config config;

        InnerFilter(MetabaseGatewayFilterFactory.Config config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

                ServerWebExchange newEx = null;
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();
                String appId = request.getHeaders().getFirst(AuthenticatorTokenFilter.HEADER_MANAGE_APPS);
                String selfAppId = request.getHeaders().getFirst(BusinessConstants.HEADER_APP_ID);
                token = (String)redisTemplate.opsForValue().get(TOKEN_KEY);
                if (StringUtils.isEmpty(token)) {
                    //获取metabase的token
                    String body = "{\"username\":\"" + config.getUsername() + "\",\"password\":\"" + config.getPassword() + "\"}";

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
                    HttpEntity<String> strEntity = new HttpEntity<String>(body, headers);
                    ResponseEntity<JSONObject> respEntity =
                        restTemplate.postForEntity(config.getUrl() + "session", strEntity, JSONObject.class);
                    log.info("获取metabase的token接口请求：url:{} body:{} resp:{}", config.getUrl(), body, respEntity.toString());
                    if (HttpStatus.OK.value() == respEntity.getStatusCodeValue()) {
                        token = respEntity.getBody().getString("id");
                        redisTemplate.opsForValue().set(TOKEN_KEY, token, config.getExp(), TimeUnit.MINUTES);
                    } else {
                        //token为空
                        String message = JSONObject.toJSONString(R.error(CodeEnum.METABASE_TOKEN_FAIL));
                        byte[] bits = message.getBytes(StandardCharsets.UTF_8);
                        DataBuffer buffer = response.bufferFactory().wrap(bits);
                        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                        //指定编码，否则在浏览器中会中文乱码
                        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                        return response.writeWith(Mono.just(buffer));
                    }

                }

                //构造metabase请求参数
                //String body = resolveBodyFromRequest(request);
                String body = exchange.getAttribute(LoggerGlobalFilter.CACHE_BODY_KEY);

                JSONArray newParams = new JSONArray();
                //从header里面获取登录的appid赋值到请求参数里面
                boolean hasAppId = false;
                boolean hasSelfAppId = false;

                if (!StringUtils.isEmpty(body)) {
                    JSONObject requestBody = JSONObject.parseObject(body);
                    if (requestBody != null) {
                        for (String key : requestBody.keySet()) {
                            if(KEY_APP_ID.equalsIgnoreCase(key))
                                hasAppId = true;
                            if(KEY_SELF_APP_ID.equalsIgnoreCase(key))
                                hasSelfAppId = true;
                            String paramStr = paramTemplate.replace("${type}", "text");
                            if (requestBody.get(key) instanceof Integer)
                                paramStr = paramTemplate.replace("${type}", "number");
                            else if(requestBody.getString(key).startsWith("[") && requestBody.getString(key).endsWith("]"))
                                paramStr = inParamTemplate.replace("${name}", key).replace("[${value}]", requestBody.getString(key));
                            paramStr = paramStr.replace("${name}", key).replace("${value}", requestBody.getString(key));
                            JSONObject param = JSONObject.parseObject(paramStr);
                            newParams.add(param);
                        }

                    }
                }
                log.info("appId:" + appId);
                if (!StringUtils.isEmpty(appId) && !hasAppId) {
                    String paramStr = inParamTemplate;
                    JSONObject param = JSONObject.parseObject(paramStr.replace("${name}", KEY_APP_ID).replace("${value}", appId));
                    newParams.add(param);
                }
                if (!StringUtils.isEmpty(selfAppId) && !hasSelfAppId) {
                    String paramStr = inParamTemplate;
                    JSONObject param = JSONObject.parseObject(paramStr.replace("${name}", KEY_SELF_APP_ID).replace("${value}", selfAppId));
                    newParams.add(param);
                }
                URI uri = exchange.getRequest().getURI();
                String query = "parameters=";
                if (!newParams.isEmpty()) {
                    try {
                        query = query + URLEncoder.encode(newParams.toJSONString(), "UTF-8");
                        log.info("query:" + query);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                URI newUri = UriComponentsBuilder.fromUri(uri).replaceQuery(query) //newParams.toJSONString())
                    .build(true).toUri();

                ServerHttpRequest newReq = exchange.getRequest().mutate().header("X-Metabase-Session", token)
                    .header("Content-Type", "application/json;UTF-8").header(HttpHeaders.CONTENT_LENGTH, "0").uri(newUri).build();
                String bodyStr = "";

                DataBuffer bodyDataBuffer = stringBuffer(bodyStr);
                Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);

                request = new ServerHttpRequestDecorator(newReq){

                    @Override
                    public Flux<DataBuffer> getBody() {
                        return bodyFlux;
                    }
                };//封装我们的request


                return chain.filter(exchange.mutate().request(request).build()).then(Mono.fromRunnable(() -> {
                    ServerHttpResponse resp = exchange.getResponse();
                    if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {//metabase token失效
                        token = null;
                        redisTemplate.opsForValue().set(TOKEN_KEY, token, config.getExp(), TimeUnit.MINUTES);
                    }
                }));
        }

        private Mono<Void> returnMono(GatewayFilterChain chain,ServerWebExchange exchange){
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                Long startTime = exchange.getAttribute("startTime");
                if (startTime != null){
                    long executeTime = (System.currentTimeMillis() - startTime);
                    log.info("请求接口:{}\t耗时:{}ms\t状态码：{}" , exchange.getRequest().getURI(), executeTime, Objects
                        .requireNonNull(exchange.getResponse().getStatusCode()).value());
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


    @Override public String name() {
        return "MetabaseGatewayFilter";
    }



    public static class Config {

        private String url;
        private String username;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public long getExp() {
            return exp;
        }

        public void setExp(long exp) {
            this.exp = exp;
        }

        private long exp = 14*24*60;
    }


    public static void main(String[] args) {
        String body = "{\"appId\":\"[16]\"}";
        JSONObject requestBody = JSONObject.parseObject(body);
        Object test = requestBody.get("appId");

        if (requestBody != null) {
            for (String key : requestBody.keySet()) {
                String paramStr = paramTemplate.replace("${type}", "text");
                if (requestBody.get(key) instanceof Integer)
                    paramStr = paramTemplate.replace("${type}", "number");
                else if(requestBody.getString(key).startsWith("[") && requestBody.getString(key).endsWith("]"))
                    paramStr = inParamTemplate;
                paramStr = paramStr.replace("${name}", key).replace("[${value}]", requestBody.getString(key));
                JSONObject param = JSONObject.parseObject(paramStr);
                int i = 0;
            }

        }

        System.out.println(test);
    }

}
