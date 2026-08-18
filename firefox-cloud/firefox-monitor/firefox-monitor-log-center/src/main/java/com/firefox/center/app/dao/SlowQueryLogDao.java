package com.firefox.center.app.dao;

import com.firefox.center.app.db.model.SlowQueryLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * @Author: sujie
 */
@Component
public interface SlowQueryLogDao extends ElasticsearchRepository<SlowQueryLog, String> {

}