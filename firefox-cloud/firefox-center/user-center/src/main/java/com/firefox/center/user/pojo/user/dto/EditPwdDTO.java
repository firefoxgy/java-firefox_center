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
@ApiModel(value = "EditPwdDTO", description = "修改密码实体")
public class EditPwdDTO {

    @ApiModelProperty(value = "原密码")
    @NotBlank(message="原密码为空")
    private String password;

    @ApiModelProperty(value = "新密码")
    @NotBlank(message="新密码为空")
    private String newPassword;

}
