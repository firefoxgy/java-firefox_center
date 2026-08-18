package com.firefox.center.credit.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "CreditRankVO", description = "积分排名实体")
public class CreditRankVO {

    private Long uid;
    private Long sid;
    private String type;
    private Integer credit;
    private Integer rank;
    private String username;
    private String nickname;
    private String headerImg;
    private Integer gender;

}