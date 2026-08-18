package com.firefox.center.credit.db.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.entity.FirefoxInfo;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.credit.db.mapper.TCreditLogMapper;
import com.firefox.center.credit.db.model.TCredit;
import com.firefox.center.credit.db.model.TCreditLog;
import com.firefox.center.db.service.BaseService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

@Service
public class TCreditLogService extends BaseService<TCreditLogMapper, TCreditLog> {

    public IPage<TCreditLog> queryPage(FirefoxInfo info, IPage<TCreditLog> page) {
        QueryWrapper<TCreditLog> queryWrapper = new QueryWrapper<TCreditLog>();
        queryWrapper.gt("create_time", LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0, 0));
        queryWrapper.lt("create_time", LocalDateTime.now());
        return baseMapper.selectPage(page, queryWrapper);
    }

}
