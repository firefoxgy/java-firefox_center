package com.firefox.center.common.utils;

import com.firefox.center.common.Record;
import com.firefox.center.common.kit.StrKit;

import java.io.UnsupportedEncodingException;

/**
 * 高效分布式ID生成算法(sequence),基于Snowflake算法优化实现64位自增ID算法。
 * 其中解决时间回拨问题的优化方案如下：
 * 1. 如果发现当前时间少于上次生成id的时间(时间回拨)，着计算回拨的时间差
 * 2. 如果时间差(offset)小于等于5ms，着等待 offset * 2 的时间再生成
 * 3. 如果offset大于5，则直接抛出异常
 *
 * @Author: sujie
 * @date 2021/04/5
 */
public class IdGen {
    private static Sequence WORKER = new Sequence();

    public static long getId() {
        return WORKER.nextId();
    }

    public static String getIdStr() {
        return String.valueOf(WORKER.nextId());
    }

    public static Record getMd5Id() {
        long id=getId();
        String md5=MD5Util.encrypt(String.valueOf(id));
        return new Record().set("id", id).set("md5", md5);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(getMd5Id());
    }
}
