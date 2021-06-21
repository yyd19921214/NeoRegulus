package com.example.yudyang.regulus.core.sql.utils;

import java.util.Map;

public class FlatMapUtils {
    public static Object flatGet(String key, Map<String, Object> map) {
        if (key.contains(CoreConstants.DOT)){
            String firstItem = key.substring(0,key.indexOf(CoreConstants.DOT));
            String secItem = key.substring(key.indexOf(CoreConstants.DOT)+1);
            if (map.containsKey(firstItem) && map.get(firstItem) instanceof Map){
                return flatGet(secItem, (Map<String, Object>) map.get(firstItem));
            }
            else {
                return null;
            }
        }
        else{
            return map.get(key);
        }
    }
}
