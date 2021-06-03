package org.FastData.Spring.Model;

import java.util.*;

public class MapResult {
    private String sql;

    private LinkedHashMap<String, Object> param = new LinkedHashMap<>();

    private boolean isSuccess;

    private String message;

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

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
