package com.firefox.center.common.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 实体父类
 * @Author: sujie
 */
public class SuperEntity<T> implements Serializable {
    public static final String FIELD_ID = "id";
    public static final String CREATE_TIME = "createTime";
    public static final String CREATE_TIME_COLUMN = "create_time";
    public static final String CREATE_USER = "createUser";
    public static final String CREATE_USER_COLUMN = "create_user";
    private static final long serialVersionUID = -4603650115461757622L;
    @TableId(
            value = "id",
            type = IdType.INPUT
    )
    @NotNull(
            message = "id不能为空",
            groups = {SuperEntity.Update.class}
    )
    protected T id;
    @TableField(
            value = "create_time",
            fill = FieldFill.INSERT
    )
    protected LocalDateTime createTime;
    @TableField(
            value = "create_user",
            fill = FieldFill.INSERT
    )
    protected T createUser;

    public T getId() {
        return this.id;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public T getCreateUser() {
        return this.createUser;
    }

    public SuperEntity<T> setId(T id) {
        this.id = id;
        return this;
    }

    public SuperEntity<T> setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public SuperEntity<T> setCreateUser(T createUser) {
        this.createUser = createUser;
        return this;
    }

    public SuperEntity() {
    }

    public SuperEntity(T id, LocalDateTime createTime, T createUser) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
    }

    public String toString() {
        return "SuperEntity(super=" + super.toString() + ", id=" + this.getId() + ", createTime=" + this.getCreateTime() + ", createUser=" + this.getCreateUser() + ")";
    }

    public interface Update extends Default {
    }

    public interface Save extends Default {
    }
}
