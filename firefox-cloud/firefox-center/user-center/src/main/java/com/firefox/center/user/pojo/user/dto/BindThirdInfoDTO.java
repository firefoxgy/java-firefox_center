package com.firefox.center.user.pojo.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 用户信息实体
 * @Author：sujie
 * @Date：2020/07/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "ThirdInfoDTO", description = "第三方帐号绑定实体")
public class BindThirdInfoDTO {

    @ApiModelProperty(value = "绑定类型")
    @NotBlank(message="类型为空")
    private String type;
    @NotBlank(message="第三方appid为空")
    private String externalappid;
    @ApiModelProperty(value = "第三方id")
    @NotBlank(message="第三方id为空")
    private String thirdid;
    private String unionid;
    private String nickname;
    private String figureurl;
    private String gender;

}