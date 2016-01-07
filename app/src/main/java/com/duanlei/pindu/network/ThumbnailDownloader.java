package com.duanlei.pindu.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.duanlei.pindu.utils.ImageUtil;
import com.duanlei.pindu.utils.ScreenUtils;

/**
 * Author: duanlei
 * Date: 2015-11-10
 */
public class ThumbnailDownloader extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    Handler mHandler;

    Context mContext;

//    Map<Token, String> requestMap =
//            Collections.synchronizedMap(new HashMap<Token, String>());

    Handler mResponseHandler;
    Listener mListener;

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
    }

    public void queueThumbnail(String url) {
        Log.d("test01", "Got an URL: " + url);
        //requestMap.put(token, url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, url).sendToTarget();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    String url = (String) msg.obj;
                    //Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(url);
                }
            }
        };
    }

    private void handleRequest(final String url) {

        final Bitmap bitmap = ImageUtil.getBitmapWithUrl(url,
                ScreenUtils.getScreenWidth(mContext) / 4, ScreenUtils.getScreenWidth(mContext) / 4);
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    mListener.onThumbnailDownloaded(url, bitmap);
                }
            }
        });

    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

}
