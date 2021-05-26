package org.FastData.Spring.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class DataReturnImpl<T>{

    private int count ;

    private T item;

    private List<T> list = new ArrayList<T>();

    private String sql ;

    private PageResultImpl<T> pageResult  = new PageResultImpl<T>();

    private WriteReturn writeReturn = new WriteReturn();
}
