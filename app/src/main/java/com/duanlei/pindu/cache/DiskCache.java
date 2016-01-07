package com.duanlei.pindu.cache;

/**
 * Author: duanlei
 * Date: 2016-01-07
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.duanlei.pindu.utils.BitmapDecoder;
import com.duanlei.pindu.utils.Md5Helper;
import com.duanlei.pindu.utils.ScreenUtils;
import com.duanlei.pindu.utils.Util;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Author: duanlei
 * Date: 2015-12-04
 */
public class DiskCache  {
    /**
     * 1MB
     */
    private static final int MB = 1024 * 1024;

    /**
     * cache dir
     */
    private static final String IMAGE_DISK_CACHE = "bitmap";
    /**
     * Disk LRU Cache
     */
    private DiskLruCache mDiskLruCache;
    /**
     * Disk Cache Instance
     */
    private static DiskCache mDiskCache;

    private Context mContext;

    /**
     * @param context
     */
    private DiskCache(Context context) {
        mContext = context;
        initDiskCache(context);
    }

    public static DiskCache getDiskCache(Context context) {
        if (mDiskCache == null) {
            synchronized (DiskCache.class) {
                if (mDiskCache == null) {
                    mDiskCache = new DiskCache(context);
                }
            }

        }
        return mDiskCache;
    }

    /**
     * 初始化sdcard缓存
     */
    private void initDiskCache(Context context) {
        try {
            File cacheDir = getDiskCacheDir(context, IMAGE_DISK_CACHE);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache
                    .open(cacheDir, getAppVersion(context), 1, 50 * MB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param uniqueName
     * @return
     */
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d("", "### context : " + context + ", dir = " + context.getExternalCacheDir());
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * @param context
     * @return
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }


    public synchronized Bitmap get(final String url) {

        // 图片解析器
        BitmapDecoder decoder = new BitmapDecoder() {

            @Override
            public Bitmap decodeBitmapWithOption(BitmapFactory.Options options) {
                final InputStream inputStream = getInputStream(Md5Helper.toMD5(url));
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null,
                        options);
                Util.closeQuietly(inputStream);
                return bitmap;
            }
        };

        return decoder.decodeBitmap(ScreenUtils.getScreenWidth(mContext) / 4,
                ScreenUtils.getScreenWidth(mContext) / 4);
    }

    private InputStream getInputStream(String md5) {
        DiskLruCache.Snapshot snapshot;
        try {
            snapshot = mDiskLruCache.get(md5);
            if (snapshot != null) {
                return snapshot.getInputStream(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * sd卡缓存只缓存从网络下下载下来的图片,本地图片则不缓存
     * @see org.simple.net.cache.Cache#put(java.lang.Object, java.lang.Object)
     */
    public void put(String url, Bitmap value) {

        DiskLruCache.Editor editor = null;
        try {
            // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
            editor = mDiskLruCache.edit(Md5Helper.toMD5(url));
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (writeBitmapToDisk(value, outputStream)) {
                    // 写入disk缓存
                    editor.commit();
                } else {
                    editor.abort();
                }
                Util.closeQuietly(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeBitmapToDisk(Bitmap bitmap, OutputStream outputStream) {
        BufferedOutputStream bos = new BufferedOutputStream(outputStream, 8 * 1024);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        boolean result = true;
        try {
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            Util.closeQuietly(bos);
        }

        return result;
    }


    public void remove(String url) {
        try {
            mDiskLruCache.remove(Md5Helper.toMD5(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
