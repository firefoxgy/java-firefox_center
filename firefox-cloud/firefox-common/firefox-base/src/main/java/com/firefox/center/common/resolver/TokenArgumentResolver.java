package com.firefox.center.common.resolver;

import cn.hutool.core.util.StrUtil;
import com.firefox.center.common.annotation.LoginUser;
import com.firefox.center.common.constants.SecurityConstants;
import com.firefox.center.common.feign.UserFeignService;
import com.firefox.center.common.model.LoginAdminUser;
import com.firefox.center.common.model.TSysRole;
import com.firefox.center.common.model.TUserAdmin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Token转化SysUser
 *
 * @Author: sujie
 * @date 2021/04/21
 */
@Slf4j
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {
    private UserFeignService userService;

    public TokenArgumentResolver(UserFeignService userService) {
        this.userService = userService;
    }

    /**
     * 入参筛选
     *
     * @param methodParameter 参数集合
     * @return 格式化后的参数
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(LoginUser.class) && methodParameter.getParameterType().equals(TUserAdmin.class);
    }

    /**
     * @param methodParameter       入参集合
     * @param modelAndViewContainer model 和 view
     * @param nativeWebRequest      web相关
     * @param webDataBinderFactory  入参解析
     * @return 包装对象
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        LoginUser loginUser = methodParameter.getParameterAnnotation(LoginUser.class);
        boolean isFull = loginUser.isFull();
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        String appId = request.getHeader(SecurityConstants.HEADER_APPID);
        String tenantId = request.getHeader(SecurityConstants.HEADER_TENANTID);
        String userId = request.getHeader(SecurityConstants.HEADER_USERID);
        String roles = request.getHeader(SecurityConstants.HEADER_ROLE);
        if (StrUtil.isBlank(userId) || "0".equals(userId)) {
            log.warn("resolveArgument error username is empty");
            return null;
        }
        LoginAdminUser user;
        if (isFull) {
            user = userService.findAdminUserByUserId(appId, Integer.valueOf(tenantId), Long.valueOf(userId));
        } else {
            user = new LoginAdminUser();
            user.setAppId(appId);
            user.setTenantId(Integer.valueOf(tenantId));
            user.setUid(Long.valueOf(userId));
        }
        List<TSysRole> sysRoleList = new ArrayList<>();
        Arrays.stream(roles.split(",")).forEach(role -> {
            TSysRole sysRole = new TSysRole();
            sysRole.setCode(role);
            sysRoleList.add(sysRole);
        });
        user.setRoles(sysRoleList);
        return user;
    }
}
