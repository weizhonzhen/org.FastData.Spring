package org.FastData.Spring.CheckModel;

import java.util.ArrayList;
import java.util.List;

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

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public List<String> getRemoveKey() {
        return removeKey;
    }

    public void setRemoveKey(List<String> removeKey) {
        this.removeKey = removeKey;
    }

    public List<ColumnType> getAddKey() {
        return addKey;
    }

    public void setAddKey(List<ColumnType> addKey) {
        this.addKey = addKey;
    }

    public List<ColumnType> getRemoveNull() {
        return removeNull;
    }

    public void setRemoveNull(List<ColumnType> removeNull) {
        this.removeNull = removeNull;
    }

    public List<ColumnType> getAddNull() {
        return addNull;
    }

    public void setAddNull(List<ColumnType> addNull) {
        this.addNull = addNull;
    }

    public List<String> getRemoveName() {
        return removeName;
    }

    public void setRemoveName(List<String> removeName) {
        this.removeName = removeName;
    }

    public List<ColumnType> getAddName() {
        return addName;
    }

    public void setAddName(List<ColumnType> addName) {
        this.addName = addName;
    }

    public List<ColumnComments> getComments() {
        return comments;
    }

    public void setComments(List<ColumnComments> comments) {
        this.comments = comments;
    }

    public List<ColumnType> getType() {
        return type;
    }

    public void setType(List<ColumnType> type) {
        this.type = type;
    }
}
