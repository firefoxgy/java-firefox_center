package com.firefox.center.user.service;

import cn.hutool.core.thread.ThreadUtil;
import com.firefox.center.common.R;
import com.firefox.center.common.email.MailCommonService;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.redis.constant.RedisConstant;
import com.firefox.center.common.redis.template.RedisRepository;
import com.firefox.center.app.feign.LogFeignService;
import com.firefox.center.app.feign.pojo.TLogMailDTO;
import com.firefox.center.app.feign.AppFeignService;
import com.firefox.center.app.feign.pojo.OauthTenantDTO;
import com.firefox.center.user.pojo.sms.dto.CheckMailDTO;
import com.firefox.center.user.pojo.sms.dto.MailSendDTO;
import com.firefox.center.common.sms.service.SMSCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 公司表 服务类
 */
@Service
@RequiredArgsConstructor
public class MailService {

    private final MailCommonService mailCommonService;
    private final SMSCommonService sMSCommonService;
    private final LogFeignService logFeignService;
    private final AppFeignService appFeignService;
    private final RedisRepository redisRepository;

    public static final String CACHE_KEY = "user"+ RedisConstant.SEPARATOR+"mail";

    public R<?> sendMail(MailSendDTO sendDTO){
        String appId=sendDTO.getArgs().getAppid();
        Integer tenantId=sendDTO.getArgs().getTenantid();
        String mail=sendDTO.getArgs().getMail();

        String appName="云南开屏";
        OauthTenantDTO oauthTenantDTO=appFeignService.getTenant(tenantId);
        if(oauthTenantDTO!=null && !"".equals(oauthTenantDTO.getAppName())){
            appName=oauthTenantDTO.getAppName();
        }

        String code= StrKit.randomNum(6);
        String title=appName+"邮箱认证";
        String content="您好："+mail+"\n" +
                "   您正在"+appName+"进行邮箱验证，您的邮箱验证码是："+code+"\n" +
                "   为确保您能正常收到系统发的续费通知，防止域名因忘记续费而被其他人抢注，我们强烈推荐您将kaiping@163.com加进您的邮箱的白名单或通讯录。\n" +
                "若有不清楚的地方，请咨询在线客服，感谢您的支持！";
        mailCommonService.sendSimpleMail(mail, title, content);
        saveLog(appId, tenantId, mail, title, content);
        cacheCode(appId, tenantId, mail, code);
        return R.ok();
    }

    public R<?> checkCode(CheckMailDTO checkDTO){
        String appId=checkDTO.getAppid();
        Integer tenantId=checkDTO.getTenantid();
        String mail=checkDTO.getMail();
        String code=checkDTO.getCode();
        String cacheCode=getCode(appId, tenantId.toString(), mail);
        if(StrKit.notBlank(cacheCode)){
            if(code.equals(cacheCode)){
                return R.ok("验证通过");
            }
            return R.error(CodeEnum.VERIFICATION_CODE_EMPTY);
        }else{
            return R.error(CodeEnum.VERIFICATION_CODE_EXPIRED);
        }
    }

    public R<?> cacheCode(String appId, Integer tenantId, String mail, String code){
        redisRepository.opsForValueSet(getKey(appId, tenantId.toString(), mail), code, 1000*60*5l);
        return R.ok();
    }

    public void saveLog(String appId, Integer tenantId, String mail, String title, String content){
        TLogMailDTO tLogMailDTO=TLogMailDTO.builder()
                .id(StrKit.getId())
                .appId(appId)
                .tenantId(tenantId)
                .mail(mail)
                .title(title)
                .content(content)
                .createTime(new Date())
                .build();
        ThreadUtil.execAsync(() ->logFeignService.saveMailLog(tLogMailDTO));
    }

    protected String getCode(String appId, String tenantId, String mail) {
        Object oToken=redisRepository.opsForValueGet(getKey(appId, tenantId, mail));
        return oToken==null?"":oToken.toString();
    }

    protected String getKey(String appId, String tenantId, String mail) {
        return CACHE_KEY+ RedisConstant.SEPARATOR+appId+ RedisConstant.SEPARATOR+tenantId+ RedisConstant.SEPARATOR+mail;
    }

}
