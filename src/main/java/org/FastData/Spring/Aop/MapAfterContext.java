package org.FastData.Spring.Aop;

import java.util.LinkedHashMap;

public class MapAfterContext {
    private String dbType ;

    private String sql;

    private String mapName;

    private LinkedHashMap<String,Object> param;

    private int aopType ;

    private Object result ;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public LinkedHashMap<String, Object> getParam() {
        return param;
    }

    public void setParam(LinkedHashMap<String, Object> param) {
        this.param = param;
    }

    public int getAopType() {
        return aopType;
    }

    public void setAopType(int aopType) {
        this.aopType = aopType;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
