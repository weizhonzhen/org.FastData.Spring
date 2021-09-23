# org.FastData.Spring
java orm(db first,code frist) for sqlserver mysql etl.

in Application add Annotation
```csharp
 @FastData(key = "test",cachePackageName = "com.example.Entity" ,
  codeFirstPackageName="com.example.Entity", servicePackageName = "com.example.Service")
  
 key is database key 
 codeFirstPackageName is code first model
 cachePackageName is cache model
 servicePackageName is interface Service
```
aop
```csharp
//in Application
FastDataConfig.setAop(new FastAop());

public class FastAop implements IFastAop {
    @Override
    public void before(BeforeContext beforeContext) {
        System.out.println("before：" + beforeContext.getSql());
    }

    @Override
    public void after(AfterContext afterContext) {
        System.out.println("after：" + afterContext.getSql());
    }

    @Override
    public void mapBefore(MapBeforeContext mapBeforeContext) {
        System.out.println("mapBefore：" + mapBeforeContext.getMapName());
    }

    @Override
    public void mapAfter(MapAfterContext mapAfterContext) {
        System.out.println("mapAfter：" + mapAfterContext.getMapName());
    }

    @Override
    public void exception(ExceptionContext exceptionContext) {
        System.out.println("exception：" + exceptionContext.getName());

    }
}
```
Annotation
```csharp
public interface TestService {
    @FastRead(sql = "select * from base_user where id=?id and orgid=?orgid",dbKey = "test")
    Map<String,Object> map(String id,String orgid);

    @FastRead(sql = "select * from base_user where id=?id and orgid=?orgid",dbKey = "test")
    T_XT_YHB model(String id,String orgid);

    @FastRead(sql = "select * from base_user where id=?id and orgid=?orgid",dbKey = "test")
    List<Map<String,Object>> listMap(StringidGH,String orgid);

    @FastRead(sql = "select * from base_user where id=?id and orgid=?orgid",dbKey = "test")
    List<T_XT_YHB> listModel(String id,String orgid);

    @FastWrite(sql = "update base_user set name=?name where id=?id",dbKey = "db")
    WriteReturn update(String name,String id);
}

@Resource
TestService test;
//or
var test = (TestService) iFastRepository.resolve(TestService.class, AppSeting.Key);


var model = test.model("admin", "101");
var map = test.map("amdin", "101");
var listMap = test.listMap("admin", "101");
var listModel = test.listModel("admin", "101");
var update = test.update("管理员", "admin", "101");
```

code first model
package Entity;
       
```csharp
import Column;
import Table;

@Table(comments = "测试")
@FieldNameConstants
@Data
public class TestTable {
    @Column(isKey = true,dataType = "NVARCHAR2",length = 15,isNull = true,comments = "id")
    private  String Id;
    @Column(dataType = "NUMBER",isNull = true,comments = "value")
    private  Number value;
    
    @NavigateType(type = TestTable_List.class)
    private TestTable_List list;
}

@Data
public class TestTable_List
{
   @Navigate(Name = TestTable.Fields.Id)
   private  String Id;
   private  Number value;
}

//@NavigateType  @Navigate  导航属性
   var model = new TestTable();
   model.setId("1");
   var list = (TestTable)ifast.queryKey(model,TestTable.class,"db");
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

//database write sql param must ?name,dtabase read sql param only ?
 try (var db = new DataContext("db")) {
   LinkedHashMap<String, Object> paramWrite = new LinkedHashMap<>();
   var mapWrite = new MapResult();
   mapWrite.setSql("update TestTable set Value=?Value where Id=?Id");
   paramWrite.put("Value", "1");
   paramWrite.put("Id", "2");
   var result = ifast.execute(mapWrite);
   
   LinkedHashMap<String, Object> paramRead = new LinkedHashMap<>();
   var mapRead = new MapResult();
   mapRead.setSql("select count(0) TestTable set Value=? where Id=?");
   paramRead.put("Value", "1");
   paramRead.put("Id", "2");
   var result = ifast.count(mapRead);

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
   var update = ifast.updateKey(model, field, db);
   var del = ifast.deleteKey(model, db);
   
   db.beginTrans();
   db.submitTrans();
   db.rollbackTrans();
 }
