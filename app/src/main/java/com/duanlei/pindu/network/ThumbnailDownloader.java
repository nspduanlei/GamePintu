package com.duanlei.pindu.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.duanlei.pindu.cache.DoubleCache;
import com.duanlei.pindu.utils.ImageUtil;
import com.duanlei.pindu.utils.ScreenUtils;

/**
 * Author: duanlei
 * Date: 2015-11-10
 */
public class ThumbnailDownloader extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

    private static final int MESSAGE_DOWNLOAD = 0;
    //private static final int GET_CACHE = 1;

    private Handler mHandler;
    private Context mContext;
    private Handler mResponseHandler;
    private Listener mListener;

    //双缓冲对象
    private DoubleCache mDoubleCache;

    public interface Listener {
        void onThumbnailDownloaded(String url, Bitmap thumbnail);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler, Context context) {
        super(TAG);
        mResponseHandler = responseHandler;
        mContext = context;
        mDoubleCache = DoubleCache.getDoubleCache(context);
    }

    public void queueThumbnail(String url) {
        //Log.d("test01", "Got an URL: " + url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, url).sendToTarget();
    }

//    public void queueCache(String url) {
//        mHandler.obtainMessage(GET_CACHE, url).sendToTarget();
//    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String url = (String) msg.obj;
                switch (msg.what) {
                    case MESSAGE_DOWNLOAD:  //下载图片
                        //Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                        handleRequest(url);
                        break;

//                    case GET_CACHE:
//                        handlerGetCache(url);
//                        break;
                }
            }
        };
    }

//    private void handlerGetCache(final String url) {
//        final Bitmap bitmap = mDoubleCache.get(url);
//
//        if (bitmap != null) {
//            mResponseHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    mListener.onThumbnailDownloaded(url, bitmap);
//                }
//            });
//        }
//    }

    private void handleRequest(final String url) {
        Bitmap bitmap = mDoubleCache.get(url);

        if (bitmap == null) {
            //从网络下载图片
            bitmap = ImageUtil.getBitmapWithUrl(url,
                    ScreenUtils.getScreenWidth(mContext) / 4,
                    ScreenUtils.getScreenWidth(mContext) / 4);

            //获取到图片后加入缓存
            if (bitmap != null) {
                mDoubleCache.put(url, bitmap);
            }
        }

        //获取到缓存后将图片填充
        if (bitmap != null) {
            final Bitmap finalBitmap = bitmap;
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onThumbnailDownloaded(url, finalBitmap);
                }
            });
        }

    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

}
