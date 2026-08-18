package com.firefox.center.sys.core.system.mapper;

import org.apache.ibatis.annotations.Select;
import com.firefox.center.sys.core.system.entity.SysDictItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {
    @Select("SELECT * FROM sys_dict_item WHERE DICT_ID = #{mainId} order by sort_order asc, item_value asc")
    public List<SysDictItem> selectItemsByMainId(String mainId);

    @Select("SELECT item_text,item_value FROM sys_dict_item WHERE DICT_ID = (select id from sys_dict where dict_code=#{code}) order by sort_order asc, item_value asc")
    public List<SysDictItem> selectItemsByDicCode(String code);

    @Select("SELECT item_text,item_value FROM sys_dict_item WHERE item_value=#{value} and DICT_ID = (select id from sys_dict where dict_code=#{code}) order by sort_order asc, item_value asc")
    public SysDictItem getItem(String code, String value);
}
