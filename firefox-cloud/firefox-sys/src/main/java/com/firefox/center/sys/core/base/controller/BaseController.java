package com.firefox.center.sys.core.base.controller;

import com.firefox.center.sys.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {
    @Resource
    protected HttpServletRequest request;
    @Resource
    protected HttpServletResponse response;

    protected LoginUser getUser() {
        return (LoginUser) SecurityUtils.getSubject().getPrincipal();
    }
}
