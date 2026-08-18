package com.firefox.center.sys.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("oauth_tenant_app")
public class OauthTenantApp implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer tenantId;
	private String appId;
	private Integer status;
}
