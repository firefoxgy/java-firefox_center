package com.firefox.center.sys.config.interceptor;

import com.firefox.center.sys.config.hander.IHander;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author: sujie
 * date: 2020-06-15
 */
@Slf4j
public class AnnotationInterceptor extends HandlerInterceptorAdapter {

    @Resource(name = "wxTokenHandler")
    private IHander wxTokenHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return wxTokenHandler.handle(log, request, response, handler);
    }
}