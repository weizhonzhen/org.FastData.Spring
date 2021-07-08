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
import org.FastData.Spring.Model.FastMap;
import org.FastData.Spring.Model.MapResult;
import java.util.*;

public class BaseTable {
    public static void check(Class<?> type, DbConfig config) {
        try {
            try (DataContext db = new DataContext(config.getKey())) {
                if (config.getDesignModel().equalsIgnoreCase(Config.codeFirst)) {
                    String tableName = type.getName().replace(type.getPackage().getName(), "").replace(".", "");
                    if (isExistsTable(tableName, db)) {
                        TableModel table = getTable(db, tableName);
                        List<ColumnModel> colList = getColumnClass(type);
                        if (colList.size() >= table.getColumn().size()) {
                            colList.forEach(p -> {
                                Optional<ColumnModel> temp = table.getColumn().stream().filter(a -> a.getName().equalsIgnoreCase(p.getName())).findFirst();
                                ColumnModel info = temp.equals(Optional.ofNullable(null)) ? new ColumnModel() : temp.get();
                                CompareModel<ColumnModel> result = CheckModel.compareTo(info, p);
                                if (result.isUpdate())
                                    updateTable(db, result, table.getName());

                                if (result.isDelete())
                                    updateTable(db, result, table.getName());
                            });
                        }
                        else {
                            table.getColumn().forEach(p -> {
                                Optional<ColumnModel> temp = colList.stream().filter(a -> a.getName().equalsIgnoreCase(p.getName())).findFirst();
                                ColumnModel info = temp.equals(Optional.ofNullable(null)) ? new ColumnModel() : temp.get();
                                CompareModel<ColumnModel> result = CheckModel.compareTo(p, info);
                                if (result.isUpdate())
                                    updateTable(db, result, tableName);

                                if (result.isDelete())
                                    updateTable(db, result, tableName);
                            });
                        }

                        String comments = getTableClass(type);
                        if (!table.getComments().equals(comments))
                        {
                            table.setComments(comments);
                            updateComments(db, table);
                        }
                    } else {
                        TableModel table = new TableModel();
                        table.setName(tableName);
                        table.setColumn( getColumnClass(type));
                        table.setComments( getTableClass(type));
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
        info.getAddName().forEach(a -> {
            MapResult map = new MapResult();
            map.setSql( String.format("alter table %s add %s %s", tableName, a.getName(), getFieldType(a)));
            db.execute(map);
        });

        info.getRemoveNull().forEach(a -> {
            MapResult map = new MapResult();
            Map key = isExistsKey(db, a.getName(), tableName);
            if (key.size() > 0) {
                map.setSql(String.format("alter table %s drop constraint %s", tableName, key.get("PK")));
                db.execute(map);
            }

            if (db.config.getDbType().equalsIgnoreCase(DataDbType.SqlServer)) {
                map.setSql( String.format("alter table %s alter column %s %s not null", tableName, a.getName(), getFieldType(a)));
                db.execute(map);
            }

            if (db.config.getDbType().equalsIgnoreCase(DataDbType.MySql) || db.config.getDbType().equalsIgnoreCase(DataDbType.Oracle)) {
                map.setSql( String.format("alter table %s modify %s %s not null", tableName, a.getName(), getFieldType(a)));
                db.execute(map);
            }

            if (key.size() > 0) {
                map.setSql( String.format("alter table %s add constraint pk_%s_%s primary key (%s)", tableName, tableName, a.getName(), a.getName()));
                db.execute(map);
            }
        });

        info.getAddNull().forEach(a -> {
            MapResult map = new MapResult();
            Map key = isExistsKey(db, a.getName(), tableName);
            if (key.size() > 0) {
                map.setSql( String.format("alter table %s drop constraint %s", tableName, key.get("PK")));
                db.execute(map);
            }

            if (db.config.getDbType().equalsIgnoreCase(DataDbType.SqlServer)) {
                map.setSql(String.format("alter table %s alter column %s %s null", tableName, a.getName(), getFieldType(a)));
                db.execute(map);
            }

            if (db.config.getDbType().equalsIgnoreCase(DataDbType.MySql) || db.config.getDbType().equalsIgnoreCase(DataDbType.Oracle)) {
                map.setSql( String.format("alter table %s modify %s %s null", tableName, a.getName(), getFieldType(a)));
                db.execute(map);
            }

            if (key.size() > 0) {
                map.setSql( String.format("alter table %s add constraint pk_%s_%s primary key (%s)", tableName, tableName, a.getName(), a.getName()));
                db.execute(map);
            }
        });

        info.getRemoveKey().forEach(a -> {
            MapResult map = new MapResult();
            map.setSql( String.format("alter table %s drop constraint pk_%s_%s", tableName, tableName, a));
            db.execute(map);
        });

        info.getAddKey().forEach(a -> {
            MapResult map = new MapResult();
            if (db.config.getDbType().equalsIgnoreCase(DataDbType.SqlServer)) {
                map.setSql( String.format("alter table %s alter column %s %s not null", tableName, a.getName(), getFieldType(a)));
                db.execute(map);
            }

            if (db.config.getDbType().equalsIgnoreCase(DataDbType.MySql) || db.config.getDbType().equalsIgnoreCase(DataDbType.Oracle)) {
                map.setSql( String.format("alter table %s modify %s %s not null", tableName, a.getName(), getFieldType(a)));
                db.execute(map);
            }

            map.setSql( String.format("alter table %s add constraint pk_%s_%s primary key (%s)", tableName, tableName, a.getName(), a.getName()));
            db.execute(map);
        });

        info.getType().forEach(p -> {
            if (info.getAddName().stream().noneMatch(a -> a.getName().equalsIgnoreCase(p.getName()))) {
                MapResult map = new MapResult();
                Map key = isExistsKey(db, p.getName(), tableName);
                if (key.size() > 0) {
                    map.setSql( String.format("alter table %s drop constraint %s", tableName, key.get("PK")));
                    db.execute(map);
                }

                if (db.config.getDbType().equalsIgnoreCase(DataDbType.SqlServer)) {
                    map.setSql( String.format("alter table %s alter column %s %s", tableName, p.getName(), getFieldType(p)));
                    db.execute(map);
                }

                if (db.config.getDbType().equalsIgnoreCase(DataDbType.MySql) || db.config.getDbType().equalsIgnoreCase(DataDbType.Oracle)) {
                    map.setSql( String.format("alter table %s modify %s %s", tableName, p.getName(), getFieldType(p)));
                    db.execute(map);
                }

                if (key.size() > 0) {
                    map.setSql( String.format("alter table %s add constraint pk_%s_%s primary key (%s)", tableName, tableName, p.getName(), p.getName()));
                    db.execute(map);
                }
            }
        });

        info.getRemoveName().forEach(a -> {
            MapResult map = new MapResult();
            map.setSql( String.format("alter table %s drop column %s", tableName, a));
            db.execute(map);
        });

        info.getComments().forEach(a -> {
            UpdateColumn(db, a.getName(), a.getComments(), getFieldType(a.getType()), tableName);
        });
    }

    private static Map<String, Object> isExistsKey(DataContext db, String tableName, String colName) {
        MapResult map = new MapResult();
        map.getParam().put("tableName", tableName.toUpperCase());
        map.getParam().put("colName", colName.toUpperCase());

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.SqlServer))
            map.setSql( "select CONSTRAINT_NAME PK from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where upper(TABLE_NAME)=? and upper(COLUMN_NAME)=?");

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.MySql))
            map.setSql("select constraint_name PK from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where upper(TABLE_NAME)=? and constraint_name='PRIMARY' and upper(column_name)=?");

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.Oracle))
            map.setSql("select a.CONSTRAINT_NAME PK from all_constraints a inner join all_cons_columns b on a.TABLE_NAME=b.TABLE_NAME and a.CONSTRAINT_NAME=b.CONSTRAINT_NAME where a.table_name=? and a.constraint_type = 'P' and b.COLUMN_NAME=?");

        List<FastMap<String,Object>> result = db.query(map).getList();
        return result.size() == 0 ? new HashMap<>() : result.get(0);
    }

    private static void addTable(DataContext db, TableModel table) {
        MapResult map = new MapResult();
        map.setSql( String.format("create table %s(", table.getName()));
        List<String> key = new ArrayList<String>();

        table.getColumn().forEach(a -> {
            map.setSql( String.format("%s %s %s %s,", map.getSql(), a.getName(), getFieldType(a), getFieldKey(a)));
            if (a.isKey())
                key.add(a.getName());
        });

        map.setSql( map.getSql().substring(0, map.getSql().length() - 1));
        map.setSql( String.format("%s)", map.getSql()));
        db.execute(map);

        key.forEach(a -> {
            MapResult keyMap = new MapResult();
            keyMap.setSql( String.format("alter table %s add constraint pk_%s_%s primary key (%s)", table.getName(), table.getName(), a, a));
            db.execute(keyMap);
        });

        table.getColumn().forEach(a -> {
            UpdateColumn(db, a.getName(), a.getComments(), getFieldType(a), table.getName());
        });
    }

    private static void UpdateColumn(DataContext db, String colName, String comments, String type, String tableName) {
        MapResult map = new MapResult();

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.MySql))
            map.setSql( String.format("alter table %s modify column %s %s comment‘%s’", tableName, colName, type, comments));

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.Oracle))
            map.setSql( String.format("Comment on column %s.%s is '%s'", tableName, colName, comments));

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.SqlServer)) {
            map.setSql( String.format("select count(0) count from syscolumns where id = object_id('%s') and name='%s'", tableName, colName));
            map.setSql( String.format("%s and exists(select 1 from sys.extended_properties where object_id('%s')=major_id and colid=minor_id)", map.getSql(), tableName));
            int count = db.count(map);
            if (count >= 1)
                map.setSql( String.format("execute sp_updateextendedproperty N'MS_Description', '%s', N'user', N'dbo', N'table', N'%s', N'column', %s", comments, tableName, colName));
            else
                map.setSql( String.format("execute sp_addextendedproperty N'MS_Description', '%s', N'user', N'dbo', N'table', N'%s', N'column', %s", comments, tableName, colName));
        }

        db.execute(map);
    }

    private static void updateComments(DataContext db, TableModel table) {
        MapResult map = new MapResult();

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.MySql))
            map.setSql( String.format("alter table %s comment '%s'", table.getName(), table.getComments()));

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.Oracle))
            map.setSql( String.format("Comment on table %s is '%s'", table.getName(), table.getComments()));

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.SqlServer)) {
            map.setSql( String.format("select count(0) count from sys.extended_properties where object_id('%s')=major_id and minor_id=0", table.getName()));
            int count = db.count(map);
            if (count >= 1)
                map.setSql( String.format("execute sp_updateextendedproperty N'MS_Description', '%s', N'user', N'dbo', N'table', N'%s', NULL, NULL", table.getComments(), table.getName()));
            else
                map.setSql( String.format("execute sp_addextendedproperty N'MS_Description', '%s', N'user', N'dbo', N'table', N'%s', NULL, NULL", table.getComments(), table.getName()));
        }
        db.execute(map);
    }

    private static TableModel getTable(DataContext db, String tableName) {
        TableModel result = new TableModel();
        result.setName( tableName);
        MapResult map = new MapResult();
        if (db.config.getDbType().equalsIgnoreCase(DataDbType.Oracle)) {
            map.setSql( String.format("select comments from user_tables a inner join user_tab_comments b on a.TABLE_NAME = b.TABLE_NAME  and a.table_name = '%s'", tableName.toUpperCase()));
            DataReturn dic = db.query(map);
            result.setComments(Optional.ofNullable(dic.getList().get(0).get("COMMENTS")).orElse("").toString().replace("'", ""));

            map.setSql( "select a.column_name,data_type,data_length,b.comments,");
            map.setSql( String.format("%s %s", map.getSql(), "(select count(0) from user_cons_columns aa, user_constraints bb where aa.constraint_name = bb.constraint_name and bb.constraint_type = 'P' and bb.table_name = '" + tableName.toUpperCase() + "' and aa.column_name = a.column_name) iskey,"));
            map.setSql( String.format("%s %s", map.getSql(), "nullable,data_precision,data_scale from user_tab_columns a inner join user_col_comments b"));
            map.setSql( String.format("%s %s", map.getSql(), "on a.table_name ='" + tableName.toUpperCase() + "' and a.table_name = b.table_name and a.column_name = b.column_name order by a.column_id asc"));
            dic = db.query(map);
            dic.getList().forEach(a -> {
                ColumnModel model = new ColumnModel();
                model.setComments( Optional.ofNullable(a.get("COMMENTS")).orElse("").toString().replace("'", ""));
                model.setDataType( a.get("DATA_TYPE").toString());
                model.setKey( a.get("ISKEY").toString().equals("1"));
                model.setNull( a.get("NULLABLE").toString().equals("Y"));
                model.setLength( Integer.parseInt(Optional.ofNullable(a.get("DATA_LENGTH")).orElse(0).toString()));
                model.setName( a.get("COLUMN_NAME").toString());
                model.setPrecision( Integer.parseInt(Optional.ofNullable(a.get("DATA_PRECISION")).orElse(0).toString()));
                model.setScale( Integer.parseInt(Optional.ofNullable(a.get("DATA_SCALE")).orElse(0).toString()));

                result.getColumn().add(model);
            });
        }

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.MySql)) {
            map.setSql( String.format("select table_comment count from information_schema.tables where upper(table_name)='%s'", tableName.toUpperCase()));
            DataReturn dic = db.query(map);
            result.setComments( Optional.ofNullable(dic.getList().get(0).get("COMMENTS")).orElse("").toString().replace("'", ""));

            map.setSql( "select column_name,data_type,character_maximum_length,column_comment,");
            map.setSql( String.format("%s %s", map.getSql(), "(select count(0) from INFORMATION_SCHEMA.KEY_COLUMN_USAGE a where upper(TABLE_NAME)='" + tableName.toUpperCase() + "' and constraint_name='PRIMARY' and c.column_name=a.column_name) iskey,"));
            map.setSql( String.format("%s %s", map.getSql(), "is_nullable,numeric_precision,numeric_scale from information_schema.columns c where upper(table_name)='" + tableName.toUpperCase() + "' order by ordinal_position asc"));
            dic = db.query(map);
            dic.getList().forEach(a -> {
                ColumnModel model = new ColumnModel();
                model.setComments( Optional.ofNullable(a.get("column_comment")).orElse("").toString().replace("'", ""));
                model.setDataType( a.get("data_type").toString());
                model.setKey( a.get("iskey").toString().equals("1"));
                model.setNull( a.get("is_nullabl").toString().equals("YES"));
                model.setLength( Integer.parseInt(Optional.ofNullable(a.get("character_maximum_length")).orElse(0).toString()));
                model.setName(a.get("column_name").toString());
                model.setPrecision( Integer.parseInt(Optional.ofNullable(a.get("numeric_precision")).orElse(0).toString()));
                model.setScale(Integer.parseInt(Optional.ofNullable(a.get("numeric_scale")).orElse(0).toString()));
                result.getColumn().add(model);
            });
        }

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.SqlServer)) {
            map.setSql( String.format("select name,(select top 1 value from sys.extended_properties where major_id=object_id(a.name) and minor_id=0) as value from sys.objects a where type = 'U' and upper(name) = '%s'", tableName.toUpperCase()));
            DataReturn dic = db.query(map);
            result.setComments(Optional.ofNullable(dic.getList().get(0).get("COMMENTS")).orElse("").toString().replace("'", ""));

            map.setSql( "select a.name,(select top 1 name from sys.systypes c where a.xtype=c.xtype) as type ,");
            map.setSql( String.format("%s %s", map.getSql(), "length,b.value,(select count(0) from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where TABLE_NAME='" + tableName.toUpperCase() + "' and COLUMN_NAME=a.name) as iskey,"));
            map.setSql( String.format("%s %s", map.getSql(), "isnullable,prec,scale from syscolumns a left join sys.extended_properties b"));
            map.setSql( String.format("%s %s", map.getSql(), "on major_id = id and minor_id = colid and b.name ='MS_Description'  where a.id=object_id('" + tableName.toUpperCase() + "') order by a.colid asc"));
            dic = db.query(map);
            dic.getList().forEach(a -> {
                ColumnModel model = new ColumnModel();
                model.setComments(Optional.ofNullable(a.get("value")).orElse("").toString().replace("'", ""));
                model.setDataType( a.get("type").toString());
                model.setKey(a.get("iskey").toString().equals("1"));
                model.setNull(a.get("isnullable").toString().equals("1"));
                model.setLength(Integer.parseInt(Optional.ofNullable(a.get("length")).orElse(0).toString()));
                model.setName(a.get("name").toString());
                model.setPrecision(Integer.parseInt(Optional.ofNullable(a.get("prec")).orElse(0).toString()));
                model.setScale(Integer.parseInt(Optional.ofNullable(a.get("scale")).orElse(0).toString()));
                result.getColumn().add(model);
            });
        }

        result.getColumn().forEach(a -> {
            if (a.getDataType().equalsIgnoreCase("nchar")
                    || a.getDataType().equalsIgnoreCase("nvarchar")
                    || a.getDataType().equalsIgnoreCase("nvarchar2")
                    || a.getDataType().equalsIgnoreCase("ntext")
                    || a.getDataType().equalsIgnoreCase("nclob"))
                a.setLength(a.getLength() / 2);
        });
        return result;
    }

    private static boolean isExistsTable(String tableName, DataContext db) {
        MapResult map = new MapResult();
        map.getParam().put(tableName, tableName.toUpperCase());

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.Oracle))
            map.setSql("select count(0) count from user_tables where table_name=?");

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.SqlServer))
            map.setSql("select count(0) count from dbo.sysobjects where upper(name)=?");

        if (db.config.getDbType().equalsIgnoreCase(DataDbType.MySql))
            map.setSql("select count(0) count from information_schema.tables where upper(table_name)=?");

        return db.count(map) > 0;
    }

    private static List<ColumnModel> getColumnClass(Class<?> type) {
        List<ColumnModel> list = new ArrayList<ColumnModel>();
        Arrays.stream(type.getDeclaredFields()).sequential().forEach(a -> {
            Column info = a.getAnnotation(Column.class);
            if(info!=null) {
                ColumnModel temp = new ColumnModel();
                temp.setComments(info.comments().replace("'", ""));
                temp.setDataType( info.dataType());
                temp.setKey(info.isKey());
                if (temp.isKey())
                    temp.setNull(false);
                else
                    temp.setNull(info.isNull());
                temp.setLength(info.length());
                temp.setName(a.getName());
                temp.setPrecision(info.precision());
                temp.setScale(info.scale());
                list.add(temp);
            }
        });
        return list;
    }

    private static String getTableClass(Class<?> type) {
        TableModel result = new TableModel();
        Table info = type.getAnnotation(Table.class);
        if (info != null) {
            result.setComments(info.comments().replace("'",""));
        }
        return result.getComments();
    }

    private static String getFieldKey(ColumnModel item) {
        if (item.isKey())
            return "not null";
        else if (!item.isNull())
            return "not null";
        else
            return "";
    }

    private static String getFieldType(ColumnType item){
        switch (item.getType().toLowerCase())
        {
            case "char":
            case "nchar":
            case "varchar":
            case "nvarchar":
            case "varchar2":
            case "nvarchar2":
                return String.format("%s(%s)", item.getType(), item.getLength());
            case "decimal":
            case "numeric":
            case "number":
                if (item.getPrecision() == 0 && item.getScale() == 0)
                    return item.getType();
                else
                    return String.format("decimal(%s,%s)", item.getPrecision(), item.getScale());
            default:
                return item.getType();
        }
    }

    private static String getFieldType(ColumnModel item) {
        switch (item.getDataType().toLowerCase()) {
            case "char":
            case "nchar":
            case "varchar":
            case "nvarchar":
            case "varchar2":
            case "nvarchar2":
                return String.format("%s(%s)", item.getDataType(), item.getLength());
            case "decimal":
            case "numeric":
            case "number":
                if (item.getPrecision() == 0 && item.getScale() == 0)
                    return String.format("%s", item.getDataType());
                else
                    return String.format("%s(%s,%s)", item.getDataType(), item.getPrecision(), item.getScale());
            default:
                return item.getDataType();
        }
    }
}