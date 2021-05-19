package org.FastData.Spring.CheckModel;

import java.util.ArrayList;
import java.util.List;

public class CompareModel <T>{

    public boolean isUpdate ;

    public boolean isDelete ;

    public T item;

    public List<String> removeKey = new ArrayList<>();

    public List<ColumnType> addKey = new ArrayList<>();

    public List<ColumnType> removeNull = new ArrayList<>();

    public List<ColumnType> addNull = new ArrayList<>();

    public List<String> removeName = new ArrayList<>();

    public List<ColumnType> addName = new ArrayList<>();

    public List<ColumnComments> comments = new ArrayList<>();

    public List<ColumnType> type = new ArrayList<>();
}
