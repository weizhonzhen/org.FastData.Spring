package org.FastData.Spring.CacheModel;

import lombok.Data;

import java.io.Serializable;

@Data
public class PropertyModel implements Serializable {
    private String name;
    private Class<?> type;
}