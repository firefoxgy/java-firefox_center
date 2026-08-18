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
@ApiModel(value = "SendDTO", description = "发送短信实体")
public class CodeSendDTO {

    @ApiModelProperty(value = "args参数")
    @NotNull(message="args为空")
    private CodeSendArgsDTO args;

    @ApiModelProperty(value = "签名")
    @NotBlank(message="sign为空")
    private String sign;



}
