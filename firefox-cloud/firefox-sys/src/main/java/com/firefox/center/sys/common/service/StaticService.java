package com.firefox.center.sys.common.service;
import com.firefox.center.sys.config.property.FirefoxProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
@RequiredArgsConstructor
public class StaticService {

    private final FirefoxProperty firefoxProperty;

    public String getUrl(String path) {
        return firefoxProperty.getWebRoot()+path;
    }

    public String getRelativeUrl(String path) {
        String result=path.replace(firefoxProperty.getWebRoot(), "");
        return result;
    }
}
