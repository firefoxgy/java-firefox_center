package com.firefox.center.sys.core.system.service;

import com.firefox.center.sys.core.system.entity.SysDictItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface ISysDictItemService extends IService<SysDictItem> {
    public List<SysDictItem> selectItemsByMainId(String mainId);
    public List<SysDictItem> selectItemsByDicCode(String code);
    public SysDictItem getItem(String code, String val);
}
