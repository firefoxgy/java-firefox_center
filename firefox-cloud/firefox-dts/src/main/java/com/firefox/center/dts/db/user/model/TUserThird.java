package com.firefox.center.dts.db.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
    private String sids;
    private String loginType;
    private String externalappid;
    private String thirdid;
    private String thirdUnionId;
    private String nickname;
    private String figureurl;
    private String email;
    private Integer gender;
    private String country;
    private String province;
    private String city;
    private Integer status;
    private String appId;
    private Integer tenantId;

}