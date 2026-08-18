package com.firefox.center.app.config;

import com.firefox.center.common.exception.DefaultExceptionAdvice;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 统一异常处理
 *
 * @Author: sujie
 * @date 2021/04/22
 */
@ControllerAdvice
public class ExceptionAdvice extends DefaultExceptionAdvice {
}
