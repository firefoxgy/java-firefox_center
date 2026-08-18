package com.firefox.center.sys.core.system.service.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.firefox.center.sys.common.system.api.ISysBaseAPI;
import com.firefox.center.sys.core.system.entity.SysLog;
import com.firefox.center.sys.core.system.mapper.SysLogMapper;
import com.firefox.center.sys.core.system.service.ISysLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 * 系统日志表 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Service
@RequiredArgsConstructor
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements ISysLogService {

	private final ISysBaseAPI sysBaseAPI;
	
	/**
	 * @功能：清空所有日志记录
	 */
	@Override
	public void removeAll() {
		baseMapper.removeAll();
	}

	@Override
	public Long findTotalVisitCount() {
		return baseMapper.findTotalVisitCount();
	}

	//update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
	@Override
	public Long findTodayVisitCount(Date dayStart, Date dayEnd) {
		return baseMapper.findTodayVisitCount(dayStart,dayEnd);
	}

	@Override
	public Long findTodayIp(Date dayStart, Date dayEnd) {
		return baseMapper.findTodayIp(dayStart,dayEnd);
	}
	//update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数

	@Override
	public List<Map<String,Object>> findVisitCount(Date dayStart, Date dayEnd) {
		try {
			String dbType = sysBaseAPI.getDatabaseType();
			return baseMapper.findVisitCount(dayStart, dayEnd,dbType);
		} catch (SQLException e) {
		}
		return null;
	}
}
