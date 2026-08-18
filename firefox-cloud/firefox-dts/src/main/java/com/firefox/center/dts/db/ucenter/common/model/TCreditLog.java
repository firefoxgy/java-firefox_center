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
@TableName("t_credit")
public class TCreditLog {
    private static final long serialVersionUID = -5886012896705137070L;

    @TableId(value = "id",type = IdType.INPUT)
    private Integer id;
    private Integer appid;
    private Integer uid;
    private String behaviortype;
    private String behaviorno;
    private Integer num;
    private String changeway;
    private Integer lastcredit;
    private Integer currcredit;
    private Integer lastcreditApp;
    private Integer currcreditApp;
    private Integer occurTime;
    private String businessno;
    private Integer sort;
    private String memo;
    private Integer createTime;

    @TableField(exist = false)
    private Integer snum;

}