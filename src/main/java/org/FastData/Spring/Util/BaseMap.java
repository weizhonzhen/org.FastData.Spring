package org.FastData.Spring.Util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public final class BaseMap {
    public static <T> Map<String, Object> toMap(T model) {
        Map<String, Object> result = new HashMap<>();
        Arrays.stream(model.getClass().getDeclaredFields()).forEach((a) -> {
            Object value = ReflectUtil.get(model, a.getName(), a.getType());
            if (!FastUtil.isNullOrEmpty(value))
                result.put(a.getName(), value);
        });
        return result;
    }

    public static <T> T toModel(Object model,Class<T> type) {
        try {
            final Object[] value = {null};
            T result = (T) type.newInstance();
            Stream<Field> field = Arrays.stream(type.getDeclaredFields());
            Arrays.stream(model.getClass().getDeclaredFields()).forEach(f -> {
                if (field.anyMatch(a -> a.getName().equalsIgnoreCase(f.getName())))
                    value[0] = ReflectUtil.get(model, f.getName(), f.getType());
                if (value[0] != null)
                    ReflectUtil.set(result, value[0], f.getName(), type);
            });

            field.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
