package org.FastData.Spring.CacheModel;

import lombok.Data;

import java.io.Serializable;
import java.sql.Connection;

@Data
public class PoolModel implements Serializable {
    private String key;
    private Connection conn;
    private boolean isUse;
    private String id;
}
