package com.firefox.center.credit.config.sharding;

import com.firefox.center.common.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;

/**
 * @author lr
 * 分表的自定义规则类(精确)
 */


public class DefaultTableShardingAlgorithm extends CommonShardDataBase implements PreciseShardingAlgorithm<Date> {

    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Date> preciseShardingValue) {

        //分表：t_credit_log_202101, t_credit_log_202102....

        Date createTime = preciseShardingValue.getValue();
        String timeValue = DateUtil.format(createTime, DB_SHARD_TIME_FORMAT);
        String columnName = preciseShardingValue.getColumnName();
        // 需要分库的逻辑表
        String table = preciseShardingValue.getLogicTableName();
        if (StringUtils.isBlank(timeValue)) {
            throw new UnsupportedOperationException(columnName + ":列，分表精确分片值为NULL;");
        }
        for (String each : collection) {
            if (each.startsWith(table)) {
                String lastTable=table + "_" + timeValue;
                return lastTable;
            }
        }
        return table;
    }
}