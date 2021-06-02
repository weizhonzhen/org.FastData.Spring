package org.FastData.Spring.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlModel {
    private List<String> key = new ArrayList<String>();

    private List<String> sql = new ArrayList<String>();

    private Map<String, Object> db = new HashMap<>();

    private Map<String, Object> param = new HashMap<>();

    private Map<String, Object> name = new HashMap<>();

    private Map<String, Object> check = new HashMap<>();

    private boolean isSuccess;

    public List<String> getKey() {
        return key;
    }

    public void setKey(List<String> key) {
        this.key = key;
    }

    public List<String> getSql() {
        return sql;
    }

    public void setSql(List<String> sql) {
        this.sql = sql;
    }

    public Map<String, Object> getDb() {
        return db;
    }

    public void setDb(Map<String, Object> db) {
        this.db = db;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public Map<String, Object> getName() {
        return name;
    }

    public void setName(Map<String, Object> name) {
        this.name = name;
    }

    public Map<String, Object> getCheck() {
        return check;
    }

    public void setCheck(Map<String, Object> check) {
        this.check = check;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
