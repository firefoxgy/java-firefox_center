package com.firefox.center.file.config;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.firefox.center.file.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.firefox.center.file.model.FileType;

/**
 * FileService工厂<br>
 * 将各个实现类放入map
 *
 * @author: sujie
 */
@Configuration
public class OssServiceFactory {

    private Map<FileType, IFileService> map = new EnumMap<>(FileType.class);
    @Autowired
    private IFileService aliyunOssServiceImpl;

    @Autowired
    private IFileService qiniuOssServiceImpl;

    @PostConstruct
    public void init() {
        map.put(FileType.ALIYUN, aliyunOssServiceImpl);
        map.put(FileType.QINIU, qiniuOssServiceImpl);
    }

    public IFileService getFileService(String fileType) {
        return map.get(FileType.valueOf(fileType));
    }
}
