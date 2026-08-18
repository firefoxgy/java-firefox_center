package com.firefox.center.sys.common.wx.mini;

import com.alibaba.fastjson.JSONObject;
import com.firefox.center.sys.common.util.RetryUtils.ResultCheck;

import java.io.Serializable;

/**
 * 封装 access_token
 */
public class AccessToken implements ResultCheck, Serializable {
    private static final long serialVersionUID = -822464425433824314L;

    private String token;           // 正确获取到token 时有值
    private Integer errcode;        // 出错时有值
    private String errmsg;          // 出错时有值
    private Integer expires_in;     // 正确获取到 oken 时有值
    private Long expiredTime;       // 正确获取到 token 时有值，存放过期时间
    private String json;

    public AccessToken(String jsonStr) {
        this.json = jsonStr;
        try {
            JSONObject temp = JSONObject.parseObject(jsonStr);
            if(temp.getIntValue("code")==0){
                token = temp.getString("access_token");
                expires_in = 7200;
                errcode = null;
                errmsg = temp.getString("errmsg");
                expiredTime = System.currentTimeMillis() + ((expires_in - 9) * 1000);
            }else{
                errcode = temp.getIntValue("errcode");
                errmsg = temp.getString("errmsg");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getJson() {
        return json;
    }

    public boolean isAvailable() {
        if (expiredTime == null)
            return false;
        if (errcode != null)
            return false;
        if (expiredTime < System.currentTimeMillis())
            return false;
        return token != null;
    }

    public String getToken() {
        return token;
    }

    public Integer getExpiresIn() {
        return expires_in;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public Integer getErrorCode() {
        return errcode;
    }

    public String getErrorMsg() {
        return errmsg;
    }

    @Override
    public boolean matching() {
        return isAvailable();
    }
}
