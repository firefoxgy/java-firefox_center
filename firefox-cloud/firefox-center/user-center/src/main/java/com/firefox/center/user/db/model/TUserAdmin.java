package com.firefox.center.user.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author: sujie
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user_admin")
@ApiModel(value = "管理用户表")
public class TUserAdmin {
	private static final long serialVersionUID = -5886012896705137070L;

	@TableId(value = "uid",type = IdType.INPUT)
	private Long uid;
	private String username;
	private String password;
	private String password02;
	private String openId;
	private String realName;
	private String shortName;
	private String manageAppIds;
	private String email;
	private String unitName;
	private String officePhone;
	private String mobile;
	private String address;
	private Integer master;
	private Integer test;
	private Integer status;
	private String appId;
	private Integer tenantId;
	private Long createUser;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private Date expireTime;
	private Long updateUser;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@TableField(exist = false)
	private String oldPassword;
	@TableField(exist = false)
	private String newPassword;
	@TableField(exist = false)
	private String tenantName;
	@TableField(exist = false)
	private String manager;
}
