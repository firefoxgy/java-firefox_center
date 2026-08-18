package com.firefox.center.user.pojo.oauth.vo;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class TUserThirdVO {

    private Long sid;
    @TableField(exist = false)
    private Long uid;
    private String phone;
    private String openId;
    @TableField(exist = false)
    private String sids;
    private String loginType;
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
    private String appId;
    private Integer tenantId;

}