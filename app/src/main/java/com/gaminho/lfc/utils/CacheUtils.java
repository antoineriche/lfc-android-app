package com.gaminho.lfc.utils;

import android.util.LruCache;

import com.gaminho.lfc.model.LFCEdition;
import com.gaminho.lfc.model.LFCStaff;
import com.gaminho.lfc.model.Location;

/**
 * Created by Bonnie on 15/04/2022
 */
public final class CacheUtils {

    public static final LruCache<String, Location> LOCATION_CACHE = new LruCache<>(buildCacheSizeInMo(1));
    public static final LruCache<String, LFCEdition> LFC_EDITION_CACHE = new LruCache<>(buildCacheSizeInMo(1));
    public static final LruCache<String, LFCStaff> STAFF_CACHE = new LruCache<>(buildCacheSizeInMo(1));
    public static final LruCache<String, Object> DEFAULT_CACHE = new LruCache<>(buildCacheSizeInMo(1));

    public static <T> LruCache<String, T> getCache(final Class<T> tClass) {
        if (tClass.equals(Location.class)) {
            return (LruCache<String, T>) LOCATION_CACHE;
        } else if (tClass.equals(LFCEdition.class)) {
            return (LruCache<String, T>) LFC_EDITION_CACHE;
        } else if (tClass.equals(LFCStaff.class)) {
            return (LruCache<String, T>) STAFF_CACHE;
        } else {
            return (LruCache<String, T>) DEFAULT_CACHE;
        }
    }

    public static int buildCacheSizeInMo(final int sizeInMo) {
        return 1024 * 1024 * sizeInMo;
    }
}
