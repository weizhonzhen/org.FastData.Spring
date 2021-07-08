package org.FastData.Spring.Model;

import java.util.HashMap;

public class FastMap<S, O> extends HashMap {
    public Object put(String key,String value){
        return super.put(key.toUpperCase(), value);
    }

    public Object get(String keys){
        return super.get(keys.toUpperCase());
    }
}
