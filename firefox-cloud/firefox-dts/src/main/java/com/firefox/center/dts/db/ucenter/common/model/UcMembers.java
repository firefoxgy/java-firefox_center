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
@TableName("uc_members")
public class UcMembers {
    private static final long serialVersionUID = -5886012896705137070L;

    @TableId(value = "uid",type = IdType.INPUT)
    private Integer uid;
    private String usercode;
    private String username;
    private String password;
    private String email;
    private String myid;
    private String myidkey;
    private String regip;
    private Integer regdate;
    private Integer lastloginip;
    private Integer lastlogintime;
    private String salt;
    private String secques;
    private String mobile;
    private Integer status;
    private Integer ismain;
    private String deviceid;
    private String devicetype;
    private String country;
    private String province;
    private String city;
    private String area;
    private String ip;
    private Integer appid;
    private String scope;
    private Integer updateTime;
    private String source;
    private String version;

    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private String headpic;
    @TableField(exist = false)
    private Integer sex;



}