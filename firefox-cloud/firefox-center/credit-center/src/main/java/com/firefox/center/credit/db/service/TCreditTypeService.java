package com.firefox.center.credit.db.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.firefox.center.credit.db.mapper.TCreditTypeMapper;
import com.firefox.center.credit.db.model.TCreditType;
import com.firefox.center.db.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class TCreditTypeService extends BaseService<TCreditTypeMapper, TCreditType> {

    public TCreditType queryRecord(String no) {
        QueryWrapper<TCreditType> queryWrapper = new QueryWrapper<TCreditType>();
        queryWrapper.eq("no", no);
        return baseMapper.selectOne(queryWrapper);
    }

}
