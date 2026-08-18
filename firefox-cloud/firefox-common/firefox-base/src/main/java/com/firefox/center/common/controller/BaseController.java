package com.firefox.center.common.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.constants.BusinessConstants;
import com.firefox.center.common.entity.FirefoxInfo;
import com.firefox.center.common.kit.StrKit;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/2/18 15:08
 */
public class BaseController {

    @Resource
    protected HttpServletRequest request;
    @Resource
    protected HttpServletResponse response;

    protected FirefoxInfo getFirefoxInfo() {
        String clientType=request.getHeader(BusinessConstants.HEADER_CLIENT_TYPE);
        String version=request.getHeader(BusinessConstants.HEADER_APP_VERSION);
        String appId=request.getHeader(BusinessConstants.HEADER_APP_ID);
        String tenantId=request.getHeader(BusinessConstants.HEADER_TENANT_ID);
        String suid=request.getHeader(BusinessConstants.HEADER_USER_ID);
        String ssid=request.getHeader(BusinessConstants.HEADER_SID);
        String utype=request.getHeader(BusinessConstants.HEADER_USER_Type);
        String scid=request.getHeader(BusinessConstants.HEADER_CACHE_UID);
        Long uid=0L,sid=0L,cid=0L;
        if(StrKit.notBlank(suid)){
            uid=Long.valueOf(suid);
        }
        if(StrKit.notBlank(ssid)){
            sid=Long.valueOf(ssid);
        }
        if(StrKit.notBlank(scid)){
            cid=Long.valueOf(scid);
        }
        return FirefoxInfo.builder()
                .clientType(clientType)
                .version(version)
                .appId(appId)
                .tenantId(Integer.valueOf(tenantId))
                .uid(uid)
                .sid(sid)
                .cid(cid)
                .uType(utype)
                .cid(cid)
                .build();
    }

    protected <T> IPage<T> getPage() {
        return getPage(20);
    }

    protected <T> IPage<T> getPage(int size) {
        int _size = size, _index = 1;
        if (StrKit.notBlank(request.getParameter("_size"))) {
            _size = Integer.parseInt(request.getParameter("_size"));
        }
        if (StrKit.notBlank(request.getParameter("_index"))) {
            _index = Integer.parseInt(request.getParameter("_index"));
        }
        Page<T> p = new Page<T>(_index, _size);
        String _sort = request.getParameter("_sort");
        if (!StringUtils.isEmpty(_sort)) {
            if("asc".equals(_sort.split(",")[1])) {
                p.addOrder(OrderItem.asc(_sort.split(",")[0]));
            } else {
                p.addOrder(OrderItem.desc(_sort.split(",")[0]));
            }
        }
        return p;
    }

}
