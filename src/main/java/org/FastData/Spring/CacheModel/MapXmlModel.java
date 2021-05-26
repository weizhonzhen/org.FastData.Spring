package org.FastData.Spring.CacheModel;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MapXmlModel implements Serializable  {
    private List<String> fileKey=new ArrayList<>();

    private String fileName;
}
