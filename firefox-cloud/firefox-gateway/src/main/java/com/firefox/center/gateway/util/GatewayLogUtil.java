package com.firefox.center.gateway.util;

import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.constants.BusinessConstants;
import com.firefox.center.gateway.model.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import org.yaml.snakeyaml.util.UriEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @Author: gengyun
 * @Description: 日志记录工具
 * @Date: 2020/06/16
 */
public class GatewayLogUtil {

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

    /**
     * 记录 GET、POST、PUT、DELETE
     * @param method
     * @return
     */
    private static boolean hasBody(HttpMethod method) {
        return method == HttpMethod.GET || method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE || method == HttpMethod.PATCH;
    }

    private static boolean shouldRecordBody(MediaType contentType) {
        String type = contentType.getType();
        String subType = contentType.getSubtype();

        if (APPLICATION.equals(type)) {
            return
                    "json".equals(subType)
                    || "x-www-form-urlencoded".equals(subType)
                    || "xml".equals(subType)
                    || "atom+xml".equals(subType)
                    || "rss+xml".equals(subType);
        } else if (MULTIPART.equals(type)) {
            return "form-data".equals(subType);
        } else if(TEXT.equals(type)){
            return true;
        } else {
            //form没有记录
            return false;
        }
    }

    private static Mono<Void> doRecordBody(Log log, Flux<DataBuffer> body, Charset charset, boolean isRequest) {

        return DataBufferUtilFix.join(body)
                .doOnNext(wrapper -> {
                    if(isRequest) {
                        log.setRequestBody(new String(wrapper.getData(), charset));
                        if(!StringUtils.isEmpty(log.getRequestBody())) {//请求参数放到body
                            Map<String,String> reqParams = parseParams(log.getRequestBody());
                            log.setBrand(StringUtils.isEmpty(reqParams.get(PARAM_cw_devicemodel))?log.getBrand():reqParams.get(PARAM_cw_devicemodel));
                            log.setCity(StringUtils.isEmpty(reqParams.get(PARAM_cw_city))?log.getCity():reqParams.get(PARAM_cw_city));
                            log.setCountry(StringUtils.isEmpty(reqParams.get(PARAM_cw_country))?log.getCountry():reqParams.get(PARAM_cw_country));
                            log.setCwClient(StringUtils.isEmpty(reqParams.get(PARAM_cw_client))?log.getCwClient():reqParams.get(PARAM_cw_client));
                            log.setCwDevice(StringUtils.isEmpty(reqParams.get(PARAM_cw_device))?log.getCwDevice():reqParams.get(PARAM_cw_device));
                            log.setCwMachineType(StringUtils.isEmpty(reqParams.get(PARAM_cw_machine_type))?log.getCwMachineType():reqParams.get(PARAM_cw_machine_type));
                            log.setDeviceId(StringUtils.isEmpty(reqParams.get(PARAM_cw_machine_id))?log.getDeviceId():reqParams.get(PARAM_cw_machine_id));
                            log.setLatitude(StringUtils.isEmpty(reqParams.get(PARAM_cw_latitude))?log.getLatitude():reqParams.get(PARAM_cw_latitude));
                            log.setLongitude(StringUtils.isEmpty(reqParams.get(PARAM_cw_longitude))?log.getLongitude():reqParams.get(PARAM_cw_longitude));
                            log.setModel(StringUtils.isEmpty(reqParams.get(PARAM_cw_devicemodel))?log.getModel():reqParams.get(PARAM_cw_devicemodel));
                            log.setNetworkType(StringUtils.isEmpty(reqParams.get(PARAM_cw_networktype))?log.getNetworkType():reqParams.get(PARAM_cw_networktype));
                            log.setOs(StringUtils.isEmpty(reqParams.get(PARAM_cw_os))?log.getOs():reqParams.get(PARAM_cw_os));
                            log.setProvince(StringUtils.isEmpty(reqParams.get(PARAM_cw_province))?log.getProvince():reqParams.get(PARAM_cw_province));
                        }
                    }

                    else {
                        log.setResponseBody(new String(wrapper.getData(), charset));
                        if(!StringUtils.isEmpty(log.getResponseBody())) {
                            try {
                                JSONObject resp = JSONObject.parseObject(log.getResponseBody());
                                if (resp != null && resp.get("code") != null) {
                                    log.setResponseCode(resp.getString("code"));
                                    log.setResponseSuccess(resp.getString("success"));
                                }
                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    wrapper.clear();
                }).then();
    }

    public static Charset getMediaTypeCharset(@Nullable MediaType mediaType) {
        if (mediaType != null && mediaType.getCharset() != null) {
            return mediaType.getCharset();
        } else {
            return StandardCharsets.UTF_8;
        }
    }

    private static Map<String, String> parseParams(String reqParams) {
        //有参数
        Map<String,String> params = new HashMap<>();
        if(StringUtils.isEmpty(reqParams)) {
            return params;
        }
        reqParams = UriEncoder.decode(reqParams);
        String[] arrayParams = reqParams.split("&");

        for (String param : arrayParams) {
            String[] keyValue = param.split("=");
            if(keyValue != null && keyValue.length > 1)
                params.put(keyValue[0], keyValue[1]);
        }
        return params;

    }

    public static Mono<Void> recorderOriginalRequest(ServerWebExchange exchange, Map<String, Object> userInfo) {
        Log log = new Log();

        exchange.getAttributes().put(REQUEST_RECORDER_LOG_BUFFER, log);
        exchange.getAttributes().put(REQUEST_RECORDER_LOG_TIME, System.currentTimeMillis());
        //return Mono.empty();


        if(userInfo != null && userInfo.get("uid") != null) {
            log.setDistinctId("" + userInfo.get("uid"));
            log.setUid("" + userInfo.get("uid"));
            log.setSid("" + userInfo.get("sid"));
            log.setUsercode("" + userInfo.get("usercode"));
            log.setMobile("" + userInfo.get("mobile"));
        }
        exchange.getAttributes().put(REQUEST_RECORDER_LOG_BUFFER, log);
        exchange.getAttributes().put(REQUEST_RECORDER_LOG_TIME, System.currentTimeMillis());
        ServerHttpRequest request = exchange.getRequest();
        return recorderRequest(request, request.getURI(), log);
    }

    public static Mono<Void> recorderRouteRequest(ServerWebExchange exchange) {
        Log log = new Log();
        URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
        log = (Log)exchange.getAttribute(REQUEST_RECORDER_LOG_BUFFER);

        return recorderRequest(exchange.getRequest(), requestUrl, log);
    }

    private static Mono<Void> recorderRequest(ServerHttpRequest request, URI uri, Log log) {
        if (uri == null) {
            uri = request.getURI();
        }

        HttpMethod method = request.getMethod();
        HttpHeaders headers = request.getHeaders();

        log.setMethod(method.toString());
        log.setUrl(uri.toString());
        log.setHeader(headers);
        log.setUserAgent(headers.getFirst("User-Agent"));
        log.setUrlQuery(request.getURI().getQuery());
        log.setAppId("");
        log.setBrand(request.getQueryParams().getFirst(PARAM_cw_devicemodel));
        log.setBrowser("");
        log.setBrowserVersion("");
        log.setCity(request.getQueryParams().getFirst(PARAM_cw_city));
        log.setCountry(request.getQueryParams().getFirst(PARAM_cw_country));
        log.setCwAuthorization(headers.getFirst("cw-authorization"));
        log.setCwClient(request.getQueryParams().getFirst(PARAM_cw_client));
        log.setCwDevice(request.getQueryParams().getFirst(PARAM_cw_device));
        log.setCwMachineType(request.getQueryParams().getFirst(PARAM_cw_machine_type));
        log.setDeviceId(request.getQueryParams().getFirst(PARAM_cw_machine_id));
        if(StringUtils.isEmpty(log.getDistinctId())) //未登录下distinctId设为设备id
            log.setDistinctId(log.getDeviceId());
        log.setHost(headers.getHost().getHostString());
        log.setIp(getRemortIP(request));
        log.setIsLoginId(StringUtils.isEmpty(log.getUid())?0:1);
        log.setLatitude(request.getQueryParams().getFirst(PARAM_cw_latitude));
        log.setLongitude(request.getQueryParams().getFirst(PARAM_cw_longitude));
        log.setModel(request.getQueryParams().getFirst(PARAM_cw_devicemodel));
        log.setNetworkType(request.getQueryParams().getFirst(PARAM_cw_networktype));
        log.setOs(request.getQueryParams().getFirst(PARAM_cw_os));
        log.setProvince(request.getQueryParams().getFirst(PARAM_cw_province));
        log.setReceiveTime(System.currentTimeMillis());
        log.setReferrer(headers.getFirst("referer"));
        log.setTime(System.currentTimeMillis());
        Charset bodyCharset = null;
        if (hasBody(method)) {
            long length = headers.getContentLength();
            if (length <= 0) {
            } else {
                MediaType contentType = headers.getContentType();
                if (contentType == null) {
                } else if (!shouldRecordBody(contentType)) {
                } else {
                    bodyCharset = getMediaTypeCharset(contentType);
                }
            }
        }

        if (bodyCharset != null) {
            return doRecordBody(log, request.getBody(), bodyCharset, true)
                    .then();
        } else {
            return Mono.empty();
        }

    }

    public static String getRemortIP(ServerHttpRequest request) {
        if (request.getHeaders().get("x-forwarded-for") == null) {
            return request.getRemoteAddress().getHostString();
        }
        return "" + request.getHeaders().get("x-forwarded-for");
    }

    public static Mono<Void> recorderResponse(ServerWebExchange exchange) {

        String appId = exchange.getRequest().getHeaders().getFirst(BusinessConstants.HEADER_APP_ID);

        ServerHttpResponse response = exchange.getResponse();
        Log log = exchange.getAttribute(REQUEST_RECORDER_LOG_BUFFER);

        assert log != null;
        HttpStatus code = response.getStatusCode();
        if (code == null) {
            return Mono.empty();
        }
        log.setAppId(appId);
        //log.setAppName(appName);
        log.setHttpStatus("" + code.value());
        log.setDuration(System.currentTimeMillis() - (Long)exchange.getAttribute(REQUEST_RECORDER_LOG_TIME));


        HttpHeaders headers = response.getHeaders();
        log.setResponseHeaders(headers);


        /** 记录响应body，因数据量太大可能导致内存溢出，暂时关闭
        Charset bodyCharset = null;
        MediaType contentType = headers.getContentType();
        if (contentType == null) {
        } else if (!shouldRecordBody(contentType)) {
        } else {
            bodyCharset = getMediaTypeCharset(contentType);
        }

        if (bodyCharset != null) {
            return doRecordBody(log, response.copy(), bodyCharset, false)
                    .then();
        } else {
        }
         **/
        return Mono.empty();
    }

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String getLogData(ServerWebExchange exchange) {
        Log log = exchange.getAttribute(REQUEST_RECORDER_LOG_BUFFER);
        return objectToJson(log);
    }

    /**
     27      * 将对象转换成json字符串。
     28      * <p>Title: pojoToJson</p>
     29      * <p>Description: </p>
     30      *
     31      * @param data
     32      * @return
     33      */
   public static String objectToJson(Object data) {
        try {
            String string = MAPPER.writeValueAsString(data);
            return string;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
   }

}
