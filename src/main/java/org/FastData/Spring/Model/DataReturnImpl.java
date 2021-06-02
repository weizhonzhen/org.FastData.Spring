package org.FastData.Spring.Model;

import java.util.ArrayList;
import java.util.List;

public class DataReturnImpl<T>{

    private int count ;

    private T item;

    private List<T> list = new ArrayList<T>();

    private String sql ;

    private PageResultImpl<T> pageResult  = new PageResultImpl<T>();

    private WriteReturn writeReturn = new WriteReturn();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public PageResultImpl<T> getPageResult() {
        return pageResult;
    }

    public void setPageResult(PageResultImpl<T> pageResult) {
        this.pageResult = pageResult;
    }

    public WriteReturn getWriteReturn() {
        return writeReturn;
    }

    public void setWriteReturn(WriteReturn writeReturn) {
        this.writeReturn = writeReturn;
    }
}
