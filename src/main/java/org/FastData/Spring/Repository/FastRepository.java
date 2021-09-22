package org.FastData.Spring.Repository;

import org.FastData.Spring.Annotation.*;
import org.FastData.Spring.Aop.*;
import org.FastData.Spring.Base.DataConfig;
import org.FastData.Spring.Base.MapXml;
import org.FastData.Spring.CacheModel.*;
import org.FastData.Spring.Check.BaseTable;
import org.FastData.Spring.Config.Config;
import org.FastData.Spring.Context.DataContext;
import org.FastData.Spring.Model.*;
import org.FastData.Spring.Util.FastUtil;
import org.FastData.Spring.Util.ScanPackage;
import org.FastData.Spring.Util.CacheUtil;
import org.springframework.context.annotation.ComponentScan;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

@ComponentScan(basePackages = {"org.FastData.Spring.Repository","org.FastData.Spring.Handler"})
public class FastRepository implements IFastRepository {
    public FastRepository() {
        try {
            for (StackTraceElement item : Thread.currentThread().getStackTrace()) {
                FastData annotation = Thread.currentThread().getContextClassLoader().loadClass(item.getClassName()).getAnnotation(FastData.class);
                if (annotation != null) {
                    init(annotation.cachePackageName(), annotation.codeFirstPackageName(), annotation.key(), annotation.servicePackageName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, String key) {
        List<?> data = new ArrayList<>();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Model);
                data = db.query(result, type, false).getList();
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Model, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(key), AopEnum.Map_List_Model);
        BaseAop.aopMapAfter(name, null, DataConfig.db(key), AopEnum.Map_List_Model, data);
        return data;
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, String key, Boolean log) {
        List<?> data = new ArrayList<>();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Model);
                data = db.query(result, type, false).getList();
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Model, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(key), AopEnum.Map_List_Model);
        BaseAop.aopMapAfter(name, null, DataConfig.db(key), AopEnum.Map_List_Model, data);
        return data;
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, DataContext db) {
        List<?> data = new ArrayList<>();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Model);
            data = db.query(result, type, false).getList();
            BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Model, data);
            return data;
        }
        BaseAop.aopMapBefore(name, null, db.config, AopEnum.Map_List_Model);
        BaseAop.aopMapAfter(name, null, db.config, AopEnum.Map_List_Model, data);
        return data;
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, DataContext db, Boolean log) {
        List<?> data = new ArrayList<>();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
            BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Model);
            data = db.query(result, type, false).getList();
            BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Model, data);
            return data;
        }
        BaseAop.aopMapBefore(name, null, db.config, AopEnum.Map_List_Model);
        BaseAop.aopMapAfter(name, null, db.config, AopEnum.Map_List_Model, data);
        return data;
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type) {
        List<?> data = new ArrayList<>();
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Model);
                data = db.query(result, type, false).getList();
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Model, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Model);
        BaseAop.aopMapAfter(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Model, data);
        return data;
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, Boolean log) {
        List<?> data = new ArrayList<>();
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
                BaseAop.aopMapBefore(name, result, DataConfig.db(mapDb(name)), AopEnum.Map_List_Model);
                data = db.query(result, type, false).getList();
                BaseAop.aopMapAfter(name, result, DataConfig.db(mapDb(name)), AopEnum.Map_List_Model, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Model);
        BaseAop.aopMapAfter(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Model, data);
        return data;
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param, String key) {
        List<FastMap<String, Object>> data = new ArrayList<FastMap<String, Object>>();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
                data = db.query(result, false).getList();
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(key), AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, DataConfig.db(key), AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param, String key, Boolean log) {
        List<FastMap<String, Object>> data = new ArrayList<FastMap<String, Object>>();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
                data = db.query(result, false).getList();
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(key), AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, DataConfig.db(key), AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param, DataContext db) {
        List<FastMap<String, Object>> data = new ArrayList<FastMap<String, Object>>();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
            data = db.query(result, false).getList();
            BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
            return data;
        }
        BaseAop.aopMapBefore(name, null, db.config, AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, db.config, AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param, DataContext db, Boolean log) {
        List<FastMap<String, Object>> data = new ArrayList<FastMap<String, Object>>();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
            BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
            data = db.query(result, false).getList();
            BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
            return data;
        }
        BaseAop.aopMapBefore(name, null, db.config, AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, db.config, AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param) {
        List<FastMap<String, Object>> data = new ArrayList<FastMap<String, Object>>();
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
                data = db.query(result, false).getList();
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param, Boolean log) {
        List<FastMap<String, Object>> data = new ArrayList<FastMap<String, Object>>();
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
                data = db.query(result, false).getList();
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, String key) {
        PageResultImpl<?> data = new PageResultImpl<>();
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_Page_Model);
                data = db.page(pModel, result, type, false);
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Page_Model, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(key), AopEnum.Map_Page_Model);
        BaseAop.aopMapAfter(name, null, DataConfig.db(key), AopEnum.Map_Page_Model, data);
        return data;
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, String key, Boolean log) {
        PageResultImpl<?> data = new PageResultImpl<>();
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_Page_Model);
                data = db.page(pModel, result, type, false);
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Page_Model, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(key), AopEnum.Map_Page_Model);
        BaseAop.aopMapAfter(name, null, DataConfig.db(key), AopEnum.Map_Page_Model, data);
        return data;
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db) {
        PageResultImpl<?> data = new PageResultImpl<>();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_Page_Model);
            data = db.page(pModel, result, type, false);
            BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Page_Model, data);
            return data;
        }
        BaseAop.aopMapBefore(name, null, db.config, AopEnum.Map_Page_Model);
        BaseAop.aopMapAfter(name, null, db.config, AopEnum.Map_Page_Model, data);
        return data;
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db, Boolean log) {
        PageResultImpl<?> data = new PageResultImpl<>();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
            BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_Page_Model);
            data = db.page(pModel, result, type, false);
            BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Page_Model, data);
            return data;
        }
        BaseAop.aopMapBefore(name, null, db.config, AopEnum.Map_Page_Model);
        BaseAop.aopMapAfter(name, null, db.config, AopEnum.Map_Page_Model, data);
        return data;
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type) {
        PageResultImpl<?> data = new PageResultImpl<>();
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_Page_Model);
                data = db.page(pModel, result, type, false);
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Page_Model, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_Page_Model);
        BaseAop.aopMapAfter(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_Page_Model, data);
        return data;
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, Boolean log) {
        PageResultImpl<?> data = new PageResultImpl<>();
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_Page_Model);
                data = db.page(pModel, result, type, false);
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Page_Model, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_Page_Model);
        BaseAop.aopMapAfter(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_Page_Model, data);
        return data;
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, String key) {
        PageResult data = new PageResult();
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
                data = db.page(pModel, result, false);
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(key), AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, DataConfig.db(key), AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, String key, Boolean log) {
        PageResult data = new PageResult();
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
                data = db.page(pModel, result, false);
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(key), AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, DataConfig.db(key), AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, DataContext db) {
        PageResult data = new PageResult();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
            data = db.page(pModel, result, false);
            BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
            return data;
        }
        BaseAop.aopMapBefore(name, null, db.config, AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, db.config, AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, DataContext db, Boolean log) {
        PageResult data = new PageResult();
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
            BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
            data = db.page(pModel, result, false);
            BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
            return data;
        }
        BaseAop.aopMapBefore(name, null, db.config, AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, db.config, AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param) {
        PageResult data = new PageResult();
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
                data = db.page(pModel, result, false);
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, Boolean log) {
        PageResult data = new PageResult();
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
                BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_List_Dic);
                data = db.page(pModel, result, false);
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_List_Dic, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Dic);
        BaseAop.aopMapAfter(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_List_Dic, data);
        return data;
    }

    @Override
    public WriteReturn add(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.add(model).getWriteReturn();
        }
    }

    @Override
    public WriteReturn add(Object model, String key, Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.add(model).getWriteReturn();
        }
    }

    @Override
    public WriteReturn add(Object model, DataContext db) {
        return db.add(model).getWriteReturn();
    }

    @Override
    public WriteReturn add(Object model, DataContext db, Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.add(model).getWriteReturn();
    }

    @Override
    public WriteReturn updateKey(Object model, List<String> field, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.update(model, field).getWriteReturn();
        }
    }

    @Override
    public WriteReturn updateKey(Object model, List<String> field, String key, Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.update(model, field).getWriteReturn();
        }
    }

    @Override
    public WriteReturn updateKey(Object model, List<String> field, DataContext db) {
        return db.update(model, field).getWriteReturn();
    }

    @Override
    public WriteReturn updateKey(Object model, List<String> field, DataContext db, Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.update(model, field).getWriteReturn();
    }

    @Override
    public WriteReturn updateKey(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.update(model).getWriteReturn();
        }
    }

    @Override
    public WriteReturn updateKey(Object model, String key, Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.update(model).getWriteReturn();
        }
    }

    @Override
    public WriteReturn updateKey(Object model, DataContext db) {
        return db.update(model).getWriteReturn();
    }

    @Override
    public WriteReturn updateKey(Object model, DataContext db, Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.update(model).getWriteReturn();
    }

    @Override
    public WriteReturn deleteKey(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.delete(model).getWriteReturn();
        }
    }

    @Override
    public WriteReturn deleteKey(Object model, String key, Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.delete(model).getWriteReturn();
        }
    }

    @Override
    public WriteReturn deleteKey(Object model, DataContext db) {
        return db.delete(model).getWriteReturn();
    }

    @Override
    public WriteReturn deleteKey(Object model, DataContext db, Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.delete(model).getWriteReturn();
    }

    @Override
    public boolean existsKey(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.exists(model).getCount() > 0;
        }
    }

    @Override
    public boolean existsKey(Object model, String key, Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.exists(model).getCount() > 0;
        }
    }

    @Override
    public boolean existsKey(Object model, DataContext db) {
        return db.exists(model).getCount() > 0;
    }

    @Override
    public boolean existsKey(Object model, DataContext db, Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.exists(model).getCount() > 0;
    }

    @Override
    public Object queryKey(Object model, Class<?> type, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.query(model, type).getItem();
        }
    }

    @Override
    public Object queryKey(Object model, Class<?> type, String key, Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.query(model, type).getItem();
        }
    }

    @Override
    public Object queryKey(Object model, Class<?> type, DataContext db) {
        return db.query(model, type).getItem();
    }

    @Override
    public Object queryKey(Object model, Class<?> type, DataContext db, Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.query(model, type).getItem();
    }

    @Override
    public FastMap<String, Object> queryKey(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.query(model).getItem();
        }
    }

    @Override
    public FastMap<String, Object> queryKey(Object model, String key, Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.query(model).getItem();
        }
    }

    @Override
    public FastMap<String, Object> queryKey(Object model, DataContext db) {
        return db.query(model).getItem();
    }

    @Override
    public FastMap<String, Object> queryKey(Object model, DataContext db, Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.query(model).getItem();
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, String key) {
        WriteReturn data = null;
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            data = checkParam(result, name);
            BaseAop.aopMapBefore(name, result, DataConfig.db(key), AopEnum.Map_Write);
            if (!data.getSuccess()) {
                BaseAop.aopMapAfter(name, result, DataConfig.db(key), AopEnum.Map_Write, data);
                return data;
            }
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                data = db.execute(result, false).getWriteReturn();
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Write, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(key), AopEnum.Map_Write);
        data = checkName(name);
        BaseAop.aopMapAfter(name, null, DataConfig.db(key), AopEnum.Map_Write, data);
        return data;
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, String key, Boolean log) {
        WriteReturn data = null;
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            data = checkParam(result, name);
            BaseAop.aopMapBefore(name, result, DataConfig.db(key), AopEnum.Map_Write);
            if (!data.getSuccess()) {
                BaseAop.aopMapAfter(name, result, DataConfig.db(key), AopEnum.Map_Write, data);
                return data;
            }
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
                data = db.execute(result, false).getWriteReturn();
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Write, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(key), AopEnum.Map_Write);
        data = checkName(name);
        BaseAop.aopMapAfter(name, null, DataConfig.db(key), AopEnum.Map_Write, data);
        return data;
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, DataContext db) {
        WriteReturn data = null;
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            data = checkParam(result, name);
            BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_Write);
            if (!data.getSuccess()) {
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Write, data);
                return data;
            }
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            data = db.execute(result, false).getWriteReturn();
            BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Write, data);
            return data;
        }
        BaseAop.aopMapBefore(name, null, db.config, AopEnum.Map_Write);
        data = checkName(name);
        BaseAop.aopMapAfter(name, null, db.config, AopEnum.Map_Write, data);
        return data;
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, DataContext db, Boolean log) {
        WriteReturn data = null;
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            data = checkParam(result, name);
            BaseAop.aopMapBefore(name, result, db.config, AopEnum.Map_Write);
            if (!data.getSuccess()) {
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Write, data);
                return data;
            }
            db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
            data = db.execute(result, false).getWriteReturn();
            BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Write, data);
            return data;
        }
        BaseAop.aopMapBefore(name, null, db.config, AopEnum.Map_Write);
        data = checkName(name);
        BaseAop.aopMapAfter(name, null, db.config, AopEnum.Map_Write, data);
        return data;
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param) {
        WriteReturn data = null;
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            data = checkParam(result, name);
            BaseAop.aopMapBefore(name, result, DataConfig.db(mapDb(name)), AopEnum.Map_Write);
            if (!data.getSuccess()) {
                BaseAop.aopMapAfter(name, result, DataConfig.db(mapDb(name)), AopEnum.Map_Write, data);
                return data;
            }
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                data = db.execute(result, false).getWriteReturn();
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Write, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_Write);
        data = checkName(name);
        BaseAop.aopMapAfter(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_Write, data);
        return data;
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, Boolean log) {
        WriteReturn data = null;
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            data = checkParam(result, name);
            BaseAop.aopMapBefore(name, result, DataConfig.db(mapDb(name)), AopEnum.Map_Write);
            if (!data.getSuccess()) {
                BaseAop.aopMapAfter(name, result, DataConfig.db(mapDb(name)), AopEnum.Map_Write, data);
                return data;
            }
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
                data = db.execute(result, false).getWriteReturn();
                BaseAop.aopMapAfter(name, result, db.config, AopEnum.Map_Write, data);
                return data;
            }
        }
        BaseAop.aopMapBefore(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_Write);
        data = checkName(name);
        BaseAop.aopMapAfter(name, null, DataConfig.db(mapDb(name)), AopEnum.Map_Write, data);
        return data;
    }

    @Override
    public WriteReturn execute(MapResult map, DataContext db, Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.executeParam(map);
    }

    @Override
    public WriteReturn execute(MapResult map, DataContext db) {
        return db.executeParam(map);
    }

    @Override
    public WriteReturn execute(MapResult map, String key, Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.executeParam(map);
        }
    }

    @Override
    public WriteReturn execute(MapResult map, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.executeParam(map);
        }
    }

    @Override
    public int count(Map<String, Object> map, Class<?> type, DataContext db, Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.count(map, type);
    }

    @Override
    public int count(Map<String, Object> map, Class<?> type, DataContext db) {
        return db.count(map, type);
    }

    @Override
    public int count(Map<String, Object> map, Class<?> type, String key, Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.count(map, type);
        }
    }

    @Override
    public int count(Map<String, Object> map, Class<?> type, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.count(map, type);
        }
    }

    @Override
    public DataReturn delete(Map<String, Object> map, Class<?> type, DataContext db, Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.delete(map, type);
    }

    @Override
    public DataReturn delete(Map<String, Object> map, Class<?> type, DataContext db) {
        return db.delete(map, type);
    }

    @Override
    public DataReturn delete(Map<String, Object> map, Class<?> type, String key, Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.delete(map, type);
        }
    }

    @Override
    public DataReturn delete(Map<String, Object> map, Class<?> type, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.delete(map, type);
        }
    }

    @Override
    public Object resolve(Class<?> interfaces, String key) {
        try (DataContext db = new DataContext(key)) {
            FastProxy fastProxy = new FastProxy(db);
            return fastProxy.instance(interfaces);
        }
    }

    @Override
    public Object resolve(Class<?> interfaces, DataContext db) {
        FastProxy fastProxy = new FastProxy(db);
        return fastProxy.instance(interfaces);
    }


    private String mapDb(String name) {
        return CacheUtil.get(String.format("%s.db", name.toLowerCase()));
    }

    private boolean mapLog(String name) {
        String result = CacheUtil.get(String.format("%s.log", name.toLowerCase()));
        if (result == null)
            return false;
        else
            return result.equals("true");
    }

    private WriteReturn checkParam(MapResult map, String name) {
        WriteReturn result = new WriteReturn();
        result.setSuccess(true);
        map.getParam().keySet().forEach(a -> {
            String param = map.getParam().get(a).toString();
            if (mapCheck(name, param) && map.getParam().get(param) == null) {
                result.setSuccess(false);
                result.setMessage(String.format("xml 中 id: %S 中参数 %s不能为空", name, param));
            }
        });

        return result;
    }

    private WriteReturn checkName(String name) {
        WriteReturn info = new WriteReturn();
        info.setSuccess(false);
        info.setMessage(String.format("xml 中 id: %S 不存在", name));
        return info;
    }

    private boolean mapCheck(String name, String param) {
        String result = CacheUtil.get(String.format("%s.%s.required", name.toLowerCase(), param.toLowerCase()));
        if (result == null)
            return false;
        else
            return result.equalsIgnoreCase("true");
    }

    /*
     cachePackageName: model cache package
     codeFirstPackageName: code first model package
     key: database key
     */
    private void init(String cachePackageName, String codeFirstPackageName, String key, String servicePackageName) {
        instanceMap();
        if (!FastUtil.isNullOrEmpty(cachePackageName))
            instanceProperties(cachePackageName);
        if (!FastUtil.isNullOrEmpty(codeFirstPackageName))
            instanceTable(codeFirstPackageName, key);
        if (!FastUtil.isNullOrEmpty(servicePackageName))
            instanceService(servicePackageName);
    }

    /* xml cache */
    private void instanceMap() {
        MapConfig list = DataConfig.map();
        if (list.getPath().size() == 0)
            return;
        list.getPath().forEach(p -> {
            try {
                MapXmlModel temp = new MapXmlModel();
                byte[] bytes = MessageDigest.getInstance("md5").digest(p.getBytes());
                temp.setFileName(p);
                temp.setFileKey(MapXml.readXml(DataConfig.Content(p), p));
                String key = new BigInteger(1, bytes).toString(32);
                CacheUtil.setModel(key, temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /* model cache */
    private void instanceProperties(String packageName) {
        List<Class> list = ScanPackage.get(packageName);
        List<FastMap<String, Object>> listtype = new ArrayList<FastMap<String, Object>>();
        list.forEach(a -> {
            List<PropertyModel> cacheList = new ArrayList<PropertyModel>();
            List<NavigateModel> navigateList = new ArrayList<NavigateModel>();
            String navigateKey = String.format("%s.Navigate", a.getName());
            Arrays.stream(a.getDeclaredFields()).forEach(f -> {
                try {
                    NavigateType navigateType = f.getAnnotation(NavigateType.class);
                    if (navigateType != null) {
                        NavigateModel navigateModel = new NavigateModel();
                        navigateModel.setPropertyType(navigateType.type());
                        navigateModel.setMemberName(f.getName());
                        navigateModel.setList(f.getType() == java.util.List.class);
                        navigateModel.setMemberType(f.getType());

                        Arrays.stream(navigateModel.getPropertyType().getDeclaredFields()).forEach(m -> {
                            Navigate navigate = m.getAnnotation(Navigate.class);
                            if (navigate != null) {
                                navigateModel.getName().add(navigate.Name());
                                navigateModel.getAppand().add(navigate.Appand());
                                navigateModel.setType(m.getType());
                            }
                        });

                        if (navigateModel.getName().size() > 0)
                            navigateList.add(navigateModel);
                    } else {
                        PropertyModel property = new PropertyModel();
                        property.setType(f.getType());
                        property.setName(f.getName());
                        cacheList.add(property);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            if (navigateList.size() > 0)
                CacheUtil.setModel(navigateKey, navigateList);
            CacheUtil.setModel(a.getName(), cacheList);
        });
    }

    /*
    cachePackageName: code first model package
    key: database key
    */
    private void instanceTable(String packageName, String key) {
        List<Class> list = ScanPackage.get(packageName);
        DbConfig config = DataConfig.db(key);
        if (config.getDesignModel().equalsIgnoreCase(Config.codeFirst)) {
            list.forEach(a -> {
                if (a.getAnnotation(Table.class) != null)
                    BaseTable.check(a, config);
            });
        }
    }

    /*
     servicePackageName: service Package Name
    */
    private void instanceService(String servicePackageName) {
        List<Class> list = ScanPackage.get(servicePackageName);
        list.forEach(a -> {
            Arrays.stream(a.getMethods()).forEach(m -> {
                LinkedHashMap<Integer, String> param = new LinkedHashMap();
                AnnotationModel model = new AnnotationModel();
                if (m.getAnnotation(FastRead.class) != null) {
                    FastRead read = (FastRead) m.getAnnotation(FastRead.class);
                    model.setSql(read.sql().toLowerCase());
                    for (int i = 0; i < m.getParameters().length; i++) {
                        param.put(i, m.getParameters()[i].getName().toLowerCase());
                    }
                    model.setParam(param);
                    model.setWrite(false);

                    if (m.getReturnType() == List.class) {
                        model.setList(true);
                        Type type = ((ParameterizedTypeImpl) m.getGenericReturnType()).getActualTypeArguments()[0];
                        if (type instanceof ParameterizedTypeImpl && ((ParameterizedTypeImpl) type).getRawType() == Map.class) {
                            model.setMap(true);
                            model.setType(((ParameterizedTypeImpl) type).getRawType());
                        } else
                            model.setType((Class<?>) type);
                    } else {
                        if (m.getReturnType() == Map.class)
                            model.setMap(true);

                        if(m.getReturnType().getSuperclass()== HashMap.class && m.getReturnType() != Map.class)
                            return;
                        model.setType(m.getReturnType());
                    }

                    CacheUtil.setModel(String.format("%s.%s", a.getName(), m.getName()), model);
                }
                if (m.getAnnotation(FastWrite.class) != null) {
                    FastWrite write = (FastWrite) m.getAnnotation(FastWrite.class);
                    model.setSql(write.sql());
                    for (int i = 0; i < m.getParameters().length; i++) {
                        param.put(i, m.getParameters()[i].getName().toLowerCase());
                    }
                    model.setParam(param);
                    model.setWrite(true);
                    CacheUtil.setModel(String.format("%s.%s", a.getName(), m.getName()), model);
                }
            });
        });
    }
}

class FastProxy implements InvocationHandler {
    public DataContext db;

    FastProxy(DataContext db) {
        this.db = db;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        AnnotationModel model = new AnnotationModel();
        Object result = null;
        MapResult map = new MapResult();
        try {
            String cachekey = String.format("%s.%s", method.getDeclaringClass().getName(), method.getName());
            if (CacheUtil.exists(cachekey)) {
                model = CacheUtil.getModel(cachekey, AnnotationModel.class);
                if (model.isVoid() && !model.isWrite())
                    return null;

                for (int i = 0; i < args.length; i++) {
                    String param = model.getParam().get(i);
                    if (model.getSql().indexOf(String.format("?%s", param)) > 0) {
                        map.getParam().put(param, args[i]);
                        model.setSql(model.getSql().replace(String.format("?%s", param), "?"));
                    }
                }
                map.setSql(model.getSql());

                if (model.isWrite())
                    return db.execute(map, true).getWriteReturn();

                if (model.isList() && model.isMap())
                    return db.query(map, true).getList();

                if (model.isList() && !model.isMap())
                    return db.query(map, model.getType(), true).getList();

                if (!model.isList() && model.isMap())
                    return db.query(map, true).getList().get(0);

                if (!model.isList() && !model.isMap())
                    return db.query(map, model.getType(), true).getList().get(0);

                return result;
            }
            return null;
        } catch (Exception ex) {
            BaseAop.aopException(ex, "FastProxy", AopEnum.FastProxy, db.config);
            return null;
        }
    }

    public Object instance(Class<?> interfaces) {
        return Proxy.newProxyInstance(FastProxy.class.getClassLoader(), new Class[]{interfaces}, new FastProxy(this.db));
    }
}