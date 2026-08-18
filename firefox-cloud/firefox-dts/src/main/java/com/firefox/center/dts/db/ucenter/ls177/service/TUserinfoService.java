package com.firefox.center.dts.db.ucenter.ls177.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.ucenter.common.mapper.TUserinfoMapper;
import com.firefox.center.dts.db.ucenter.common.model.TUserinfo;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service("ls177_TUserinfoService")
@DS("ls177")
public class TUserinfoService extends BaseService<TUserinfoMapper, TUserinfo> {

}
