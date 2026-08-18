package com.firefox.center.config.db.service;

import com.firefox.center.config.db.mapper.TConfigVersionMapper;
import com.firefox.center.config.db.model.TConfigAllVersion;
import com.firefox.center.db.service.BaseService;
import org.springframework.stereotype.Service;

@Service()
public class TConfigAllVersionService extends BaseService<TConfigVersionMapper, TConfigAllVersion> {

    public Integer selectVersionByType(String type){
        return baseMapper.selectVersionByType(type);
    }

}
