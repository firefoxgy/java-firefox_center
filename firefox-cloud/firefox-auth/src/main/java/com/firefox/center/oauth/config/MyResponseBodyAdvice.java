package com.firefox.center.oauth.config;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/5/8 9:22
 */
import com.firefox.center.common.R;
import com.firefox.center.common.enums.CodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@ControllerAdvice
public class MyResponseBodyAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        //此处返回true,表示对任何handler的responsebody都调用beforeBodyWrite方法，如果有特殊方法不使用可以考虑使用注解等方式过滤
        return true;
    }

    /**
     * 对Controller的所有返回结果进行处理
     * @param body 是controller方法中返回的值，对其进行修改后再return
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    public R beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        log.info("请求返回数据类型class="+ body.getClass().getName());
        if (body.toString().contains("error") && body.toString().contains("Unauthorized")){
            return R.error(CodeEnum.UNAUTHORIZED);
        }else if (body.toString().contains("error")){
            serverHttpResponse.setStatusCode(HttpStatus.OK);
            return R.error(body);
        }
        if(body instanceof R){
            serverHttpResponse.setStatusCode(HttpStatus.OK);
            return (R)body;
        }
        serverHttpResponse.setStatusCode(HttpStatus.OK);
        return R.ok(body);
    }
}
