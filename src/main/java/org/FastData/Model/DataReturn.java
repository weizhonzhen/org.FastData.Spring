package org.FastData.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataReturn {
    public int count;

    public String sql;

    public List<Map<String, Object>> list = new ArrayList<>();

    public Map<String, Object> item = new HashMap<>();

    public PageResult pageResult = new PageResult();

    public WriteReturn writeReturn = new WriteReturn();
}
