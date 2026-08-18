package com.firefox.center.sys.core.system.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.firefox.center.sys.core.system.entity.SysAnnouncementSend;
import com.firefox.center.sys.core.system.mapper.SysAnnouncementSendMapper;
import com.firefox.center.sys.core.system.model.AnnouncementSendModel;
import com.firefox.center.sys.core.system.service.ISysAnnouncementSendService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户通告阅读标记表
 * * @Date:  2019-02-21
 * @Version: V1.0
 */
@Service
public class SysAnnouncementSendServiceImpl extends ServiceImpl<SysAnnouncementSendMapper, SysAnnouncementSend> implements ISysAnnouncementSendService {

	@Override
	public List<String> queryByUserId(String userId) {
		return baseMapper.queryByUserId(userId);
	}

	@Override
	public Page<AnnouncementSendModel> getMyAnnouncementSendPage(Page<AnnouncementSendModel> page,
			AnnouncementSendModel announcementSendModel) {
		 return page.setRecords(baseMapper.getMyAnnouncementSendList(page, announcementSendModel));
	}

}
