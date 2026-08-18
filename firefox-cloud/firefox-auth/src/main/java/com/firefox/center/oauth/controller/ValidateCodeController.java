package com.firefox.center.oauth.controller;

import com.firefox.center.common.constants.SecurityConstants;
import com.firefox.center.oauth.service.ValidateCodeService;
import com.google.code.kaptcha.Producer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

/**
 * 验证码提供
 * @Author: sujie
 * @date 2021/04/18
 */
@Validated
@Controller
@RequiredArgsConstructor
public class ValidateCodeController {

    private final Producer producer;
    private final ValidateCodeService validateCodeService;

    /**
     * 创建验证码
     *
     * @throws Exception
     */
    @GetMapping(SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX + "/{deviceId}")
    public void createCode(@PathVariable String deviceId, HttpServletResponse response) throws Exception {
        Assert.notNull(deviceId, "机器码不能为空");
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        validateCodeService.saveImageCode(deviceId, text);
        try (ServletOutputStream out = response.getOutputStream()) {
            ImageIO.write(image, "JPEG", out);
        }
    }

}
