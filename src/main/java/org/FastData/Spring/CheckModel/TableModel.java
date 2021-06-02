package org.FastData.Spring.CheckModel;

import java.util.ArrayList;
import java.util.List;

public class TableModel {

    private String name;

    private String comments;

    private List<ColumnModel> column=new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<ColumnModel> getColumn() {
        return column;
    }

    public void setColumn(List<ColumnModel> column) {
        this.column = column;
    }
}
