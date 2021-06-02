package org.FastData.Spring.CacheModel;

import java.io.Serializable;
import java.sql.Connection;

public class PoolModel implements Serializable {
    private String key;
    private Connection conn;
    private boolean isUse;
    private String id;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
