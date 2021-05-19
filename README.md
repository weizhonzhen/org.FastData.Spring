# org.FastData
java orm(db first,code frist) for sqlserver mysql etl.

nuget url: https://www.nuget.org/packages/Fast.Data.Core/

in Application add Annotation

            @FastData(key = "Emr",cachePackageName = "com.example.Entity" ,codeFirstPackageName="com.example.Entity")
            key is database key 
            codeFirstPackageName is code first model
            cachePackageName is cache model
