package com.firefox.center.credit.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.firefox.center.common.model.Entity;
import com.firefox.center.common.model.UserSids;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("t_credit_log")
public class TCreditLog {
    private static final long serialVersionUID = -5886012896705137070L;

    @TableId(value = "id",type = IdType.INPUT)
    private Long id;
    private String appId;
    private Integer tenantId;
    private Long uid;
    private Long sid;
    private String typeNo;
    private String typeName;
    private String behaviorNo;
    private String behaviorName;
    private Integer num;
    private Date createTime;

}