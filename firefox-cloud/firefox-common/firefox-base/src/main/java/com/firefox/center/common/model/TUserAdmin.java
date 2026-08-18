package com.firefox.center.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @Author: sujie
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TUserAdmin extends TUser {
	private static final long serialVersionUID = -5886012896705137070L;

	private String sids;
	private String password02;
	private String openId;
	private Integer status;
	private String appId;
	private Integer tenantId;
	private LocalDateTime createTime;
	private Integer createUser;
	private LocalDateTime updateTime;
	private Integer updateUser;
	@TableField(exist = false)
	private String oldPassword;
	@TableField(exist = false)
	private String newPassword;

	@TableField(exist = false)
	private List<TSysRole> roles;
	@TableField(exist = false)
	private String roleId;

	@TableField(exist = false)
	private String clientType;
	@TableField(exist = false)
	private String version;
	private String manageAppIds;

	private String centers;
}
