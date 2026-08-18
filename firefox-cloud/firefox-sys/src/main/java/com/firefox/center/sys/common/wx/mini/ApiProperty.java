package com.firefox.center.sys.common.wx.mini;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.io.Serializable;

@Data
@Configuration
@ConfigurationProperties(prefix = ApiProperty.PREFIX)
public class ApiProperty implements Serializable {
    private static final long serialVersionUID = 5243926308290263767L;
    public static final String PREFIX = "firefox.pay";
    private String basePath = "";           //开屏支付地址
    private String loginCode = null;        //子系统接注册至结算系统时生成
    private String secret = null;           //token私钥

    private Order order = new Order();
    private NoteTemplate noteTemplate = new NoteTemplate();
    /**
     * 下单相关
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Order {
        private String subAppId=null;
        private String subVendorId=null;
        private String carSubNotifyUrl=null;
        private String eatSubNotifyUrl=null;
        private String isShare=null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoteTemplate {
        private String noteCarCustom=null;
        private String noteCarUser=null;
        private String noteSign=null;
        private String noteEat=null;
    }

}
