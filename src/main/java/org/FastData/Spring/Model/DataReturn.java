package org.FastData.Spring.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataReturn {
    private int count;

    private String sql;

    private List<FastMap<String, Object>> list = new ArrayList<>();

    private FastMap<String, Object> item = new FastMap<>();

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

    public List<FastMap<String, Object>> getList() {
        return list;
    }

    public void setList(List<FastMap<String, Object>> list) {
        this.list = list;
    }

    public FastMap<String, Object> getItem() {
        return item;
    }

    public void setItem(FastMap<String, Object> item) {
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
