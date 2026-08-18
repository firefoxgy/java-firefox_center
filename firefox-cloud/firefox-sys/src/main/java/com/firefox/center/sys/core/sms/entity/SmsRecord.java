package com.firefox.center.sys.core.sms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.firefox.center.sys.common.system.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
@TableName("sms_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SmsRecord extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Excel(name = "id")
	private String id;

	@Excel(name = "手机号")
	private String phone;

	@Excel(name = "redis的key")
	private String redis_key;

	@Excel(name = "验证码")
	private String code;

	@Excel(name = "短信发送状态 0成功 -1失败")
	private Integer state;

}
