package com.firefox.center.sys.modules.common.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "firefox.upload")
public class FileProperty {
    /**
     * 路径
     */
    //保存硬盘地址
    private String path;
    //保存硬盘地址
    private String webapp;
    //默认图片类型文件夹
    private String imageFolder;
    //默认文件类型文件夹
    private String documentFolder;
    //默认的视频类型文件夹
    private String videoFolder;
    //默认的音频类型文件夹
    private String musicFolder;
    //默认的条形码类型文件夹
    private String barCodeFolder;
    //默认的二维码类型文件夹
    private String qrCodeFolder;
    //默认的icon类型文件夹
    private String iconFolder;
    /**
     * 类型
     */
    //图片类型
    private String[] imageType;
    //文件类型
    private String[] documentType;
    //视频类型
    private String[] videoType;
    //音频类型
    private String[] musicType;

}
