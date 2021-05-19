package org.FastData.Spring.Check;

import org.FastData.Spring.CheckModel.ColumnComments;
import org.FastData.Spring.CheckModel.ColumnModel;
import org.FastData.Spring.CheckModel.ColumnType;
import org.FastData.Spring.CheckModel.CompareModel;

import java.util.Optional;

public class CheckModel {
    private static ColumnType getColumnType(ColumnModel item, String type, String name) {
        ColumnType result = new ColumnType();
        result.name = name;

        switch (type.toLowerCase()) {
            case "char":
            case "nchar":
            case "varchar":
            case "nvarchar":
            case "varchar2":
            case "nvarchar2":
                result.length = item.length;
                result.type = type;
                break;
            case "decimal":
            case "numeric":
            case "number":
                result.precision = item.precision;
                result.scale = item.scale;
                result.type = type;
                break;
            default:
                result.type = type;
                break;
        }

        return result;
    }

    public static CompareModel<ColumnModel> compareTo(ColumnModel cacheItem, ColumnModel modelItem) {
        CompareModel<ColumnModel> result = new CompareModel<ColumnModel>();
        result.item = modelItem;

        if (modelItem.name == null) {
            result.removeName.add(cacheItem.name);
            result.isDelete = true;
            return result;
        }

        String type = modelItem.dataType;
        if (type.equals(""))
            type = cacheItem.dataType;

        String name = modelItem.name;
        if (name.equals("")) {
            name = cacheItem.name;
            result.item = cacheItem;
        }

        if (modelItem.isKey != cacheItem.isKey) {
            result.isUpdate = true;
            if (modelItem.isKey)
                result.addKey.add(getColumnType(modelItem, type, name));
            else
                result.removeKey.add(name);
        }

        if (modelItem.isNull != cacheItem.isNull) {
            result.isUpdate = true;
            if (modelItem.isNull)
                result.addNull.add(getColumnType(modelItem, type, name));
            else
                result.removeNull.add(getColumnType(modelItem, type, name));
        }

        if (!modelItem.name.equalsIgnoreCase(cacheItem.name)) {
            result.isUpdate = true;
            if (Optional.of(modelItem.name).orElse("").equals(""))
                result.removeName.add(name);
            else
                result.addName.add(getColumnType(modelItem, type, name));
        }

        if (!modelItem.dataType.equalsIgnoreCase(cacheItem.dataType)){
            result.isUpdate = true;
            result.type.add(getColumnType(modelItem, type, name));
        }
       else {
            switch (modelItem.dataType.toLowerCase()) {
                case "char":
                case "nchar":
                case "varchar":
                case "nvarchar":
                case "varchar2":
                case "nvarchar2":
                    if (modelItem.length != cacheItem.length) {
                        result.isUpdate = true;
                        result.type.add(getColumnType(modelItem, type, name));
                    }
                    break;
                case "decimal":
                case "numeric":
                case "number":
                    if (modelItem.precision != cacheItem.precision || modelItem.scale != cacheItem.scale) {
                        result.isUpdate = true;
                        result.type.add(getColumnType(modelItem, type, name));
                    }
                    break;
            }
        }

        if (!modelItem.comments.equals(cacheItem.comments)) {
            result.isUpdate = true;
            ColumnComments temp = new ColumnComments();
            temp.comments = modelItem.comments;
            temp.type = getColumnType(modelItem, type, name);
            temp.name = name;
            result.comments.add(temp);
        }

        return result;
    }
}
