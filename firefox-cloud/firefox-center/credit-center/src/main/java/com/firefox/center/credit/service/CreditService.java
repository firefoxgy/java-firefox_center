package com.firefox.center.credit.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.common.Record;
import com.firefox.center.common.entity.FirefoxInfo;
import com.firefox.center.common.utils.IdGen;
import com.firefox.center.credit.config.sharding.CommonShardDataBase;
import com.firefox.center.credit.db.model.TCredit;
import com.firefox.center.credit.db.model.TCreditBehavior;
import com.firefox.center.credit.db.model.TCreditLog;
import com.firefox.center.credit.db.model.TCreditType;
import com.firefox.center.credit.db.service.TCreditLogService;
import com.firefox.center.credit.db.service.TCreditService;
import com.firefox.center.credit.pojo.dto.CreditRegDTO;
import com.firefox.center.credit.pojo.vo.CreditRankVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final DataSourceSharding sharding;
    private final TCreditService tCreditService;
    private final TCreditLogService tCreditLogService;

    private final ShardingTable shardingTable;

    private static final String DB_MASTER="master";
    private static final String DB_SCHAME="firefox_credit";
    private static final String LOGICTABLE="t_credit_log";

    @Transactional
    public void reg(FirefoxInfo info, TCreditType tCreditType, TCreditBehavior tCreditBehavior, CreditRegDTO creditRegDTO){

        //没有表则先创建表
        LocalDateTime datetime=LocalDateTime.now();
        String timeValue = datetime.format(DateTimeFormatter.ofPattern(CommonShardDataBase.DB_SHARD_TIME_FORMAT));
        String lastTable=LOGICTABLE + "_" + timeValue;
        shardingTable.checkTable(DB_MASTER, DB_SCHAME, LOGICTABLE, lastTable);

        //log入库
        TCreditLog tCreditLog=TCreditLog.builder()
                .id(IdGen.getId())
                .appId(info.getAppId())
                .tenantId(info.getTenantId())
                .uid(info.getUid())
                .sid(info.getSid())
                .typeNo(creditRegDTO.getTypeNo())
                .typeName(tCreditType.getName())
                .behaviorNo(creditRegDTO.getBehaviorNo())
                .behaviorName(tCreditBehavior.getName())
                .num(tCreditBehavior.getNum())
                .createTime(new Date())
                .build();
        tCreditLogService.save(tCreditLog);

        TCredit tCredit=tCreditService.queryRecord(info.getAppId(), info.getTenantId(), info.getUid(), info.getSid());
        if(tCredit==null){
            Date date=new Date();
            tCredit=TCredit.builder()
                    .id(IdGen.getId())
                    .appId(info.getAppId())
                    .tenantId(info.getTenantId())
                    .uid(info.getUid())
                    .sid(info.getSid())
                    .creditDay(tCreditBehavior.getNum())
                    .creditWeek(tCreditBehavior.getNum())
                    .creditMonth(tCreditBehavior.getNum())
                    .creditSeason(tCreditBehavior.getNum())
                    .creditYear(tCreditBehavior.getNum())
                    .createTime(date)
                    .updateTime(date)
                    .build();
            tCreditService.save(tCredit);
        }else{
            tCredit.setCreditDay(tCredit.getCreditDay()+tCreditBehavior.getNum());
            tCredit.setCreditWeek(tCredit.getCreditWeek()+tCreditBehavior.getNum());
            tCredit.setCreditMonth(tCredit.getCreditMonth()+tCreditBehavior.getNum());
            tCredit.setCreditSeason(tCredit.getCreditSeason()+tCreditBehavior.getNum());
            tCredit.setCreditYear(tCredit.getCreditYear()+tCreditBehavior.getNum());
            tCredit.setUpdateTime(new Date());
            tCreditService.updateById(tCredit);
        }

    }

    public Record getRank(FirefoxInfo info){
        String[] RANK_TYPE = {"day", "week", "month", "season", "year"};
        String userId="";
        if(info.getUid()!=0){
            userId="u_"+info.getUid();
        }else{
            if(info.getSid()!=0){
                userId="s_"+info.getSid();
            }
        }
        Record r= new Record();
        Integer credit=0,rank=0;
        for(String type:RANK_TYPE){
            String sql="select * from ( " +
                    "select id,credit_"+type+" credit, " +
                    "CASE " +
                    "WHEN @prevRank = credit_"+type+" THEN @curRank " +
                    "WHEN @prevRank := credit_"+type+ " THEN @curRank := @curRank + 1 " +
                    "END AS rank " +
                    "from( " +
                    "select concat('u_',uid) id,sum(credit_"+type+") credit_"+type+" from t_credit " +
                    "where app_id='"+info.getAppId()+"' and tenant_id= "+info.getTenantId()+" and uid<>0 " +
                    "group by uid " +
                    "union " +
                    "select concat('s_',sid) id,credit_"+type+" from t_credit " +
                    "where app_id='"+info.getAppId()+ "' and tenant_id="+info.getTenantId()+" and uid=0 " +
                    ") a1, " +
                    "(SELECT @curRank :=0, @prevRank := NULL) r " +
                    "ORDER BY credit_"+type+" desc,id asc " +
                    ") a2 " +
                    "where id='"+userId+"'";
            Record record=sharding.selectOne(sql);
            if(record!=null){
                credit=record.getInt("credit");
                rank=record.getFloat("rank").intValue();
            }
            if("day".equals(type)){
                r.set("credit_"+type, credit);
                r.set("rank_"+type, rank);
            }else if("week".equals(type)){
                r.set("credit_"+type, credit);
                r.set("rank_"+type, rank);
            }else if("month".equals(type)){
                r.set("credit_"+type, credit);
                r.set("rank_"+type, rank);
            }else if("season".equals(type)){
                r.set("credit_"+type, credit);
                r.set("rank_"+type, rank);
            }else if("year".equals(type)){
                r.set("credit_"+type, credit);
                r.set("rank_"+type, rank);
            }
        }
        return r;
    }

    public IPage<CreditRankVO> getRankPage(FirefoxInfo info, IPage<TCredit> pageParam, String type){
        String sqlTotal="select count(id) cnum from(" +
                            "select concat( 'u_', uid ) id " +
                            "from t_credit " +
                            "where app_id = '"+info.getAppId()+"' and tenant_id= "+info.getTenantId()+" and uid<>0 " +
                            "GROUP BY uid " +
                            "UNION " +
                            "SELECT concat( 's_', sid ) id " +
                            "FROM t_credit " +
                            "WHERE app_id = '"+info.getAppId()+ "' and tenant_id="+info.getTenantId()+" and uid=0 " +
                        ") t ";
        Record record=sharding.selectOne(sqlTotal);

        long start=(pageParam.getCurrent()-1)*pageParam.getSize();
        String sql="select * from ( " +
                        "select id,credit_"+type+" credit, " +
                        "CASE " +
                        "WHEN @prevRank = credit_"+type+" THEN @curRank " +
                        "WHEN @prevRank := credit_"+type+ " THEN @curRank := @curRank + 1 " +
                        "END AS rank, type, username, nickname, header_img, gender " +
                        "from( " +
                            "select concat('u_',t.uid) id,sum(t.credit_"+type+") credit_"+type+", tu.type, tu.username, tu.nickname, tu.header_img, tu.gender " +
                            "from t_credit t " +
                            "left join t_credit_userinfo tu on tu.type='app' and t.app_id=tu.app_id and t.tenant_id=tu.tenant_id and t.uid=tu.uid " +
                            "where t.app_id='"+info.getAppId()+"' and t.tenant_id= "+info.getTenantId()+" and t.uid<>0 " +
                            "group by t.uid, tu.type, tu.username, tu.nickname, tu.header_img, tu.gender " +
                            "union " +
                            "select concat('s_',t.sid) id,t.credit_"+type+", tu.type, username, nickname, header_img, gender " +
                            "from t_credit t " +
                            "left join t_credit_userinfo tu on tu.type='third' and t.app_id=tu.app_id and t.tenant_id=tu.tenant_id and t.sid=tu.uid " +
                            "where t.app_id='"+info.getAppId()+ "' and t.tenant_id="+info.getTenantId()+" and t.uid=0 " +
                        ") a1, " +
                        "(SELECT @curRank :=0, @prevRank := NULL) r " +
                        "ORDER BY credit_"+type+" desc,id asc " +
                    ") a2 " +
                    "limit "+start+","+pageParam.getSize();
        List<Map<String, Object>> listRecord=sharding.selectList(sql);
        List<CreditRankVO> list= Lists.newArrayList();
        if(listRecord.size()!=0){
            CreditRankVO creditRankVO = null;
            for(Map<String, Object> map : listRecord){
                creditRankVO = new CreditRankVO();
                String id=getVal(map.get("id"));
                if(id.indexOf("u_")!=-1){
                    creditRankVO.setUid(Long.valueOf(id.replace("u_", "")));
                }else if(id.indexOf("s_")!=-1){
                    creditRankVO.setUid(Long.valueOf(id.replace("s_", "")));
                }
                creditRankVO.setCredit(Integer.valueOf(getVal(map.get("credit"))));
                creditRankVO.setRank(Float.valueOf(getVal(map.get("rank"))).intValue());
                creditRankVO.setType(getVal(map.get("type")));
                creditRankVO.setUsername(getVal(map.get("username")));
                creditRankVO.setNickname(getVal(map.get("nickname")));
                creditRankVO.setHeaderImg(getVal(map.get("header_img")));
                if(!"".equals(getVal(map.get("gender")))){
                    creditRankVO.setGender(Integer.valueOf(getVal(map.get("gender"))));
                }
                list.add(creditRankVO);
            }
        }
        Page<CreditRankVO> page = new Page<CreditRankVO>(pageParam.getCurrent(), pageParam.getSize());
        page.setTotal(record.getInt("cnum"));
        page.setRecords(list);
        return page;
    }

    protected String getVal(Object obj){
        return obj==null?"":obj.toString();
    }
}
