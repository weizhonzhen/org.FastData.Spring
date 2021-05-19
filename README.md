# org.FastData
java orm(db first,code frist) for sqlserver mysql etl.

nuget url: https://www.nuget.org/packages/Fast.Data.Core/

in Application add Annotation

            @FastData(key = "test",cachePackageName = "com.example.Entity" ,codeFirstPackageName="com.example.Entity")
            key is database key 
            codeFirstPackageName is code first model
            cachePackageName is cache model

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
              <select id="GetUser" log="true">
                select a.* from base_user a
                <dynamic prepend=" where 1=1">
                  <isPropertyAvailable prepend=" and " property="userId">a.userId=?userId</isPropertyAvailable>                  
                  <isNotNullOrEmpty prepend=" and " property="isAdmin">a.isAdmin=?isAdmin</isNotNullOrEmpty>
                  <if condition="areaId>8" prepend=" and " property="areaId">a.areaId=?areaId</if>            
                  <choose property="userNo">
                     <condition prepend=" and " property="userNo>5">a.userNo=:userNo and a.userNo=5</condition>                    
                     <condition prepend=" and " property="userNo>6">a.userNo=:userNo and a.userNo=6</condition>
                     <other prepend=" and ">a.userNo=:userNo and a.userNo=7</other><!--by above 2.3.4-->
                  </choose>     
                </dynamic>
              </select>
              
              <select id="Patient.Test">
                select * from base_user where 1=1
                <dynamic prepend="">
                  <isNotNullOrEmpty prepend=" and " property="userid">userid = :userid</isNotNullOrEmpty>
                </dynamic>                
              </select>
          </sqlMap>
  
  
```
