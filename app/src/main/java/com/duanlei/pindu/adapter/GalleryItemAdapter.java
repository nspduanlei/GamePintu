package com.duanlei.pindu.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.duanlei.pindu.R;
import com.duanlei.pindu.model.GalleryItem;
import com.duanlei.pindu.network.ThumbnailDownloader;

import java.util.ArrayList;

/**
 * Author: duanlei
 * Date: 2015-11-12
 */
public class GalleryItemAdapter extends ArrayAdapter<GalleryItem> implements AbsListView.OnScrollListener {

    private Activity mContext;
    private ArrayList<GalleryItem> mItems;

    private ThumbnailDownloader mThumbnailThread;
    private GridView mGridView;

    /**
     * 第一张可见图片的下标
     */
    private int mFirstVisibleItem;

    /**
     * 一屏有多少张图片可见
     */
    private int mVisibleItemCount;

    /**
     * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会加载图片的问题
     */
    private boolean isFirstEnter = true;


    public GalleryItemAdapter(Activity context, ArrayList<GalleryItem> items,
                              GridView gridView, Handler handler) {
        super(context, 0, items);
        mContext = context;
        mItems = items;
        mGridView = gridView;

        //新建HandlerThread 加载图片
        mThumbnailThread = new ThumbnailDownloader(handler, mContext);
        mThumbnailThread.setListener(new ThumbnailDownloader.Listener() {
            @Override
            public void onThumbnailDownloaded(String url, Bitmap thumbnail) {
                //addBitmapToMemoryCache(url, thumbnail);
                ImageView imageView = (ImageView) mGridView.findViewWithTag(url);
                if (imageView != null &&  thumbnail != null) {
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });
        mThumbnailThread.start();
        mThumbnailThread.getLooper();

        mGridView.setOnScrollListener(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mContext.getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);

        GalleryItem item = getItem(position);
        String url = item.getUrl();
        imageView.setTag(url);
        //setImageView(imageView, url);
        return convertView;
    }

//    private void setImageView(ImageView imageView, String url) {
//        mThumbnailThread.queueCache(url);
//        imageView.setImageResource(R.mipmap.default_img);
//    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //仅当GirdView静止时才去下载图片， GridView滚动时取消所有正在下载的任务
        if (scrollState == SCROLL_STATE_IDLE) {
            Log.d("test01", "mFirstVisibleItem : " + mFirstVisibleItem + ", mVisibleItemCount : " + mVisibleItemCount);
            loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
        } else {
            mThumbnailThread.clearQueue();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        mVisibleItemCount = visibleItemCount;
        mFirstVisibleItem = firstVisibleItem;

        /**
         * 下载任务应该在onScrollStateChanged里调用，但首次进入程序
         * onScrollStateChanged并不会调用
         * 因此在这里为首次进入程序开启下载线程
         */
        if (isFirstEnter && visibleItemCount > 0) {
            loadBitmaps(firstVisibleItem, visibleItemCount);
            isFirstEnter = false;
        }
    }

    public void notifyLoad() {
        loadBitmaps(0, mVisibleItemCount);
    }

    /**
     * 加载Bitmap对象，此方法后在LruCache中检查所有屏幕中可见的ImageView的对象
     * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片
     *
     * @param firstVisibleItem
     * @param visibleItemCount 在显示GalleryItem时为前十个和后十个GalleryItem预加载Bitmap
     */
    private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            if (mItems.size() == 0)
                return;

            String imageUrl = mItems.get(i).getUrl();

            mThumbnailThread.queueThumbnail(imageUrl);
        }
    }

    public void quit() {
        mThumbnailThread.quit();
    }

    public void clearQueue() {
        mThumbnailThread.clearQueue();
    }
}
