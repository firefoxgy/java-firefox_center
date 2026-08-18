package com.firefox.center.sys.core.system.service.impl;

import com.firefox.center.sys.core.system.entity.SysDictItem;
import com.firefox.center.sys.core.system.mapper.SysDictItemMapper;
import com.firefox.center.sys.core.system.service.ISysDictItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements ISysDictItemService {

    @Override
    public List<SysDictItem> selectItemsByMainId(String mainId) {
        return baseMapper.selectItemsByMainId(mainId);
    }

    @Override
    public List<SysDictItem> selectItemsByDicCode(String code) {
        return baseMapper.selectItemsByDicCode(code);
    }

    @Override
    public SysDictItem getItem(String code, String value) {
        return baseMapper.getItem(code, value);
    }
}
