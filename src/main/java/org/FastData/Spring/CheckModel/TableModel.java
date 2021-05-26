package org.FastData.Spring.CheckModel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class TableModel {

    private String name;

    private String comments;

    private List<ColumnModel> column=new ArrayList<>();
}
