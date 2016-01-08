package com.duanlei.pindu.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Author: duanlei
 * Date: 2016-01-08
 */
public class MemoryCache implements InterCache {


    private static MemoryCache mMemoryCache;

    private LruCache<String, Bitmap> mLruMemoryCache;

    public MemoryCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mLruMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //重写此方法来衡量每张图片的大小，默认返回图片数量
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static MemoryCache getMemoryCache() {
        if (mMemoryCache == null) {
            synchronized (DiskCache.class) {
                if (mMemoryCache == null) {
                    mMemoryCache = new MemoryCache();
                }
            }
        }
        return mMemoryCache;
    }

    @Override
    public Bitmap get(String key) {
        return mLruMemoryCache.get(key);
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        if (get(key) == null) {
            mLruMemoryCache.put(key, bitmap);
        }
    }
}
