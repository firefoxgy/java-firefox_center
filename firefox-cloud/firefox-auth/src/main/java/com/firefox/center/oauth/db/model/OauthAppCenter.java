package com.firefox.center.oauth.db.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("oauth_app_center")
@ApiModel(value = "OauthAppCenter", description = "租户中心授权表")
public class OauthAppCenter implements Serializable {
	private static final long serialVersionUID = 1L;

    private String id;
	private String appId;
	private Integer centerId;
	private Integer status;

}
