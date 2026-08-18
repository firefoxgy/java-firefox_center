package com.firefox.center.config.feign.pojo;

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
public class TConfigSmsDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	public Integer id;
	public String appKey;
	public String appSecret;
	public String module;
	public Integer type;
	public String signName;
	public String code;
	public Integer expire;
	public String url;
	public int length;
}
