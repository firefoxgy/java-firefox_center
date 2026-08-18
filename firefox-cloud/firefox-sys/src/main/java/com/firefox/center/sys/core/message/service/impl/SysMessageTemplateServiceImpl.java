package com.firefox.center.sys.core.message.service.impl;

import com.firefox.center.sys.common.system.base.service.impl.BaseServiceImpl;
import com.firefox.center.sys.core.message.entity.SysMessageTemplate;
import com.firefox.center.sys.core.message.mapper.SysMessageTemplateMapper;
import com.firefox.center.sys.core.message.service.ISysMessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @Description: 消息模板
 * * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageTemplateServiceImpl extends BaseServiceImpl<SysMessageTemplateMapper, SysMessageTemplate> implements ISysMessageTemplateService {

    @Override
    public List<SysMessageTemplate> selectByCode(String code) {
        return baseMapper.selectByCode(code);
    }

}
