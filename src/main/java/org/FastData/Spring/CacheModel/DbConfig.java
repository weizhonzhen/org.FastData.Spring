package org.FastData.Spring.CacheModel;

import lombok.Data;
import java.io.Serializable;

@Data
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
}
