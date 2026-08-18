package com.firefox.center.file.service.impl;

import com.firefox.center.file.model.FileInfo;
import com.firefox.center.file.model.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSSClient;

/**
 * 阿里云oss存储文件
 *
 * @author: sujie
 */
@Service("aliyunOssServiceImpl")
@RequiredArgsConstructor
public class AliyunOssServiceImplI extends AbstractIFileService {

    private final OSSClient ossClient;
    @Value("${aliyun.oss.bucketName:xxxxx}")
    private String bucketName;
    @Value("${aliyun.oss.domain:xxxxx}")
    private String domain;

    @Override
    protected FileType fileType() {
        return FileType.ALIYUN;
    }

    @Override
    protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
        ossClient.putObject(bucketName, fileInfo.getName(), file.getInputStream());
        fileInfo.setUrl(domain + "/" + fileInfo.getName());
    }

    @Override
    protected boolean deleteFile(FileInfo fileInfo) {
        ossClient.deleteObject(bucketName, fileInfo.getName());
        return true;
    }
}
