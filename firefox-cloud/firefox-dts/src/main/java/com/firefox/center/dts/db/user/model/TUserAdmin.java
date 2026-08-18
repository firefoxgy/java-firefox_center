package com.firefox.center.dts.db.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

	@TableId(value = "sid",type = IdType.INPUT)
	private Long sid;
	@TableField(exist = false)
	private Long uid;
	@TableField(exist = false)
	private String sids;
	private String username;
	private String password;
	private String password02;
	private String openId;
	private Integer status;
	private Integer appId;
	private Integer tenantId;
	private Integer createTime;
	private Integer createUser;
	private Integer updateTime;
	private Integer updateUser;
	@TableField(exist = false)
	private String oldPassword;
	@TableField(exist = false)
	private String newPassword;
}
