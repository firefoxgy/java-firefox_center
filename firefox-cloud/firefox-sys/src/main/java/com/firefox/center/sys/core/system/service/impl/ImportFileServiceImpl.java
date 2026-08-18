package com.firefox.center.sys.core.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.firefox.center.sys.common.util.CommonUtils;
import org.jeecgframework.poi.excel.imports.base.ImportFileServiceI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * excel导入 实现类
 */
@Slf4j
@Service
public class ImportFileServiceImpl implements ImportFileServiceI {

    @Value("${firefox.upload.path}")
    private String upLoadPath;

    @Value(value="${firefox.uploadType}")
    private String uploadType;

    @Override
    public String doUpload(byte[] data) {
        return CommonUtils.uploadOnlineImage(data, upLoadPath, "import", uploadType);
    }
}
