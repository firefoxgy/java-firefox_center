package com.firefox.center.common.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @Author: sujie
 * 角色
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_role")
@AllArgsConstructor
public class TSysRole extends Entity<Long> {
    private static final long serialVersionUID = 4497149010220586111L;
    private String code;
    private String name;
    private Long userId;
}
