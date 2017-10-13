package com.caizhenliang.mylibrary.util;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.caizhenliang.mylibrary.util.base.ACache;

import java.util.List;

/**
 * Created by caizhenliang on 2017/10/13.
 */

public class SCCache {

    private static SCCache ourInstance;
    private static ACache mCache;

    // - - - - - - - - -  - -  - - -  - - - -
    public static SCCache getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new SCCache(context, "SCCache");
        }
        return ourInstance;
    }

    public static SCCache getInstance(Context context, String cacheName) {
        if (ourInstance == null) {
            ourInstance = new SCCache(context, cacheName);
        }
        return ourInstance;
    }

    private SCCache() {
    }

    private SCCache(Context context) {
        mCache = ACache.get(context);
    }

    private SCCache(Context context, String cacheName) {
        mCache = ACache.get(context, cacheName);
    }

    // - - - - - - - - -  - -  - - -  - - - -
    public void put(String key, List<?> list) {
        mCache.put(key, JSONArray.toJSONString(list));
    }

    public <T> List<T> get(String key, Class<T> clazz) {
        String str = mCache.getAsString(key);
        return JSONArray.parseArray(str, clazz);
    }


}
