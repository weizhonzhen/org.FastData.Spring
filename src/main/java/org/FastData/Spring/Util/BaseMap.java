package org.FastData.Spring.Util;

import java.util.Arrays;
import java.util.LinkedHashMap;

public final class BaseMap {
    public static <T> LinkedHashMap<String,Object> toMap(T model) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        Arrays.stream(model.getClass().getDeclaredFields()).forEach(a->{
            result.put(a.getName(),ReflectUtil.get(model,a.getName(),a.getType()));
        });
        return result;
    }
}
