package com.firefox.center.common.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @Author: ZJL
 * @Description: 获取日志模块
 * @Date: 2020/06/30
 */
public class ModuleConverter extends ClassicConverter {

    private static final int MAX_LENGTH = 20;

    @Override
    public String convert(ILoggingEvent event) {
        if (event.getLoggerName().length() > MAX_LENGTH) {
            return "";
        } else {
            return event.getLoggerName();
        }
    }
}
