package com.firefox.center.user.pojo.user.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
@ApiModel(value = "EditInfoPO", description = "修改用户信息实体")
public class EditAdminDTO {

    private String shortName;
    private String unitName;
    private String address;
    private String officePhone;
    private String mobile;
    private String email;

}