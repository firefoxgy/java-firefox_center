package com.firefox.center.sys.common.sms.service;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.firefox.center.sys.common.Assert;
import com.firefox.center.sys.common.exception.ExceptionCode;
import com.firefox.center.sys.common.mail.MailService;
import com.firefox.center.sys.common.sms.property.SmsProperty;
import com.firefox.center.sys.common.sms.util.SMSSignUtils;
import com.firefox.center.sys.common.util.DateUtil;
import com.firefox.center.sys.common.util.RedisUtil;
import com.firefox.center.sys.common.util.StrKit;
import com.firefox.center.common.R;
import com.firefox.center.common.Record;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SMSService {

    private final SmsProperty SmsProperty;
    private final RestTemplate restTemplate;
    private final MailService mailService;
    private final RedisUtil redisUtil;
    public static final String CACHE_KEY = "sms:bind";

    protected void cacheCode(String phone, String code) {
        String key=CACHE_KEY+":"+phone;
        redisUtil.set(key, code, SmsProperty.getExpire());
    }

    public String getCode(String phone) {
        String key=CACHE_KEY+":"+phone;
        Object oToken=redisUtil.get(key);
        return oToken==null?"":oToken.toString();
    }

    public void delCode(String phone) {
        String key=CACHE_KEY+":"+phone;
        redisUtil.del(key);
    }

    public R<?> sendCode(String phone) {
        try{
            String code=StrKit.getRandomNum(6);
            String requestUrl = SmsProperty.getApiurl();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Record params = new Record();
            params.set("appkey", SmsProperty.getAppKey());
            params.set("module", SmsProperty.getModule());
            params.set("ts", DateUtil.nowTimeStamp());
            params.set("type", SmsProperty.getType());
            params.set("json", new Record().set("code", code).toJson());
            params.set("tel", phone);
            params.set("signName", SmsProperty.getSignName());
            params.set("templateCode", SmsProperty.getTemplateCode());
            String sign= SMSSignUtils.sign(params.getColumns(), SmsProperty.getAppSecret());
            params.set("sign", sign);
            HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(params.getColumns(), headers);
            ResponseEntity<String> entity = restTemplate.postForEntity(requestUrl, request, String.class);
            if(entity==null){
                ThreadUtil.execAsync(() -> mailService.sendMessage("智慧云报短信发送失败","手机号为："+phone+", 发送短信验证码["+code+"]失败！"));
            }
            Assert.notNull(entity, ExceptionCode.PAY_REQ_FAIL);
            JSONObject json= JSON.parseObject(entity.getBody());
            if("true".equals(json.getString("success"))){
                cacheCode(phone, code);
                return R.ok("验证码发送成功");
            }else{
                ThreadUtil.execAsync(() -> mailService.sendMessage("智慧云报短信发送失败",entity.getBody()));
                return R.error("验证码发送失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return R.error("验证码发送失败");
        }
    }

    public R<?> checkCode(String phone, String code) {
        if(StrKit.notBlank(getCode(phone))){
            if(code.equals(getCode(phone))){
                return R.ok();
            }
            return R.error("验证码错误");
        }else{
            return R.error("验证码已过期");
        }
    }

}
