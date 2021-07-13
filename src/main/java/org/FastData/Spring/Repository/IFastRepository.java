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
    List<?> query(String name, Map<String, Object> param, Class<?> type, String key);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      db: database context
     */
    List<?> query(String name, Map<String, Object> param, Class<?> type, DataContext db);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      db: database context
     log:sql log
     */
    List<?> query(String name, Map<String, Object> param, Class<?> type, DataContext db,Boolean log);

    /*
    name: xml file -> sqlMap -> id
    param: sql preparedStatement
    type: model type
    key: database key
     log:sql log
    */
    List<?> query(String name, Map<String, Object> param, Class<?> type, String key,Boolean log);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      xml file -> sql have db key value (db="dbkey")
     log:sql log
     */
    List<?> query(String name, Map<String, Object> param, Class<?> type);

    /*
     name: xml file -> sqlMap -> id
     param: sql preparedStatement
     type: model type
     xml file -> sql have db key value (db="dbkey")
     log:sql log
    */
    List<?> query(String name, Map<String, Object> param, Class<?> type,Boolean log);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      key: database key
    */
    List<FastMap<String, Object>> query(String name, Map<String, Object> param, String key);

    /*
     name: xml file -> sqlMap -> id
     param: sql preparedStatement
     key: database key
     log:sql log
   */
    List<FastMap<String, Object>> query(String name, Map<String, Object> param, String key,Boolean log);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      db: database context
    */
    List<FastMap<String, Object>> query(String name, Map<String, Object> param, DataContext db);

    /*
         name: xml file -> sqlMap -> id
         param: sql preparedStatement
         db: database context
     log:sql log
       */
    List<FastMap<String, Object>> query(String name, Map<String, Object> param, DataContext db,Boolean log);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      xml file -> sql have db key value (db="dbkey")
    */
    List<FastMap<String, Object>> query(String name, Map<String, Object> param);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      xml file -> sql have db key value (db="dbkey")
     log:sql log
    */
    List<FastMap<String, Object>> query(String name, Map<String, Object> param,Boolean log);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      key: database key
    */
    PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, String key);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      key: database key
      log:sql log
    */
    PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, String key,Boolean log);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      db: database context
    */
    PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db);


    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      db: database context
      log:sql log
    */
    PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db,Boolean log);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      xml file -> sql have db key value (db="dbkey")
    */
    PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type);


    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      type: model type
      xml file -> sql have db key value (db="dbkey")
      log:sql log
    */
    PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type,Boolean log);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      key: database key
    */
    PageResult page(PageModel pModel, String name, Map<String, Object> param, String key);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      key: database key
      log:sql log
    */
    PageResult page(PageModel pModel, String name, Map<String, Object> param, String key,Boolean log);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      db: database context
    */
    PageResult page(PageModel pModel, String name, Map<String, Object> param, DataContext db);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      db: database context
      log:sql log
    */
    PageResult page(PageModel pModel, String name, Map<String, Object> param, DataContext db,Boolean log);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      xml file -> sql have db key value (db="dbkey")
    */
    PageResult page(PageModel pModel, String name, Map<String, Object> param);

    /*
      query by page
      pModel: page model
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      xml file -> sql have db key value (db="dbkey")
      log:sql log
    */
    PageResult page(PageModel pModel, String name, Map<String, Object> param,Boolean log);

    /*
     insert table
      model: database table
      db: database context
    */
    WriteReturn add(Object model, DataContext db);

    /*
     insert table
      model: database table
      db: database context
      log:sql log
    */
    WriteReturn add(Object model, DataContext db,Boolean log);

    /*
    insert table
     model: database table
     key: database key
   */
    WriteReturn add(Object model, String key);

    /*
    insert table
     model: database table
     key: database key
     log:sql log
    */
    WriteReturn add(Object model, String key,Boolean log);

    /*
     model: database table
     field: update table field by primary Key
     db: database context
    */
    WriteReturn update(Object model, List<String> field, DataContext db);

    /*
     model: database table
     field: update table field by primary Key
     db: database context
     log:sql log
    */
    WriteReturn update(Object model, List<String> field, DataContext db,Boolean log);

    /*
     update table by primary Key
     model: database table
     field: update field
     key: database key
    */
    WriteReturn update(Object model, List<String> field, String key);

    /*
     update table by primary Key
     model: database table
     field: update field
     key: database key
     log:sql log
    */
    WriteReturn update(Object model, List<String> field, String key,Boolean log);

    /*
     update table by primary Key
     model: database table
     key: database key
    */
    WriteReturn update(Object model,String key);

    /*
     update table by primary Key
     model: database table
     key: database key
      log:sql log
    */
    WriteReturn update(Object model,String key,Boolean log);

    /*
     update table by primary Key
     model: database table
     db: database context
    */
    WriteReturn update(Object model,DataContext db);

    /*
     update table by primary Key
     model: database table
     db: database context
      log:sql log
    */
    WriteReturn update(Object model,DataContext db,Boolean log);

    /*
     delete table by primary Key
     model: database table
     db: database context
    */
    WriteReturn delete(Object model, DataContext db);

    /*
     delete table by primary Key
     model: database table
     db: database context
      log:sql log
    */
    WriteReturn delete(Object model, DataContext db,Boolean log);

    /*
     delete table by primary Key
     model: database table
     key: database key
    */
    WriteReturn delete(Object model, String key);

    /*
     delete table by primary Key
     model: database table
     key: database key
      log:sql log
    */
    WriteReturn delete(Object model, String key,Boolean log);

    /*
     query exists by primary Key
     model: database table
     db: database context
    */
    boolean exists(Object model, DataContext db);

    /*
     query exists by primary Key
     model: database table
     db: database context
      log:sql log
    */
    boolean exists(Object model, DataContext db,Boolean log);

    /*
     query exists by primary Key
     model: database table
     key: database key
    */
    boolean exists(Object model, String key);


    /*
     query exists by primary Key
     model: database table
     key: database key
      log:sql log
    */
    boolean exists(Object model, String key,Boolean log);

    /*
     query model by primary Key
     model: database table
     key: database key
   */
    Object query(Object model,Class<?> type, String key);

    /*
     query model by primary Key
     model: database table
     key: database key
      log:sql log
   */
    Object query(Object model,Class<?> type, String key,Boolean log);

    /*
       query model by primary Key
       model: database table
      db: database context
    */
    Object query(Object model,Class<?> type, DataContext db);

    /*
       query model by primary Key
       model: database table
      db: database context
      log:sql log
    */
    Object query(Object model,Class<?> type, DataContext db,Boolean log);

    /*
      query model by primary Key
      model: database table
         key: database key
    */
    Map<String,Object> query(Object model, String key);

    /*
      query model by primary Key
      model: database table
         key: database key
      log:sql log
    */
    FastMap<String,Object> query(Object model, String key,Boolean log);

    /*
      query model by primary Key
      model: database table
      db: database context
    */
    FastMap<String,Object> query(Object model, DataContext db);

    /*
      query model by primary Key
      model: database table
      db: database context
      log:sql log
    */
    FastMap<String,Object> query(Object model, DataContext db,Boolean log);

    /*
     update,delete sql
     name: xml file -> sqlMap -> id
     param: sql preparedStatement
     key: database key
   */
    WriteReturn write(String name,Map<String, Object> param, String key);

    /*
     update,delete sql
     name: xml file -> sqlMap -> id
     param: sql preparedStatement
     key: database key
      log:sql log
   */
    WriteReturn write(String name,Map<String, Object> param, String key,Boolean log);

    /*
     update,delete sql
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      db: database context
    */
    WriteReturn write(String name,Map<String, Object> param, DataContext db);

    /*
     update,delete sql
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      db: database context
      log:sql log
    */
    WriteReturn write(String name,Map<String, Object> param, DataContext db,Boolean log);

    /*
      update,delete sql
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      xml file -> sql have db key value (db="dbkey")
    */
    WriteReturn write(String name,Map<String, Object> param);

    /*
      update,delete sql
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      xml file -> sql have db key value (db="dbkey")
      log:sql log
    */
    WriteReturn write(String name,Map<String, Object> param,Boolean log);

    /*
     exec sql
     map:sql param
     db: database context
     log:sql log
    */
    WriteReturn execute(MapResult map, DataContext db, Boolean log);

    /*
     exec sql
     map:sql param
     db: database context
    */
    WriteReturn execute(MapResult map, DataContext db);

    /*
     exec sql
     map:sql param
     key: database key
     log:sql log
   */
    WriteReturn execute(MapResult map, String key, Boolean log);

    /*
     exec sql
     key: database key
   */
    WriteReturn execute(MapResult map, String key);

    /*
    map:sql param
    db: database context
    log:sql log
   */
    int count(Map<String,Object> map,Class<?> type, DataContext db, Boolean log);

    /*
     map:sql param
     db: database context
    */
    int count(Map<String,Object> map,Class<?> type, DataContext db);

    /*
     map:sql param
     key: database key
     log:sql log
   */
    int count(Map<String,Object> map,Class<?> type,  String key, Boolean log);

    /*
     key: database key
   */
    int count(Map<String,Object> map,Class<?> type,  String key);

    /*
   map:sql param
   db: database context
   log:sql log
  */
    DataReturn delete(Map<String,Object> map,Class<?> type, DataContext db, Boolean log);

    /*
     map:sql param
     db: database context
    */
    DataReturn delete(Map<String,Object> map,Class<?> type, DataContext db);

    /*
     map:sql param
     key: database key
     log:sql log
   */
    DataReturn delete(Map<String,Object> map,Class<?> type,  String key, Boolean log);

    /*
     key: database key
   */
    DataReturn delete(Map<String,Object> map,Class<?> type,  String key);
}
