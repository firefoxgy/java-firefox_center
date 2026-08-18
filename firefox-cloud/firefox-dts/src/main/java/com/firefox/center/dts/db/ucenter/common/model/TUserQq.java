package com.firefox.center.dts.db.ucenter.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_user_qq")
public class TUserQq {
    private static final long serialVersionUID = -5886012896705137070L;

    @TableId(value = "id",type = IdType.INPUT)
    private Integer id;
    private Integer uid;
    private String externalappid;
    private String nickname;
    private Integer gender;
    private String country;
    private String province;
    private String city;
    private String figureurl;
    private String openid;
    private Integer updateTime;
    private Integer createTime;

    @TableField(exist = false)
    private String nickname2;
    @TableField(exist = false)
    private String headpic;
    @TableField(exist = false)
    private Integer sex2;

}