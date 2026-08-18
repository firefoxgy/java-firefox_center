package com.firefox.center.credit.db.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.firefox.center.credit.db.mapper.TCreditBehaviorMapper;
import com.firefox.center.credit.db.model.TCreditBehavior;
import com.firefox.center.db.service.BaseService;
import org.springframework.stereotype.Service;

@Service

public class TCreditBehaviorService extends BaseService<TCreditBehaviorMapper, TCreditBehavior> {

    public TCreditBehavior queryRecord(int typeId, String no) {
        QueryWrapper<TCreditBehavior> queryWrapper = new QueryWrapper<TCreditBehavior>();
        queryWrapper.eq("no", no);
        queryWrapper.eq("type_id", typeId);
        return baseMapper.selectOne(queryWrapper);
    }

}
