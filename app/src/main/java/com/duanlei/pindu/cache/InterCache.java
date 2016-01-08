package com.duanlei.pindu.cache;

import android.graphics.Bitmap;

/**
 * Author: duanlei
 * Date: 2016-01-08
 */
public interface InterCache {
    Bitmap get(String key);

    void put(String key, Bitmap bitmap);
}
