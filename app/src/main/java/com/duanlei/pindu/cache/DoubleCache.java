package com.duanlei.pindu.cache;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Author: duanlei
 * Date: 2016-01-08
 */
public class DoubleCache implements InterCache {

    private DiskCache mDiskCache;
    private MemoryCache mMemoryCache;
    private static DoubleCache mDoubleCache;

    public DoubleCache(Context context) {
        mMemoryCache = MemoryCache.getMemoryCache();
        mDiskCache = DiskCache.getDiskCache(context);
    }

    public static DoubleCache getDoubleCache(Context context) {
        if (mDoubleCache == null) {
            synchronized (DiskCache.class) {
                if (mDoubleCache == null) {
                    mDoubleCache = new DoubleCache(context);
                }
            }
        }
        return mDoubleCache;
    }

    @Override
    public Bitmap get(String key) {
        Bitmap bitmapMem = mMemoryCache.get(key);
        Bitmap bitmapDisk = mDiskCache.get(key);
        if (bitmapMem != null) {
            return bitmapMem;
        } else if (bitmapDisk != null){
            return bitmapDisk;
        }
        return null;
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        mDiskCache.put(key, bitmap);
        mMemoryCache.put(key, bitmap);
    }
}
