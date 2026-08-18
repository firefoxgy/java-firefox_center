package com.firefox.center.credit.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("t_credit_userinfo")
public class TCreditUserInfo {

    @TableId(value = "id",type = IdType.INPUT)
    private Long id;
    private String appId;
    private Integer tenantId;
    private Long uid;
    private String type;
    private String username;
    private String nickname;
    private String headerImg;
    private Integer gender;

}