package org.FastData.Spring.Check;

import lombok.Data;
import org.FastData.Spring.CheckModel.ColumnComments;
import org.FastData.Spring.CheckModel.ColumnModel;
import org.FastData.Spring.CheckModel.ColumnType;
import org.FastData.Spring.CheckModel.CompareModel;

import java.util.Optional;

@Data
public class CheckModel {
    private static ColumnType getColumnType(ColumnModel item, String type, String name) {
        ColumnType result = new ColumnType();
        result.setName(name);

        switch (type.toLowerCase()) {
            case "char":
            case "nchar":
            case "varchar":
            case "nvarchar":
            case "varchar2":
            case "nvarchar2":
                result.setLength(item.getLength());
                result.setType(type);
                break;
            case "decimal":
            case "numeric":
            case "number":
                result.setPrecision(item.getPrecision());
                result.setScale(item.getScale());
                result.setType(type);
                break;
            default:
                result.setType(type);
                break;
        }

        return result;
    }

    public static CompareModel<ColumnModel> compareTo(ColumnModel cacheItem, ColumnModel modelItem) {
        CompareModel<ColumnModel> result = new CompareModel<ColumnModel>();
        result.setItem(modelItem);

        if (modelItem.getName() == null) {
            result.getRemoveName().add(cacheItem.getName());
            result.setDelete(true);
            return result;
        }

        String type = modelItem.getDataType();
        if (type.equals(""))
            type = cacheItem.getDataType();

        String name = modelItem.getName();
        if (name.equals("")) {
            name = cacheItem.getName();
            result.setItem(cacheItem);
        }

        if (modelItem.isKey() != cacheItem.isKey()) {
            result.setUpdate(true);
            if (modelItem.isKey())
                result.getAddKey().add(getColumnType(modelItem, type, name));
            else
                result.getRemoveKey().add(name);
        }

        if (modelItem.isNull() != cacheItem.isNull() && !modelItem.isKey()) {
            result.setUpdate(true);
            if (modelItem.isNull())
                result.getAddNull().add(getColumnType(modelItem, type, name));
            else
                result.getRemoveNull().add(getColumnType(modelItem, type, name));
        }

        if (!modelItem.getName().equalsIgnoreCase(cacheItem.getName())) {
            result.setUpdate(true);
            if (Optional.of(modelItem.getName()).orElse("").equals(""))
                result.getRemoveName().add(name);
            else
                result.getAddName().add(getColumnType(modelItem, type, name));
        }

        if (!modelItem.getDataType().equalsIgnoreCase(cacheItem.getDataType())) {
            result.setUpdate(true);
            result.getType().add(getColumnType(modelItem, type, name));
        } else {
            switch (modelItem.getDataType().toLowerCase()) {
                case "char":
                case "nchar":
                case "varchar":
                case "nvarchar":
                case "varchar2":
                case "nvarchar2":
                    if (modelItem.getLength() != cacheItem.getLength()) {
                        result.setUpdate(true);
                        result.getType().add(getColumnType(modelItem, type, name));
                    }
                    break;
                case "decimal":
                case "numeric":
                case "number":
                    if (modelItem.getPrecision() != cacheItem.getPrecision() || modelItem.getScale() != cacheItem.getScale()) {
                        result.setUpdate(true);
                        result.getType().add(getColumnType(modelItem, type, name));
                    }
                    break;
            }
        }

        if (!modelItem.getComments().equals(cacheItem.getComments())) {
            result.setUpdate(true);
            ColumnComments temp = new ColumnComments();
            temp.setComments(modelItem.getComments());
            temp.setType(getColumnType(modelItem, type, name));
            temp.setName(name);
            result.getComments().add(temp);
        }

        return result;
    }
}
