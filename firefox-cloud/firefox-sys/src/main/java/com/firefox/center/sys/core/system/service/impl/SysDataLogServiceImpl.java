package com.firefox.center.sys.core.system.service.impl;

import com.firefox.center.sys.core.system.entity.SysDataLog;
import com.firefox.center.sys.core.system.mapper.SysDataLogMapper;
import com.firefox.center.sys.core.system.service.ISysDataLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class SysDataLogServiceImpl extends ServiceImpl<SysDataLogMapper,SysDataLog> implements ISysDataLogService {
	@Autowired
	private SysDataLogMapper logMapper;

	/**
	 * 添加数据日志
	 */
	@Override
	public void addDataLog(String tableName, String dataId, String dataContent) {
		String versionNumber = "0";
		String dataVersion = logMapper.queryMaxDataVer(tableName, dataId);
		if(dataVersion != null ) {
			versionNumber = String.valueOf(Integer.parseInt(dataVersion)+1);
		}
		SysDataLog log = new SysDataLog();
		log.setDataTable(tableName);
		log.setDataId(dataId);
		log.setDataContent(dataContent);
		log.setDataVersion(versionNumber);
		this.save(log);
	}

}
