package com.firefox.center.user.pojo.sms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/3/25 15:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "CheckDTO", description = "短信验证实体")
public class CheckCodeDTO {

    @ApiModelProperty(value = "应用id")
    @NotBlank(message="sign为空")
    private String appid;

    @ApiModelProperty(value = "租户id")
    @NotNull(message="tenantid为空")
    private Integer tenantid;

    @ApiModelProperty(value = "手机号")
    @NotBlank(message="手机号为空")
    private String phone;

    @ApiModelProperty(value = "验证码")
    @NotBlank(message="验证码为空")
    private String code;

}
