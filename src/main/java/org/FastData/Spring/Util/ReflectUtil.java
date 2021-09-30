package org.FastData.Spring.Util;

import com.esotericsoftware.reflectasm.MethodAccess;

import java.lang.invoke.ConstantCallSite;
import java.lang.reflect.Method;
import java.util.Arrays;
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

            String key = String.format("%s.set%s", model.getClass().getName(), name);
            if (cache.get(key) == null) {
                if (type == Boolean.TYPE)
                    method = Arrays.stream(model.getClass().getMethods()).filter(a -> a.getName().contains(name)).findFirst().get();
                else
                    method =  Arrays.stream(model.getClass().getMethods()).filter(a->a.getName().equalsIgnoreCase(String.format("set%s", name))).findFirst().get();
                cache.put(key, method);
            } else
                method = cache.get(key);

            MethodAccess methodAccess = MethodAccess.get(model.getClass());
            methodAccess.invoke(model, method.getName(), convert(value, type));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static <T> Object get(T model, String name, Class<?> type) {
        Object result = null;
        try {
            Method method = null;
            String key = String.format("%s.get%s", model.getClass().getName(), name);
            if (cache.get(key) == null) {
                if (type == Boolean.TYPE)
                    method = Arrays.stream(model.getClass().getMethods()).filter(a -> a.getName().contains(name)).findFirst().get();
                else
                    method =  Arrays.stream(model.getClass().getMethods()).filter(a->a.getName().equalsIgnoreCase(String.format("get%s", name))).findFirst().get();
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

    public static Object get(Object model,String name) {
        Object result = null;
        try {
            Method method = null;
            String key = String.format("%s.get%s", model.getClass().getName(), name);
            if (cache.get(key) == null) {
                if (model.getClass() == Boolean.TYPE)
                    method = Arrays.stream(model.getClass().getMethods()).filter(a -> a.getName().contains(name)).findFirst().get();
                else
                    method =  Arrays.stream(model.getClass().getMethods()).filter(a->a.getName().equalsIgnoreCase(String.format("get%s", name))).findFirst().get();
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

    private static Object convert(Object value, Class<?> type) {
        if (Double.class.equals(type))
            return Double.valueOf(value.toString());
        else if (Float.class.equals(type))
            return Float.valueOf(value.toString());
        else if (Long.class.equals(type))
            return Long.valueOf(value.toString());
        else if (Integer.class.equals(type))
            return Integer.valueOf(value.toString());
        else if (String.class.equals(type))
            return value.toString();
        else
            return value;
    }
}
