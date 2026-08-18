package com.firefox.center.oauth.service;

import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.constants.Consts;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.exception.BusinessException;
import com.firefox.center.common.feign.UserFeignService;
import com.firefox.center.common.feign.pojo.ThirdDTO;
import com.firefox.center.common.kit.Assert;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.common.model.LoginThirdUser;
import com.firefox.center.config.feign.pojo.TTenantThirdConfDTO;
import com.firefox.center.oauth.auth.wx.util.WXUserInfoVO;
import com.firefox.center.oauth.auth.wx.util.WxUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: sujie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxService {

    private final UserFeignService userFeignService;

    public LoginThirdUser weChatLogin(TTenantThirdConfDTO tTenantThirdConfDTO, String phone, String code) {
        LoginThirdUser loginThirdUser=null;
        try {
            //根据code获取 access_token信息
            JSONObject jsonObject = WxUtil.getAccessToken(tTenantThirdConfDTO.getThirdAppId(), tTenantThirdConfDTO.getThirdSecretId(), code);
            Assert.isTrue(jsonObject.containsKey("access_token"), CodeEnum.WEIXIN_GET_ACCESSTOKEN_ERROR);
            String accessToken = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");

            // 判断用户是否注册过
            loginThirdUser = userFeignService.findThirdUser("", "", tTenantThirdConfDTO.getAppId(), tTenantThirdConfDTO.getTenantId(), Consts.grantType.WEIXIN, openid);
            if (loginThirdUser == null){
                // 开始用户注册
                // 根据token和openid获取用户的信息
                JSONObject userInfoJsonObject = WxUtil.getUserInfo(accessToken,openid);
                Assert.isTrue(userInfoJsonObject.containsKey("unionid"), CodeEnum.WEIXIN_USERINFO_QUERY_ERROR);
                WXUserInfoVO wxUserInfo = userInfoJsonObject.toJavaObject(WXUserInfoVO.class);
                ThirdDTO thirdDTO=ThirdDTO.builder()
                        .appId(tTenantThirdConfDTO.getAppId())
                        .tenantId(tTenantThirdConfDTO.getTenantId())
                        .phone(phone)
                        .authType(Consts.grantType.WEIXIN)
                        .thirdid(openid)
                        .thirdUnionId(wxUserInfo.getUnionid())
                        .nickname(wxUserInfo.getNickname())
                        .figureurl(wxUserInfo.getHeadimgurl())
                        .gender(wxUserInfo.getSex())
                        .country(wxUserInfo.getCountry())
                        .province(wxUserInfo.getProvince())
                        .city(wxUserInfo.getCity())
                        .build();
                loginThirdUser=userFeignService.regUserFromThird(thirdDTO);
            }

        } catch (Exception e) {
            throw new BusinessException("用户登录失败");
        }
        return loginThirdUser;
    }

}
