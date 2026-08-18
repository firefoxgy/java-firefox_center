package com.firefox.center.oauth.db.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.firefox.center.db.mapper.SuperMapper;
import com.firefox.center.oauth.db.model.OauthClientDetails;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: sujie
 */
public interface OauthClientDetailsMapper extends SuperMapper<OauthClientDetails> {

    List<OauthClientDetails> findList(Page<OauthClientDetails> page, @Param("params") Map<String, Object> params );

}
