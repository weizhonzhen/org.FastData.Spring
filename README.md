# org.FastData
java orm(db first,code frist) for sqlserver mysql etl.

in Application add Annotation
```csharp
 @FastData(key = "test",cachePackageName = "com.example.Entity" ,codeFirstPackageName="com.example.Entity")
 key is database key 
 codeFirstPackageName is code first model
 cachePackageName is cache model
```
code first model
package Entity;
       
```csharp
import Column;
import Table;

@Table(comments = "测试")
public class TestTable {
    @Column(isKey = true,dataType = "NVARCHAR2",length = 15,isNull = true,comments = "id")
    public  String Id;
    @Column(dataType = "NUMBER",isNull = true,comments = "value")
    public  Number value;
}
```

in resources add db.json
in db.json         
```csharp
 {      
 "dataConfig": [
    {
      "providerName": "oracle.jdbc.OracleDriver",
      "dbType": "Oracle",
      "user": "user",
      "passWord": "pwd",
      "connStr": "jdbc:oracle:thin:@127.0.0.1/data",
      "isOutSql": true,
      "isOutError": true,
      "key": "test",
      "designModel": "CodeFirst"
    }
  ]
}
```
in resources add map.json
```csharp
"SqlMap" :{"Path": [ "map/admin/Api.xml", "map/admin/Area.xml"]}
```
in resources add map/admin/Api.xml map/admin/Area.xml
   ```xml
    <?xml version="1.0" encoding="utf-8" ?>
            <sqlMap>
              <select id="GetUser" log="true" db="test">
                select a.* from base_user a
                <dynamic prepend=" where 1=1">
                  <isPropertyAvailable prepend=" and " property="userId">a.userId=?userId</isPropertyAvailable>                  
                  <isNotNullOrEmpty prepend=" and " property="isAdmin">a.isAdmin=?isAdmin</isNotNullOrEmpty>
                  <if condition="areaId>8" prepend=" and " property="areaId">a.areaId=?areaId</if>            
                  <choose property="userNo">
                     <condition prepend=" and " property="userNo>5">a.userNo=?userNo and a.userNo=5</condition>                    
                     <condition prepend=" and " property="userNo>6">a.userNo=?userNo and a.userNo=6</condition>
                     <other prepend=" and ">a.userNo=?userNo and a.userNo=7</other>
                  </choose>     
                </dynamic>
              </select>
              
              <update id="test.Update">
                    update base_user set
                <dynamic ltrim="," prepend =" ">
                    <isNotNullOrEmpty prepend=" " property="Name">,name=?Name</isNotNullOrEmpty>
                    <isNotNullOrEmpty prepend=" " property="Age">,age=?Age</isNotNullOrEmpty>
                    <isNotNullOrEmpty prepend=" " property="Id" required="true">where id=?id</isNotNullOrEmpty>
                </dynamic>
            </update>
          </sqlMap>
    
```
in Controller
``````csharp
 @Resource
   IFastRepository iFast;

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
      xml file -> sql have db key value (db="dbkey")
     */
    List<?> query(String name, Map<String, Object> param, Class<?> type);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      key: database key
    */
    List<Map<String, Object>> query(String name, Map<String, Object> param, String key);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      db: database context
    */
    List<Map<String, Object>> query(String name, Map<String, Object> param, DataContext db);

    /*
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      xml file -> sql have db key value (db="dbkey")
    */
    List<Map<String, Object>> query(String name, Map<String, Object> param);

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
      db: database context
    */
    PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db);

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
      key: database key
    */
    PageResult page(PageModel pModel, String name, Map<String, Object> param, String key);

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
      xml file -> sql have db key value (db="dbkey")
    */
    PageResult page(PageModel pModel, String name, Map<String, Object> param);

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
    WriteReturn update(Object model, List<String> field, DataContext db);

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
     key: database key
    */
    WriteReturn update(Object model,String key);

    /*
     update table by primary Key
     model: database table
     db: database context
    */
    WriteReturn update(Object model,DataContext db);

    /*
     delete table by primary Key
     model: database table
     db: database context
    */
    WriteReturn delete(Object model, DataContext db);

    /*
     delete table by primary Key
     model: database table
     key: database key
    */
    WriteReturn delete(Object model, String key);

    /*
     query exists by primary Key
     model: database table
     db: database context
    */
    boolean exists(Object model, DataContext db);

    /*
     query exists by primary Key
     model: database table
     key: database key
    */
    boolean exists(Object model, String key);

    /*
     query model by primary Key
     model: database table
     key: database key
   */
    Object query(Object model,Class<?> type, String key);

    /*
       query model by primary Key
       model: database table
      db: database context
    */
    Object query(Object model,Class<?> type, DataContext db);

    /*
      query model by primary Key
      model: database table
         key: database key
    */
    Map<String,Object> query(Object model, String key);

    /*
      query model by primary Key
      model: database table
      db: database context
    */
    Map<String,Object> query(Object model, DataContext db);

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
      db: database context
    */
    WriteReturn write(String name,Map<String, Object> param, DataContext db);

    /*
      update,delete sql
      name: xml file -> sqlMap -> id
      param: sql preparedStatement
      xml file -> sql have db key value (db="dbkey")
    */
    WriteReturn write(String name,Map<String, Object> param);
