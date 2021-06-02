package org.FastData.Spring.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataReturn {
    private int count;

    private String sql;

    private List<Map<String, Object>> list = new ArrayList<>();

    private Map<String, Object> item = new HashMap<>();

    private PageResult pageResult = new PageResult();

    private WriteReturn writeReturn = new WriteReturn();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public Map<String, Object> getItem() {
        return item;
    }

    public void setItem(Map<String, Object> item) {
        this.item = item;
    }

    public PageResult getPageResult() {
        return pageResult;
    }

    public void setPageResult(PageResult pageResult) {
        this.pageResult = pageResult;
    }

    public WriteReturn getWriteReturn() {
        return writeReturn;
    }

    public void setWriteReturn(WriteReturn writeReturn) {
        this.writeReturn = writeReturn;
    }
}
