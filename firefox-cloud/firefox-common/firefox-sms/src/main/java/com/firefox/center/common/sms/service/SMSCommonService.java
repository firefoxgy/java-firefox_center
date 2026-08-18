package com.firefox.center.common.sms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.R;
import com.firefox.center.common.Record;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.kit.Assert;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.redis.constant.RedisConstant;
import com.firefox.center.common.redis.template.RedisRepository;
import com.firefox.center.common.sms.pojo.SMSProperty;
import com.firefox.center.common.sms.util.SMSSignUtil;
import com.firefox.center.common.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 公司表 服务类
 */
@Service
@RequiredArgsConstructor
public class SMSCommonService {

    private final RestTemplate restTemplate;
    private final RedisRepository redisRepository;
    public static final String CACHE_KEY = "user"+ RedisConstant.SEPARATOR+"sms";

    public R sendCode(String appId, Integer tenantId, SMSProperty sms, String phone, String code) {
        try{
            Record params = new Record();
            params.set("appkey", sms.getAppKey());
            params.set("module", sms.getModule());
            params.set("ts", DateUtil.nowTimeStamp());
            params.set("type", sms.getType());
            params.set("json", new Record().set("code", code).toJson());
            params.set("tel", phone);
            params.set("signName", sms.getSignName());
            params.set("templateCode", sms.getTemplateCode());
            String sign= null;
            try {
                sign = SMSSignUtil.sign(params.getColumns(), sms.getAppSecret());
            } catch (Exception e) {
                e.printStackTrace();
            }
            params.set("sign", sign);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(params.getColumns(), headers);
            ResponseEntity<String> entity = restTemplate.postForEntity(sms.getUrl(), request, String.class);
            Assert.notNull(entity, CodeEnum.SMS_SEND_ERROR);
            JSONObject json= JSON.parseObject(entity.getBody());
            if("true".equals(json.getString("success"))){
                cacheCode(appId, tenantId.toString(), params.getStr("tel"), code, sms.getExpire());
                return R.ok("验证码发送成功");
            }else{
                return R.error(CodeEnum.SMS_SEND_ERROR);
            }
        }catch (Exception e){
            e.printStackTrace();
            return R.error(CodeEnum.SMS_SEND_ERROR);
        }
    }

    public R checkCode(String appId, Integer tenantId, String phone, String code) {
        String cacheCode=getCode(appId, tenantId.toString(), phone);
        if(StrKit.notBlank(cacheCode)){
            if(code.equals(cacheCode)){
                return R.ok("验证通过");
            }
            return R.error(CodeEnum.VERIFICATION_CODE_EMPTY);
        }else{
            return R.error(CodeEnum.VERIFICATION_CODE_EXPIRED);
        }
    }

    public void delCacheCode(String appId, String tenantId, String phone) {
        redisRepository.deleteKey(getKey(appId, tenantId, phone));
    }

    public void cacheCode(String appId, String tenantId, String phone, String code, Long expire) {
        redisRepository.opsForValueSet(getKey(appId, tenantId, phone), code, expire);
    }

    protected String getCode(String appId, String tenantId, String phone) {
        Object oToken=redisRepository.opsForValueGet(getKey(appId, tenantId, phone));
        return oToken==null?"":oToken.toString();
    }

    protected String getKey(String appId, String tenantId, String phone) {
        return CACHE_KEY+ RedisConstant.SEPARATOR+appId+ RedisConstant.SEPARATOR+tenantId+ RedisConstant.SEPARATOR+phone;
    }

}
