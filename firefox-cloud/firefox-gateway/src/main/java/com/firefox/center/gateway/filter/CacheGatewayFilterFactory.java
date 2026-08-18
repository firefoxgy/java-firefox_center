package com.firefox.center.gateway.filter;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.R;
import com.firefox.center.common.constants.BusinessConstants;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.gateway.decorator.RecorderServerHttpRequestDecorator;
import com.firefox.center.gateway.decorator.RecorderServerHttpResponseDecorator;
import com.firefox.center.gateway.model.Log;
import com.firefox.center.gateway.util.DataBufferUtilFix;
import com.firefox.center.gateway.util.GatewayLogUtil;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: java-firefox_center
 * @description: metabase接口请求处理过滤器
 * @author: yungeng
 * @created: 2021/08/06 10:24
 */
@Component
@Slf4j
public class CacheGatewayFilterFactory extends AbstractGatewayFilterFactory<CacheGatewayFilterFactory.Config> {

    private static String token;

    public static final String KEY_APP_ID = "appId";

    private final Class<Config> configClass = Config.class;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Class<Config> getConfigClass() {
        return configClass;
    }

    @Override
    public Config newConfig() {
        return BeanUtils.instantiateClass(this.configClass);
    }


    @Autowired
    private RestTemplate restTemplate;

    @Override public GatewayFilter apply(Config config) {
        return new InnerFilter(config);
    }

    protected DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }

    private String  resolveBodyFromRequest(ServerHttpRequest serverHttpRequest){

        Flux<DataBuffer> body = serverHttpRequest.getBody();

        AtomicReference<String> bodyRef = new AtomicReference<>();

        body.subscribe(buffer -> {

            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());

            DataBufferUtils.release(buffer);

            bodyRef.set(charBuffer.toString());

        });

        return bodyRef.get();

    }


    @Override public String name() {
        return "CacheGatewayFilter";
    }

    public static final String CACHE_KEY_PRE = "API-CACHE-";
    public static final String CACHE_KEY_EXP = "CACHE-EXP";


    /**
     * 创建一个内部类，来实现2个接口，指定顺序
     */
    private class InnerFilter implements GatewayFilter, Ordered {

        private Config config;

        private AntPathMatcher antPathMatcher = new AntPathMatcher();

        InnerFilter(Config config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            // 在then方法里的，相当于aop中的后置通知

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            String appId = request.getHeaders().getFirst(BusinessConstants.HEADER_APP_ID);
            String tenantId = request.getHeaders().getFirst(BusinessConstants.HEADER_TENANT_ID);
            String uid = request.getHeaders().getFirst(BusinessConstants.HEADER_USER_ID);
            //String body = resolveBodyFromRequest(request);
            String body = exchange.getAttribute(LoggerGlobalFilter.CACHE_BODY_KEY);
            String keyBody = Base64.encode(body);
            String query = request.getURI().getQuery();
            String keyQuery = Base64.encode(query);
            String method = request.getMethod().toString();
            String url = request.getURI().toString();

            String urlPath = request.getURI().getPath();
            AtomicBoolean ignoreThisUrl = new AtomicBoolean(false);
            if(config.getIgnore() != null) {
                config.getIgnore().forEach(ignoreUrl -> {
                    if (antPathMatcher.match(ignoreUrl, urlPath)) {
                        ignoreThisUrl.set(true);
                    }
                });
            }

            if(!ignoreThisUrl.get()) {

                String cacheKey =
                    CACHE_KEY_PRE + url + "-" + method + "-" + keyQuery + "-" + tenantId + "-" + appId + "-" + uid + "-"
                        + keyBody;
                exchange.getAttributes().put(CACHE_KEY_PRE,cacheKey);
                exchange.getAttributes().put(CACHE_KEY_EXP,"" + config.getExp());

                String respBody = (String)redisTemplate.opsForValue().get(cacheKey); //从缓存取返回数据
                log.info("获取缓存结果 key:{} cache:{}", cacheKey, respBody);
                if (respBody != null) { //如果缓存有数据直接返回，不再转发接口
                    //获取metabase的token

                    DataBuffer buffer = response.bufferFactory().wrap(respBody.getBytes());
                    response.setStatusCode(HttpStatus.OK);
                    //指定编码，否则在浏览器中会中文乱码
                    response.getHeaders().add("Content-Type", "application/json;UTF-8");
                    return response.writeWith(Mono.just(buffer));

                }
            }


            return chain.filter(exchange);//.then(Mono.defer(() -> execResponse(exchange, cacheKey)));

        }

        private Mono<Void> execResponse(ServerWebExchange exchange,String cacheKey) {

                RecorderServerHttpResponseDecorator nResp = (RecorderServerHttpResponseDecorator)exchange.getResponse();

                return DataBufferUtilFix.join(nResp.copy())
                    .doOnNext(wrapper -> {
                        Charset bodyCharset = null;
                        MediaType contentType = nResp.getHeaders().getContentType();
                        if (contentType != null) {
                            bodyCharset = GatewayLogUtil.getMediaTypeCharset(contentType);
                        }
                        if(wrapper.getData() != null) {
                            String responseBody = new String(wrapper.getData(), bodyCharset);
                            if (HttpStatus.OK == nResp.getStatusCode())
                                redisTemplate.opsForValue().set(cacheKey, responseBody, config.getExp(), TimeUnit.SECONDS);
                        }
                        wrapper.clear();
                    }).then();

        }

        @Override
        public int getOrder() {
            return -1;
        }
    }

    public static class Config {

        public long getExp() {
            return exp;
        }

        public void setExp(long exp) {
            this.exp = exp;
        }

        private long exp = 60;

        public List<String> getIgnore() {
            return ignore;
        }

        public void setIgnore(List<String> ignore) {
            this.ignore = ignore;
        }

        private List<String> ignore;


    }


}

