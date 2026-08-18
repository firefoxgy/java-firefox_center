package com.firefox.center.sys.common.util;


import com.alibaba.fastjson.JSON;
import com.firefox.center.common.R;
import org.slf4j.Logger;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author: sujie
 * date: 2020-06-15
 */
public class ResponseUtil {

    public static void sendError(Logger logger, HttpServletResponse response, int status, int code, String msg) {
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", HttpContextUtils.getOrigin());
        response.setStatus(status);
        try {
            response.getWriter().write(JSON.toJSONString(R.error(code, msg)));
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }
}