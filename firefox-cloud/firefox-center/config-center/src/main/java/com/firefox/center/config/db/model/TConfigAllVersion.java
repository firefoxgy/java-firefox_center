package com.firefox.center.config.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("t_config_all_version")
@ApiModel(value = "公共配置表")
public class TConfigAllVersion implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type= IdType.AUTO)
	public Integer id;
	public String type;
	public Integer version;
}
