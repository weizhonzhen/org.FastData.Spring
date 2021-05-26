package org.FastData.Spring.CheckModel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompareModel <T>{

    private boolean isUpdate ;

    private boolean isDelete ;

    private T item;

    private List<String> removeKey = new ArrayList<>();

    private List<ColumnType> addKey = new ArrayList<>();

    private List<ColumnType> removeNull = new ArrayList<>();

    private List<ColumnType> addNull = new ArrayList<>();

    private List<String> removeName = new ArrayList<>();

    private List<ColumnType> addName = new ArrayList<>();

    private List<ColumnComments> comments = new ArrayList<>();

    private List<ColumnType> type = new ArrayList<>();
}
