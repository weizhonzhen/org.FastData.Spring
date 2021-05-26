package org.FastData.Spring.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
public class DataReturn {
    private int count;

    private String sql;

    private List<Map<String, Object>> list = new ArrayList<>();

    private Map<String, Object> item = new HashMap<>();

    private PageResult pageResult = new PageResult();

    private WriteReturn writeReturn = new WriteReturn();
}
