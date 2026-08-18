package com.firefox.center.db.service;

/**
 * @author sujie
 * @Description:
 * @date 2021/1/2116:39
 */
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

}
