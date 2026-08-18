package com.firefox.center.db.service;

import com.baomidou.mybatisplus.extension.service.IService;


public interface SuperService<T> extends IService<T> {
    /**
     * 获取实体的类型
     *
     * @return
     */
    Class<T> getEntityClass();

    /**
     * 根据id修改 entity 的所有字段
     *
     * @param entity
     * @return
     */
    boolean updateAllById(T entity);

}
