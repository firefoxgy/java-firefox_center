package com.firefox.center.config.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("t_config_sms")
@ApiModel(value = "短信模板表")
public class TConfigSms implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type= IdType.AUTO)
	public Integer id;
	public String appKey;
	public String appSecret;
	public String module;
	public Integer type;
	public String signName;
	public String code;
	public Integer expire;
	public String detail;
}
