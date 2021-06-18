package org.FastData.Spring.Base;

import org.FastData.Spring.CacheModel.DbConfig;
import org.FastData.Spring.CacheModel.PropertyModel;
import org.FastData.Spring.Model.MapResult;
import org.FastData.Spring.Config.DataDbType;
import org.FastData.Spring.Util.CacheUtil;
import org.FastData.Spring.Util.ReflectUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class BaseModel {
    public static MapResult insert(Object model) {
        Class<?> type = model.getClass();
        MapResult result = new MapResult();
        String cacheKey = type.getName();
        List<PropertyModel> property = CacheUtil.getList(cacheKey, PropertyModel.class);
        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();
        name.append(String.format("insert into %s (", type.getName().replace(type.getPackage().getName(), "").replace(".", "")));
        value.append(" values (");
        property.forEach(a -> {
            name.append(String.format("%s,", a.getName()));
            value.append("?,");
            result.getParam().put(a.getName(), ReflectUtil.get(model,a.getName(),a.getType()));
        });
        result.setSql(String.format("%s) %s)", name.substring(0, name.length() - 1), value.substring(0, value.length() - 1)));
        return result;
    }

    public static MapResult delete(Object model, DbConfig config, Connection conn) {
        MapResult result = new MapResult();
        result.setSuccess(true);
        Class<?> type=model.getClass();
        List<PropertyModel> property = CacheUtil.getList(type.getName(), PropertyModel.class);
        String tableName = type.getName().replace(type.getPackage().getName(), "").replace(".", "");
        List<String > list = primaryKey(config, conn, tableName);
        if (list.size() == 0) {
            result.setSuccess(false);
            result.setMessage(String.format("%s没有主键", tableName));
        }

        result.setSql(String.format("delete %s ", tableName));
        result = check(model,tableName,result,list,property);
        return result;
    }

    public static MapResult exists(Object model, DbConfig config, Connection conn) {
        MapResult result = new MapResult();
        result.setSuccess(true);
        Class<?> type=model.getClass();
        List<PropertyModel> property = CacheUtil.getList(type.getName(), PropertyModel.class);
        String tableName = type.getName().replace(type.getPackage().getName(), "").replace(".", "");
        List<String > list = primaryKey(config, conn, tableName);
        if (list.size() == 0) {
            result.setSuccess(false);
            result.setMessage(String.format("%s没有主键", tableName));
        }

        result.setSql(String.format("select count(0) from %s ", tableName));
        result = check(model,tableName,result,list,property);
        return result;
    }

    public static MapResult query(Object model, DbConfig config, Connection conn) {
        MapResult result = new MapResult();
        result.setSuccess(true);
        Class<?> type=model.getClass();
        List<PropertyModel> property = CacheUtil.getList(type.getName(), PropertyModel.class);
        String tableName = type.getName().replace(type.getPackage().getName(), "").replace(".", "");
        List<String > list = primaryKey(config, conn, tableName);
        if (list.size() == 0) {
            result.setSuccess(false);
            result.setMessage(String.format("%s没有主键", tableName));
        }

        result.setSql(String.format("select * from %s ", tableName));
        result = check(model,tableName,result,list,property);
        return result;
    }

    public static MapResult update(Object model,List<String> field, DbConfig config, Connection conn) {
        MapResult result = new MapResult();
        result.setSuccess(true);
        Class<?> type=model.getClass();
        List<PropertyModel> property = CacheUtil.getList(type.getName(), PropertyModel.class);
        String tableName = type.getName().replace(type.getPackage().getName(), "").replace(".", "");
        List<String> list = primaryKey(config, conn, tableName);
        if (list.size() == 0) {
            result.setSuccess(false);
            result.setMessage(String.format("%s没有主键", tableName));
        }

        result.setSql(String.format("update %s set", tableName));

        if (field.size() == 0) {
            result.setSuccess(false);
            result.setMessage(String.format("%s修改字段不能为空", tableName));
        }

        for (String item : field) {
            if(property.stream().anyMatch(a-> a.getName().equalsIgnoreCase(item))) {
                PropertyModel pinfo=property.stream().filter(a-> a.getName().equalsIgnoreCase(item)).findFirst().get();
                result.setSql(String.format("%s %s=?,", result.getSql(), pinfo.getName()));
                result.getParam().put(pinfo.getName(), ReflectUtil.get(model,pinfo.getName(),pinfo.getType()));
            }
            else {
                result.setSuccess(false);
                result.setMessage(String.format("表%s,字段%s不存在", tableName, item));
                return result;
            }
        }

        result.setSql(result.getSql().substring(0, result.getSql().length() - 1));;
        result = check(model,tableName,result,list,property);
        return result;
    }

    public static MapResult update(Object model,DbConfig config, Connection conn) {
        MapResult result = new MapResult();
        result.setSuccess(true);
        Class<?> type=model.getClass();
        List<PropertyModel> property = CacheUtil.getList(type.getName(), PropertyModel.class);
        String tableName = type.getName().replace(type.getPackage().getName(), "").replace(".", "");
        List<String> list = primaryKey(config, conn, tableName);

        if (list.size() == 0) {
            result.setSuccess(false);
            result.setMessage(String.format("%s没有主键", tableName));
        }

        result.setSql(String.format("update %s set ", tableName));;

        for (PropertyModel item : property) {
            result.setSql(String.format("%s %s=?,", result.getSql(), item.getName()));
            result.getParam().put(item.getName(), ReflectUtil.get(model, item.getName(),item.getType()));
        }

        result.setSql(result.getSql().substring(0, result.getSql().length() - 1));
        result = check(model,tableName,result,list,property);
        return result;
    }

    private static List<String> primaryKey(DbConfig config, Connection conn,String tableName) {
        List<String> list = new ArrayList<String>();
        String sql = "";
        if (config.getDbType().equalsIgnoreCase(DataDbType.Oracle))
            sql = String.format("select a.COLUMN_NAME as name from all_cons_columns a,all_constraints b where a.constraint_name = b.constraint_name and b.constraint_type = 'P' and b.table_name = '%s'", tableName.toUpperCase());

        if (config.getDbType().equalsIgnoreCase(DataDbType.SqlServer))
            sql = String.format("select column_name as name from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where TABLE_NAME='%s'", tableName);

        if (config.getDbType().equalsIgnoreCase(DataDbType.MySql))
            sql = String.format("select column_name as name from INFORMATION_SCHEMA.KEY_COLUMN_USAGE a where TABLE_NAME='%s' and constraint_name='PRIMARY'", tableName.toUpperCase());

        if (config.getDbType().equalsIgnoreCase(DataDbType.DB2))
            sql = String.format("select a.colname as name from sysibm.syskeycoluse a，syscat.tabconst b where a.tabname=b.tabnameand b.tabname='%s' and b.type=p", tableName.toUpperCase());

        if (sql.equals(""))
            return list;
        else {
            try {
                Statement statement = conn.createStatement();
                ResultSet resultset = statement.executeQuery(sql);
                while (resultset.next()) {
                    list.add(resultset.getString("name"));
                }
                resultset.close();
                statement.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    private static MapResult check(Object model,String tableName,MapResult result,List<String> list,List<PropertyModel> property) {
        int count = 1;
        for (String item : list) {
            if (property.stream().anyMatch(a -> a.getName().equalsIgnoreCase(item))) {
                PropertyModel pinfo = property.stream().filter(a -> a.getName().equalsIgnoreCase(item)).findFirst().get();
                Object value = ReflectUtil.get(model, pinfo.getName(),pinfo.getType());
                if (value == null) {
                    result.setSuccess(false);
                    result.setMessage(String.format("主键%s值为空", item));
                    return result;
                }

                result.getParam().put(item, value);
            } else {
                result.setSuccess(false);
                result.setMessage(String.format("表%s,字段%s不存在", tableName, item));
                return result;
            }

            if (count == 1)
                result.setSql(String.format("%s where %s=? ", result.getSql(), item));
            else
                result.setSql(String.format("%s and %s=?", result.getSql(), item));

            count++;
        }

        return result;
    }
}