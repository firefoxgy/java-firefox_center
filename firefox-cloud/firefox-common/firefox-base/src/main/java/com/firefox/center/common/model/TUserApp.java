package com.firefox.center.common.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户实体
 * @Author：sujie
 * @Date：2020/07/06
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TUserApp extends TUser {
    private static final long serialVersionUID = -5886012896705137070L;

    @TableField(exist = false)
    private String sids;
    private String phone;
    private String email;
    private String password02;
    private String salt;
    private Integer pwdEncrypt;
    private Long openIntId;
    private String openId;
    private String nickname;
    private String headerImg;
    private Integer gender;
    private Integer status;
    private String regFrom;
    private String version;
    private String appId;
    private Integer tenantId;
    private Date lastLoginTime;
    private Date createTime;
    @TableField(exist = false)
    private String oldPassword;
    @TableField(exist = false)
    private String newPassword;
    @TableField(exist = false)
    private String clientType;



}