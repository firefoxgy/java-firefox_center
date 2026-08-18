package com.firefox.center.common.exception;

import com.firefox.center.common.enums.CodeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 业务异常
 *
 * @Author: sujie
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {

    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = CodeEnum.FAILURE.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(CodeEnum CodeEnum) {
        super(CodeEnum.getMessage());
        this.code = CodeEnum.getCode();
    }

    public BusinessException(String message, Throwable cause) {
        this(CodeEnum.FAILURE.getCode(), message, cause);
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public static BusinessException wrap(int code, String message) {
        return new BusinessException(code, message);
    }

    public static BusinessException wrap(String message) {
        return new BusinessException(-1, message);
    }

    public static BusinessException validFail(String message) {
        return new BusinessException(-9, message);
    }

    public static BusinessException wrap(CodeEnum ex) {
        return new BusinessException(ex.getCode(), ex.getMessage());
    }

    public static BusinessException wrap(CodeEnum ex, Throwable cause) {
        return new BusinessException(ex.getCode(), ex.getMessage(), cause);
    }
}
