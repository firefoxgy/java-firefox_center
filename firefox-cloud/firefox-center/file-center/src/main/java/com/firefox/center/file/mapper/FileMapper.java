package com.firefox.center.file.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.db.mapper.SuperMapper;

import com.firefox.center.file.model.FileInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 上传存储db
 *
 * @Author: sujie
 */
public interface FileMapper extends SuperMapper<FileInfo> {
    List<FileInfo> findList(Page<FileInfo> page, @Param("f") Map<String, Object> params);
}
