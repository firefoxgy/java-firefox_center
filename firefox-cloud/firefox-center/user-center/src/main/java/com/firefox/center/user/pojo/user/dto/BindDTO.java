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
@ApiModel(value = "BindDTO", description = "绑定实体")
public class BindDTO {

    @ApiModelProperty(value = "手机号")
    @NotBlank(message="手机号为空")
    private String phone;
    @ApiModelProperty(value = "验证码")
    @NotBlank(message="验证码为空")
    private String code;

}