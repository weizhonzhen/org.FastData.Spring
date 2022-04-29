package org.FastData.Spring.Repository;

import org.FastData.Spring.Context.DataContext;
import org.FastData.Spring.Function.FastExpression;
import org.FastData.Spring.Model.FastMap;
import org.FastData.Spring.Model.PageModel;
import org.FastData.Spring.Model.PageResult;
import org.FastData.Spring.Model.PageResultImpl;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public interface IFastQuery<T>{
    IFastQuery<T> eq(FastExpression<T, Object> expression, Object value);

    IFastQuery<T> notEq(FastExpression<T, Object> expression, Object value);

    IFastQuery<T> great(FastExpression<T, Object> expression, Object value);

    IFastQuery<T> greatEq(FastExpression<T, Object> expression, Object value);

    IFastQuery<T> less(FastExpression<T, Object> expression, Object value);

    IFastQuery<T> lessEq(FastExpression<T, Object> expression, Object value);

    IFastQuery<T> beginLike(FastExpression<T, Object> expression, Object value);

    IFastQuery<T> endLike(FastExpression<T, Object> expression, Object value);

    IFastQuery<T> like(FastExpression<T, Object> expression, Object value);

    IFastQuery<T> in(FastExpression<T, Object> expression, Arrays value);

    IFastQuery<T> between(FastExpression<T, Object> expression, Object value1, Object value2);

    IFastQuery<T> orderBy(FastExpression<T, Object> expression, boolean isDesc);

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
