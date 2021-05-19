package org.FastData.Spring.Check;

import org.FastData.Spring.Annotation.Column;
import org.FastData.Spring.Annotation.Table;
import org.FastData.Spring.CacheModel.DbConfig;
import org.FastData.Spring.CheckModel.ColumnModel;
import org.FastData.Spring.CheckModel.ColumnType;
import org.FastData.Spring.CheckModel.CompareModel;
import org.FastData.Spring.CheckModel.TableModel;
import org.FastData.Spring.Config.Config;
import org.FastData.Spring.Config.DataDbType;
import org.FastData.Spring.Context.DataContext;
import org.FastData.Spring.Model.DataReturn;
import org.FastData.Spring.Model.MapResult;
import java.util.*;

public class BaseTable {
    public static void check(Class<?> type, DbConfig config) {
        try {
            try (DataContext db = new DataContext(config.key)) {
                if (config.designModel.equalsIgnoreCase(Config.codeFirst)) {
                    String tableName = type.getName().replace(type.getPackage().getName(), "").replace(".", "");
                    if (isExistsTable(tableName, db)) {
                        TableModel table = getTable(db, tableName);
                        List<ColumnModel> colList = getColumnClass(type);
                        if (colList.size() >= table.column.size()) {
                            colList.forEach(p -> {
                                Optional<ColumnModel> temp = table.column.stream().filter(a -> a.name.equalsIgnoreCase(p.name)).findFirst();
                                ColumnModel info = temp.equals(Optional.ofNullable(null)) ? new ColumnModel() : temp.get();
                                CompareModel<ColumnModel> result = CheckModel.compareTo(info, p);
                                if (result.isUpdate)
                                    updateTable(db, result, table.name);

                                if (result.isDelete)
                                    updateTable(db, result, table.name);
                            });
                        }
                        else {
                            table.column.forEach(p -> {
                                Optional<ColumnModel> temp = colList.stream().filter(a -> a.name.equalsIgnoreCase(p.name)).findFirst();
                                ColumnModel info = temp.equals(Optional.ofNullable(null)) ? new ColumnModel() : temp.get();
                                CompareModel<ColumnModel> result = CheckModel.compareTo(p, info);
                                if (result.isUpdate)
                                    updateTable(db, result, tableName);

                                if (result.isDelete)
                                    updateTable(db, result, tableName);
                            });
                        }

                        String comments = getTableClass(type);
                        if (!table.comments.equals(comments))
                        {
                            table.comments = comments;
                            updateComments(db, table);
                        }
                    } else {
                        TableModel table = new TableModel();
                        table.name=tableName;
                        table.column = getColumnClass(type);
                        table.comments = getTableClass(type);
                        addTable(db, table);
                        updateComments(db, table);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void updateTable(DataContext db, CompareModel<ColumnModel> info, String tableName) {
        info.addName.forEach(a -> {
            MapResult map = new MapResult();
            map.sql = String.format("alter table %s add %s %s", tableName, a.name, getFieldType(a));
            db.execute(map);
        });

        info.removeNull.forEach(a -> {
            MapResult map = new MapResult();
            Map key = isExistsKey(db, a.name, tableName);
            if (key.size() > 0) {
                map.sql = String.format("alter table %s drop constraint %s", tableName, key.get("PK"));
                db.execute(map);
            }

            if (db.config.dbType.equalsIgnoreCase(DataDbType.SqlServer)) {
                map.sql = String.format("alter table %s alter column %s %s not null", tableName, a.name, getFieldType(a));
                db.execute(map);
            }

            if (db.config.dbType.equalsIgnoreCase(DataDbType.MySql) || db.config.dbType.equalsIgnoreCase(DataDbType.Oracle)) {
                map.sql = String.format("alter table %s modify %s %s not null", tableName, a.name, getFieldType(a));
                db.execute(map);
            }

            if (key.size() > 0) {
                map.sql = String.format("alter table %s add constraint pk_%s_%s primary key (%s)", tableName, tableName, a.name, a.name);
                db.execute(map);
            }
        });

        info.addNull.forEach(a -> {
            MapResult map = new MapResult();
            Map key = isExistsKey(db, a.name, tableName);
            if (key.size() > 0) {
                map.sql = String.format("alter table %s drop constraint %s", tableName, key.get("PK"));
                db.execute(map);
            }

            if (db.config.dbType.equalsIgnoreCase(DataDbType.SqlServer)) {
                map.sql = String.format("alter table %s alter column %s %s null", tableName, a.name, getFieldType(a));
                db.execute(map);
            }

            if (db.config.dbType.equalsIgnoreCase(DataDbType.MySql) || db.config.dbType.equalsIgnoreCase(DataDbType.Oracle)) {
                map.sql = String.format("alter table %s modify %s %s null", tableName, a.name, getFieldType(a));
                db.execute(map);
            }

            if (key.size() > 0) {
                map.sql = String.format("alter table %s add constraint pk_%s_%s primary key (%s)", tableName, tableName, a.name, a.name);
                db.execute(map);
            }
        });

        info.removeKey.forEach(a -> {
            MapResult map = new MapResult();
            map.sql = String.format("alter table %s drop constraint pk_%s_%s", tableName, tableName, a);
            db.execute(map);
        });

        info.addKey.forEach(a -> {
            MapResult map = new MapResult();
            if (db.config.dbType.equalsIgnoreCase(DataDbType.SqlServer)) {
                map.sql = String.format("alter table %s alter column %s %s not null", tableName, a.name, getFieldType(a));
                db.execute(map);
            }

            if (db.config.dbType.equalsIgnoreCase(DataDbType.MySql) || db.config.dbType.equalsIgnoreCase(DataDbType.Oracle)) {
                map.sql = String.format("alter table %s modify %s %s not null", tableName, a.name, getFieldType(a));
                db.execute(map);
            }

            map.sql = String.format("alter table %s add constraint pk_%s_%s primary key (%s)", tableName, tableName, a.name, a.name);
            db.execute(map);
        });

        info.type.forEach(p -> {
            if (info.addName.stream().noneMatch(a -> a.name.equalsIgnoreCase(p.name))) {
                MapResult map = new MapResult();
                Map key = isExistsKey(db, p.name, tableName);
                if (key.size() > 0) {
                    map.sql = String.format("alter table %s drop constraint %s", tableName, key.get("PK"));
                    db.execute(map);
                }

                if (db.config.dbType.equalsIgnoreCase(DataDbType.SqlServer)) {
                    map.sql = String.format("alter table %s alter column %s %s", tableName, p.name, getFieldType(p));
                    db.execute(map);
                }

                if (db.config.dbType.equalsIgnoreCase(DataDbType.MySql) || db.config.dbType.equalsIgnoreCase(DataDbType.Oracle)) {
                    map.sql = String.format("alter table %s modify %s %s", tableName, p.name, getFieldType(p));
                    db.execute(map);
                }

                if (key.size() > 0) {
                    map.sql = String.format("alter table %s add constraint pk_%s_%s primary key (%s)", tableName, tableName, p.name, p.name);
                    db.execute(map);
                }
            }
        });

        info.removeName.forEach(a -> {
            MapResult map = new MapResult();
            map.sql = String.format("alter table %s drop column %s", tableName, a);
            db.execute(map);
        });

        info.comments.forEach(a -> {
            UpdateColumn(db, a.name, a.comments, getFieldType(a.type), tableName);
        });
    }

    private static Map<String, Object> isExistsKey(DataContext db, String tableName, String colName) {
        MapResult map = new MapResult();
        map.name.add("tableName");
        map.name.add("colName");
        map.param.put("tableName", tableName.toUpperCase());
        map.param.put("colName", colName.toUpperCase());

        if (db.config.dbType.equalsIgnoreCase(DataDbType.SqlServer))
            map.sql = "select CONSTRAINT_NAME PK from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where upper(TABLE_NAME)=? and upper(COLUMN_NAME)=?";

        if (db.config.dbType.equalsIgnoreCase(DataDbType.MySql))
            map.sql = "select constraint_name PK from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where upper(TABLE_NAME)=? and constraint_name='PRIMARY' and upper(column_name)=?";

        if (db.config.dbType.equalsIgnoreCase(DataDbType.Oracle))
            map.sql = "select a.CONSTRAINT_NAME PK from all_constraints a inner join all_cons_columns b on a.TABLE_NAME=b.TABLE_NAME and a.CONSTRAINT_NAME=b.CONSTRAINT_NAME where a.table_name=? and a.constraint_type = 'P' and b.COLUMN_NAME=?";

        List<Map<String,Object>> result = db.query(map).list;
        return result.size() == 0 ? new HashMap<>() : result.get(0);
    }

    private static void addTable(DataContext db, TableModel table) {
        MapResult map = new MapResult();
        map.sql = String.format("create table %s(", table.name);
        List<String> key = new ArrayList<String>();

        table.column.forEach(a -> {
            map.sql = String.format("%s %s %s %s,", map.sql, a.name, getFieldType(a), getFieldKey(a));
            if (a.isKey)
                key.add(a.name);
        });

        map.sql = map.sql.substring(0, map.sql.length() - 1);
        map.sql = String.format("%s)", map.sql);
        db.execute(map);

        key.forEach(a -> {
            MapResult keyMap = new MapResult();
            keyMap.sql = String.format("alter table %s add constraint pk_%s_%s primary key (%s)", table.name, table.name, a, a);
            db.execute(keyMap);
        });

        table.column.forEach(a -> {
            UpdateColumn(db, a.name, a.comments, getFieldType(a), table.name);
        });
    }

    private static void UpdateColumn(DataContext db, String colName, String comments, String type, String tableName) {
        MapResult map = new MapResult();

        if (db.config.dbType.equalsIgnoreCase(DataDbType.MySql))
            map.sql = String.format("alter table %s modify column %s %s comment‘%s’", tableName, colName, type, comments);

        if (db.config.dbType.equalsIgnoreCase(DataDbType.Oracle))
            map.sql = String.format("Comment on column %s.%s is '%s'", tableName, colName, comments);

        if (db.config.dbType.equalsIgnoreCase(DataDbType.SqlServer)) {
            map.sql = String.format("select count(0) count from syscolumns where id = object_id('%s') and name='%s'", tableName, colName);
            map.sql = String.format("%s and exists(select 1 from sys.extended_properties where object_id('%s')=major_id and colid=minor_id)", map.sql, tableName);
            int count = db.count(map);
            if (count >= 1)
                map.sql = String.format("execute sp_updateextendedproperty N'MS_Description', '%s', N'user', N'dbo', N'table', N'%s', N'column', %s", comments, tableName, colName);
            else
                map.sql = String.format("execute sp_addextendedproperty N'MS_Description', '%s', N'user', N'dbo', N'table', N'%s', N'column', %s", comments, tableName, colName);
        }

        db.execute(map);
    }

    private static void updateComments(DataContext db, TableModel table) {
        MapResult map = new MapResult();

        if (db.config.dbType.equalsIgnoreCase(DataDbType.MySql))
            map.sql = String.format("alter table %s comment '%s'", table.name, table.comments);

        if (db.config.dbType.equalsIgnoreCase(DataDbType.Oracle))
            map.sql = String.format("Comment on table %s is '%s'", table.name, table.comments);

        if (db.config.dbType.equalsIgnoreCase(DataDbType.SqlServer)) {
            map.sql = String.format("select count(0) count from sys.extended_properties where object_id('%s')=major_id and minor_id=0", table.name);
            int count = db.count(map);
            if (count >= 1)
                map.sql = String.format("execute sp_updateextendedproperty N'MS_Description', '%s', N'user', N'dbo', N'table', N'%s', NULL, NULL", table.comments, table.name);
            else
                map.sql = String.format("execute sp_addextendedproperty N'MS_Description', '%s', N'user', N'dbo', N'table', N'%s', NULL, NULL", table.comments, table.name);
        }
        db.execute(map);
    }

    private static TableModel getTable(DataContext db, String tableName) {
        TableModel result = new TableModel();
        result.name = tableName;
        MapResult map = new MapResult();
        if (db.config.dbType.equalsIgnoreCase(DataDbType.Oracle)) {
            map.sql = String.format("select comments from user_tables a inner join user_tab_comments b on a.TABLE_NAME = b.TABLE_NAME  and a.table_name = '%s'", tableName.toUpperCase());
            DataReturn dic = db.query(map);
            result.comments = Optional.ofNullable(dic.list.get(0).get("COMMENTS")).orElse("").toString().replace("'", "");

            map.sql = "select a.column_name,data_type,data_length,b.comments,";
            map.sql = String.format("%s %s", map.sql, "(select count(0) from user_cons_columns aa, user_constraints bb where aa.constraint_name = bb.constraint_name and bb.constraint_type = 'P' and bb.table_name = '" + tableName.toUpperCase() + "' and aa.column_name = a.column_name) iskey,");
            map.sql = String.format("%s %s", map.sql, "nullable,data_precision,data_scale from user_tab_columns a inner join user_col_comments b");
            map.sql = String.format("%s %s", map.sql, "on a.table_name ='" + tableName.toUpperCase() + "' and a.table_name = b.table_name and a.column_name = b.column_name order by a.column_id asc");
            dic = db.query(map);
            dic.list.forEach(a -> {
                ColumnModel model = new ColumnModel();
                model.comments = Optional.ofNullable(a.get("COMMENTS")).orElse("").toString().replace("'", "");
                model.dataType = a.get("DATA_TYPE").toString();
                model.isKey = a.get("ISKEY").toString().equals("1");
                model.isNull = a.get("NULLABLE").toString().equals("Y");
                model.length = Integer.parseInt(Optional.ofNullable(a.get("DATA_LENGTH")).orElse(0).toString());
                model.name = a.get("COLUMN_NAME").toString();
                model.precision = Integer.parseInt(Optional.ofNullable(a.get("DATA_PRECISION")).orElse(0).toString());
                model.scale = Integer.parseInt(Optional.ofNullable(a.get("DATA_SCALE")).orElse(0).toString());

                result.column.add(model);
            });
        }

        if (db.config.dbType.equalsIgnoreCase(DataDbType.MySql)) {
            map.sql = String.format("select table_comment count from information_schema.tables where upper(table_name)='%s'", tableName.toUpperCase());
            DataReturn dic = db.query(map);
            result.comments = Optional.ofNullable(dic.list.get(0).get("COMMENTS")).orElse("").toString().replace("'", "");

            map.sql = "select column_name,data_type,character_maximum_length,column_comment,";
            map.sql = String.format("%s %s", map.sql, "(select count(0) from INFORMATION_SCHEMA.KEY_COLUMN_USAGE a where upper(TABLE_NAME)='" + tableName.toUpperCase() + "' and constraint_name='PRIMARY' and c.column_name=a.column_name) iskey,");
            map.sql = String.format("%s %s", map.sql, "is_nullable,numeric_precision,numeric_scale from information_schema.columns c where upper(table_name)='" + tableName.toUpperCase() + "' order by ordinal_position asc");
            dic = db.query(map);
            dic.list.forEach(a -> {
                ColumnModel model = new ColumnModel();
                model.comments = Optional.ofNullable(a.get("column_comment")).orElse("").toString().replace("'", "");
                model.dataType = a.get("data_type").toString();
                model.isKey = a.get("iskey").toString().equals("1");
                model.isNull = a.get("is_nullabl").toString().equals("YES");
                model.length = Integer.parseInt(Optional.ofNullable(a.get("character_maximum_length")).orElse(0).toString());
                model.name = a.get("column_name").toString();
                model.precision = Integer.parseInt(Optional.ofNullable(a.get("numeric_precision")).orElse(0).toString());
                model.scale = Integer.parseInt(Optional.ofNullable(a.get("numeric_scale")).orElse(0).toString());
                result.column.add(model);
            });
        }

        if (db.config.dbType.equalsIgnoreCase(DataDbType.SqlServer)) {
            map.sql = String.format("select name,(select top 1 value from sys.extended_properties where major_id=object_id(a.name) and minor_id=0) as value from sys.objects a where type = 'U' and upper(name) = '%s'", tableName.toUpperCase());
            DataReturn dic = db.query(map);
            result.comments = Optional.ofNullable(dic.list.get(0).get("COMMENTS")).orElse("").toString().replace("'", "");

            map.sql = "select a.name,(select top 1 name from sys.systypes c where a.xtype=c.xtype) as type ,";
            map.sql = String.format("%s %s", map.sql, "length,b.value,(select count(0) from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where TABLE_NAME='" + tableName.toUpperCase() + "' and COLUMN_NAME=a.name) as iskey,");
            map.sql = String.format("%s %s", map.sql, "isnullable,prec,scale from syscolumns a left join sys.extended_properties b");
            map.sql = String.format("%s %s", map.sql, "on major_id = id and minor_id = colid and b.name ='MS_Description'  where a.id=object_id('" + tableName.toUpperCase() + "') order by a.colid asc");
            dic = db.query(map);
            dic.list.forEach(a -> {
                ColumnModel model = new ColumnModel();
                model.comments = Optional.ofNullable(a.get("value")).orElse("").toString().replace("'", "");
                model.dataType = a.get("type").toString();
                model.isKey = a.get("iskey").toString().equals("1");
                model.isNull = a.get("isnullable").toString().equals("1");
                model.length = Integer.parseInt(Optional.ofNullable(a.get("length")).orElse(0).toString());
                model.name = a.get("name").toString();
                model.precision = Integer.parseInt(Optional.ofNullable(a.get("prec")).orElse(0).toString());
                model.scale = Integer.parseInt(Optional.ofNullable(a.get("scale")).orElse(0).toString());
                result.column.add(model);
            });
        }

        result.column.forEach(a -> {
            if (a.dataType.equalsIgnoreCase("nchar")
                    || a.dataType.equalsIgnoreCase("nvarchar")
                    || a.dataType.equalsIgnoreCase("nvarchar2")
                    || a.dataType.equalsIgnoreCase("ntext")
                    || a.dataType.equalsIgnoreCase("nclob"))
                a.length = a.length / 2;
        });
        return result;
    }

    private static boolean isExistsTable(String tableName, DataContext db) {
        MapResult map = new MapResult();
        map.name.add(tableName);
        map.param.put(tableName, tableName.toUpperCase());

        if (db.config.dbType.equalsIgnoreCase(DataDbType.Oracle))
            map.sql = "select count(0) count from user_tables where table_name=?";

        if (db.config.dbType.equalsIgnoreCase(DataDbType.SqlServer))
            map.sql = "select count(0) count from dbo.sysobjects where upper(name)=?";

        if (db.config.dbType.equalsIgnoreCase(DataDbType.MySql))
            map.sql = "select count(0) count from information_schema.tables where upper(table_name)=?";

        return db.count(map) > 0;
    }

    private static List<ColumnModel> getColumnClass(Class<?> type) {
        List<ColumnModel> list = new ArrayList<ColumnModel>();
        Arrays.stream(type.getFields()).sequential().forEach(a -> {
            Column info = a.getAnnotation(Column.class);
            if(info!=null) {
                ColumnModel temp = new ColumnModel();
                temp.comments = info.comments().replace("'", "");
                temp.dataType = info.dataType();
                temp.isKey = info.isKey();
                if (temp.isKey)
                    temp.isNull = false;
                else
                    temp.isNull = info.isNull();
                temp.length = info.length();
                temp.name = a.getName();
                temp.precision = info.precision();
                temp.scale = info.scale();
                list.add(temp);
            }
        });
        return list;
    }

    private static String getTableClass(Class<?> type) {
        TableModel result = new TableModel();
        Table info = type.getAnnotation(Table.class);
        if (info != null) {
            result.comments = info.comments().replace("'","");
        }
        return result.comments;
    }

    private static String getFieldKey(ColumnModel item) {
        if (item.isKey)
            return "not null";
        else if (!item.isNull)
            return "not null";
        else
            return "";
    }

    private static String getFieldType(ColumnType item){
        switch (item.type.toLowerCase())
        {
            case "char":
            case "nchar":
            case "varchar":
            case "nvarchar":
            case "varchar2":
            case "nvarchar2":
                return String.format("%s(%s)", item.type, item.length);
            case "decimal":
            case "numeric":
            case "number":
                if (item.precision == 0 && item.scale == 0)
                    return item.type;
                else
                    return String.format("decimal(%s,%s)", item.precision, item.scale);
            default:
                return item.type;
        }
    }

    private static String getFieldType(ColumnModel item) {
        switch (item.dataType.toLowerCase()) {
            case "char":
            case "nchar":
            case "varchar":
            case "nvarchar":
            case "varchar2":
            case "nvarchar2":
                return String.format("%s(%s)", item.dataType, item.length);
            case "decimal":
            case "numeric":
            case "number":
                if (item.precision == 0 && item.scale == 0)
                    return String.format("%s", item.dataType);
                else
                    return String.format("%s(%s,%s)", item.dataType, item.precision, item.scale);
            default:
                return item.dataType;
        }
    }
}