package org.FastData.Spring.Aop;

import org.FastData.Spring.CacheModel.DbConfig;
import org.FastData.Spring.Model.MapResult;
import org.FastData.Spring.Util.FastUtil;

import java.util.LinkedHashMap;

public final class BaseAop {
    public static void aopBefore(String tableName, MapResult result, DbConfig config, boolean isRead, int aopType) {
        IFastAop aop = FastDataConfig.getAop();
        if (aop != null) {
            BeforeContext context = new BeforeContext();

            if (!FastUtil.isNullOrEmpty(tableName))
                context.setTableName(tableName);

            if (result != null) {
                context.setSql(result.getSql());

                if (result.getParam() != null)
                    context.setParam(result.getParam());
            } else {
                context.setSql("");
                context.setParam(new LinkedHashMap<String, Object>());
            }

            context.setDbType(config.getDbType());
            context.setIsRead(isRead);
            context.setIsWrite(!isRead);
            context.setAopType(aopType);

            aop.before(context);
        }
    }

    public static void aopAfter(String tableName, MapResult result, DbConfig config, boolean isRead, int aopType, Object data) {
        IFastAop aop = FastDataConfig.getAop();
        if (aop != null) {
            AfterContext context = new AfterContext();

            if (!FastUtil.isNullOrEmpty(tableName))
                context.setTableName(tableName);

            if (result != null) {
                context.setSql(result.getSql());
                if (result.getParam() != null)
                    context.setParam(result.getParam());
            } else {
                context.setSql("");
                context.setParam(new LinkedHashMap<String, Object>());
            }

            context.setAopType(aopType);
            context.setDbType(config.getDbType());
            context.setRead(isRead);
            context.setWrite(!isRead);
            context.setResult(data);

            aop.after(context);
        }
    }

    public static void aopException(Exception ex, String name, int aopType, DbConfig config) {
        if (FastDataConfig.getAop() != null) {
            ExceptionContext context = new ExceptionContext();
            context.setAopType(aopType);
            context.setEx(ex);
            context.setName(name);
            if (config != null)
                context.setDbType(config.getDbType());
            FastDataConfig.getAop().exception(context);
        }
    }

    public static void aopMapBefore(String mapName, MapResult map, DbConfig config,int aopType){
        IFastAop aop = FastDataConfig.getAop();
        if (aop != null) {
            MapBeforeContext context = new MapBeforeContext();
            context.setMapName(mapName);
            context.setAopType(aopType);

            if (map != null) {
                context.setSql(map.getSql());
                if (map.getParam() != null)
                    context.setParam(map.getParam());
            } else {
                context.setSql("");
                context.setParam(new LinkedHashMap<String, Object>());
            }

            if (config != null)
                context.setDbType(config.getDbType());
            aop.mapBefore(context);
        }
    }

    public static void aopMapAfter(String mapName, MapResult map, DbConfig config,int aopType,Object data){
        IFastAop aop = FastDataConfig.getAop();
        if (aop != null) {
            MapAfterContext context = new MapAfterContext();
            context.setMapName(mapName);
            context.setAopType(aopType);
            context.setResult(data);

            if (map != null) {
                context.setSql(map.getSql());
                if (map.getParam() != null)
                    context.setParam(map.getParam());
            } else {
                context.setSql("");
                context.setParam(new LinkedHashMap<String, Object>());
            }

            if (config != null)
                context.setDbType(config.getDbType());
            aop.mapAfter(context);
        }
    }
}
