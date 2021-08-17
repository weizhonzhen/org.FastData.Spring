package org.FastData.Spring.Repository;

import org.FastData.Spring.Annotation.FastData;
import org.FastData.Spring.Annotation.Table;
import org.FastData.Spring.Aop.FastDataConfig;
import org.FastData.Spring.Aop.IFastAop;
import org.FastData.Spring.Aop.MapContext;
import org.FastData.Spring.Base.DataConfig;
import org.FastData.Spring.Base.MapXml;
import org.FastData.Spring.CacheModel.DbConfig;
import org.FastData.Spring.CacheModel.MapConfig;
import org.FastData.Spring.CacheModel.MapXmlModel;
import org.FastData.Spring.CacheModel.PropertyModel;
import org.FastData.Spring.Check.BaseTable;
import org.FastData.Spring.Config.Config;
import org.FastData.Spring.Context.DataContext;
import org.FastData.Spring.Model.*;
import org.FastData.Spring.Util.FastUtil;
import org.FastData.Spring.Util.ScanPackage;
import org.FastData.Spring.Util.CacheUtil;
import org.springframework.context.annotation.ComponentScan;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

@ComponentScan(basePackages = {"org.FastData.Spring.Repository","org.FastData.Spring.Handler"})
public class FastRepository implements IFastRepository {
    public FastRepository(){
        try {
            for(StackTraceElement item:Thread.currentThread().getStackTrace()) {
                FastData annotation = Thread.currentThread().getContextClassLoader().loadClass(item.getClassName()).getAnnotation(FastData.class);
                if (annotation != null) {
                    init(annotation.cachePackageName(), annotation.codeFirstPackageName(), annotation.key());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                aopMap(name, result, db.config);
                return db.query(result, type).getList();
            }
        }
        aopMap(name, null, DataConfig.db(key));
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                aopMap(name, result, db.config);
                return db.query(result, type).getList();
            }
        }
        aopMap(name, null, DataConfig.db(key));
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            aopMap(name, result, db.config);
            return db.query(result, type).getList();
        }
        aopMap(name, null, db.config);
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
            aopMap(name, result, db.config);
            return db.query(result, type).getList();
        }
        aopMap(name, null, db.config);
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                aopMap(name, result, db.config);
                return db.query(result, type).getList();
            }
        }
        aopMap(name, null, DataConfig.db(mapDb(name)));
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                aopMap(name, result, db.config);
                return db.query(result, type).getList();
            }
        }
        aopMap(name, null, DataConfig.db(mapDb(name)));
        return new ArrayList<>();
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                aopMap(name, result, db.config);
                return db.query(result).getList();
            }
        }
        aopMap(name, null, DataConfig.db(key));
        return new ArrayList<FastMap<String, Object>>();
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                aopMap(name, result, db.config);
                return db.query(result).getList();
            }
        }
        aopMap(name, null, DataConfig.db(key));
        return new ArrayList<FastMap<String, Object>>();
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            aopMap(name, result, db.config);
            return db.query(result).getList();
        }
        aopMap(name, null, db.config);
        return new ArrayList<FastMap<String, Object>>();
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
            aopMap(name, result, db.config);
            return db.query(result).getList();
        }
        aopMap(name, null, db.config);
        return new ArrayList<FastMap<String, Object>>();
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                aopMap(name, result, db.config);
                return db.query(result).getList();
            }
        }
        aopMap(name, null, DataConfig.db(mapDb(name)));
        return new ArrayList<FastMap<String, Object>>();
    }

    @Override
    public List<FastMap<String, Object>> query(String name, Map<String, Object> param,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                aopMap(name, result, db.config);
                return db.query(result).getList();
            }
        }
        aopMap(name, null, DataConfig.db(mapDb(name)));
        return new ArrayList<FastMap<String, Object>>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                aopMap(name, result, db.config);
                return db.page(pModel, result, type);
            }
        }
        aopMap(name, null, DataConfig.db(key));
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                aopMap(name, result, db.config);
                return db.page(pModel, result, type);
            }
        }
        aopMap(name, null, DataConfig.db(key));
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            aopMap(name, result, db.config);
            return db.page(pModel, result, type);
        }
        aopMap(name, null, db.config);
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
            aopMap(name, result, db.config);
            return db.page(pModel, result, type);
        }
        aopMap(name, null, db.config);
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                aopMap(name, result, db.config);
                return db.page(pModel, result, type);
            }
        }
        aopMap(name, null, DataConfig.db(mapDb(name)));
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                aopMap(name, result, db.config);
                return db.page(pModel, result, type);
            }
        }
        aopMap(name, null, DataConfig.db(mapDb(name)));
        return new PageResultImpl<>();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                aopMap(name, result, db.config);
                return db.page(pModel, result);
            }
        }
        aopMap(name, null, DataConfig.db(key));
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                aopMap(name, result, db.config);
                return db.page(pModel, result);
            }
        }
        aopMap(name, null, DataConfig.db(key));
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            aopMap(name, result, db.config);
            return db.page(pModel, result);
        }
        aopMap(name, null, db.config);
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
            aopMap(name, result, db.config);
            return db.page(pModel, result);
        }
        aopMap(name, null, db.config);
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                aopMap(name, result, db.config);
                return db.page(pModel, result);
            }
        }
        aopMap(name, null,DataConfig.db(mapDb(name)));
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                aopMap(name, result, db.config);
                return db.page(pModel, result);
            }
        }
        aopMap(name, null,DataConfig.db(mapDb(name)));
        return new PageResult();
    }

    @Override
    public WriteReturn add(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.add(model).getWriteReturn();
        }
    }

    @Override
    public WriteReturn add(Object model, String key,Boolean log) {
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
    public WriteReturn add(Object model, DataContext db,Boolean log) {
        db.config.setOutSql(db.config.isOutSql() ||log);
        return db.add(model).getWriteReturn();
    }

    @Override
    public WriteReturn update(Object model, List<String> field, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.update(model,field).getWriteReturn();
        }
    }

    @Override
    public WriteReturn update(Object model, List<String> field, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() ||log);
            return db.update(model,field).getWriteReturn();
        }
    }

    @Override
    public WriteReturn update(Object model, List<String> field, DataContext db) {
        return db.update(model,field).getWriteReturn();
    }

    @Override
    public WriteReturn update(Object model, List<String> field, DataContext db,Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.update(model,field).getWriteReturn();
    }

    @Override
    public WriteReturn update(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.update(model).getWriteReturn();
        }
    }

    @Override
    public WriteReturn update(Object model, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.update(model).getWriteReturn();
        }
    }

    @Override
    public WriteReturn update(Object model, DataContext db) {
        return db.update(model).getWriteReturn();
    }

    @Override
    public WriteReturn update(Object model, DataContext db,Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.update(model).getWriteReturn();
    }

    @Override
    public WriteReturn delete(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.delete(model).getWriteReturn();
        }
    }

    @Override
    public WriteReturn delete(Object model, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() ||log);
            return db.delete(model).getWriteReturn();
        }
    }

    @Override
    public WriteReturn delete(Object model, DataContext db) {
        return db.delete(model).getWriteReturn();
    }

    @Override
    public WriteReturn delete(Object model, DataContext db,Boolean log) {
        db.config.setOutSql(db.config.isOutSql() || log);
        return db.delete(model).getWriteReturn();
    }

    @Override
    public boolean exists(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.exists(model).getCount()>0;
        }
    }

    @Override
    public boolean exists(Object model, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() || log);
            return db.exists(model).getCount()>0;
        }
    }

    @Override
    public boolean exists(Object model, DataContext db) {
        return db.exists(model).getCount()>0;
    }

    @Override
    public boolean exists(Object model, DataContext db,Boolean log) {
        db.config.setOutSql(db.config.isOutSql() ||log);
        return db.exists(model).getCount()>0;
    }

    @Override
    public Object query(Object model,Class<?> type, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.query(model,type).getItem();
        }
    }

    @Override
    public Object query(Object model,Class<?> type, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() ||log);
            return db.query(model,type).getItem();
        }
    }

    @Override
    public Object query(Object model,Class<?> type, DataContext db) {
        return db.query(model,type).getItem();
    }

    @Override
    public Object query(Object model,Class<?> type, DataContext db,Boolean log) {
        db.config.setOutSql(db.config.isOutSql()||log);
        return db.query(model,type).getItem();
    }

    @Override
    public Map<String,Object> query(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.query(model).getItem();
        }
    }

    @Override
    public FastMap<String,Object> query(Object model, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() ||log);
            return db.query(model).getItem();
        }
    }

    @Override
    public FastMap<String,Object> query(Object model, DataContext db) {
        return db.query(model).getItem();
    }

    @Override
    public FastMap<String,Object> query(Object model, DataContext db,Boolean log) {
        db.config.setOutSql(db.config.isOutSql() ||log);
        return db.query(model).getItem();
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.getSuccess())
                return check;
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                aopMap(name, result, db.config);
                return db.execute(result).getWriteReturn();
            }
        }
        aopMap(name, null, DataConfig.db(key));
        return checkName(name);
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.getSuccess())
                return check;
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                aopMap(name, result, db.config);
                return db.execute(result).getWriteReturn();
            }
        }
        aopMap(name, null, DataConfig.db(key));
        return checkName(name);
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.getSuccess())
                return check;
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            aopMap(name, result, db.config);
            return db.execute(result).getWriteReturn();
        }
        aopMap(name, null, db.config);
        return checkName(name);
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.getSuccess())
                return check;
            db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
            aopMap(name, result, db.config);
            return db.execute(result).getWriteReturn();
        }
        aopMap(name, null, db.config);
        return checkName(name);
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.getSuccess())
                return check;
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                aopMap(name, result, db.config);
                return db.execute(result).getWriteReturn();
            }
        }
        aopMap(name, null, DataConfig.db(mapDb(name)));
        return checkName(name);
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.getSuccess())
                return check;
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name) || log);
                aopMap(name, result, db.config);
                return db.execute(result).getWriteReturn();
            }
        }
        aopMap(name, null, DataConfig.db(mapDb(name)));
        return checkName(name);
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

    private WriteReturn checkParam(MapResult map,String name) {
        WriteReturn result = new WriteReturn();
        result.setSuccess(true);
        map.getParam().keySet().forEach(a->{
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

    private boolean mapCheck(String name,String param) {
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
    private void init(String cachePackageName, String codeFirstPackageName, String key) {
        instanceMap();
        if (!FastUtil.isNullOrEmpty(cachePackageName))
            instanceProperties(cachePackageName);
        if (!FastUtil.isNullOrEmpty(codeFirstPackageName))
            instanceTable(codeFirstPackageName, key);
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
                temp.setFileKey(MapXml.readXml(DataConfig.Content(p)));
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
        list.forEach(a -> {
            List<PropertyModel> cacheList = new ArrayList<PropertyModel>();
            Arrays.stream(a.getDeclaredFields()).forEach(f -> {
                try {
                    PropertyModel property = new PropertyModel();
                    property.setType(f.getType());
                    property.setName(f.getName());
                    cacheList.add(property);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
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

    private void aopMap(String mapName, MapResult map, DbConfig config){
        IFastAop aop = FastDataConfig.getAop();
        if (aop != null) {
            MapContext context = new MapContext();
            context.setMapName(mapName);

            if (map != null) {
                context.setSql(map.getSql());
                if (map.getParam() != null)
                    context.setParam(map.getParam());
            }
            else {
                context.setSql("");
                context.setParam(new LinkedHashMap<String, Object>());
            }

            context.setDbType(config.getDbType());
            aop.map(context);
        }
    }
}
