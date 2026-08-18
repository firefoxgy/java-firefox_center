package com.firefox.center.common;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

/**
 * author: sujie
 * date: 2020-06-15
 */
public class Record implements Serializable {
    private static final long serialVersionUID = 905784513600884082L;
    private Map<String, Object> columns;

    public Record() {
    }

    void setColumnsMap(Map<String, Object> columns) {
        this.columns = columns;
    }

    public Map<String, Object> getColumns() {
        if (this.columns == null) {
           this.columns = new HashMap<String, Object>();
        }
        return this.columns;
    }

    public Record setColumns(Map<String, Object> columns) {
        this.getColumns().putAll(columns);
        return this;
    }

    public Record remove(String column) {
        this.getColumns().remove(column);
        return this;
    }

    public Record remove(String... columns) {
        if (columns != null) {
            String[] var2 = columns;
            int var3 = columns.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String c = var2[var4];
                this.getColumns().remove(c);
            }
        }

        return this;
    }

    public Record removeNullValueColumns() {
        Iterator it = this.getColumns().entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry<String, Object> e = (Map.Entry)it.next();
            if (e.getValue() == null) {
                it.remove();
            }
        }

        return this;
    }

    public Record keep(String... columns) {
        if (columns != null && columns.length > 0) {
            Map<String, Object> newColumns = new HashMap(columns.length);
            String[] var3 = columns;
            int var4 = columns.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String c = var3[var5];
                if (this.getColumns().containsKey(c)) {
                    newColumns.put(c, this.getColumns().get(c));
                }
            }

            this.getColumns().clear();
            this.getColumns().putAll(newColumns);
        } else {
            this.getColumns().clear();
        }

        return this;
    }

    public Record keep(String column) {
        if (this.getColumns().containsKey(column)) {
            Object keepIt = this.getColumns().get(column);
            this.getColumns().clear();
            this.getColumns().put(column, keepIt);
        } else {
            this.getColumns().clear();
        }

        return this;
    }

    public Record clear() {
        this.getColumns().clear();
        return this;
    }

    public Record set(String column, Object value) {
        this.getColumns().put(column, value);
        return this;
    }

    public Record set(String column, Record record) {
        record=record==null?new Record():record;
        this.getColumns().put(column, record.getColumns());
        return this;
    }

    public Record set(String column, List<Record> list) {
        List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();
        if(list!=null){
            for(Record record:list){
                recordList.add(record.getColumns());
            }
            this.getColumns().put(column, recordList);
        }else{
            this.getColumns().put(column, list);
        }
        return this;
    }

    public Object get(String column) {
        return this.getColumns().get(column);
    }

    public Object get(String column, Object defaultValue) {
        Object result = this.getColumns().get(column);
        return result != null ? result : defaultValue;
    }

    public Object getObject(String column) {
        return this.getColumns().get(column);
    }

    public Object getObject(String column, Object defaultValue) {
        Object result = this.getColumns().get(column);
        return result != null ? result : defaultValue;
    }

    public String getStr(String column) {
        Object s = this.getColumns().get(column);
        return s != null ? s.toString() : null;
    }

    public Integer getInt(String column) {
        return Integer.valueOf(this.getStr(column));
    }

    public Long getLong(String column) {
        return Long.valueOf(this.getStr(column));
    }

    public BigInteger getBigInteger(String column) {
        return (BigInteger)this.getColumns().get(column);
    }

    public Date getDate(String column) {
        return (Date)this.getColumns().get(column);
    }

    public Time getTime(String column) {
        return (Time)this.getColumns().get(column);
    }

    public Timestamp getTimestamp(String column) {
        return (Timestamp)this.getColumns().get(column);
    }

    public Double getDouble(String column) {
        return Double.valueOf(this.getStr(column));
    }

    public Float getFloat(String column) {
        return Float.valueOf(this.getStr(column));
    }

    public Short getShort(String column) {
        Number n = this.getNumber(column);
        return n != null ? n.shortValue() : null;
    }

    public Byte getByte(String column) {
        Number n = this.getNumber(column);
        return n != null ? n.byteValue() : null;
    }

    public Boolean getBoolean(String column) {
        return (Boolean)this.getColumns().get(column);
    }

    public BigDecimal getBigDecimal(String column) {
        return (BigDecimal)this.getColumns().get(column);
    }

    public byte[] getBytes(String column) {
        return (byte[])((byte[])this.getColumns().get(column));
    }

    public Number getNumber(String column) {
        return (Number)this.getColumns().get(column);
    }

    public String toString() {
        if (this.columns == null) {
            return "{}";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            boolean first = true;

            Map.Entry e;
            Object value;
            for(Iterator var3 = this.getColumns().entrySet().iterator(); var3.hasNext(); sb.append((String)e.getKey()).append(':').append(value)) {
                e = (Map.Entry)var3.next();
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }

                value = e.getValue();
                if (value != null) {
                    value = value.toString();
                }
            }

            sb.append('}');
            return sb.toString();
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof Record)) {
            return false;
        } else {
            return o == this ? true : this.getColumns().equals(((Record)o).getColumns());
        }
    }

    public int hashCode() {
        return this.getColumns().hashCode();
    }

    public String[] getColumnNames() {
        Set<String> attrNameSet = this.getColumns().keySet();
        return (String[])attrNameSet.toArray(new String[attrNameSet.size()]);
    }

    public Object[] getColumnValues() {
        Collection<Object> attrValueCollection = this.getColumns().values();
        return attrValueCollection.toArray(new Object[attrValueCollection.size()]);
    }

    public String toJson() {
        return JSON.toJSONString(this.getColumns());
    }
}