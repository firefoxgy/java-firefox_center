package com.firefox.center.sys.common.util;

import javax.servlet.http.HttpServletRequest;

/**
 * author: sujie
 * date: 2020-06-15
 */
public class RequestUtil {
    static String[] mobileAgents = new String[]{"iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi", "opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod", "nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma", "docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos", "techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem", "wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos", "pantech", "gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "^lct", "320x320", "240x320", "176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac", "blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs", "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi", "mot-", "moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port", "prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem", "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tsm-", "upg1", "upsi", "vk-v", "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-", "googlebot-mobile"};

    public RequestUtil() {
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(header);
    }

    public static boolean isMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().indexOf("multipart") != -1;
    }

    public static boolean isMobileBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (ua == null) {
            return false;
        } else {
            ua = ua.toLowerCase();
            String[] var2 = mobileAgents;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String mobileAgent = var2[var4];
                if (ua.contains(mobileAgent)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isWechatBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (ua == null) {
            return false;
        } else {
            ua = ua.toLowerCase();
            return ua.indexOf("micromessenger") > 0;
        }
    }

    public static boolean isWechatPcBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (ua == null) {
            return false;
        } else {
            ua = ua.toLowerCase();
            return ua.indexOf("windowswechat") > 0;
        }
    }

    public static boolean isIEBrowser(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (ua == null) {
            return false;
        } else {
            ua = ua.toLowerCase();
            if (ua.indexOf("msie") > 0) {
                return true;
            } else {
                return ua.indexOf("gecko") > 0 && ua.indexOf("rv:11") > 0;
            }
        }
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-requested-For");
        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }

        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            String[] ips = ip.split(",");

            for(int index = 0; index < ips.length; ++index) {
                String strIp = ips[index];
                if (!"unknown".equalsIgnoreCase(strIp)) {
                    ip = strIp;
                    break;
                }
            }
        }

        return ip;
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("Referer");
    }

    public static String getBaseUrl(HttpServletRequest request) {
        int port = request.getServerPort();
        StringBuilder defaultDomain = new StringBuilder(request.getScheme());
        defaultDomain.append("://").append(request.getServerName()).append(port == 80 ? "" : ":" + port).append(request.getContextPath());
        return defaultDomain.toString();
    }

}