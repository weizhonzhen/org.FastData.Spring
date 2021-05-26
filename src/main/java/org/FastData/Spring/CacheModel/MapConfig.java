package org.FastData.Spring.CacheModel;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class MapConfig implements Serializable {
    private List<String> path = new ArrayList<>();

    private Date lastWrite;
}
