package com.firefox.center.sys.common;

import cn.hutool.core.util.ArrayUtil;
import com.firefox.center.sys.common.exception.ExceptionCode;
import com.firefox.center.common.exception.BusinessException;
import com.firefox.center.common.kit.StrKit;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/2/18 10:18
 */
public class Assert {

    /**
     * 统一参数验证异常码
     */
    private static int BASE_VALID_PARAM = -1;

    private Assert() {}

    public static void fail(int code, String message) {
        throw new BusinessException(code, message);
    }

    public static void fail(ExceptionCode ExceptionCode) {
        if (ExceptionCode != null) {
            throw new BusinessException(ExceptionCode.getCode(), ExceptionCode.getMsg());
        }
        fail(BASE_VALID_PARAM, "参数验证异常");
    }

    /**
     * Fails a test with no message.
     *
     * @
     */
    public static void fail() {
        fail(BASE_VALID_PARAM, "参数验证异常");
    }

    public static void fail(String message) {
        if (message == null || "".equals(message)) {
            message = "参数验证异常";
        }
        fail(BASE_VALID_PARAM, message);
    }

    /**
     * 断言条件为真。如果不是，它会抛出一个带有给定消息的异常
     * {@link ExceptionCode}
     *
     * @param ExceptionCode 错误码
     * @param condition     被检查的条件
     * @
     */
    public static void isTrue(boolean condition, ExceptionCode ExceptionCode) {
        if (!condition) {
            fail(ExceptionCode);
        }
    }

    public static void isTrue(boolean condition, Integer code, String message) {
        if (!condition) {
            fail(code, message);
        }
    }

    /**
     * 断言条件为真。如果不是，它会抛出一个参数检测异常
     * {@link BusinessException}
     *
     * @param condition 被检查的条件
     * @
     */
    public static void isTrue(boolean condition, String exceptionMessage) {
        if (!condition) {
            fail(exceptionMessage);
        }
    }

    /**
     * 断言条件为真。如果不是，它会抛出一个参数检测异常
     * {@link BusinessException}
     *
     * @param condition 被检查的条件
     */
    public static void isTrue(boolean condition) {
        if (!condition) {
            fail();
        }
    }

    /**
     * 断言条件为假。如果不是，它会抛出一个带有给定消息的异常
     * {@link BusinessException}
     *
     * @param ExceptionCode 错误码
     * @param condition     被检查的条件
     * @
     */
    public static void isFalse(boolean condition, ExceptionCode ExceptionCode) {
        if (condition) {
            fail(ExceptionCode);
        }
    }

    public static void isFalse(boolean condition, String exceptionMessage) {
        if (condition) {
            fail(exceptionMessage);
        }
    }


    /**
     * 断言检查这个对象不是 Null。 如果是null，用给定的错误码<code>ExceptionCode</code>抛出异常
     * {@link BusinessException}
     *
     * @param ExceptionCode 错误码
     * @param object        检查对象
     * @
     */
    public static void notNull(Object object, ExceptionCode ExceptionCode) {
        if (object == null) {
            fail(ExceptionCode);
        }
    }

    public static void notNull(Object object) {
        if (object == null) {
            fail();
        }
    }

    public static void notNull(Object object, String message) {
        if (StrKit.isBlank(object)) {
            fail(message);
        }
    }

    /**
     * 断言检查这个对象是 Null。 如果不是null，用给定的错误码<code>ExceptionCode</code>抛出异常
     * {@link BusinessException}
     *
     * @param message 错误码
     * @param object        检查对象
     * @
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            fail(message);
        }
    }

    public static void isNull(Object object, ExceptionCode ExceptionCode) {
        if (object != null) {
            fail(ExceptionCode);
        }
    }

    /**
     * 断言检查这个对象是 空。 如果不是null，用给定的错误码<code>ExceptionCode</code>抛出异常
     * {@link BusinessException}
     *
     * @param ExceptionCode 错误码
     * @param str        检查对象
     * @
     */
    public static void notBlank(Object str, ExceptionCode ExceptionCode) {
        if (StrKit.isBlank(str)) {
            fail(ExceptionCode);
        }
    }

    public static void notBlank(Object str, String message) {
        if (StrKit.isBlank(str)) {
            fail(message);
        }
    }


    /**
     * 断言集合不为空，如果为null或者empty，用指定错误码抛出异常
     * {@link BusinessException}
     *
     * @param ExceptionCode 错误码
     * @param collection    集合
     * @
     */
    public static void notEmpty(Collection<?> collection, ExceptionCode ExceptionCode) {
        if (collection == null || collection.isEmpty()) {
            fail(ExceptionCode);
        }
    }

    public static <T> void notEmpty(T[] array, ExceptionCode ExceptionCode) {
        if (ArrayUtil.hasNull(array)) {
            fail(ExceptionCode);
        }
    }

    /**
     * 断言字符串不为空，如果为null或者empty，用指定错误码抛出异常
     * {@link BusinessException}
     *
     * @param ExceptionCode 错误码
     * @param value         字符串
     * @
     */
    public static void notEmpty(String value, ExceptionCode ExceptionCode) {
        if (value == null || value.isEmpty()) {
            fail(ExceptionCode);
        }
    }

    public static void notEmpty(String value, String exceptionMsgs) {
        if (value == null || value.isEmpty()) {
            fail(exceptionMsgs);
        }
    }

    public static void notEmpty(String value) {
        if (value == null || value.isEmpty()) {
            fail();
        }
    }

    /**
     * 断言2个对象不是相等的。如果相等则抛出异常
     * {@link BusinessException}。
     * 如果<code>unexpected</code> 和 <code>actual</code> 是 <code>null</code>,
     * 他们被认为是相等的。
     *
     * @param ExceptionCode 错误码
     * @param unexpected    意想不到的值
     * @param actual        要检查的值 <code>unexpected</code>
     * @
     */
    public static void notEquals(Object unexpected, Object actual, ExceptionCode ExceptionCode) {
        if (unexpected == actual) {
            fail(ExceptionCode);
        }
        if (unexpected != null && unexpected.equals(actual)) {
            fail(ExceptionCode);
        }

    }

    /**
     * 断言2个字符串是否相等，如果不等用指定错误码抛出异常
     * {@link BusinessException}
     *
     * @param ExceptionCode 错误码
     * @param expected      预期的值
     * @param actual        需要比较的字符串<code>expected</code>
     * @
     */
    public static void equals(String expected, String actual, ExceptionCode ExceptionCode) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        fail(ExceptionCode);
    }

    public static void equals(String expected, String actual, String exceptionMsgs) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        fail(exceptionMsgs);
    }

    public static void equals(Object expected, Object actual, String exceptionMsgs) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        fail(exceptionMsgs);
    }


    /**
     * 断言 预期值（expected） 大于 实际值（actual）
     *
     * @param expected      预期值
     * @param actual        实际值
     * @param exceptionMsgs
     */
    public static void gt(LocalDateTime expected, LocalDateTime actual, String exceptionMsgs) {
        if (expected == null || actual == null) {
            fail(exceptionMsgs);
        }

        if (expected.isAfter(actual)) {
            fail(exceptionMsgs);
        }
    }

}
