package com.firefox.center.sys.core.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.firefox.center.sys.core.system.entity.SysConfig;

import java.util.Map;

/**
 * @Description: 编码校验规则
 * * @Date: 2020-02-04
 * @Version: V1.0
 */
public interface ISysConfigService extends IService<SysConfig> {

    public Map<String, Object> findAll();
    public void saveConf(SysConfig config);
    public void udpateConf(Map<String, String> maps);
    public String findByCode(String code);

}
