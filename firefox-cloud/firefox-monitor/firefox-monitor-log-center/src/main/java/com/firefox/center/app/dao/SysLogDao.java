package com.firefox.center.app.dao;

import com.firefox.center.app.db.model.SysLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * @Author: sujie
 */
@Component
public interface SysLogDao extends ElasticsearchRepository<SysLog, String> {

}