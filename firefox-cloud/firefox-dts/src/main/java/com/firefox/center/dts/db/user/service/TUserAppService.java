package com.firefox.center.dts.db.user.service;

import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.user.mapper.TUserAppMapper;
import com.firefox.center.dts.db.user.model.TUserApp;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * App用户服务类
 */
@Service
public class TUserAppService extends BaseService<TUserAppMapper, TUserApp> {

    public int selectCount() {
        return this.baseMapper.selectCount();
    }

    public List<TUserApp> selectList(int startId, int length){
        return baseMapper.selectList(startId, length);
    }

}
