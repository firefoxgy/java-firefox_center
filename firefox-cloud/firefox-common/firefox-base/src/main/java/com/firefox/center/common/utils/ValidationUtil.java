package com.firefox.center.common.utils;

import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * Description: 通用验证类
 *
 * @author sujie
 * @since JDK 1.8
 * date: 2020/7/9 15:45
 */
public class ValidationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtil.class);
    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    /**
     * 验证数据项不为空
     */
    public static void checkNotEmpty(Object obj, String logMsg) {
        boolean checkInd = isCheckNotPass(obj);
        //检查不通过
        if (!checkInd) {
            LOGGER.warn(logMsg);
            throw new BusinessException(CodeEnum.BUSINESS_ERROR_PARAMETER.getCode(),logMsg);
        }
    }


    private static boolean isCheckNotPass(Object obj) {
        boolean checkInd = true;
        if (null == obj) {
            checkInd = false;
        } else {
            //字符串
            if (obj instanceof String) {
                String str = (String) obj;
                if (StringUtils.isEmpty(str)) {
                    checkInd = false;
                }
            }
            //数组
            if (obj instanceof Collection<?>) {
                Collection<?> collection = (Collection<?>) obj;
                if (CollectionUtils.isEmpty(collection)) {
                    checkInd = false;
                }
            }
            // map
            if (obj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) obj;
                if (map.isEmpty()) {
                    checkInd = false;
                }
            }
        }
        return checkInd;
    }

    /**
     * 校验数据为空
     * @param obj
     * @param logMsg
     */
    public static void checkEmpty(Object obj, String logMsg) {
        boolean checkNotPass = isCheckNotPass(obj);
        // 检查通过通过
        if (checkNotPass) {
            throw new BusinessException(CodeEnum.BUSINESS_ERROR_PARAMETER.getCode(),logMsg);
        }
    }

    /**
     * 校验字符长度
     * @param str 目标字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @param logMsg 提示log
     */
    public static void checkLength(String str, int minLength, int maxLength, String logMsg) {
        if(minLength > maxLength ){
            LOGGER.warn("验证方法参数错误ok");
            return;
        }
        str = (null == str ? "" : str);
        int fieldLen = str.length();
        if(!(fieldLen >= minLength && fieldLen <= maxLength)){
            LOGGER.warn(logMsg);
            throw new BusinessException(CodeEnum.BUSINESS_ERROR_PARAMETER.getCode(),logMsg);
        }
    }

    /**
     * 校验实体类参数,返回校验不通过结果list
     * @author sujie
     */
    public static <T> List<String> validateDO(T t, Class... groupClass) {
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t, groupClass);
        Set<ConstraintViolation<T>> sortSet = new TreeSet<>(Comparator.comparing(ConstraintViolation::getMessage));
        sortSet.addAll(constraintViolations);
        List<String> messageList = new ArrayList<>();
        sortSet.forEach(c -> messageList.add(c.getMessage()));
        return messageList;

    }

}
