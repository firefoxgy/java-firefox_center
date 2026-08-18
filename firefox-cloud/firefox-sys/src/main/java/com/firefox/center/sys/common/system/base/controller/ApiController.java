package com.firefox.center.sys.common.system.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.sys.common.util.StrKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: Controller基类
 * @Author: dangzhenghui@163.com
 * @Date: 2019-4-21 8:13
 * @Version: 1.0
 */
@Slf4j
public class ApiController {

    @Resource
    protected HttpServletRequest request;
    @Resource
    protected HttpServletResponse response;

    protected <T> IPage<T> getPage() {
        return getPage(10);
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
