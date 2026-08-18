package com.firefox.center.sentinel.config;

import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.firefox.center.common.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * https://zhuanlan.zhihu.com/p/150058613
 * Sentinel配置类
 *
 * @Author: sujie
 * @date 2021/04/22
 */
public class SentinelAutoConfigure {
    public SentinelAutoConfigure() {
        WebCallbackManager.setUrlBlockHandler(new CustomUrlBlockHandler());
    }

    /**
     * 限流、熔断统一处理类
     */
    public class CustomUrlBlockHandler implements UrlBlockHandler {
        @Override
        public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
            R result = null;
            if(e instanceof FlowException){
                result=R.error("限流了");
            }
            httpServletResponse.getWriter().print(JSONUtil.toJsonStr(result));
        }
    }
}
