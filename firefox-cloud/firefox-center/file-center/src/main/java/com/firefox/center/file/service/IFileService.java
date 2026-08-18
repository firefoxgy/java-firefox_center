package com.firefox.center.file.service;

import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.firefox.center.common.model.PageResult;
import com.firefox.center.file.model.FileInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件service 目前仅支持阿里云oss,七牛云
 *
 * @author: sujie
*/
public interface IFileService extends IService<FileInfo> {
	FileInfo upload(MultipartFile file ) throws Exception;
	
	PageResult<FileInfo> findList(Map<String, Object> params);
}
