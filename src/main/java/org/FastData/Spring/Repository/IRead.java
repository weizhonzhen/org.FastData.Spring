package org.FastData.Spring.Repository;

import org.FastData.Spring.Context.DataContext;
import org.FastData.Spring.Function.FastExpression;
import org.FastData.Spring.Model.FastMap;
import org.FastData.Spring.Model.PageModel;
import org.FastData.Spring.Model.PageResult;
import org.FastData.Spring.Model.PageResultImpl;
import java.util.Arrays;
import java.util.List;

public interface IRead<T>{
    IRead<T> eq(FastExpression<T, Object> expression, Object value);

    IRead<T> eqLower(FastExpression<T, Object> expression, Object value);

    IRead<T> eqUpper(FastExpression<T, Object> expression, Object value);

    IRead<T> take(FastExpression<T,Object> expression,int value);

    IRead<T> notEq(FastExpression<T, Object> expression, Object value);

    IRead<T> great(FastExpression<T, Object> expression, Object value);

    IRead<T> greatEq(FastExpression<T, Object> expression, Object value);

    IRead<T> less(FastExpression<T, Object> expression, Object value);

    IRead<T> lessEq(FastExpression<T, Object> expression, Object value);

    IRead<T> beginLike(FastExpression<T, Object> expression, Object value);

    IRead<T> endLike(FastExpression<T, Object> expression, Object value);

    IRead<T> like(FastExpression<T, Object> expression, Object value);

    IRead<T> in(FastExpression<T, Object> expression, Arrays value);

    IRead<T> between(FastExpression<T, Object> expression, Object value1, Object value2);

    IRead<T> orderBy(FastExpression<T, Object> expression, boolean isDesc);

    IRead<T> select(List<FastExpression<T, Object>> expression);

    List<T> toList(String key);

    List<T> toList(DataContext db);

    T toItem(String key);

    T toItem(DataContext db);

    List<FastMap<String, Object>> toMap(String key);

    List<FastMap<String, Object>> toMap(DataContext db);

    PageResultImpl<T> toPage(PageModel page,String key);

    PageResultImpl<T> toPage(PageModel page,DataContext db);

    PageResult toPageMap(PageModel page, String key);

    PageResult toPageMap(PageModel page,DataContext db);

    int toCount(String key);

    int toCount(DataContext db);
}
