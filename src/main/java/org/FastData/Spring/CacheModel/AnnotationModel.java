package org.FastData.Spring.CacheModel;

import java.util.LinkedHashMap;

public class AnnotationModel {
    private LinkedHashMap<Integer,String> param;
    private String sql;
    private boolean isWrite;
    private boolean isList;
    private Class<?> type;
    private boolean isMap;
    private String dbKey;
    private boolean paramIsMap;
    private boolean isPage;
    public Class<?> pageType;
    public boolean isXml;

    public boolean isXml() {
        return isXml;
    }

    public void setXml(boolean xml) {
        isXml = xml;
    }

    public Class<?> getPageType() {
        return pageType;
    }

    public void setPageType(Class<?> pageType) {
        this.pageType = pageType;
    }

    public boolean isPage() {
        return isPage;
    }

    public void setPage(boolean page) {
        isPage = page;
    }

    public boolean isParamIsMap() {
        return paramIsMap;
    }

    public void setParamIsMap(boolean paramIsMap) {
        this.paramIsMap = paramIsMap;
    }

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public boolean isMap() {
        return isMap;
    }

    public void setMap(boolean map) {
        isMap = map;
    }

    private boolean  isVoid;

    public boolean isVoid() {
        return isVoid;
    }

    public void setVoid(boolean aVoid) {
        isVoid = aVoid;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isWrite() {
        return isWrite;
    }

    public void setWrite(boolean write) {
        isWrite = write;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public LinkedHashMap<Integer,String> getParam() {
        return param;
    }

    public void setParam(LinkedHashMap<Integer,String> param) {
        this.param = param;
    }
}
