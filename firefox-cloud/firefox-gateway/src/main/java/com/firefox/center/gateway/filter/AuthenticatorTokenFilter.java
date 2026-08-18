package com.firefox.center.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.R;
import com.firefox.center.common.constants.BusinessConstants;
import com.firefox.center.common.constants.FileNameConstants;
import com.firefox.center.common.constants.SecurityConstants;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.exception.BusinessException;
import com.firefox.center.common.model.OauthTenantPackage;
import com.firefox.center.common.utils.DateUtil;
import com.firefox.center.gateway.properties.MyGatewayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//import com.firefox.common.redis.utils.UserUtils;

/**
 * @Author: ZJL
 * @Description: token校验
 * @Date: 2020/06/16
 */
@Slf4j @Component public class AuthenticatorTokenFilter implements GlobalFilter, Ordered {

    public final static String HEADER_MANAGE_APPS = "AppIds";

    public final static String ATTRIBUTE_TOKEN = "token";

    @Autowired ServerCodecConfigurer codecConfigurer;

    //@Autowired
    //private UserUtils userUtils;

    @Autowired private MyGatewayProperties myGatewayProperties;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired private RedisTemplate redisTemplate;

    @Override public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerWebExchange newEx = null;
        //pre filter
        log.info("---------------------Token校验 前置 Filter---------------------");
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String urlPath = request.getURI().getPath();

        AtomicBoolean ignoreThisUrl = new AtomicBoolean(false);
        myGatewayProperties.getIgnore().getTokenFilter().forEach(ignoreUrl -> {
            if (antPathMatcher.match(ignoreUrl, urlPath)) {
                ignoreThisUrl.set(true);
            }
        });

        ServerHttpRequest newReq = exchange.getRequest();
        if (!ignoreThisUrl.get()) {
            //List<String> authorization = exchange.getRequest().getHeaders().get("Authorization");
            String token = exchange.getRequest().getHeaders().getFirst(OpenIMFilterFactory.HEADER_TOKEN);
            if (StringUtils.isEmpty(token)) {
                List<String> authorization = exchange.getRequest().getHeaders().get("cw-authorization");
                if (null == authorization || authorization.size() <= 0 || !StringUtils.hasText(authorization.get(0))) {
                    //token为空
                    String message = JSONObject.toJSONString(R.error(CodeEnum.NONE_AUTHORIZATION));
                    byte[] bits = message.getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = response.bufferFactory().wrap(bits);
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    //指定编码，否则在浏览器中会中文乱码
                    response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                    return response.writeWith(Mono.just(buffer));
                } else {
                    if (authorization.get(0).startsWith("bearer ") || authorization.get(0).startsWith("Bearer ")) {
                        token = authorization.get(0).split(" ")[1];
                    }
                }
            }
            exchange.getAttributes().put(ATTRIBUTE_TOKEN, token);
            if (!StringUtils.isEmpty(token)) {
                try {
                    //公钥解密校验
                    ClassPathResource resource = new ClassPathResource(FileNameConstants.PUBLIC_KEY);
                    String publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
                    String tokenInfo = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey)).getClaims();
                    log.info(tokenInfo);
                    JSONObject jsonToken = JSON.parseObject(tokenInfo);
                    long expire = (int)jsonToken.get("exp");
                    if (DateUtil.nowTimeStamp() > expire) {//token已过期
                        //token为空
                        String message = JSONObject.toJSONString(R.error(CodeEnum.EXP_AUTHORIZATION));
                        byte[] bits = message.getBytes(StandardCharsets.UTF_8);
                        DataBuffer buffer = response.bufferFactory().wrap(bits);
                        response.setStatusCode(HttpStatus.FORBIDDEN);
                        //指定编码，否则在浏览器中会中文乱码
                        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                        return response.writeWith(Mono.just(buffer));
                    }
                    //获取当前路由id
                    Route route = (Route)exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                    String serviceId = route.getId();

                    if (BusinessConstants.TYPE_CREDENTIALS.equalsIgnoreCase(jsonToken.getString("utype"))) {
                        String appId = jsonToken.getString(SecurityConstants.TOKEN_KEY_APP_ID);
                        boolean isValid = false;
                        Date now = new Date();
                        List<OauthTenantPackage> packages = (List<OauthTenantPackage>)redisTemplate.opsForValue()
                            .get(SecurityConstants.CACHE_PACKAGE_KEY + "_" + appId);
                        if (packages != null && !packages.isEmpty()) {
                            for (OauthTenantPackage pack : packages) {
                                log.info("now service:" + serviceId + " package info:startTime:" + pack.getStartTime()
                                    + " endTime:" + pack.getEndTime() + " serviceName:" + pack.getService()
                                    + "centerId:" + pack.getCenterId());
                                if ((pack.getEndTime() == null || now.before(pack.getEndTime())) && now
                                    .after(pack.getStartTime()) && serviceId.equalsIgnoreCase(pack.getService())) {
                                    isValid = true;
                                    break;
                                }

                            }
                        }
                        if (!isValid) {//无有效套餐，拒绝访问
                            String message = JSONObject.toJSONString(R.error(CodeEnum.NONE_PACKAGE));
                            byte[] bits = message.getBytes(StandardCharsets.UTF_8);
                            DataBuffer buffer = response.bufferFactory().wrap(bits);
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
                            //指定编码，否则在浏览器中会中文乱码
                            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                            return response.writeWith(Mono.just(buffer));
                        }
                    }

                    //向headers中放文件，记得build
                    newReq = exchange.getRequest().mutate().header(BusinessConstants.HEADER_TOKEN_CHECK, "true")
                        .header(BusinessConstants.HEADER_CLIENT_TYPE, jsonToken.getString("client_type"))
                        .header(BusinessConstants.HEADER_APP_VERSION, jsonToken.getString("version"))
                        .header(BusinessConstants.HEADER_APP_ID,
                            jsonToken.getString(SecurityConstants.TOKEN_KEY_APP_ID))
                        .header(BusinessConstants.HEADER_TENANT_ID, jsonToken.getString("tenant_id"))
                        .header(BusinessConstants.HEADER_USER_Type, jsonToken.getString("utype"))
                        .header(BusinessConstants.HEADER_USER_ID, jsonToken.getString("uid"))
                        .header(BusinessConstants.HEADER_SID, jsonToken.getString("sid"))
                        .header(BusinessConstants.HEADER_CACHE_UID, jsonToken.getString("cache_uid"))
                        .header(BusinessConstants.HEADER_SIDS, jsonToken.getString("sids"))
                        .header(HEADER_MANAGE_APPS, jsonToken.getString("manage_app_ids")).build();
                    log.info("headers:" + newReq.getHeaders());

                } catch (Exception e) {
                    e.printStackTrace();
                    //throw new BusinessException(CodeEnum.NONE_AUTHORIZATION);
                    //token为空
                    String message = JSONObject.toJSONString(R.error(CodeEnum.NONE_AUTHORIZATION));
                    byte[] bits = message.getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = response.bufferFactory().wrap(bits);
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    //指定编码，否则在浏览器中会中文乱码
                    response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                    return response.writeWith(Mono.just(buffer));
                }
            } else {
                //向headers中放文件，记得build
                newReq = exchange.getRequest().mutate().header(BusinessConstants.HEADER_TOKEN_CHECK, "false").build();
            }
        }

        return chain.filter(exchange.mutate().request(newReq).build()).then(Mono.fromRunnable(() -> {
            // post filter
            log.info("---------------------Token校验 后置 Filter---------------------");
        }));
    }

    @Override public int getOrder() {
        return -1;
    }

    @Bean public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter() {
            @Override public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                return chain.filter(exchange);
            }
        };
    }

}
