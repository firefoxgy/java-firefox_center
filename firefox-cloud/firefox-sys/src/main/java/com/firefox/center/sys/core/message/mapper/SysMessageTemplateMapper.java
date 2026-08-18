package com.firefox.center.sys.core.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import com.firefox.center.sys.core.message.entity.SysMessageTemplate;

import java.util.List;

/**
 * @Description: 消息模板
 * * @Date:  2019-04-09
 * @Version: V1.0
 */
public interface SysMessageTemplateMapper extends BaseMapper<SysMessageTemplate> {

    @Select("SELECT * FROM sys_sms_template WHERE template_code = #{code}")
    List<SysMessageTemplate> selectByCode(String code);

}
