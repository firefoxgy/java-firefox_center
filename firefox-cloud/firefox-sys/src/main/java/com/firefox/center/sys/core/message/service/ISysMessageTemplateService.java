package com.firefox.center.sys.core.message.service;

import java.util.List;

import com.firefox.center.sys.common.system.base.service.BaseService;
import com.firefox.center.sys.core.message.entity.SysMessageTemplate;

/**
 * @Description: 消息模板
 * * @Date:  2019-04-09
 * @Version: V1.0
 */
public interface ISysMessageTemplateService extends BaseService<SysMessageTemplate> {
    List<SysMessageTemplate> selectByCode(String code);
}
