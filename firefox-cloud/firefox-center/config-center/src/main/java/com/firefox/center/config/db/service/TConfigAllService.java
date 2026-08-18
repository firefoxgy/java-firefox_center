package com.firefox.center.config.db.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.firefox.center.common.Record;
import com.firefox.center.common.enums.StatusEnum;
import com.firefox.center.common.kit.StrKit;
import com.firefox.center.config.db.mapper.TConfigAllMapper;
import com.firefox.center.config.db.model.TConfigAll;
import com.firefox.center.config.db.model.TConfigAllVersion;
import com.firefox.center.db.service.BaseService;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service()
@RequiredArgsConstructor
public class TConfigAllService extends BaseService<TConfigAllMapper, TConfigAll> {

    public static HashMap<String, Record> configMap = Maps.newLinkedHashMap();
    public static HashMap<String, Integer> configTypeMap = Maps.newLinkedHashMap();

    private final TConfigAllVersionService tConfigAllVersionService;

    public Record getConfByType(String type) {
        if (StrKit.isBlank(type)) {
            return null;
        }
        if(!configTypeMap.containsKey(type)){
            return getConfigMapByType(type);
        }
        int dbVersion=tConfigAllVersionService.selectVersionByType(type);
        if(dbVersion>configTypeMap.get(type)){
            configTypeMap.put(type, dbVersion);
            return getConfigMapByType(type);
        }else{
            if (configMap.containsKey(type)) {
                return configMap.get(type);
            } else{
                return getConfigMapByType(type);
            }
        }
    }

    public boolean initConfigMap() {
        HashMap<String, Integer> configTypeMap = Maps.newLinkedHashMap();
        List<TConfigAllVersion> typeList=tConfigAllVersionService.list();
        for(TConfigAllVersion configTypePO:typeList){
            configTypeMap.put(configTypePO.getType(), configTypePO.getVersion());
        }
        TConfigAllService.configTypeMap = configTypeMap;

        HashMap<String, Record> configMap = Maps.newLinkedHashMap();
        LambdaQueryWrapper<TConfigAll> query = Wrappers.lambdaQuery();
        query.eq(TConfigAll::getStatus, StatusEnum.ENABLE.getCode());
        List<TConfigAll> list = list(query);
        Record record=null;
        for (TConfigAll configAll : list) {
            if(!configMap.containsKey(configAll.getType())){
                record=new Record();
            }else{
                record= configMap.get(configAll.getType());
            }
            record.set(configAll.getConfKey(),configAll.getConfValue());
            configMap.put(configAll.getType(), record);
        }
        TConfigAllService.configMap = configMap;
        return true;
    }

    public Record getConfigMapByType(String type) {
        HashMap<String, Record> configMap = Maps.newLinkedHashMap();
        LambdaQueryWrapper<TConfigAll> query = Wrappers.lambdaQuery();
        query.eq(TConfigAll::getType, type);
        query.eq(TConfigAll::getStatus, StatusEnum.ENABLE.getCode());
        List<TConfigAll> list = list(query);
        Record record=null;
        for (TConfigAll configAll : list) {
            if(!configMap.containsKey(configAll.getType())){
                record=new Record();
            }else{
                record= configMap.get(configAll.getType());
            }
            record.set(configAll.getConfKey(),configAll.getConfValue());
            configMap.put(configAll.getType(), record);
        }
        TConfigAllService.configMap = configMap;
        return record;
    }

}
