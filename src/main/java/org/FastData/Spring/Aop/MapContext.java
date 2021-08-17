package org.FastData.Spring.Aop;

import java.util.LinkedHashMap;

public class MapContext {
    private String dbType ;

    private String sql ;

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

    private String mapName ;

    private LinkedHashMap<String,Object> param;
}
