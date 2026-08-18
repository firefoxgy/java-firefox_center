package com.firefox.center.credit.job;

import com.firefox.center.common.utils.DateUtil;
import com.firefox.center.credit.job.thread.CleanCreditThread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Date;

/**
 * @author liran
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreditJob {

    private final CleanCreditThread cleanCreditThread;

    /**
     * 当天积分清零
     * @return
     * @throws SQLException
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cfWdtRdCalculateTask() {

        Date date = new Date();
        String dateToday=DateUtil.format(date, DateUtil.DATE_FORMAT_YMD);
        String dateYearYMD=DateUtil.format(DateUtil.getYearStart(), DateUtil.DATE_FORMAT_YMD);
        String dateSeasonYMD=DateUtil.format(DateUtil.getSeasonStart(), DateUtil.DATE_FORMAT_YMD);
        String dateMonthYMD=DateUtil.format(DateUtil.getMonthStart(), DateUtil.DATE_FORMAT_YMD);
        String dateWeekYMD=DateUtil.format(DateUtil.getWeekStart(), DateUtil.DATE_FORMAT_YMD);
        //1年的开始
        if(dateToday.equals(dateYearYMD)){
            System.out.println("执行年任务");
            cleanCreditThread.cleanCreditYear();
        }else if(dateToday.equals(dateSeasonYMD)){
            System.out.println("执行季任务");
            cleanCreditThread.cleanCreditSeason();
        }else if(dateToday.equals(dateMonthYMD)){
            System.out.println("执行月任务");
            cleanCreditThread.cleanCreditMonth();
        }else if(dateToday.equals(dateWeekYMD)){
            System.out.println("执行周任务");
            cleanCreditThread.cleanCreditWeek();
        }else{
            System.out.println("执行天任务");
            cleanCreditThread.cleanCreditDay();
        }

    }


}
