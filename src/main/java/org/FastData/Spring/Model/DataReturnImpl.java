package org.FastData.Spring.Model;

import java.util.ArrayList;
import java.util.List;

public class DataReturnImpl<T>{

    public int count ;

    public T item;

    public List<T> list = new ArrayList<T>();

    public String sql ;

    public PageResultImpl<T> pageResult  = new PageResultImpl<T>();

    public WriteReturn writeReturn = new WriteReturn();
}
