package com.firefox.center.common.verification;

import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.exception.BusinessException;
import com.firefox.center.common.utils.JsonUtil;
import com.firefox.center.common.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.List;

/**
 * Description: 参数校验类
 *
 * @author sujie
 * @since JDK 1.8
 * date: 2020/7/13 11:13
 */
public class ParamCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtil.class);

    /**
     * checkRequestBodyAndParse:解析post请求消息体,并对解析转换后的实例进行非空验证,空则直接抛出异常. 非空则返回实例,即不报错一定非空. 检查请求body非空,检查解析之后实例非空.
     * @author sujie
     **/
    public static  <V> V checkRequestBodyAndParse(HttpServletRequest request, Class<V> valueClass) {
        String msgBody = readReqMsg(request);
        LOGGER.info("request uri: {}.", request.getRequestURI());
        LOGGER.info("request body: {}.", msgBody);
        V result = null;
        if (!StringUtils.isEmpty(msgBody)) {
            //body最大长度
            Integer maxLength = 10240;
            if (msgBody.length() < maxLength) {
                request.setAttribute("requestbody", msgBody);
            } else {
                request.setAttribute("requestbody", "newBody too large");
            }
            result = JsonUtil.jsonToObject(msgBody, valueClass, false);
        }
        if (result == null) {
            throw new BusinessException(CodeEnum.BUSINESS_ERROR_PARAMETER);
        }
        return result;
    }

    /**
     * 读取请求信息
     * @author sujie
     */
    public static String readReqMsg(HttpServletRequest request) {
        StringBuffer reqMsg = new StringBuffer();
        BufferedReader reader;
        try {
            reader = request.getReader();
            String str = "";
            while ((str = reader.readLine()) != null) {
                reqMsg.append(str);
            }
            return reqMsg.toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 校验参数
     */
    public static List<String> checkParam(Object object, Class... groups) {
        List<String> validationRes;
        try {
            validationRes = ValidationUtil.validateDO(object, groups);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CodeEnum.BUSINESS_ERROR_PARAMETER);
        }
        return validationRes;
    }
}
