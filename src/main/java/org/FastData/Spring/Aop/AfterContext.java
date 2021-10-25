package org.FastData.Spring.Aop;

import java.util.LinkedHashMap;

public class AfterContext {
    private String dbType;

    private String tableName;

    private String sql;

    private LinkedHashMap<String,Object> param;

    private Object result;

    private boolean isRead;

    private boolean isWrite ;

    private  int aopType;

    private Object model;

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    public int getAopType() {
        return aopType;
    }

    void setAopType(int aopType) {
        this.aopType = aopType;
    }

    public String getDbType() {
        return dbType;
    }

    void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getTableName() {
        return tableName;
    }

    void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSql() {
        return sql;
    }

    void setSql(String sql) {
        this.sql = sql;
    }

    public LinkedHashMap<String, Object> getParam() {
        return param;
    }

    void setParam(LinkedHashMap<String, Object> param) {
        this.param = param;
    }

    public Object getResult() {
        return result;
    }

    void setResult(Object result) {
        this.result = result;
    }

    public boolean isRead() {
        return isRead;
    }

    void setRead(boolean read) {
        isRead = read;
    }

    public boolean isWrite() {
        return isWrite;
    }

    void setWrite(boolean write) {
        isWrite = write;
    }
}
