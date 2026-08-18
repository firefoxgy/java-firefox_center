package com.firefox.center.credit.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "CreditRegDTO", description = "积分登记实体")
public class CreditRegDTO {

    @ApiModelProperty(value = "行为类型")
    @NotBlank(message="行为类型为空")
    private String typeNo;
    @ApiModelProperty(value = "行为")
    @NotBlank(message="行为为空")
    private String behaviorNo;

}