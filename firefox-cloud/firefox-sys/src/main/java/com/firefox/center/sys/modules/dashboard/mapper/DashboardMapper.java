package com.firefox.center.sys.modules.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.firefox.center.common.Record;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DashboardMapper extends BaseMapper<Record> {

    Map<String, Object> selectAllData(@Param("params") Map<String, Object> map);
    List<Map<String, Object>> selectVisitorCharData();
    List<Map<String, Object>> selectVisitorList();
    List<Map<String, Object>> selectRoomCharData();
    List<Map<String, Object>> selectRoomList();
}
