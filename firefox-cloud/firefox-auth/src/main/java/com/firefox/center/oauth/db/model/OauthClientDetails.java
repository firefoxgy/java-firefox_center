package com.firefox.center.oauth.db.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.firefox.center.common.model.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: sujie
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oauth_client_details")
public class OauthClientDetails extends Entity {
   private static final long serialVersionUID = -8185413579135897885L;
   private String clientId;
   private String resourceIds = "";
   private String clientSecret;
   private String scope = "all";
   private String authorizedGrantTypes = "authorization_code,password,refresh_token,client_credentials";
   private String webServerRedirectUri;
   private String authorities = "";
   //12小时
   @TableField(value = "access_token_validity")
   private Integer accessTokenValiditySeconds = 43200;
   //30天
   @TableField(value = "refresh_token_validity")
   private Integer refreshTokenValiditySeconds = 2592000;
   private String additionalInformation = "{}";
   private String autoapprove = "true";
}
