package com.firefox.center.gateway.model;

import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.utils.IdGen;
import lombok.Data;
import org.springframework.http.HttpHeaders;

/**
 * @program: behavior
 * @description: 记录日志类
 * @author: yungeng
 * @created: 2021/03/31 10:39
 */
@Data
public class Log {
    String appId;
    String appName;
    String cwAuthorization;
    String model;
    String os;
    String networkType;
    String cwClient;
    String cwDevice;
    String cwMachineType;
    long time;
    String latitude;
    String longitude;
    String url;
    String urlQuery;
    String referrer;
    String referrerHost;
    String userAgent;
    long receiveTime;
    String brand;
    String deviceId;
    String distinctId;
    String browser;
    String browserVersion;
    String ip;
    String city;
    String province;
    String country;
    int isLoginId;
    HttpHeaders header;
    String method;
    String host;
    String requestBody;
    String responseBody;
    HttpHeaders responseHeaders;
    String httpStatus;
    String responseCode;
    String responseSuccess;
    String uid;
    String sid;
    String usercode;
    String mobile;
    long duration;
    long id;
    JSONObject tokenInfo;

    public Log() {
        this.id = IdGen.getId();
    }
}
