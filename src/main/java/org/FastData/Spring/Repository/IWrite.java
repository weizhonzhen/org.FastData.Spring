package org.FastData.Spring.Repository;

import org.FastData.Spring.Context.DataContext;
import org.FastData.Spring.Function.FastExpression;
import org.FastData.Spring.Model.WriteReturn;
import java.util.Arrays;

public interface IWrite<T> {
    IWrite<T> eq(FastExpression<T, Object> expression, Object value);

    IWrite<T> notEq(FastExpression<T, Object> expression, Object value);

    IWrite<T> great(FastExpression<T, Object> expression, Object value);

    IWrite<T> greatEq(FastExpression<T, Object> expression, Object value);

    IWrite<T> less(FastExpression<T, Object> expression, Object value);

    IWrite<T> lessEq(FastExpression<T, Object> expression, Object value);

    IWrite<T> beginLike(FastExpression<T, Object> expression, Object value);

    IWrite<T> endLike(FastExpression<T, Object> expression, Object value);

    IWrite<T> like(FastExpression<T, Object> expression, Object value);

    IWrite<T> in(FastExpression<T, Object> expression, Arrays value);

    IWrite<T> between(FastExpression<T, Object> expression, Object value1, Object value2);

    IWrite<T> update(FastExpression<T, Object> expression,Object value);

    WriteReturn toUpdate(DataContext db);

    WriteReturn toUpdate(String key);

    WriteReturn toDelete(DataContext db);

    WriteReturn toDelete(String key);
}
