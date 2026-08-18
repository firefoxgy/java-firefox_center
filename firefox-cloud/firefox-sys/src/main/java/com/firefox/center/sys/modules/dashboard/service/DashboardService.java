package com.firefox.center.sys.modules.dashboard.service;

import com.firefox.center.sys.common.base.BaseService;
import com.firefox.center.sys.common.util.DateUtil;
import com.firefox.center.common.Record;
import com.firefox.center.sys.modules.dashboard.mapper.DashboardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DashboardService extends BaseService<DashboardMapper, Record> {

    public Map<String, Object> selectAllData(Map params){
        params.put("start", params.get("start").toString()+" 00:00:00");
        params.put("end", params.get("end").toString()+" 23:59:59");
        Map<String, Object> map=baseMapper.selectAllData(params);
        int roomSum=Integer.valueOf(map.get("roomSum").toString());
        int visitorSum=Integer.valueOf(map.get("visitorSum").toString());
        int suggestSum=Integer.valueOf(map.get("suggestSum").toString());
        int repairSum=Integer.valueOf(map.get("repairSum").toString());
        long diff= DateUtil.getDiffDay(params.get("start").toString(), params.get("end").toString())+1;
        String roomDay="0", visitorDay="0", suggestDay="0", repairDay="0";
        if(roomSum!=0){
            if(roomSum%diff==0) {
                roomDay = String.valueOf(roomSum / diff);
            }else{
                roomDay = String.format("%.1f", roomSum*1.0/diff);
            }
        }
        if(visitorSum!=0){
            if(visitorSum%diff==0) {
                visitorDay = String.valueOf(visitorSum/diff);
            }else{
                visitorDay = String.format("%.1f", visitorSum*1.0/diff);
            }
        }
        if(suggestSum!=0){
            if(suggestSum%diff==0) {
                suggestDay = String.valueOf(suggestSum / diff);
            }else{
                suggestDay = String.format("%.1f", suggestSum*1.0/diff);
            }
        }
        if(repairSum!=0){
            if(repairSum%diff==0) {
                repairDay = String.valueOf(repairSum / diff);
            }else{
                repairDay = String.format("%.1f", repairSum*1.0/diff);
            }
        }
        map.put("roomDay", roomDay);
        map.put("visitorDay", visitorDay);
        map.put("suggestDay", suggestDay);
        map.put("repairDay", repairDay);
        return map;
    }

    public Map<String, Object> selectChartData(){
        List<Map<String, Object>> vChartData=baseMapper.selectVisitorCharData();
        List<Map<String, Object>> vListData=baseMapper.selectVisitorList();
        List<Map<String, Object>> RoomChartData=baseMapper.selectRoomCharData();
        List<Map<String, Object>> RoomListData=baseMapper.selectRoomList();
        Record result = new Record()
                .set("vChartData", vChartData)
                .set("vListData", vListData)
                .set("RoomChartData", RoomChartData)
                .set("RoomListData", RoomListData);
        return result.getColumns();
    }


}
