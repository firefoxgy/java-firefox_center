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
public class CreditRankDTO {

    @ApiModelProperty(value = "年")
    private Integer year;
    @ApiModelProperty(value = "季度")
    private Integer season;
    @ApiModelProperty(value = "月")
    private String month;
    @ApiModelProperty(value = "月")
    private String week;

}