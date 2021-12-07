package org.FastData.Spring.FastDataAop;

import org.FastData.Spring.CacheModel.DbConfig;
import org.FastData.Spring.Model.MapResult;
import org.FastData.Spring.Util.CacheUtil;
import org.FastData.Spring.Util.FastUtil;
import java.util.LinkedHashMap;

public final class BaseAop {
    public static void aopBefore(String tableName, MapResult result, DbConfig config, boolean isRead, int aopType,Object model) {
        IFastDataAop aop =  CacheUtil.getModel("FastDataAop", IFastDataAop.class);
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
            context.setModel(model);

            aop.before(context);
        }
    }
    
    public static void aopAfter(String tableName, MapResult result, DbConfig config, boolean isRead, int aopType, Object data,Object model) {
        IFastDataAop aop = CacheUtil.getModel("FastDataAop", IFastDataAop.class);
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
            context.setModel(model);

            aop.after(context);
        }
    }

    public static void aopException(Exception ex, String name, int aopType, DbConfig config,Object model) {
        IFastDataAop aop = CacheUtil.getModel("FastDataAop", IFastDataAop.class);
        if (aop != null) {
            ExceptionContext context = new ExceptionContext();
            context.setAopType(aopType);
            context.setEx(ex);
            context.setName(name);
            context.setModel(model);
            if (config != null)
                context.setDbType(config.getDbType());
            aop.exception(context);
        }
    }

    public static void aopMapBefore(String mapName, MapResult map, DbConfig config,int aopType){
        IFastDataAop aop = CacheUtil.getModel("FastDataAop", IFastDataAop.class);
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
        IFastDataAop aop = CacheUtil.getModel("FastDataAop", IFastDataAop.class);
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
