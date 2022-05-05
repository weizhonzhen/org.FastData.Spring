package org.FastData.Spring.Repository;

import org.FastData.Spring.Context.DataContext;
import org.FastData.Spring.Function.FastExpression;
import org.FastData.Spring.Model.*;
import org.FastData.Spring.Util.FastUtil;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Read<T> implements IRead<T> {
    private Class<T> type;
    private String where;
    private  int i;
    private List<String> field =new ArrayList<>();
    private LinkedHashMap<String, Object> param = new LinkedHashMap<>();

    public Read(Class<T> _type) {
        type = _type;
    }

    private void sql(FastExpression<T, Object> expression, String condtion,Object value1,Object value2) {
        try {
            Method method = expression.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) method.invoke(expression);
            String name = lambda.getImplMethodName().replace("get", "");
            String key = String.format("%s%s",name, i++);
            String flag = "?";

            if(condtion.equals("select")) {
                field.add(name);
                return;
            }

            if (condtion.equals("like"))
                param.put(key, String.format("%s%%", value1));
            else if (condtion.equals("beginLike")) {
                param.put(key, String.format("%s%", value1));
                condtion = "like";
            } else if (condtion.equals("endLike")) {
                param.put(key, String.format("s%%", value1));
                condtion = "like";
            } else if (condtion.equals("in")) {
                param.put(key, String.format("%s%%", String.join(",", (CharSequence) value1)));
                flag = "(?)";
            } else if (condtion.equals("between")) {
                param.put(key, value1);
                param.put(key, value2);
                flag = "? and ?";
            } else if (condtion.equals("orderByDesc")) {
                flag = "desc";
                condtion = "order by";
            } else if (condtion.equals("orderByAsc")) {
                flag = "asc";
                condtion = "order by";
            } else
                param.put(key, value1);

            if (FastUtil.isNullOrEmpty(where)) {
                String tableName = lambda.getImplClass().substring(lambda.getImplClass().lastIndexOf("/") + 1);
                if (condtion.equals("order by"))
                    where = String.format("select * from %s order by %s %s", tableName, name, flag);
                else
                    where = String.format("select * from %s where %s %s %s", tableName, name, condtion, flag);
            } else {
                if (condtion.equals("order by"))
                    where = String.format(" %s order by %s %s", where, name, flag);
                else
                    where = String.format(" %s and %s %s %s", where, name, condtion, flag);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public IRead<T> eq(FastExpression<T, Object> expression, Object value) {
        sql(expression, "=", value, null);
        return this;
    }

    @Override
    public IRead<T> notEq(FastExpression<T, Object> expression, Object value) {
        sql(expression, "!=", value, null);
        return this;
    }

    @Override
    public IRead<T> great(FastExpression<T, Object> expression, Object value) {
        sql(expression, ">", value, null);
        return this;
    }

    @Override
    public IRead<T> greatEq(FastExpression<T, Object> expression, Object value) {
        sql(expression, ">=", value, null);
        return this;
    }

    @Override
    public IRead<T> less(FastExpression<T, Object> expression, Object value) {
        sql(expression, "<", value, null);
        return this;
    }

    @Override
    public IRead<T> lessEq(FastExpression<T, Object> expression, Object value) {
        sql(expression, "=<", value, null);
        return this;
    }

    @Override
    public IRead<T> like(FastExpression<T, Object> expression, Object value) {
        sql(expression, "like", value, null);
        return this;
    }

    @Override
    public IRead<T> beginLike(FastExpression<T, Object> expression, Object value) {
        sql(expression, "beginLike", value, null);
        return this;
    }

    @Override
    public IRead<T> endLike(FastExpression<T, Object> expression, Object value) {
        sql(expression, "endLike", value, null);
        return this;
    }

    @Override
    public IRead<T> in(FastExpression<T, Object> expression, Arrays value) {
        sql(expression, "in", value, null);
        return this;
    }

    @Override
    public IRead<T> between(FastExpression<T, Object> expression, Object value1, Object value2) {
        sql(expression, "between", value1, value2);
        return this;
    }

    @Override
    public IRead<T> orderBy(FastExpression<T, Object> expression, boolean isDesc) {
        if (isDesc)
            sql(expression, "orderByDesc", null, null);
        else
            sql(expression, "orderByAsc", null, null);
        return this;
    }

    @Override
    public IRead<T> select(List<FastExpression<T, Object>> expression) {
        for (FastExpression<T, Object> item : expression)
            sql(item, "select", null, null);
        return this;
    }

    @Override
    public List<T> toList(String key) {
        try (DataContext db = new DataContext(key)) {
            MapResult result = new MapResult();
            if (field.size() > 0)
                result.setSql(where.replace("*", String.join(",", field)));
            else
                result.setSql(where);
            result.setParam(param);
            return db.query(result, type, true).getList();
        }
    }

    private MapResult setSql(MapResult map)
    {
        if (field.size() > 0)
            map.setSql(where.replace("*", String.join(",", field)));
        else
            map.setSql(where);
        return map;
    }

    @Override
    public List<T> toList(DataContext db) {
        MapResult result = new MapResult();
        setSql(result);
        result.setParam(param);
        return  db.query(result, type, true).getList();
    }

    @Override
    public T toItem(String key) {
        try {
            return toList(key).stream().findFirst().orElse(type.newInstance());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public T toItem(DataContext db) {
        try {
            return toList(db).stream().findFirst().orElse(type.newInstance());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<FastMap<String, Object>> toMap(String key) {
        try (DataContext db = new DataContext(key)) {
            MapResult result = new MapResult();
            setSql(result);
            result.setParam(param);
            return db.query(result, true).getList();
        }
    }

    @Override
    public List<FastMap<String, Object>> toMap(DataContext db) {
        MapResult result = new MapResult();
        setSql(result);
        result.setParam(param);
        return  db.query(result, true).getList();
    }

    @Override
    public PageResultImpl<T> toPage(PageModel page, String key) {
        try (DataContext db = new DataContext(key)) {
            MapResult result = new MapResult();
            setSql(result);
            result.setParam(param);
            return db.page(page, result, type, true);
        }
    }

    @Override
    public PageResultImpl<T> toPage(PageModel page, DataContext db) {
        MapResult result = new MapResult();
        setSql(result);
        result.setParam(param);
        return db.page(page, result, type, true);
    }

    @Override
    public PageResult toPageMap(PageModel page, String key) {
        try (DataContext db = new DataContext(key)) {
            MapResult result = new MapResult();
            setSql(result);
            result.setParam(param);
            return db.page(page, result, true);
        }
    }

    @Override
    public PageResult toPageMap(PageModel page, DataContext db) {
        MapResult result = new MapResult();
        setSql(result);
        result.setParam(param);
        return db.page(page, result, true);
    }

    @Override
    public int toCount(String key) {
        try (DataContext db = new DataContext(key)) {
            MapResult result = new MapResult();
            result.setSql(where.replace("select * from","select count(0) from"));
            result.setParam(param);
            return db.count(result, true);
        }
    }

    @Override
    public int toCount(DataContext db) {
        MapResult result = new MapResult();
        result.setSql(where.replace("select * from","select count(0) from"));
        result.setParam(param);
        return db.count(result, true);
    }
}
