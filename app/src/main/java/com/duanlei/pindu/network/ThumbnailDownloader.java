package com.duanlei.pindu.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;

import java.io.IOException;

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
        //Log.i(TAG, "Got an URL: " + url);
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
        try {

//            Log.i(TAG, url + "--start");

            byte[] bitmapBytes = new TieTuKuFetcher().getUrlBytes(url);

            /**
             * inJustDecodeBounds 属性设置为true就可以让解析方法禁止为bitmap分配内存，返回值也
             * 不再是一个Bitmap对象，而是null
             * 虽然Bitmap是null， 但是BitmapFactory.Options的outWidth,outHeight和outMimeType
             * 属性都会被赋值。
             * 这个技巧可以在加载图片之前就获取到图片的长宽和MIME类型，从而根据情况对图片进行压缩
             */
            BitmapFactory.Options options = new BitmapFactory.Options();

            //第一次解析inJustDecodeBounds设置为true， 来获取图片的大小
            options.inJustDecodeBounds = true;

            BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);

            //调用定义的方法计算inSampleSize的

            int reqWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, mContext.getResources().getDisplayMetrics());
            int reqHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, mContext.getResources().getDisplayMetrics());

            options.inSampleSize = calculateSampleSize(
                    options, reqWidth, reqHeight);

            //使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;

            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (bitmap != null) {
//                        Log.i(TAG, url + "--end");
                        mListener.onThumbnailDownloaded(url, bitmap);
                    }
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    /**
     * 计算合适的inSampleSize的值
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public int calculateSampleSize(BitmapFactory.Options options,
                                          int reqWidth, int reqHeight) {
        //源图片的高度和宽度

        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            //计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float)height/ (float)reqHeight);
            final int widthRatio = Math.round((float)width / (float)reqWidth);

            /**
             * 选择宽和高最小的比率作为inSampleSize的值，这样可以保证最终图片的宽高
             * 一定都大于等于目标的宽和高
             *
             */
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
