package com.firefox.center.sys.common.util.security.entity;

import lombok.Data;

@Data
public class SecuritySignResp {
    private String data;
    private String signData;
    private String aesKey;
}
