package org.FastData.Spring.CacheModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapConfig implements Serializable {
    private List<String> path = new ArrayList<>();

    private Date lastWrite;

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public Date getLastWrite() {
        return lastWrite;
    }

    public void setLastWrite(Date lastWrite) {
        this.lastWrite = lastWrite;
    }
}
