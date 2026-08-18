package com.firefox.center.user.pojo.user.vo;

import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel(value = "管理用户表")
public class TUserAdminVO {
	private static final long serialVersionUID = -5886012896705137070L;

	private Long uid;
	private String username;
	private String openId;
	private String realName;
	private String shortName;
	private String email;
	private String unitName;
	private String officePhone;
	private String mobile;
	private String address;
	private Integer master;
	private Integer test;
	private Integer status;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private Date expireTime;
	private String tenantName;
	private String manager;
}
