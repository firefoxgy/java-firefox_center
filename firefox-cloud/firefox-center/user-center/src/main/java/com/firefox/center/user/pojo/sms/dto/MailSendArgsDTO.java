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
@ApiModel(value = "SendArgsDTO", description = "args实体")
public class MailSendArgsDTO {

    @ApiModelProperty(value = "应用id")
    @NotBlank(message="应用id为空")
    private String appid;

    @ApiModelProperty(value = "租户id")
    @NotNull(message="租户id为空")
    private Integer tenantid;

    @ApiModelProperty(value = "邮箱")
    @NotBlank(message="邮箱为空")
    private String mail;

    @ApiModelProperty(value = "时间戳")
    @NotNull(message="ts为空")
    private Long ts;
}
