package org.FastData.Spring.Util;

import com.esotericsoftware.reflectasm.MethodAccess;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class ReflectUtil {
    private static Map<String, Method> cache;

    static {
        cache = new HashMap<String, Method>();
    }

    public static <T> void set(T model, Object value, String name, Class<?> type) {
        try {
            if (value == null)
                return;
            Method method = null;
            String key = String.format("%s.set%s",model.getClass().getName(),name);
            if (cache.get(key) == null) {
                method = model.getClass().getMethod(String.format("set%s", name), type);
                cache.put(key, method);
            } else
                method = cache.get(key);

            MethodAccess methodAccess = MethodAccess.get(model.getClass());
            methodAccess.invoke(model, method.getName(), value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static <T> Object get(T model, String name) {
        Object result = null;
        try {
            Method method = null;
            String key = String.format("%s.get%s",model.getClass().getName(),name);
            if (cache.get(key) == null) {
                method = model.getClass().getMethod(String.format("get%s", name));
                cache.put(key, method);
            } else
                method = cache.get(key);

            MethodAccess methodAccess = MethodAccess.get(model.getClass());
            result = methodAccess.invoke(model, method.getName());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }
}
