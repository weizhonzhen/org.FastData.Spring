package org.FastData.Spring.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class BaseMap {
    public static <T> Map<String, Object> toMap(T model) {
        Map<String, Object> result = new HashMap<>();
        Arrays.stream(model.getClass().getDeclaredFields()).forEach((a) -> {
            Object value = ReflectUtil.get(model, a.getName(), a.getType());
            if (!Util.isNullOrEmpty(value))
                result.put(a.getName(), value);
        });
        return result;
    }
}
