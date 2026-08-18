package com.firefox.center.user.feign;

import cn.hutool.core.thread.ThreadUtil;
import com.firefox.center.common.R;
import com.firefox.center.common.Record;
import com.firefox.center.common.email.MailCommonService;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.redis.constant.RedisConstant;
import com.firefox.center.common.redis.template.RedisRepository;
import com.firefox.center.config.feign.ConfigFeignService;
import com.firefox.center.app.feign.LogFeignService;
import com.firefox.center.app.feign.pojo.TLogMailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/4/27 16:30
 */
@RestController
@RequiredArgsConstructor
public class MailServiceImpl implements MailFeignService {

    private final MailCommonService mailCommonService;
    private final ConfigFeignService configFeignService;
    private final LogFeignService logFeignService;
    private final RedisRepository redisRepository;

    private static final String MAIL_CONFIG_TYPE="mail";
    private static final String MAIL_CONFIG_KEY_TITLE="mail_code_title";
    private static final String MAIL_CONFIG_KEY_CONETENT="mail_code_conten";
    public static final String CACHE_KEY = "user"+ RedisConstant.SEPARATOR+"mail";
    public static final Long EXPIRE_SEC = 300L;

    @Override
    public void sendCode(String appId, Integer tenantId, String mail) {
        String code= StrKit.randomNum(6);
        Record record =configFeignService.getConfs(MAIL_CONFIG_TYPE);
        String title=record.getStr(MAIL_CONFIG_KEY_TITLE);
        String content=record.getStr(MAIL_CONFIG_KEY_CONETENT);
        content=content.replace("#{mail}", mail).replace("#{code}", code);
        mailCommonService.sendSimpleMail(mail, title, content);
        cacheCode(appId, tenantId.toString(), mail, code, EXPIRE_SEC);
        saveLog(appId, tenantId, mail, title, content);
    }

    @Override
    public R<?> checkCode(String appId, Integer tenantId, String mail, String code) {
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

    public void saveLog(String appId, Integer tenantId, String mail, String title, String content){
        TLogMailDTO tLogMailDTO=TLogMailDTO.builder()
                .id(StrKit.getId())
                .appId(appId)
                .tenantId(tenantId)
                .mail(mail)
                .title(title)
                .content(content)
                .status(1)
                .createTime(new Date())
                .build();
        ThreadUtil.execAsync(() ->logFeignService.saveMailLog(tLogMailDTO));
    }

    public void cacheCode(String appId, String tenantId, String mail, String code, Long expire) {
        redisRepository.opsForValueSet(getKey(appId, tenantId, mail), code, expire);
    }

    protected String getCode(String appId, String tenantId, String mail) {
        Object oToken=redisRepository.opsForValueGet(getKey(appId, tenantId, mail));
        return oToken==null?"":oToken.toString();
    }

    protected String getKey(String appId, String tenantId, String mail) {
        return CACHE_KEY+ RedisConstant.SEPARATOR+appId+ RedisConstant.SEPARATOR+tenantId+ RedisConstant.SEPARATOR+mail;
    }

}
