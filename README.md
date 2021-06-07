# org.FastData.Spring
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
    private  String Id;
    @Column(dataType = "NUMBER",isNull = true,comments = "value")
    private  Number value;
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
      "designModel": "CodeFirst",
      "poolSize":50
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
```csharp
in Interceptor
@Resource	
private org.FastData.Spring.Handler.FastApiInterceptor fastApiInterceptor;

registry.addInterceptor(this.fastApiInterceptor) is dyn http api 
 
http://127.0.0.1:8080/GetUser?userId=1
```
in Controller
``````csharp
 @Resource
   IFastRepository iFast;

 try (var db = new DataContext("db")) {
   LinkedHashMap<String, Object> param = new LinkedHashMap<>();
   var map = new MapResult();
   map.setSql("update TestTable set Value=?Value where Id=?Id");
   param.put("Value", "1");
   param.put("Id", "2");
   var result = ifast.execute(map);

   var query = new HashMap<String, Object>();
   query.put("Id", "00010162");
   query.put("Name", "中1国");
   var data1 = ifast.query("TestTable.info", query, db);
   var data2 = ifast.query("table.info", query, TestTable.class, db);

   var pmodel = new PageModel();
   var pageList1 = ifast.page(pmodel, "TestTable.info", query, db);
   var pageList2 = ifast.page(pmodel, "TestTable.info", query, TestTable.class, db);

   var model = new TestTable();
   model.setId("1");
   var exists = ifast.exists(model, db);

   var field = new ArrayList<String>();
   field.add("Value");
   var update = ifast.update(model, field, db);
   var del = ifast.delete(model, db);
   
   db.beginTrans();
   db.submitTrans();
   db.rollbackTrans();
 }
