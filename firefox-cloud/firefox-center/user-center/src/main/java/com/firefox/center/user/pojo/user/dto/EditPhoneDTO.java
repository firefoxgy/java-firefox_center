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
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/3/25 15:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "EditPhoneDTO", description = "修改手机号实体")
public class EditPhoneDTO {

    @ApiModelProperty(value = "手机号")
    @NotBlank(message="手机号为空")
    private String phone;

    @ApiModelProperty(value = "验证码为空")
    @NotBlank(message="验证码为空")
    private String code;

}
