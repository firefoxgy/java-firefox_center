package com.firefox.center.sys.common.code;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/2/21 15:54
 */
public class BarcodeUtil {

    public static void createBarCode(String path, String content){
        createBarCode(path, content, 10.0, 0.4, true, false);
    }

    public static void createBarCode(String path, String content, Double height, Double width, boolean withQuietZone,
                                     boolean hideText){
        try {
            File file=new File(path);
            OutputStream ous=new FileOutputStream(file);
            if(StringUtils.isEmpty(content) || ous==null)
                return;
            //选择条形码类型(好多类型可供选择)
            Code128Bean bean = new Code128Bean();
            // 分辨率
            int dpi = 300;
            // 设置两侧是否留白
            bean.doQuietZone(withQuietZone);

            // 设置条形码高度和宽度
            bean.setBarHeight((double) ObjectUtils.defaultIfNull(height, 9.0D));
            if (width != null) {
                bean.setModuleWidth(width);
            }
            // 设置文本位置（包括是否显示）
            if (hideText) {
                bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
            }
            // 设置图片类型
            String format = "image/png";
            // 输出流
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format,
                    dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            //生成条码
            bean.generateBarcode(canvas,content);
            canvas.finish();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String msg = "320000153628260425";
        String path = "D:\\320000153628260425.png";
        createBarCode(path, msg);
    }

}
