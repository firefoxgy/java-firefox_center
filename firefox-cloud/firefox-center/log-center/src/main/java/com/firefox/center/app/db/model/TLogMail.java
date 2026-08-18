package com.firefox.center.app.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_log_mail")
@ApiModel(value = "短信邮箱日志表")
public class TLogMail {

    @TableId(value = "id", type= IdType.INPUT)
    private String id;
    private String appId;
    private Integer tenantId;
    private String mail;
    private String title;
    private String content;
    private Integer status;
    private Date createTime;

}