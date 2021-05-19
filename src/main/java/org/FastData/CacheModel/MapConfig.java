package org.FastData.CacheModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapConfig implements Serializable {
    public List<String> path = new ArrayList<>();

    public Date lastWrite;
}
