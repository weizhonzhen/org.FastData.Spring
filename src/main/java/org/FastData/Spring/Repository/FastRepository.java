package org.FastData.Spring.Repository;

import org.FastData.Spring.Annotation.FastData;
import org.FastData.Spring.Annotation.Table;
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
import org.FastData.Spring.Util.ScanPackage;
import org.FastData.Spring.Util.CacheUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "org.FastData.Spring.Repository")
public class FastRepository implements IFastRepository {
    @PostConstruct
    public void init() {
        try {
            String mainClass = Thread.currentThread().getStackTrace()[Thread.currentThread().getStackTrace().length - 1].getClassName();
            FastData annotation = Thread.currentThread().getContextClassLoader().loadClass(mainClass).getAnnotation(FastData.class);
            init(annotation.cachePackageName(),annotation.codeFirstPackageName(),annotation.key());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name);
                return db.query(result, type).list;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name) || log;
                return db.query(result, type).list;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.isOutSql = db.config.isOutSql || mapLog(name);
            return db.query(result, type).list;
        }
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.isOutSql = db.config.isOutSql || mapLog(name) || log;
            return db.query(result, type).list;
        }
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name);
                return db.query(result, type).list;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name) || log;
                return db.query(result, type).list;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name);
                return db.query(result).list;
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
                return db.query(result).list;
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.isOutSql = db.config.isOutSql || mapLog(name);
            return db.query(result).list;
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
            return db.query(result).list;
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name);
                return db.query(result).list;
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
                return db.query(result).list;
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.isOutSql = db.config.isOutSql || mapLog(name);
                return db.page(pModel, result, type);
            }
        }
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
                return db.page(pModel, result, type);
            }
        }
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.isOutSql = db.config.isOutSql || mapLog(name);
            return db.page(pModel, result, type);
        }
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
            return db.page(pModel, result, type);
        }
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.isOutSql = db.config.isOutSql || mapLog(name);
                return db.page(pModel, result, type);
            }
        }
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
                return db.page(pModel, result, type);
            }
        }
        return new PageResultImpl<>();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.isOutSql = db.config.isOutSql || mapLog(name);
                return db.page(pModel, result);
            }
        }
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
                return db.page(pModel, result);
            }
        }
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.isOutSql = db.config.isOutSql || mapLog(name);
            return db.page(pModel, result);
        }
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
            return db.page(pModel, result);
        }
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.isOutSql = db.config.isOutSql || mapLog(name);
                return db.page(pModel, result);
            }
        }
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
                return db.page(pModel, result);
            }
        }
        return new PageResult();
    }

    @Override
    public WriteReturn add(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.add(model).writeReturn;
        }
    }

    @Override
    public WriteReturn add(Object model, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.isOutSql=db.config.isOutSql||log;
            return db.add(model).writeReturn;
        }
    }

    @Override
    public WriteReturn add(Object model, DataContext db) {
        return db.add(model).writeReturn;
    }

    @Override
    public WriteReturn add(Object model, DataContext db,Boolean log) {
        db.config.isOutSql=db.config.isOutSql||log;
        return db.add(model).writeReturn;
    }

    @Override
    public WriteReturn update(Object model, List<String> field, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.update(model,field).writeReturn;
        }
    }

    @Override
    public WriteReturn update(Object model, List<String> field, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.isOutSql=db.config.isOutSql||log;
            return db.update(model,field).writeReturn;
        }
    }

    @Override
    public WriteReturn update(Object model, List<String> field, DataContext db) {
        return db.update(model,field).writeReturn;
    }

    @Override
    public WriteReturn update(Object model, List<String> field, DataContext db,Boolean log) {
        db.config.isOutSql=db.config.isOutSql||log;
        return db.update(model,field).writeReturn;
    }

    @Override
    public WriteReturn update(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.update(model).writeReturn;
        }
    }

    @Override
    public WriteReturn update(Object model, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.isOutSql=db.config.isOutSql||log;
            return db.update(model).writeReturn;
        }
    }

    @Override
    public WriteReturn update(Object model, DataContext db) {
        return db.update(model).writeReturn;
    }

    @Override
    public WriteReturn update(Object model, DataContext db,Boolean log) {
        db.config.isOutSql=db.config.isOutSql||log;
        return db.update(model).writeReturn;
    }

    @Override
    public WriteReturn delete(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.delete(model).writeReturn;
        }
    }

    @Override
    public WriteReturn delete(Object model, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.isOutSql=db.config.isOutSql||log;
            return db.delete(model).writeReturn;
        }
    }

    @Override
    public WriteReturn delete(Object model, DataContext db) {
        return db.delete(model).writeReturn;
    }

    @Override
    public WriteReturn delete(Object model, DataContext db,Boolean log) {
        db.config.isOutSql=db.config.isOutSql||log;
        return db.delete(model).writeReturn;
    }

    @Override
    public boolean exists(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.exists(model).count>0;
        }
    }

    @Override
    public boolean exists(Object model, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.isOutSql=db.config.isOutSql||log;
            return db.exists(model).count>0;
        }
    }

    @Override
    public boolean exists(Object model, DataContext db) {
        return db.exists(model).count>0;
    }

    @Override
    public boolean exists(Object model, DataContext db,Boolean log) {
        db.config.isOutSql=db.config.isOutSql||log;
        return db.exists(model).count>0;
    }

    @Override
    public Object query(Object model,Class<?> type, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.query(model,type).item;
        }
    }

    @Override
    public Object query(Object model,Class<?> type, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.isOutSql=db.config.isOutSql||log;
            return db.query(model,type).item;
        }
    }

    @Override
    public Object query(Object model,Class<?> type, DataContext db) {
        return db.query(model,type).item;
    }

    @Override
    public Object query(Object model,Class<?> type, DataContext db,Boolean log) {
        db.config.isOutSql=db.config.isOutSql||log;
        return db.query(model,type).item;
    }

    @Override
    public Map<String,Object> query(Object model, String key) {
        try (DataContext db = new DataContext(key)) {
            return db.query(model).item;
        }
    }

    @Override
    public Map<String,Object> query(Object model, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.isOutSql=db.config.isOutSql||log;
            return db.query(model).item;
        }
    }

    @Override
    public Map<String,Object> query(Object model, DataContext db) {
        return db.query(model).item;
    }

    @Override
    public Map<String,Object> query(Object model, DataContext db,Boolean log) {
        db.config.isOutSql=db.config.isOutSql||log;
        return db.query(model).item;
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.isSuccess)
                return check;
            try (DataContext db = new DataContext(key)) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name);
                return db.execute(result).writeReturn;
            }
        }
        return checkName(name);
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.isSuccess)
                return check;
            try (DataContext db = new DataContext(key)) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
                return db.execute(result).writeReturn;
            }
        }
        return checkName(name);
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.isSuccess)
                return check;
            db.config.isOutSql = db.config.isOutSql || mapLog(name);
            return db.execute(result).writeReturn;
        }
        return checkName(name);
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.isSuccess)
                return check;
            db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
            return db.execute(result).writeReturn;
        }
        return checkName(name);
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.isSuccess)
                return check;
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name);
                return db.execute(result).writeReturn;
            }
        }
        return checkName(name);
    }

    @Override
    public WriteReturn write(String name, Map<String, Object> param,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            WriteReturn check = checkParam(result, name);
            if (!check.isSuccess)
                return check;
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.isOutSql = db.config.isOutSql || mapLog(name)||log;
                return db.execute(result).writeReturn;
            }
        }
        return checkName(name);
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
        result.isSuccess = true;
        for (int i = 0; i < map.name.size(); i++) {
            String param = map.name.get(i);
            if (mapCheck(name, param) && map.param.get(param) == null) {
                result.isSuccess = false;
                result.message = String.format("xml 中 id: %S 中参数 %s不能为空", name, param);
            }
        }
        return result;
    }

    private WriteReturn checkName(String name) {
        WriteReturn info = new WriteReturn();
        info.isSuccess = false;
        info.message = String.format("xml 中 id: %S 不存在", name);
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
        if (cachePackageName != null && cachePackageName.equals(""))
            instanceProperties(cachePackageName);
        if (codeFirstPackageName != null && !codeFirstPackageName.equals(""))
            instanceTable(codeFirstPackageName, key);
    }

    /* xml cache */
    private void instanceMap() {
        MapConfig list = DataConfig.map();
        if (list.path.size() == 0)
            return;
        list.path.forEach(p -> {
            try {
                MapXmlModel temp = new MapXmlModel();
                byte[] bytes = MessageDigest.getInstance("md5").digest(p.getBytes());
                temp.fileName = p;
                temp.fileKey = MapXml.readXml(DataConfig.Content(p));
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
            Arrays.stream(a.getFields()).forEach(f -> {
                try {
                    PropertyModel property = new PropertyModel();
                    property.type = f.getType();
                    property.name = f.getName();
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
        if (config.designModel.equalsIgnoreCase(Config.codeFirst)) {
            list.forEach(a -> {
                if (a.getAnnotation(Table.class) != null)
                    BaseTable.check(a, config);
            });
        }
    }
}
