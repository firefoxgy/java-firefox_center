package com.firefox.center.app.db.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.app.db.mapper.TLogSmsMapper;
import com.firefox.center.app.db.model.TLogSms;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
@RequiredArgsConstructor
public class TLogSmsService extends BaseService<TLogSmsMapper, TLogSms> {

}
