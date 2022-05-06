package org.FastData.Spring.Repository;

import org.FastData.Spring.Context.DataContext;
import org.FastData.Spring.Function.FastExpression;
import org.FastData.Spring.Model.MapResult;
import org.FastData.Spring.Model.WriteReturn;
import org.FastData.Spring.Util.FastUtil;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Write <T> implements IWrite<T> {
    private Class<T> type;
    private LinkedHashMap<String, Object> field = new LinkedHashMap<>();
    private String table;
    private String where;
    private int i;
    private LinkedHashMap<String, Object> param = new LinkedHashMap<>();

    public Write(Class<T> _type) {
        type = _type;
    }

    private void sql(FastExpression<T, Object> expression, String condtion, Object value1, Object value2) {
        try {
            Method method = expression.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) method.invoke(expression);
            String name = lambda.getImplMethodName().replace("get", "");
            String key = String.format("%s%s", name, i++);
            String flag = "?";

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
            } else
                param.put(key, value1);

            if (FastUtil.isNullOrEmpty(where)) {
                table = lambda.getImplClass().substring(lambda.getImplClass().lastIndexOf("/") + 1);
                where = String.format("where %s %s %s", name, condtion, flag);
            }
            else if(condtion.equals("eqLower"))
                where = String.format("where lower(%s) = lower(%s)", name, flag);
            else if(condtion.equals("eqUpper"))
                where = String.format("where upper(%s) = upper(%s)", name, flag);
            else
                where = String.format(" %s and %s %s %s", where, name, condtion, flag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public IWrite<T> eq(FastExpression<T, Object> expression, Object value) {
        sql(expression,"=",value,null);
        return this;
    }

    @Override
    public IWrite<T> eqLower(FastExpression<T, Object> expression, Object value) {
        sql(expression,"eqUpper",value,null);
        return this;
    }

    @Override
    public IWrite<T> eqUpper(FastExpression<T, Object> expression, Object value) {
        sql(expression,"eqUpper",value,null);
        return this;
    }

    @Override
    public IWrite<T> notEq(FastExpression<T, Object> expression, Object value) {
        sql(expression,"!=",value,null);
        return this;
    }

    @Override
    public IWrite<T> great(FastExpression<T, Object> expression, Object value) {
        sql(expression,">",value,null);
        return this;
    }

    @Override
    public IWrite<T> greatEq(FastExpression<T, Object> expression, Object value) {
        sql(expression,">=",value,null);
        return this;
    }

    @Override
    public IWrite<T> less(FastExpression<T, Object> expression, Object value) {
        sql(expression,"<",value,null);
        return this;
    }

    @Override
    public IWrite<T> lessEq(FastExpression<T, Object> expression, Object value) {
        sql(expression,"<=",value,null);
        return this;
    }

    @Override
    public IWrite<T> beginLike(FastExpression<T, Object> expression, Object value) {
        sql(expression, "beginLike", value, null);
        return this;
    }

    @Override
    public IWrite<T> endLike(FastExpression<T, Object> expression, Object value) {
        sql(expression, "endLike", value, null);
        return this;
    }

    @Override
    public IWrite<T> like(FastExpression<T, Object> expression, Object value) {
        sql(expression, "like", value, null);
        return this;
    }

    @Override
    public IWrite<T> in(FastExpression<T, Object> expression, Arrays value) {
        sql(expression, "in", value, null);
        return this;
    }

    @Override
    public IWrite<T> between(FastExpression<T, Object> expression, Object value1, Object value2) {
        sql(expression, "between", value1, value2);
        return this;
    }

    @Override
    public IWrite<T> set(FastExpression<T, Object> expression,Object value) {
        try {
            Method method = expression.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) method.invoke(expression);
            String name = lambda.getImplMethodName().replace("get", "");
            field.put(name, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public WriteReturn toUpdate(DataContext db) {
        if (FastUtil.isNullOrEmpty(where)) {
            WriteReturn writeReturn = new WriteReturn();
            writeReturn.setMessage("where 条件不能为空");
            writeReturn.setSuccess(false);
            return writeReturn;
        }
        MapResult result = new MapResult();
        String sql = String.format("update %s set ", table);
        for (String k : field.keySet()) {
            result.getParam().put(k, field.get(k));
            sql = String.format(" %s %s=?,", sql, k);
        }
        for (String k : param.keySet()) {
            result.getParam().put(k, param.get(k));
        }
        sql = String.format("%s %s", sql.substring(0, sql.length() - 1), where);
        result.setSql(sql);
        return db.execute(result, true).getWriteReturn();
    }

    @Override
    public WriteReturn toUpdate(String key) {
        if (FastUtil.isNullOrEmpty(where)) {
            WriteReturn writeReturn = new WriteReturn();
            writeReturn.setMessage("where 条件不能为空");
            writeReturn.setSuccess(false);
            return writeReturn;
        }
        try (DataContext db = new DataContext(key)) {
            MapResult result = new MapResult();
            String sql = String.format("update %s set ", table);

            for (String k : field.keySet()) {
                result.getParam().put(k, field.get(k));
                sql = String.format(" %s %s=?,", sql, k);
            }
            for (String k : param.keySet()) {
                result.getParam().put(k, param.get(k));
            }
            sql = String.format("%s %s", sql.substring(0,sql.length()-1), where);
            result.setSql(sql);
            return db.execute(result, true).getWriteReturn();
        }
    }

    @Override
    public WriteReturn toDelete(DataContext db) {
        if (FastUtil.isNullOrEmpty(where)) {
            WriteReturn writeReturn = new WriteReturn();
            writeReturn.setMessage("where 条件不能为空");
            writeReturn.setSuccess(false);
            return writeReturn;
        }
        MapResult map = new MapResult();
        map.setParam(param);
        map.setSql(String.format("delete %s %s", table, where));
        return db.execute(map, true).getWriteReturn();
    }

    @Override
    public WriteReturn toDelete(String key) {
        if (FastUtil.isNullOrEmpty(where)) {
            WriteReturn writeReturn = new WriteReturn();
            writeReturn.setMessage("where 条件不能为空");
            writeReturn.setSuccess(false);
            return writeReturn;
        }
        try (DataContext db = new DataContext(key)) {
            MapResult map = new MapResult();
            map.setParam(param);
            map.setSql(String.format("delete %s %s", table, where));
            return db.execute(map, true).getWriteReturn();
        }
    }
}
