package com.firefox.center.sys.modules.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户实体
 * @Author：sujie
 * @Date：2020/07/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("t_user_app")
@ApiModel(value = "App端用户表")
public class TUserApp {
    private static final long serialVersionUID = -5886012896705137070L;

    @TableId(value = "id",type = IdType.INPUT)
    private Long id;
    private Long openIntId;
    private String openId;
    private Long uid;
    @TableField(exist = false)
    private Long sid;
    @TableField(exist = false)
    private String sids;
    private String username;
    private String phone;
    private String email;
    private String password;
    private String password02;
    private String salt;
    private Integer pwdEncrypt;
    private String nickname;
    private String headerImg;
    private Integer gender;
    private Integer status;
    private String regFrom;
    private String version;
    private String appId;
    private Integer tenantId;
    private Integer source;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}