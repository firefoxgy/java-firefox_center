package com.firefox.center.credit.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.underlying.common.rule.DataNode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于范围分表的 ActualDataNodes 动态刷新JOB
 *
 * @author qimok
 * @since 2020-09-07
 */
@Slf4j
@Component
public class ShardingTableRule {

    @Resource(name = "shardingDataSource")
    private DataSource dataSource;

    public void refreshActualDataNodes(List<DataNode> dataNodes) throws NoSuchFieldException, IllegalAccessException {
        String dynamicTableName = "t_credit_log";
        TableRule tableRule = null;
        ShardingDataSource shardingDataSource = (ShardingDataSource) dataSource;
        ShardingRule shardingRule = shardingDataSource.getRuntimeContext().getRule();
        tableRule = shardingRule.getTableRule(dynamicTableName);
        String dataSourceName = tableRule.getActualDataNodes().get(0).getDataSourceName();
        String logicTableName = tableRule.getLogicTable();
        assert tableRule != null;
        if (dataNodes.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        dynamicRefreshDatasource(dataSourceName, tableRule, dataNodes);
    }

    /**
     * 动态刷新数据源
     */
    private void dynamicRefreshDatasource(String dataSourceName, TableRule tableRule, List<DataNode> newDataNodes)
            throws NoSuchFieldException, IllegalAccessException {
        Set<String> actualTables = Sets.newHashSet();
        Map<DataNode, Integer> dataNodeIndexMap = Maps.newHashMap();
        AtomicInteger index = new AtomicInteger(0);
        newDataNodes.forEach(dataNode -> {
            actualTables.add(dataNode.getTableName());
            if (index.intValue() == 0) {
                dataNodeIndexMap.put(dataNode, 0);
            } else {
                dataNodeIndexMap.put(dataNode, index.intValue());
            }
            index.incrementAndGet();
        });
        // 动态刷新：actualDataNodesField
        Field actualDataNodesField = TableRule.class.getDeclaredField("actualDataNodes");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(actualDataNodesField, actualDataNodesField.getModifiers() & ~Modifier.FINAL);
        actualDataNodesField.setAccessible(true);
        actualDataNodesField.set(tableRule, newDataNodes);
        // 动态刷新：actualTablesField
        Field actualTablesField = TableRule.class.getDeclaredField("actualTables");
        actualTablesField.setAccessible(true);
        actualTablesField.set(tableRule, actualTables);
        // 动态刷新：dataNodeIndexMapField
        Field dataNodeIndexMapField = TableRule.class.getDeclaredField("dataNodeIndexMap");
        dataNodeIndexMapField.setAccessible(true);
        dataNodeIndexMapField.set(tableRule, dataNodeIndexMap);
        // 动态刷新：datasourceToTablesMapField
        Map<String, Collection<String>> datasourceToTablesMap = Maps.newHashMap();
        datasourceToTablesMap.put(dataSourceName, actualTables);
        Field datasourceToTablesMapField = TableRule.class.getDeclaredField("datasourceToTablesMap");
        datasourceToTablesMapField.setAccessible(true);
        datasourceToTablesMapField.set(tableRule, datasourceToTablesMap);
    }

}
