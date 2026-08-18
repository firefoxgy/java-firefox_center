package com.firefox.center.sys.config.hander;

import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author: sujie
 * date: 2020-06-15
 */
public interface IHander {

    boolean handle(Logger logger, HttpServletRequest request, HttpServletResponse response, Object handler);
}
