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

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("oauth_tenant")
@ApiModel(value = "OauthTenant", description = "租户表")
public class OauthTenant implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type= IdType.AUTO)
	private Integer id;
	private String name;
	private String appName;
    private String domain;
    private String logo;
    private int status;

}
