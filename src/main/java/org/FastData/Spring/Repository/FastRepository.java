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
import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ComponentScan(basePackages = {"org.FastData.Spring.Repository","org.FastData.Spring.Handler"})
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
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                return db.query(result, type).getList();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                return db.query(result, type).getList();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            return db.query(result, type).getList();
        }
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
            return db.query(result, type).getList();
        }
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                return db.query(result, type).getList();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<?> query(String name, Map<String, Object> param, Class<?> type,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                return db.query(result, type).getList();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                return db.query(result).getList();
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param, String key,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(key)) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                return db.query(result).getList();
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            return db.query(result).getList();
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
            return db.query(result).getList();
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
                return db.query(result).getList();
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> query(String name, Map<String, Object> param,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            MapResult result = MapXml.getMapSql(name, param);
            try (DataContext db = new DataContext(mapDb(name))) {
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                return db.query(result).getList();
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, String key) {
        if (CacheUtil.exists(name.toLowerCase())) {
            try (DataContext db = new DataContext(key)) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
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
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                return db.page(pModel, result, type);
            }
        }
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            return db.page(pModel, result, type);
        }
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
            return db.page(pModel, result, type);
        }
        return new PageResultImpl<>();
    }

    @Override
    public PageResultImpl<?> page(PageModel pModel, String name, Map<String, Object> param, Class<?> type) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
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
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
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
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
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
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                return db.page(pModel, result);
            }
        }
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, DataContext db) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name));
            return db.page(pModel, result);
        }
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param, DataContext db,Boolean log) {
        if (CacheUtil.exists(name.toLowerCase())) {
            MapResult result = MapXml.getMapSql(name, param);
            db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
            return db.page(pModel, result);
        }
        return new PageResult();
    }

    @Override
    public PageResult page(PageModel pModel, String name, Map<String, Object> param) {
        if (CacheUtil.exists(name.toLowerCase()) && mapDb(name) != null) {
            try (DataContext db = new DataContext(mapDb(name))) {
                MapResult result = MapXml.getMapSql(name, param);
                db.config.setOutSql(db.config.isOutSql() || mapLog(name));
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
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                return db.page(pModel, result);
            }
        }
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
    public Map<String,Object> query(Object model, String key,Boolean log) {
        try (DataContext db = new DataContext(key)) {
            db.config.setOutSql(db.config.isOutSql() ||log);
            return db.query(model).getItem();
        }
    }

    @Override
    public Map<String,Object> query(Object model, DataContext db) {
        return db.query(model).getItem();
    }

    @Override
    public Map<String,Object> query(Object model, DataContext db,Boolean log) {
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
                return db.execute(result).getWriteReturn();
            }
        }
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
                return db.execute(result).getWriteReturn();
            }
        }
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
            return db.execute(result).getWriteReturn();
        }
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
            return db.execute(result).getWriteReturn();
        }
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
                return db.execute(result).getWriteReturn();
            }
        }
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
                db.config.setOutSql(db.config.isOutSql() || mapLog(name)||log);
                return db.execute(result).getWriteReturn();
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
        if (cachePackageName != null && !cachePackageName.equals(""))
            instanceProperties(cachePackageName);
        if (codeFirstPackageName != null && !codeFirstPackageName.equals(""))
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
}
