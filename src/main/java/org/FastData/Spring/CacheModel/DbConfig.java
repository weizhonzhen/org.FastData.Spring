package org.FastData.Spring.CacheModel;

import java.io.Serializable;

public class DbConfig implements Serializable {

    public String providerName;

    public String dbType;

    public String user;

    public String passWord;

    public String connStr;

    public boolean isOutSql;

    public boolean isOutError;

    public String key;

    public String designModel;

    public int poolSize = 50;
}
