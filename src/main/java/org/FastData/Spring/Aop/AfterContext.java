package org.FastData.Spring.Aop;

import java.util.LinkedHashMap;

public class AfterContext {
    private String dbType;

    private String tableName;

    private String sql;

    private LinkedHashMap<String,Object> param;

    private Object result;

    private boolean isRead;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public LinkedHashMap<String, Object> getParam() {
        return param;
    }

    public void setParam(LinkedHashMap<String, Object> param) {
        this.param = param;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isWrite() {
        return isWrite;
    }

    public void setWrite(boolean write) {
        isWrite = write;
    }

    private boolean isWrite ;
}
