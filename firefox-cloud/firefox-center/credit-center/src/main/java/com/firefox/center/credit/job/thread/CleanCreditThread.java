package com.firefox.center.credit.job.thread;

import com.firefox.center.common.utils.DateUtil;
import com.firefox.center.credit.db.model.TCreditJob;
import com.firefox.center.credit.db.service.TCreditJobService;
import com.firefox.center.credit.db.service.TCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.firefox.center.common.email.MailCommonService;

import java.util.Date;

/**
 * @author liran
 */
@Component
@RequiredArgsConstructor
public class CleanCreditThread {

    private final TCreditService tCreditService;
    private final TCreditJobService tCreditJobService;
    private final MailCommonService mailCommonService;

    private static final String CREDIT_DAY="day";
    private static final String CREDIT_WEEK="week";
    private static final String CREDIT_MONTH="month";
    private static final String CREDIT_SEASON="season";
    private static final String CREDIT_YEAR="year";

    public void cleanCredit(String type){
        if(type.equals(CREDIT_DAY)){
            cleanCreditDay();
        }else if(type.equals(CREDIT_WEEK)){
            cleanCreditWeek();
        }else if(type.equals(CREDIT_MONTH)){
            cleanCreditMonth();
        }else if(type.equals(CREDIT_SEASON)){
            cleanCreditSeason();
        }else if(type.equals(CREDIT_YEAR)){
            cleanCreditYear();
        }
    }

    public void cleanCreditDay(){
        tCreditService.cleanCredit(CREDIT_DAY);
        saveJob(CREDIT_DAY);
        sendMail("当日");
    }

    public void cleanCreditWeek(){
        tCreditService.cleanCredit(CREDIT_WEEK);
        saveJob(CREDIT_WEEK);
        sendMail("本周");
    }

    public void cleanCreditMonth(){
        tCreditService.cleanCredit(CREDIT_MONTH);
        saveJob(CREDIT_MONTH);
        sendMail("本月");
    }

    public void cleanCreditSeason(){
        tCreditService.cleanCredit(CREDIT_SEASON);
        saveJob(CREDIT_SEASON);
        sendMail("本季");
    }

    public void cleanCreditYear(){
        tCreditService.cleanCredit(CREDIT_YEAR);
        saveJob(CREDIT_YEAR);
        sendMail("本年");
    }

    public void saveJob(String type){
        Date date = new Date();
        TCreditJob tCreditJob = TCreditJob.builder()
                .type(type)
                .detail("已执行")
                .execDate(date)
                .createTime(date)
                .build();
        tCreditJobService.save(tCreditJob);
    }

    protected void sendMail(String type){
        String mail="153282687@qq.com";
        String title="积分排行清零任务";
        String content="【"+type+"】积分排行清零任务已执行，执行时间"+ DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMDHMS);
        mailCommonService.sendSimpleMail(mail, title, content);
    }

}
