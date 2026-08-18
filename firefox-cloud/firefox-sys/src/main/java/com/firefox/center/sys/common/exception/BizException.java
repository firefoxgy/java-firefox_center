package com.firefox.center.sys.common.exception;

/**
 * 业务异常
 * 用于在处理业务逻辑时，进行抛出的异常。
 *
 * @author sujie
 */
public class BizException extends RuntimeException implements BaseException {

    private static final long serialVersionUID = -3843907364558373817L;

    /**
     * 异常信息
     */
    protected String msg;

    /**
     * 具体异常码
     */
    protected int code;

    public BizException(String msg) {
        super(msg);
        this.code = -1;
        this.msg = msg;
    }

    public BizException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BizException(int code, String format, Object... args) {
        super(String.format(format, args));
        this.code = code;
        this.msg = String.format(format, args);
    }

    /**
     * 实例化异常
     *
     * @param code    自定义异常编码
     * @param msg 自定义异常消息
     * @param args    已定义异常参数
     * @return
     */
    public static BizException wrap(int code, String msg, Object... args) {
        return new BizException(code, msg, args);
    }

    public static BizException wrap(String msg, Object... args) {
        return new BizException(-1, msg, args);
    }

    public static BizException validFail(String msg, Object... args) {
        return new BizException(-9, msg, args);
    }

    public static BizException wrap(BaseException ex) {
        return new BizException(ex.getCode(), ex.getMessage());
    }

    @Override
    public String toString() {
        return "BizException [msg=" + msg + ", code=" + code + "]";
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public int getCode() {
        return code;
    }
}
