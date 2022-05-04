package org.FastData.Spring.Repository;

import org.FastData.Spring.Model.*;
import org.FastData.Spring.Context.DataContext;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public interface IFastRepository {
    /*
    name: xml file -> sqlMap -> id
    param: sql preparedStatement
    type: model type
    key: database key
    */
    List<?> queryMap(String name, Map<String, Object> param, Class<?> type, String key);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      db: database context
     */
    List<?> queryMap(String name, Map<String, Object> param, Class<?> type, DataContext db);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      xml file -> sql have db key value (db="dbkey")
     log:sql log
     */
    List<?> queryMap(String name, Map<String, Object> param, Class<?> type);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      key: database key
    */
    List<FastMap<String, Object>> queryMap(String name, Map<String, Object> param, String key);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      db: database context
    */
    List<FastMap<String, Object>> queryMap(String name, Map<String, Object> param, DataContext db);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      xml file -> sql have db key value (db="dbkey")
    */
    List<FastMap<String, Object>> queryMap(String name, Map<String, Object> param);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      key: database key
    */
    PageResultImpl<?> pageMap(PageModel pModel, String name, Map<String, Object> param, Class<?> type, String key);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      db: database context
    */
    PageResultImpl<?> pageMap(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      xml file -> sql have db key value (db="dbkey")
    */
    PageResultImpl<?> pageMap(PageModel pModel, String name, Map<String, Object> param, Class<?> type);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      key: database key
    */
    PageResult pageMap(PageModel pModel, String name, Map<String, Object> param, String key);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      db: database context
    */
    PageResult pageMap(PageModel pModel, String name, Map<String, Object> param, DataContext db);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      xml file -> sql have db key value (db="dbkey")
    */
    PageResult pageMap(PageModel pModel, String name, Map<String, Object> param);

    /*
     insert table
      model: database table
      db: database context
    */
    WriteReturn add(Object model, DataContext db);

    /*
    insert table
     model: database table
     key: database key
   */
    WriteReturn add(Object model, String key);

    /*
     model: database table
     field: update table field by primary Key
     db: database context
    */
    WriteReturn updateKey(Object model, List<String> field, DataContext db);

    /*
     update table by primary Key
     model: database table
     field: update field
     key: database key
    */
    WriteReturn updateKey(Object model, List<String> field, String key);

    /*
     update table by primary Key
     model: database table
     key: database key
    */
    WriteReturn updateKey(Object model,String key);

    /*
     update table by primary Key
     model: database table
     db: database context
    */
    WriteReturn updateKey(Object model,DataContext db);

    /*
     delete table by primary Key
     model: database table
     db: database context
    */
    WriteReturn deleteKey(Object model, DataContext db);

    /*
     delete table by primary Key
     model: database table
     key: database key
    */
    WriteReturn deleteKey(Object model, String key);

    /*
     query exists by primary Key
     model: database table
     db: database context
    */
    boolean existsKey(Object model, DataContext db);

    /*
     query exists by primary Key
     model: database table
     key: database key
    */
    boolean existsKey(Object model, String key);

    /*
     query model by primary Key
     model: database table
     key: database key
   */
    Object queryKey(Object model,Class<?> type, String key);

    /*
       query model by primary Key
       model: database table
      db: database context
    */
    Object queryKey(Object model,Class<?> type, DataContext db);

    /*
      query model by primary Key
      model: database table
         key: database key
    */
    FastMap<String,Object> queryKey(Object model, String key);

    /*
      query model by primary Key
      model: database table
      db: database context
    */
    FastMap<String,Object> queryKey(Object model, DataContext db);

    Object resolve(Class<?> interfaces,String key);

    Object resolve(Class<?> interfaces,DataContext db);

    <T> Read<T> read(Class<T> type);

    <T> Write<T> write(Class<T> type);
}
