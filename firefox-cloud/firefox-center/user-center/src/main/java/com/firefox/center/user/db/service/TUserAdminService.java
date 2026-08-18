package com.firefox.center.user.db.service;

import com.firefox.center.common.entity.FirefoxInfo;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.kit.Assert;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.utils.AesUtil;
import com.firefox.center.db.service.BaseService;
import com.firefox.center.user.db.mapper.TUserAdminMapper;
import com.firefox.center.user.db.model.TUserAdmin;
import com.firefox.center.user.pojo.user.dto.EditAdminDTO;
import com.firefox.center.user.pojo.user.dto.EditPwdDTO;
import com.firefox.center.user.pojo.user.vo.TUserAdminVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 公司表 服务类
 */
@Service
public class TUserAdminService extends BaseService<TUserAdminMapper, TUserAdmin> {

    public final int STATUS_DISABLE = 0;

    public TUserAdmin selectByUsername(String appId, Integer tenantId, String username){
        return baseMapper.selectByUsernameAndAppId(appId, tenantId, username);
    }

    public TUserAdmin selectByUsername(String username){
        return baseMapper.selectByUsername(username);
    }

    public TUserAdmin selectByUserId(String appId, Integer tenantId, Long uid){
        return baseMapper.selectByUserId(appId, tenantId, uid);
    }

    public TUserAdminVO selectByUserId2(String appId, Integer tenantId, Long uid){
        TUserAdmin tUserAdmin=baseMapper.selectByUserId2(appId, tenantId, uid);
        TUserAdminVO tUserAdminVO=new TUserAdminVO();
        BeanUtils.copyProperties(tUserAdmin, tUserAdminVO);
        return tUserAdminVO;
    }

    public void editinfo(FirefoxInfo info, EditAdminDTO editAdminDTO){
        TUserAdmin tUserAdmin=baseMapper.selectByUserId(info.getAppId(), info.getTenantId(), info.getUid());
        if(tUserAdmin!=null){
            if(StrKit.notBlank(editAdminDTO.getShortName())){
                tUserAdmin.setShortName(editAdminDTO.getShortName());
            }
            if(StrKit.notBlank(editAdminDTO.getUnitName())){
                tUserAdmin.setUnitName(editAdminDTO.getUnitName());
            }
            if(StrKit.notBlank(editAdminDTO.getAddress())){
                tUserAdmin.setAddress(editAdminDTO.getAddress());
            }
            if(StrKit.notBlank(editAdminDTO.getOfficePhone())){
                tUserAdmin.setOfficePhone(editAdminDTO.getOfficePhone());
            }
            if(StrKit.notBlank(editAdminDTO.getMobile())){
                tUserAdmin.setMobile(editAdminDTO.getMobile());
            }
            if(StrKit.notBlank(editAdminDTO.getEmail())){
                tUserAdmin.setEmail(editAdminDTO.getEmail());
            }
            baseMapper.updateById(tUserAdmin);
        }
    }

    public void editpwd(FirefoxInfo info, EditPwdDTO editPwdDTO){
        TUserAdmin tUserAdmin=baseMapper.selectByUserId(info.getAppId(), info.getTenantId(), info.getUid());
        if(tUserAdmin!=null) {
            BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
            Assert.isTrue(encode.matches(editPwdDTO.getPassword(), tUserAdmin.getPassword()), CodeEnum.USER_PASSWORD_ERROR);
            String password = encode.encode(editPwdDTO.getNewPassword());
            tUserAdmin.setPassword(password);
            tUserAdmin.setPassword02(AesUtil.encode(editPwdDTO.getNewPassword()));
            baseMapper.updateById(tUserAdmin);
        }
    }

    public boolean disableUserThirdId(long uid, String thirdId) {
        boolean ret = false;
        if(baseMapper.updateStatusByUidAndWxopenId(uid, thirdId, STATUS_DISABLE) > 0)
            ret = true;
        else
            log.warn("停用第三方openid失败 uid:" + uid + " openId:" + thirdId);
        return ret;
    }


}
