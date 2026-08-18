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
public class TTenantThirdConfDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	public String thirdAppId;
	public String thirdSecretId;
	public String thirdType;
	public String appId;
	public Integer tenantId;
}
