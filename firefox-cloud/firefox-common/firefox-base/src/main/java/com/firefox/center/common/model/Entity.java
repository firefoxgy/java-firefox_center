package com.firefox.center.common.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.time.LocalDateTime;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/4/26 18:07
 */
public class Entity<T> extends SuperEntity<T> {

    public static final String UPDATE_TIME = "updateTime";
    public static final String UPDATE_USER = "updateUser";
    private static final long serialVersionUID = 5169873634279173683L;
    @TableField(
            value = "update_time",
            fill = FieldFill.INSERT_UPDATE
    )
    protected LocalDateTime updateTime;
    @TableField(
            value = "update_user",
            fill = FieldFill.INSERT_UPDATE
    )
    protected T updateUser;

    public Entity(T id, LocalDateTime createTime, T createUser, LocalDateTime updateTime, T updateUser) {
        super(id, createTime, createUser);
        this.updateTime = updateTime;
        this.updateUser = updateUser;
    }

    public Entity() {
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public T getUpdateUser() {
        return this.updateUser;
    }

    public Entity<T> setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Entity<T> setUpdateUser(T updateUser) {
        this.updateUser = updateUser;
        return this;
    }

    public String toString() {
        return "Entity(super=" + super.toString() + ", updateTime=" + this.getUpdateTime() + ", updateUser=" + this.getUpdateUser() + ")";
    }
}
