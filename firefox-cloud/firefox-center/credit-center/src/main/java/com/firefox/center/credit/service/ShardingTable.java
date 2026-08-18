package com.firefox.center.credit.service;

import com.firefox.center.common.utils.DateUtil;
import com.firefox.center.credit.property.FirefoxProperties;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.underlying.common.rule.DataNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.support.StandardServletEnvironment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author liran
 */
@Slf4j
@Setter
@Getter
@Component
@RequiredArgsConstructor
public class ShardingTable {

    private final FirefoxProperties firefox;
    private final ShardingDataSource shardingDataSource;
    private final StandardServletEnvironment env;
    private final ShardingTableRule shardingTableRule;

    @PostConstruct
    public void init() {
        ShardingRule rule = shardingDataSource.getRuntimeContext().getRule();
        Collection<TableRule> tableRules = rule.getTableRules();
        for (TableRule tableRule : tableRules) {
            String logicTable = tableRule.getLogicTable();
            List<DataNode> actualDataNodes = tableRule.getActualDataNodes();
            for (DataNode actualDataNode : actualDataNodes) {
                createTable(actualDataNode.getDataSourceName(), logicTable, actualDataNode.getTableName());
            }
        }

    }

    public void checkTable(String db, String dbschame, String table, String lastTable) {
        String msg = " create table: " + lastTable + "  origin table: " + table + "  db: " + db;
        String sql = "SHOW CREATE TABLE " + table;
        String existSql = "select * from information_schema.tables where table_name ='" + table + "' AND table_schema =";
        DataSource dataSource = shardingDataSource.getDataSourceMap().get(db);
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();

            ResultSet resTable = stmt.executeQuery(sql);
            Assert.isTrue(resTable.next(), msg + "初始化表不存在");
            String existTableName = resTable.getString(1);
            String createSqlOrigin = resTable.getString(2);

            existSql = existSql + "'" + dbschame + "'";
            String existSqlNew = StringUtils.replaceOnce(existSql, existTableName, lastTable);
            ResultSet executeQuery = stmt.executeQuery(existSqlNew);
            if (!executeQuery.next()) {
                String creatsql = StringUtils.replaceOnce(createSqlOrigin, existTableName, lastTable);
                if (0 == stmt.executeUpdate(creatsql)) {
                    shardingTableRule.refreshActualDataNodes(getDataNode(db, table, firefox.getSharding().getStart()));
                }
            }
        } catch (Exception e) {
            log.error("create  table fail  error : {} ", e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("SQLException", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("SQLException", e);
                }
            }
        }
    }

    public void createTable(String db, String table, String lastTable) {
        DataSource dataSource = shardingDataSource.getDataSourceMap().get(db);
        String sql = "SHOW CREATE TABLE " + table;
        String existSql = "select * from information_schema.tables where table_name ='" + table + "' AND table_schema =";
        doCreate(dataSource, sql, existSql, db, table, lastTable);
    }


    private void doCreate(DataSource dataSource, String sql, String existSql, String db, String table, String lastTable) {
        String msg = " create table: " + lastTable + "  origin table: " + table + "  db: " + db;
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            ResultSet database = stmt.executeQuery("select database()");
            Assert.isTrue(database.next(), msg + "database 不存在");
            String dbschame = database.getString(1);
            existSql = existSql + "'" + dbschame + "'";
            ResultSet resultSet = stmt.executeQuery(existSql);
            Assert.isTrue(resultSet.next(), msg + "初始化表不存在");

            ResultSet resTable = stmt.executeQuery(sql);
            Assert.isTrue(resTable.next(), msg + "初始化表不存在");
            String existTableName = resTable.getString(1);
            String createSqlOrigin = resTable.getString(2);
            // log.info(existTableName, createSqlOrigin);

            String existSqlNew = StringUtils.replaceOnce(existSql, existTableName, lastTable);
            ResultSet executeQuery = stmt.executeQuery(existSqlNew);


            if (executeQuery.next()) {
                log.info("table exist : " + msg);
            } else {
                String creatsql = StringUtils.replaceOnce(createSqlOrigin, existTableName, lastTable);
                if (0 == stmt.executeUpdate(creatsql)) {
                    shardingTableRule.refreshActualDataNodes(getDataNode(db, table, firefox.getSharding().getStart()));
                    log.info(msg + " success ！");
                } else {
                    log.error(msg + " fail ！");
                }
            }
        } catch (Exception e) {
            log.error("create  table fail  error : {} ", e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("SQLException", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("SQLException", e);
                }
            }
        }
    }

    protected List<DataNode> getDataNode(String db, String logicTable, String start){
        String pattern="yyyyMM";
        List<DataNode> list = Lists.newArrayList();
        list.add(new DataNode(db+"."+logicTable+"_"+start));
        Date startDate= DateUtil.add(DateUtil.format(start, pattern), Calendar.MONTH, 1);
        Date now = new Date();
        while(!isSameYM(startDate, now)){
            list.add(new DataNode(db+"."+logicTable+"_"+DateUtil.formatDate(startDate, pattern)));
            startDate=DateUtil.add(startDate, Calendar.MONTH, 1);
        }
        list.add(new DataNode(db+"."+logicTable+"_"+DateUtil.formatDate(startDate, pattern)));
        return list;
    }

    protected boolean isSameYM(Date date1, Date date2) {
        try {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
            boolean isSameMonth = cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
            boolean isSameDate = isSameYear && isSameMonth;
            return isSameDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 定时建表
     *
     * @return
     * @throws SQLException
     */
    // @Scheduled(cron = "0/60 * * * * ?")
    public void cfWdtRdCalculateTask() throws SQLException {
        init();
    }


}
