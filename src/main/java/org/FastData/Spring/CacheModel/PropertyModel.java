package org.FastData.Spring.CacheModel;

import java.io.Serializable;

public class PropertyModel implements Serializable {
    private String name;
    private Class<?> type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}