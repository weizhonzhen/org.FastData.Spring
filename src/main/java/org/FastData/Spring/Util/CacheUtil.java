package org.FastData.Spring.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CacheUtil {
    private static Map<String, Object> cache = new HashMap<String, Object>();

    public static void set(String key, String value) {
        cache.remove(key);
        cache.put(key, value);
    }

    public static void setMap(String key, Map value) {
        cache.remove(key);
        cache.put(key, value);
    }

    public static <T> void setModel(String key, T value) {
        cache.remove(key);
        cache.put(key, value);
    }

    public static String get(String key) {
        return (String) cache.get(key);
    }

    public static Map getMap(String key) {
        return (Map) cache.get(key);
    }

    public static <T> T getModel(String key, Class<T> type) {
        return (T) cache.get(key);
    }

    public static <T> List<T> getList(String key, Class<T> type) {
        return (List<T>) cache.get(key);
    }

    public static boolean exists(String key) {
        return cache.containsKey(key);
    }

    public synchronized static void remove(String key) {
        cache.remove(key);
    }
}
