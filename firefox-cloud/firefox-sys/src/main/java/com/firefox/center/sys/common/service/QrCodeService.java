package com.firefox.center.sys.common.service;

import com.firefox.center.sys.common.code.CodeCreateUtil;
import com.firefox.center.sys.common.codec.Md5Utils;
import com.firefox.center.sys.common.io.FileUtils;
import com.firefox.center.sys.common.util.StrKit;
import com.firefox.center.sys.core.system.service.ISysConfigService;
import com.firefox.center.sys.modules.common.property.FileProperty;
import com.firefox.center.common.R;
import com.firefox.center.common.Record;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class QrCodeService {

    private final FileProperty fileConfig;
    private final ISysConfigService sysConfigService;

    public R<?> createQrCode(String text, int width) {
        return createQrCode(text, null, null, width);
    }

    public R<?> createQrCode(String text, String icon, String iconPath, int width) {
        try {
            String qrcodeFolder = fileConfig.getPath()+fileConfig.getQrCodeFolder();
            String datePath = new SimpleDateFormat("/yyyy/MM/dd/").format(new Date());

            //保存目录
            String upload_path=qrcodeFolder+datePath;
            File upload_dir = new File(upload_path);

            //创建保存目录
            if (!upload_dir.exists() && !FileUtils.createDirectory(upload_path)) {
                return R.error("目录创建失败");
            }

            //二维码文件名称
            String md5 = Md5Utils.md5(text);
            String fileRealPath = upload_path + md5 + ".jpg";
            String fileRelativePath = "/static"+ fileConfig.getQrCodeFolder() + datePath + md5 + ".jpg";

            //生成二维码
            if (!CodeCreateUtil.createQrCode(new FileOutputStream(new File(fileRealPath)), text, width, "JPEG")) {
                return R.error("二维码生成失败");
            }
            if(StrKit.notBlank(icon)){
                String door_qrcode_icon=sysConfigService.findByCode(icon);
                if(StrKit.notBlank(door_qrcode_icon) && "1".equals(door_qrcode_icon)){
                    //二维码打icon
                    try {
                        String icon_path=sysConfigService.findByCode(iconPath);
                        File iconFile = new File(fileConfig.getPath() + icon_path);
                        if (iconFile.exists()) {
                            Thumbnails.of(fileRealPath).scale(1f).outputQuality(1d).watermark(Positions.CENTER, ImageIO.read(iconFile), 1f).toFile(fileRealPath);
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        return R.error("出现异常，水印添加失败");
                    }
                }
            }
            return R.ok(new Record().set("text", text).set("path", fileRelativePath).getColumns());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("出现异常，水印添加失败");
        }
    }

}
