package com.firefox.center.sys.modules.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户实体
 * @Author：sujie
 * @Date：2020/07/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("t_user_app_third")
@ApiModel(value = "用户关联表")
public class TUserAppThird {
    /**
     * id
     */
    @TableId(value = "id", type= IdType.AUTO)
    private Long id;
    private Long uid;
    private Long sid;
    private String appId;
    private Integer tenantId;

}