# org.FastData
java orm(db first,code frist) for sqlserver mysql etl.

nuget url: https://www.nuget.org/packages/Fast.Data.Core/

in Application add Annotation


            Configuration = configuration;

            // old pagepackages init model Properties cahce 
            FastMap.InstanceProperties("DataModel","db.json");

            //old pagepackages init code first
            FastMap.InstanceTable("DataModel.Base", "db.json");

            //old pagepackages by Repository
            services.AddFastRedis(a => { a.Server = "127.0.0.1:6379,abortConnect=true,allowAdmin=true,connectTimeout=10000,syncTimeout=10000"; });
            services.AddFastData();
            
            //old pagepackages init map cache
            FastData.Core.FastMap.InstanceMap("dbKey", "db.json", "map.json");
            
            //old pagepackages init map cache by Resource （xml file， db.json， map.json）
            FastData.Core.FastMap.InstanceMapResource("dbKey", "db.json", "map.json");
            
            //new pagepackages
            services.AddFastData(new ConfigData { mapFile = "map.json", dbKey = "dbkey", IsResource = true, dbFile = "db.json",NamespaceProperties = "DataModel." });
               or
            services.AddFastData(a=> { a.mapFile = "map.json"; a.dbKey = "dbkey"; a.IsResource = true; a.dbFile = "db.json"; a.NamespaceProperties = "DataModel."; });
  
