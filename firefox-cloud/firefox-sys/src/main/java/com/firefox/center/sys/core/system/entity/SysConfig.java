package com.firefox.center.sys.core.system.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("sys_config")
@ApiModel(value = "config", description = "系统配置")
public class SysConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "config_id",type = IdType.AUTO)//指定自增策略
    private Integer configId;
    private String configCode;
    private String configType;
    private String configTitle;
    private Integer configGroup;
    private String configOptions;
    private String configRemark;
    private String configValue;
    private Integer configSort;
}