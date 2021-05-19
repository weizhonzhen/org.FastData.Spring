package org.FastData.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapResult {
    public String sql;

    public Map<String, Object> param = new HashMap<>();

    public List<String> name = new ArrayList<>();

    public boolean isSuccess;

    public String message;
}
