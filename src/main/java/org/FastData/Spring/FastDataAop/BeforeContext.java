package org.FastData.Spring.FastDataAop;

import java.util.LinkedHashMap;

public class BeforeContext {
    private String dbType;

    private String tableName;

    private String sql;

    private LinkedHashMap<String,Object> param;

    private boolean isRead;

    private boolean isWrite;

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    private Object model;

    public int getAopType() {
        return aopType;
    }

    void setAopType(int aopType) {
        this.aopType = aopType;
    }

    private  int aopType;

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

    public boolean getIsRead() {
        return isRead;
    }

    void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean getIsWrite() {
        return isWrite;
    }

    void setIsWrite(boolean isWrite) {
        this.isWrite = isWrite;
    }
}
