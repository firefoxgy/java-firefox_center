package com.firefox.center.sys.common.util.security.entity;

import lombok.Data;

@Data
public class SecurityReq {
    private String data;
    private String pubKey;
    private String signData;
    private String aesKey;
}
