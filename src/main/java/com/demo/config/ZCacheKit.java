package com.demo.config;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * @author ygzheng
 * 启动缓存很慢，所以增加一个配置
 */
public class ZCacheKit {

    public static boolean useCache() {
        return PropKit.getBoolean("useCache", false);
    }

    public static void put(String cacheName, Object key, Object value) {
        if (useCache()) {
            CacheKit.put(cacheName, key, value);
        }

    }

    public static <T> T get(String cacheName, Object key) {
        if (useCache()) {
            return CacheKit.get(cacheName, key);
        }
        return null;
    }
}
