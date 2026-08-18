package com.firefox.center.credit.service;

import com.firefox.center.common.Record;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liran
 */
@Slf4j
@Setter
@Getter
@Component
@RequiredArgsConstructor
public class DataSourceSharding {

    @Resource
    private ShardingDataSource shardingDataSource;

    private static final String DB_QUERY_MASTER="master";
    private static final String DB_QUERY_SLAVE="slave1";

    public Connection getMasterConnection() {
        //原生JDBC开发
        DataSource dataSource = null;
        Connection conn = null;
        try {
            dataSource = shardingDataSource.getDataSourceMap().get(DB_QUERY_MASTER);
            conn = dataSource.getConnection();
            if (!conn.isClosed()) {
                System.out.println("数据库连接成功!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }


    public Record selectOne(String sql) {
        DataSource dataSource = shardingDataSource.getDataSourceMap().get(DB_QUERY_SLAVE);
        Connection conn = null;
        Statement stmt = null;
        Record record = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            record=getRecord(rs);
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
        return record;
    }

    public List<Map<String, Object>> selectList(String sql) {
        DataSource dataSource = shardingDataSource.getDataSourceMap().get(DB_QUERY_SLAVE);
        Connection conn = null;
        Statement stmt = null;
        List<Map<String, Object>> list = Lists.newArrayList();
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            list=getRecordList(rs);
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
        return list;
    }

    protected Record getRecord(ResultSet rs){
        Record record = null;
        try {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            if(rs.next()) {
                record = new Record();
                for (int i = 1; i <= columnCount; i++) {
                    record.set(md.getColumnName(i), rs.getObject(i));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return record;
    }

    protected List<Map<String, Object>> getRecordList(ResultSet rs){
        List<Map<String, Object>> list = Lists.newArrayList();
        Record record = null;
        try {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            while(rs.next()) {
                record = new Record();
                for (int i = 1; i <= columnCount; i++) {
                    record.set(md.getColumnName(i), rs.getObject(i));
                }
                list.add(record.getColumns());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


}
