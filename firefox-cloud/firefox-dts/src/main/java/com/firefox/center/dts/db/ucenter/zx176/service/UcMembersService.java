package com.firefox.center.dts.db.ucenter.zx176.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.dts.db.ucenter.common.mapper.UcMembersMapper;
import com.firefox.center.dts.db.ucenter.common.model.UcMembers;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公司表 服务类
 */
@Service("zx176_UcMembersService")
@DS("zx176")
public class UcMembersService extends BaseService<UcMembersMapper, UcMembers> {

    public int selectCount() {
        return this.baseMapper.selectCount();
    }

    public List<UcMembers> selectList(int startId, int length) {
        return this.baseMapper.selectList(startId, length);
    }
}
