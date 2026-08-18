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
@TableName("t_user_third")
@ApiModel(value = "第三方用户表")
public class TUserThird {

    @TableId(value = "id",type = IdType.INPUT)
    private Long id;
    private Long sid;
    @TableField(exist = false)
    private Long uid;
    @TableField(exist = false)
    private String phone;
    @TableField(exist = false)
    private Long openIntId;
    @TableField(exist = false)
    private String openId;
    @TableField(exist = false)
    private String sids;
    private String loginType;
    private String externalappid;
    private String thirdid;
    private String thirdUnionId;
    private String nickname;
    private String figureurl;
    private String email;
    private String gender;
    private String country;
    private String province;
    private String city;
    private Integer status;
    private String regFrom;
    private String version;
    private String appId;
    private Integer tenantId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}