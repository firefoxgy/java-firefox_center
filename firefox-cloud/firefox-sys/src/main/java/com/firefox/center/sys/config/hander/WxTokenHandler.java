package com.firefox.center.sys.config.hander;

import com.firefox.center.sys.Consts;
import com.firefox.center.sys.common.exception.ExceptionCode;
import com.firefox.center.sys.common.system.util.JwtUtil;
import com.firefox.center.sys.common.util.ResponseUtil;
import com.firefox.center.sys.config.annotation.WxToken;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author: sujie
 * date: 2020-06-15
 */
@Component("wxTokenHandler")
public class WxTokenHandler implements IHander {

    public boolean handle(Logger logger, HttpServletRequest request, HttpServletResponse response, Object handler){
        WxToken annotation;
        if(handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(WxToken.class);
        }else{
            return true;
        }

        if(annotation == null){
            return true;
        }

        //获取用户凭证
        String token = request.getHeader(Consts.Api.TOKEN);
        //凭证为空
        if(StringUtils.isEmpty(token)){
            ResponseUtil.sendError(logger, response, HttpServletResponse.SC_UNAUTHORIZED, ExceptionCode.WX_MINI_TOKEN_.getCode(),ExceptionCode.WX_MINI_TOKEN_NULL.getMsg());
            return false;
        }
        if(StringUtils.isEmpty(JwtUtil.getValue(token, Consts.Jwt.CLAIM_OPEN_ID))) {
            ResponseUtil.sendError(logger,response, HttpServletResponse.SC_UNAUTHORIZED , ExceptionCode.WX_MINI_TOKEN_.getCode(),ExceptionCode.WX_MINI_TOKEN_ERROR.getMsg());
            return false;
        } else {
            return true;
        }
    }
}