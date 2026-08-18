package com.firefox.center.common.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author: ZJL
 * @Description: 获取IP地址
 * @Date: 2020/06/30
 */
@Slf4j
public class IpAddressConverter extends ClassicConverter {

    private static String ipAddress;

    static {
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("未知的IP地址", e);
            ipAddress = "UNKNOWN";
        }
    }

    @Override
    public String convert(ILoggingEvent event) {
        return ipAddress;
    }

}
