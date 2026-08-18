package com.firefox.center.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.constants.FileNameConstants;
import com.firefox.center.common.constants.PropertiesConstants;
import com.firefox.center.common.constants.SecurityConstants;
import com.firefox.center.gateway.model.Log;
import com.firefox.center.gateway.util.GatewayLogUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.yaml.snakeyaml.util.UriEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.net.URI;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ZJL
 * @Description: 全局日志记录
 * @Date: 2020/06/16
 */
@Slf4j
@Component
@ConditionalOnProperty(value = PropertiesConstants.MY_GATEWAY_PREFIX + ".enable-logger", matchIfMissing = true)
public class LoggerGlobalFilter implements GlobalFilter, Ordered {

    private final static String REQUEST_RECORDER_LOG_BUFFER = "LoggerGlobalFilter.request_recorder_log_buffer";

    private final static String REQUEST_RECORDER_LOG_TIME = "LoggerGlobalFilter.request_recorder_log_time";

    private final static String APPLICATION = "application";

    private final static String MULTIPART = "multipart";

    private final static String TEXT = "text";

    public final static String PARAM_cw_city = "cw_city";
    public final static String PARAM_cw_country = "cw_country";
    public final static String PARAM_cw_client = "cw_client";
    public final static String PARAM_cw_device = "cw_device";
    public final static String PARAM_cw_machine_type = "cw_machine_type";
    public final static String PARAM_cw_machine_id = "cw_machine_id";
    public final static String PARAM_cw_latitude = "cw_latitude";
    public final static String PARAM_cw_longitude = "cw_longitude";
    public final static String PARAM_cw_devicemodel = "cw_devicemodel";
    public final static String PARAM_cw_networktype = "cw_networktype";
    public final static String PARAM_cw_os = "cw_os";
    public final static String PARAM_cw_province = "cw_province";

    private final static String HTTP = "http";

    private final static String HTTPS = "https";

    private final static String WEBSOCKET = "websocket";

    public final static String CACHE_BODY_KEY  = "cache_body_key";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("---------------------日志记录 前置 Filter---------------------");
        ServerHttpRequest originalRequest = exchange.getRequest();
        URI originalRequestUrl = originalRequest.getURI();
        String tokenStr = exchange.getRequest().getHeaders().getFirst("cw-authorization");
        JSONObject tokenInfo = null;
        try {
            if (!StringUtils.isEmpty(tokenStr) && (tokenStr.startsWith("bearer ") || tokenStr.startsWith("Bearer "))) {
                String token = tokenStr.split(" ")[1];
                    //公钥解密校验
                    ClassPathResource resource = new ClassPathResource(FileNameConstants.PUBLIC_KEY);
                    String publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
                    String strTokenInfo = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey)).getClaims();
                    log.debug(strTokenInfo);
                    JSONObject jsonToken = JSON.parseObject(strTokenInfo);

                    if (jsonToken == null) {
                        return chain.filter(exchange);
                    }
                    tokenInfo = jsonToken;
                }
        }catch(Exception e){
            e.printStackTrace();
        }


        //只记录http的请求
        String scheme = originalRequestUrl.getScheme();
        if ((!HTTP.equals(scheme) && !HTTPS.equals(scheme))) {
            return chain.filter(exchange);
        }

        String upgrade = originalRequest.getHeaders().getUpgrade();
        if (WEBSOCKET.equalsIgnoreCase(upgrade)) {
            return chain.filter(exchange);
        }
        /*
        RecorderServerHttpRequestDecorator request = new RecorderServerHttpRequestDecorator(exchange.getRequest());
        RecorderServerHttpResponseDecorator response = new RecorderServerHttpResponseDecorator(exchange.getResponse());

        ServerWebExchange ex = exchange.mutate()
                .request(request)
                .response(response)
                .build();



        return GatewayLogUtil.recorderOriginalRequest(ex, userInfo)
                .then(Mono.defer(() -> chain.filter(ex)))
                .then(Mono.defer(() -> finishLog(ex)));

         */
        return operationExchange(exchange, chain, tokenInfo)
            .then(Mono.defer(() -> finishLog(exchange)));
    }

    private Mono<Void> finishLog(ServerWebExchange ex) {
        return GatewayLogUtil.recorderResponse(ex)
                .doOnSuccess(x -> log.info(GatewayLogUtil.getLogData(ex)))
                .doOnSuccess(x -> log.debug("---------------------日志记录 结束 Filter---------------------"));
    }

    private static Map<String, String> parseParams(String reqParams) {
        //有参数
        Map<String,String> params = new HashMap<>();
        if(org.apache.commons.lang.StringUtils.isEmpty(reqParams)) {
            return params;
        }
        try {
            reqParams = UriEncoder.decode(reqParams);
            String[] arrayParams = reqParams.split("&");

            for (String param : arrayParams) {
                String[] keyValue = param.split("=");
                if (keyValue != null && keyValue.length > 1)
                    params.put(keyValue[0], keyValue[1]);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return params;

    }

    public static String getRemortIP(ServerHttpRequest request) {
        if (request.getHeaders().get("x-forwarded-for") == null) {
            return request.getRemoteAddress().getHostString();
        }
        return "" + request.getHeaders().get("x-forwarded-for");
    }

    private Mono<Void> operationExchange(ServerWebExchange exchange, GatewayFilterChain chain,JSONObject tokenInfo) {
        Log reqLog = new Log();

        exchange.getAttributes().put(REQUEST_RECORDER_LOG_BUFFER, reqLog);
        exchange.getAttributes().put(REQUEST_RECORDER_LOG_TIME, System.currentTimeMillis());
        //return Mono.empty();
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();

        HttpMethod method = request.getMethod();
        HttpHeaders headers = request.getHeaders();


        if(tokenInfo != null && tokenInfo.get("uid") != null) {
            reqLog.setDistinctId("" + tokenInfo.get("uid"));
            reqLog.setUid("" + tokenInfo.get("uid"));
            reqLog.setSid("" + tokenInfo.get("sid"));
            reqLog.setUsercode("" + tokenInfo.get("usercode"));
            reqLog.setMobile("" + tokenInfo.get("mobile"));

            reqLog.setAppId(tokenInfo.getString(SecurityConstants.TOKEN_KEY_APP_ID));
            reqLog.setTokenInfo(tokenInfo);
        }
        exchange.getAttributes().put(REQUEST_RECORDER_LOG_BUFFER, reqLog);
        exchange.getAttributes().put(REQUEST_RECORDER_LOG_TIME, System.currentTimeMillis());

        reqLog.setMethod(method.toString());
        reqLog.setUrl(uri.toString());
        reqLog.setHeader(headers);
        reqLog.setUserAgent(headers.getFirst("User-Agent"));
        reqLog.setUrlQuery(request.getURI().getQuery());
        reqLog.setBrand(request.getQueryParams().getFirst(PARAM_cw_devicemodel));
        reqLog.setBrowser("");
        reqLog.setBrowserVersion("");
        reqLog.setCity(request.getQueryParams().getFirst(PARAM_cw_city));
        reqLog.setCountry(request.getQueryParams().getFirst(PARAM_cw_country));
        reqLog.setCwAuthorization(headers.getFirst("cw-authorization"));
        reqLog.setCwClient(request.getQueryParams().getFirst(PARAM_cw_client));
        reqLog.setCwDevice(request.getQueryParams().getFirst(PARAM_cw_device));
        reqLog.setCwMachineType(request.getQueryParams().getFirst(PARAM_cw_machine_type));
        reqLog.setDeviceId(request.getQueryParams().getFirst(PARAM_cw_machine_id));
        if(org.apache.commons.lang.StringUtils.isEmpty(reqLog.getDistinctId())) //未登录下distinctId设为设备id
            reqLog.setDistinctId(reqLog.getDeviceId());
        reqLog.setHost(headers.getHost().getHostString());
        reqLog.setIp(getRemortIP(request));
        reqLog.setIsLoginId(org.apache.commons.lang.StringUtils.isEmpty(reqLog.getUid())?0:1);
        reqLog.setLatitude(request.getQueryParams().getFirst(PARAM_cw_latitude));
        reqLog.setLongitude(request.getQueryParams().getFirst(PARAM_cw_longitude));
        reqLog.setModel(request.getQueryParams().getFirst(PARAM_cw_devicemodel));
        reqLog.setNetworkType(request.getQueryParams().getFirst(PARAM_cw_networktype));
        reqLog.setOs(request.getQueryParams().getFirst(PARAM_cw_os));
        reqLog.setProvince(request.getQueryParams().getFirst(PARAM_cw_province));
        reqLog.setReceiveTime(System.currentTimeMillis());
        reqLog.setReferrer(headers.getFirst("referer"));
        reqLog.setTime(System.currentTimeMillis());
        // read & modify body
        ServerRequest serverRequest = new DefaultServerRequest(exchange);
        MediaType contentType = headers.getContentType();
        if(contentType == null)
            return chain.filter(exchange);
        String type = contentType.getType();
        String subType = contentType.getSubtype();
        if (MULTIPART.equals(type) && "form-data".equals(subType))
            return chain.filter(exchange);
        else {
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
                if(body == null || "null".equalsIgnoreCase(body))
                    return Mono.just(body);
                exchange.getAttributes().put(CACHE_BODY_KEY, body);
                Map<String, String> bodyMap = null;
                if (MediaType.APPLICATION_JSON_VALUE.equals(contentType.toString())) {
                    bodyMap = (Map)JSON.parse(body);
                } else
                    bodyMap = parseParams(body);
                reqLog.setRequestBody(body);
                if (!StringUtils.isEmpty(reqLog.getRequestBody())) {//请求参数放到body
                    Map<String, String> reqParams = bodyMap;
                    reqLog.setBrand(
                        StringUtils.isEmpty((String)reqParams.get(PARAM_cw_devicemodel)) ? reqLog.getBrand() : (String)reqParams.get(PARAM_cw_devicemodel));
                    reqLog.setCity(StringUtils.isEmpty((String)reqParams.get(PARAM_cw_city)) ? reqLog.getCity() : (String)reqParams.get(PARAM_cw_city));
                    reqLog.setCountry(
                        StringUtils.isEmpty((String)reqParams.get(PARAM_cw_country)) ? reqLog.getCountry() : (String)reqParams.get(PARAM_cw_country));
                    reqLog.setCwClient(
                        StringUtils.isEmpty((String)reqParams.get(PARAM_cw_client)) ? reqLog.getCwClient() : (String)reqParams.get(PARAM_cw_client));
                    reqLog.setCwDevice(
                        StringUtils.isEmpty((String)reqParams.get(PARAM_cw_device)) ? reqLog.getCwDevice() : (String)reqParams.get(PARAM_cw_device));
                    reqLog.setCwMachineType(
                        StringUtils.isEmpty((String)reqParams.get(PARAM_cw_machine_type)) ? reqLog.getCwMachineType() :
                            (String)reqParams.get(PARAM_cw_machine_type));
                    reqLog.setDeviceId(
                        StringUtils.isEmpty((String)reqParams.get(PARAM_cw_machine_id)) ? reqLog.getDeviceId() : (String)reqParams.get(PARAM_cw_machine_id));
                    reqLog.setLatitude(
                        StringUtils.isEmpty((String)reqParams.get(PARAM_cw_latitude)) ? reqLog.getLatitude() : (String)reqParams.get(PARAM_cw_latitude));
                    reqLog.setLongitude(
                        StringUtils.isEmpty((String)reqParams.get(PARAM_cw_longitude)) ? reqLog.getLongitude() : (String)reqParams.get(PARAM_cw_longitude));
                    reqLog.setModel(
                        StringUtils.isEmpty((String)reqParams.get(PARAM_cw_devicemodel)) ? reqLog.getModel() : (String)reqParams.get(PARAM_cw_devicemodel));
                    reqLog.setNetworkType(
                        StringUtils.isEmpty((String)reqParams.get(PARAM_cw_networktype)) ? reqLog.getNetworkType() : (String)reqParams.get(PARAM_cw_networktype));
                    reqLog.setOs(StringUtils.isEmpty((String)reqParams.get(PARAM_cw_os)) ? reqLog.getOs() : (String)reqParams.get(PARAM_cw_os));
                    reqLog.setProvince(
                        StringUtils.isEmpty((String)reqParams.get(PARAM_cw_province)) ? reqLog.getProvince() : (String)reqParams.get(PARAM_cw_province));
                }
                return Mono.just(body);
            });
            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            HttpHeaders newHeaders = new HttpHeaders();
            newHeaders.putAll(exchange.getRequest().getHeaders());
            newHeaders.remove(HttpHeaders.CONTENT_LENGTH);
            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, newHeaders);

            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override public HttpHeaders getHeaders() {
                        long contentLength = headers.getContentLength();
                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.putAll(super.getHeaders());
                        if (contentLength > 0) {
                            httpHeaders.setContentLength(contentLength);
                        } else {
                            httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                        }
                        return httpHeaders;
                    }

                    @Override public Flux<DataBuffer> getBody() {
                        return outputMessage.getBody();
                    }
                };
                return chain.filter(exchange.mutate().request(decorator).build());
            }));
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private static String SALT = "ptvuWgys3ZuVpKbp1lWvetMBuHD8weIV";

    private static Key getKeyInstance() {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        String apiKey = DatatypeConverter.printBase64Binary(SALT.getBytes());
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        return signingKey;
    }

    public static Map<String, Object> verifyJavaWebToken(String jwt) {
        try {
            Map<String, Object> jwtClaims =
                Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwt).getBody();
            return jwtClaims;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }


}
