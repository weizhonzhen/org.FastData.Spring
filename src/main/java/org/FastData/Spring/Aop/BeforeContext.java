package org.FastData.Spring.Aop;

import org.apache.poi.ss.usermodel.RichTextString;

import java.util.LinkedHashMap;

public class BeforeContext {
    private String dbType;

    private String tableName;

    private String sql;

    private LinkedHashMap<String,Object> param;

    private boolean isRead;

    private boolean isWrite;

    public int getAopType() {
        return aopType;
    }

    public void setAopType(int aopType) {
        this.aopType = aopType;
    }

    private  int aopType;

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

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean getIsWrite() {
        return isWrite;
    }

    public void setIsWrite(boolean isWrite) {
        this.isWrite = isWrite;
    }
}
