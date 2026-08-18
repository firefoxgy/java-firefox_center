package com.firefox.center.sys.core.system.service.impl;

import com.firefox.center.sys.core.system.entity.SysUserRole;
import com.firefox.center.sys.core.system.mapper.SysUserRoleMapper;
import com.firefox.center.sys.core.system.service.ISysUserRoleService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 * 用户角色表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    public SysUserRole getSysUserRole(String userId, String roleId){
        return baseMapper.getSysUserRole(userId, roleId);
    }

}
