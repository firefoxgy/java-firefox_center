package com.firefox.center.oauth.db.model;

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
@TableName("oauth_tenant_app")
@ApiModel(value = "OauthTenantApp", description = "租户应用授权表")
public class OauthTenantApp implements Serializable {
	private static final long serialVersionUID = 1L;

    private String appId;
	private Integer tenantId;
	private Integer status;

}
