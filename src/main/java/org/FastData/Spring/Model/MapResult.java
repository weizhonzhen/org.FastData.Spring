package org.FastData.Spring.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
public class MapResult {
    private String sql;

    private Map<String, Object> param = new HashMap<>();

    private List<String> name = new ArrayList<>();

    private boolean isSuccess;

    private String message;
}
