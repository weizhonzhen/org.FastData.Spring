package org.FastData.Spring.CacheModel;

import java.io.Serializable;
import java.sql.Connection;

public class PoolModel implements Serializable {
    public String key;
    public Connection conn;
    public boolean isUse;
    public String id;
}
