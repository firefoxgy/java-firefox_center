package com.firefox.center.app.db.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.app.db.mapper.TLogMailMapper;
import com.firefox.center.app.db.model.TLogMail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
@RequiredArgsConstructor
public class TLogMailService extends BaseService<TLogMailMapper, TLogMail> {

}
