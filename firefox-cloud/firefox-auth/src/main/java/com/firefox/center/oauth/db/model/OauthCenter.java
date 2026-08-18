package com.firefox.center.oauth.db.model;

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
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("oauth_center")
@ApiModel(value = "OauthCenter", description = "中心表")
public class OauthCenter implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type= IdType.AUTO)
	private Integer id;
	private String name;
    private String service;
    private String path;
    private Integer status;

}
