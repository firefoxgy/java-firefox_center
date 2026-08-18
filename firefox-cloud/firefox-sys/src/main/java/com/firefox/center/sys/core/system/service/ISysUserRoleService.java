package com.firefox.center.sys.core.system.service;

import com.firefox.center.sys.core.system.entity.SysUserRole;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户角色表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

    public SysUserRole getSysUserRole(String userId, String roleId);
}
