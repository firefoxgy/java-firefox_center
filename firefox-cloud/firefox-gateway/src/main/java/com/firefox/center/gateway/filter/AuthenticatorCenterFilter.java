package com.firefox.center.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.R;
import com.firefox.center.common.constants.BusinessConstants;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.gateway.component.CacheService;
import com.firefox.center.gateway.properties.MyGatewayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: ZJL
 * @Description: 权限校验
 * @Date: 2020/06/16
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticatorCenterFilter {

    private final CacheService cacheService;
    private final MyGatewayProperties myGatewayProperties;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Bean
    @Order(2)
    public GlobalFilter permissionFilter(){
        return ((ServerWebExchange exchange, GatewayFilterChain chain) -> {
            //pre filter
            log.info("---------------------权限校验 前置 Filter---------------------");
            if(exchange != null) {
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();
                String urlPath = request.getURI().getPath();

                AtomicBoolean ignoreThisUrl = new AtomicBoolean(false);
                myGatewayProperties.getIgnore().getTokenFilter().forEach(ignoreUrl -> {
                    if(antPathMatcher.match(ignoreUrl, urlPath)){
                        ignoreThisUrl.set(true);
                    }
                });
                String tokenCheck=request.getHeaders().getFirst(BusinessConstants.HEADER_TOKEN_CHECK);
                if("true".equals(tokenCheck)){
                    String uType=request.getHeaders().getFirst(BusinessConstants.HEADER_USER_Type);
                    Route route =(Route) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                    String id = route.getId();
                    if(!ignoreThisUrl.get() && !BusinessConstants.TYPE_ADMIN.equals(uType) && !BusinessConstants.TYPE_CREDENTIALS.equals(uType)){
                        String token=request.getHeaders().getFirst("Authorization");
                        String userId=request.getHeaders().getFirst(BusinessConstants.HEADER_CACHE_UID);
                        if(!"0".equals(userId)){
                            List<String> pathList= cacheService.getPath(Long.valueOf(userId));
                            AtomicBoolean authUrl = new AtomicBoolean(false);
                            pathList.forEach(path -> {
                                if(antPathMatcher.match(getRoutePath(path), urlPath)){
                                    authUrl.set(true);
                                }
                            });
                            if(!authUrl.get()){
                                //token为空
                                String message = JSONObject.toJSONString(R.error(CodeEnum.TENANT_API_NO_AUTH));
                                byte[] bits = message.getBytes(StandardCharsets.UTF_8);
                                DataBuffer buffer = response.bufferFactory().wrap(bits);
                                response.setStatusCode(HttpStatus.FORBIDDEN);
                                //指定编码，否则在浏览器中会中文乱码
                                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                                return response.writeWith(Mono.just(buffer));
                            }
                        }else {
                            if(StrKit.isBlank(token)){
                                //token为空
                                String message = JSONObject.toJSONString(R.error(CodeEnum.TENANT_API_NO_AUTH));
                                byte[] bits = message.getBytes(StandardCharsets.UTF_8);
                                DataBuffer buffer = response.bufferFactory().wrap(bits);
                                response.setStatusCode(HttpStatus.FORBIDDEN);
                                //指定编码，否则在浏览器中会中文乱码
                                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                                return response.writeWith(Mono.just(buffer));
                            }
                        }
                    }
                }
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // post filter
                log.info("---------------------权限校验 后置 Filter---------------------");
            }));
        });
    }

    protected String getRoutePath(String path){
        return path+"/**";
    }

}
