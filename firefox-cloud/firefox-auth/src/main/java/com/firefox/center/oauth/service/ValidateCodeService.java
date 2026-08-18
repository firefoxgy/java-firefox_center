package com.firefox.center.oauth.service;

import com.firefox.center.common.constants.SecurityConstants;
import com.firefox.center.common.feign.UserFeignService;
import com.firefox.center.common.redis.template.RedisRepository;
import com.firefox.center.oauth.exception.ValidateCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: sujie
 * @date 2021/04/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ValidateCodeService {

    private final RedisRepository redisRepository;
    private final UserFeignService userService;

    /**
     * 保存用户验证码，和randomStr绑定
     *
     * @param deviceId 客户端生成
     * @param imageCode 验证码信息
     */
    public void saveImageCode(String deviceId, String imageCode) {
        redisRepository.setExpire(buildKey(deviceId), imageCode, SecurityConstants.DEFAULT_IMAGE_EXPIRE);
    }

    /**
     * 获取验证码
     * @param deviceId 前端唯一标识/手机号
     */
    public String getCode(String deviceId) {
        return (String)redisRepository.get(buildKey(deviceId));
    }

    /**
     * 删除验证码
     * @param deviceId 前端唯一标识/手机号
     */
    public void remove(String deviceId) {
        redisRepository.del(buildKey(deviceId));
    }

    /**
     * 验证验证码
     */
    public void validate(HttpServletRequest request) {
        String deviceId = request.getParameter("deviceId");
        if (StringUtils.isBlank(deviceId)) {
            throw new ValidateCodeException("请在请求参数中携带deviceId参数");
        }
        String code = this.getCode(deviceId);
        String codeInRequest;
        try {
            codeInRequest = ServletRequestUtils.getStringParameter(request, "validCode");
        } catch (ServletRequestBindingException e) {
            throw new ValidateCodeException("获取验证码的值失败");
        }
        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("请填写验证码");
        }

        if (code == null) {
            throw new ValidateCodeException("验证码不存在或已过期");
        }

        if (!StringUtils.equals(code, codeInRequest)) {
            throw new ValidateCodeException("验证码不正确");
        }

        this.remove(deviceId);
    }

    private String buildKey(String deviceId) {
        return SecurityConstants.DEFAULT_CODE_KEY + ":" + deviceId;
    }
}
