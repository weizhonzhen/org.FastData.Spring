package org.FastData.Spring.Base;

import com.alibaba.fastjson.JSON;
import org.FastData.Spring.CacheModel.DbConfig;
import org.FastData.Spring.CacheModel.MapConfig;
import org.FastData.Spring.Util.CacheUtil;
import org.FastData.Spring.Util.FastUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DataConfig {
    public static DbConfig db(String key) {
        if(FastUtil.isNullOrEmpty(key)) return new DbConfig();
        String cacheKey = "FastData.Config";
        if (CacheUtil.exists(cacheKey)) {
            List<DbConfig> list = CacheUtil.getList(cacheKey, DbConfig.class);
            Optional<DbConfig> confgOptional = list.stream().filter(a -> a.getKey().equals(key)).findFirst();
            return confgOptional.orElseGet(DbConfig::new);
        } else {
            Map map = JSON.parseObject(Content("db.json"), Map.class);
            if (map == null)
                return new DbConfig();
            List<DbConfig> list = JSON.parseArray(map.get("dataConfig").toString(), DbConfig.class);
            CacheUtil.setModel(cacheKey, list);
            Optional<DbConfig> confgOptional = list.stream().filter(a -> a.getKey().equals(key)).findFirst();
            return confgOptional.orElseGet(DbConfig::new);
        }
    }

    public static MapConfig map() {
        Map map = JSON.parseObject(Content("map.json"), Map.class);
        if (map == null)
            return new MapConfig();
        MapConfig config = JSON.parseObject(map.get("SqlMap").toString(), MapConfig.class);
        return config;
    }

    public static String Content(String fileName) {
        String s;
        StringBuilder sb = new StringBuilder();
        try {
            InputStream stream=Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                while ((s = reader.readLine()) != null) {
                    sb.append(s);
                }
                reader.close();
                stream.close();
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
