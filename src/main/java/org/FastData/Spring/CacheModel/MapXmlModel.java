package org.FastData.Spring.CacheModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapXmlModel implements Serializable  {
    private List<String> fileKey=new ArrayList<>();

    private String fileName;

    public List<String> getFileKey() {
        return fileKey;
    }

    public void setFileKey(List<String> fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
