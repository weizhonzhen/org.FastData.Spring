package org.FastData.Spring.CacheModel;

import java.io.Serializable;

public class DbConfig implements Serializable {

    private String providerName;

    private String dbType;

    private String user;

    private String passWord;

    private String connStr;

    private boolean isOutSql;

    private boolean isOutError;

    private String key;

    private String designModel;

    private int poolSize = 50;

    private int timeOut = 3;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getConnStr() {
        return connStr;
    }

    public void setConnStr(String connStr) {
        this.connStr = connStr;
    }

    public boolean isOutSql() {
        return isOutSql;
    }

    public void setOutSql(boolean outSql) {
        isOutSql = outSql;
    }

    public boolean isOutError() {
        return isOutError;
    }

    public void setOutError(boolean outError) {
        isOutError = outError;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesignModel() {
        return designModel;
    }

    public void setDesignModel(String designModel) {
        this.designModel = designModel;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getTimeout() {
        return timeOut;
    }

    public void setTimeout(int timeOut) { this.timeOut = timeOut;
    }
}
