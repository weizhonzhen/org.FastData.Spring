# org.FastData
java orm(db first,code frist) for sqlserver mysql etl.

nuget url: https://www.nuget.org/packages/Fast.Data.Core/

in Application add Annotation

            @FastData(key = "Emr",cachePackageName = "com.example.Entity" ,codeFirstPackageName="com.example.Entity")
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
      "user": "qebemr",
      "passWord": "qebsoft",
      "connStr": "jdbc:oracle:thin:@127.0.0.1/emrdata",
      "isOutSql": true,
      "isOutError": true,
      "key": "Emr",
      "designModel": "CodeFirst"
    }
  ]
      }
```
