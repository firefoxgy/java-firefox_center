package com.firefox.center.credit.feign;

import com.firefox.center.common.utils.IdGen;
import com.firefox.center.credit.db.model.TCredit;
import com.firefox.center.credit.db.model.TCreditLog;
import com.firefox.center.credit.db.model.TCreditUserInfo;
import com.firefox.center.credit.db.service.TCreditLogService;
import com.firefox.center.credit.db.service.TCreditService;
import com.firefox.center.credit.db.service.TCreditUserInfoService;
import com.firefox.center.credit.feign.pojo.FeignTCredit;
import com.firefox.center.credit.feign.pojo.FeignTCreditLog;
import com.firefox.center.credit.feign.pojo.FeignTUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/4/27 16:30
 */
@RestController
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditFeignService {

    private final TCreditService tCreditService;
    private final TCreditLogService tCreditLogService;
    private final TCreditUserInfoService tCreditUserInfoService;

    @Override
    public boolean bindUid(FeignTCredit feignTCredit) {
        TCredit tCredit=tCreditService.queryRecordBySid(feignTCredit.getAppId(), feignTCredit.getTenantId(), feignTCredit.getSid());
        if(tCredit!=null){
            tCredit.setUid(feignTCredit.getUid());
            tCreditService.updateById(tCredit);
        }
        return true;
    }

    @Override
    public boolean unbindUid(FeignTCredit feignTCredit) {
        TCredit tCredit=tCreditService.queryRecordBySid(feignTCredit.getAppId(), feignTCredit.getTenantId(), feignTCredit.getSid());
        if(tCredit!=null){
            tCredit.setUid(0L);
            tCreditService.updateById(tCredit);
        }
        return true;
    }

    @Override
    public boolean saveCredit(FeignTCredit feignTCredit) {
        TCredit tCredit = new TCredit();
        BeanUtils.copyProperties(feignTCredit, tCredit);
        tCredit.setId(IdGen.getId());
        tCreditService.save(tCredit);
        return true;
    }

    @Override
    public boolean saveCreditLog(FeignTCreditLog feignTCreditLog) {
        TCreditLog tCreditLog = new TCreditLog();
        BeanUtils.copyProperties(feignTCreditLog, tCreditLog);
        tCreditLog.setId(IdGen.getId());
        tCreditLogService.save(tCreditLog);
        return true;
    }

    @Override
    public boolean saveUserInfo(FeignTUserInfo feignTUserInfo) {
        TCreditUserInfo tCreditUserInfo = new TCreditUserInfo();
        TCreditUserInfo dbTCreditUserInfo=tCreditUserInfoService.selectRecord(feignTUserInfo.getAppId(), feignTUserInfo.getTenantId(), feignTUserInfo.getUid(), feignTUserInfo.getType());
        if(dbTCreditUserInfo==null){
            BeanUtils.copyProperties(feignTUserInfo, tCreditUserInfo);
            tCreditUserInfo.setId(IdGen.getId());
            tCreditUserInfoService.save(tCreditUserInfo);
        }else{
            dbTCreditUserInfo.setUsername(feignTUserInfo.getUsername());
            dbTCreditUserInfo.setNickname(feignTUserInfo.getNickname());
            dbTCreditUserInfo.setHeaderImg(feignTUserInfo.getHeaderImg());
            dbTCreditUserInfo.setGender(feignTUserInfo.getGender());
            tCreditUserInfoService.updateById(dbTCreditUserInfo);
        }
        return true;
    }
}
