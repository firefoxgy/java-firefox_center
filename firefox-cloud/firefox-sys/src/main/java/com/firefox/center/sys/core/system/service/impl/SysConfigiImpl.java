package com.firefox.center.sys.core.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.firefox.center.common.Record;
import com.firefox.center.sys.core.Consts;
import com.firefox.center.sys.core.system.entity.SysConfig;
import com.firefox.center.sys.core.system.mapper.SysConfigMapper;
import com.firefox.center.sys.core.system.service.ISysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 底层共通业务API，提供其他独立模块调用
 * @Author: scott
 * @Date:2019-4-20 
 * @Version:V1.0
 */
@Slf4j
@Service
public class SysConfigiImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

	public Map<String, Object> findAll() {
		List<SysConfig> confList=baseMapper.queryAll();
		String tabs="";
		for(SysConfig conf: confList){
			if(conf.getConfigCode().equals(Consts.Config.CONF_GROUP_CODE)){
				tabs=conf.getConfigValue();
			}
		}
		String[] tagsArr = tabs.split("\r\n");
		List<Map<String, Object>> tablist = new ArrayList<Map<String, Object>>();
		for(String tag:tagsArr){
			Record record = new Record();
			record.set("dicId", tag.split(":")[0]);
			record.set("dicName", tag.split(":")[1]);
			tablist.add(record.getColumns());
		}
		List<Map<String, Object>> tabProplist = new ArrayList<Map<String, Object>>();
		for(Map map:tablist){
			Record tabProp = new Record();
			List<SysConfig> list = new ArrayList<SysConfig>();
			for(SysConfig conf: confList){
				if(Integer.parseInt(map.get("dicId").toString()) == conf.getConfigGroup()){
					list.add(conf);
				}
			}
			tabProp.set("dicId", map.get("dicId").toString()).set("dicName", map.get("dicName").toString());
			tabProp.set("props", list);
			tabProplist.add(tabProp.getColumns());
		}
		return new Record().set("tabs", tablist).set("tabProps", tabProplist).getColumns();
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveConf(SysConfig config) {
		if(config.getConfigId()==null){
			save(config);
		}else{
			updateById(config);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void udpateConf(Map<String, String> maps) {
		maps.forEach((key, value) -> {
			value=value.replace("\n", "\r\n");
			baseMapper.updateConf(key, value);
		});
	}

	public String findByCode(String code) {
		QueryWrapper<SysConfig> queryWrapper = Wrappers.query();
		queryWrapper.select("config_value");
		queryWrapper.eq("config_code", code);
		SysConfig config=baseMapper.selectOne(queryWrapper);
		return config!=null?config.getConfigValue():"";
	}

}