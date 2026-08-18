package com.firefox.center.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 用户实体
 * @Author：sujie
 * @Date：2020/07/06
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TUserThird extends TUser {

    @TableId(value = "id",type = IdType.INPUT)
    private Long id;
    private Long sid;
    @TableField(exist = false)
    private Long uid;
    @TableField(exist = false)
    private String sids;
    private String loginType;
    private String thirdid;
    private String accessToken;
    private String nickname;
    private String figureurl;
    private String gender;
    private String country;
    private String province;
    private String city;
    private Integer status;
    private String regFrom;
    private String version;
    private String appId;
    private Integer tenantId;
    private Date lastLoginTime;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String clientType;


}