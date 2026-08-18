package com.firefox.center.sys.core.sms.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.firefox.center.sys.core.sms.entity.SmsRecord;
import com.firefox.center.sys.core.sms.mapper.SmsRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class SmsService extends ServiceImpl<SmsRecordMapper, SmsRecord> {

	public void sendCode(String phone){

	}
}
