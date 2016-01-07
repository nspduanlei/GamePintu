package com.duanlei.pindu.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.duanlei.pindu.R;
import com.duanlei.pindu.adapter.GalleryItemAdapter;
import com.duanlei.pindu.model.GalleryItem;
import com.duanlei.pindu.network.TieTuKuFetcher;
import com.duanlei.pindu.view.RefreshableViewScroll;

import java.util.ArrayList;

/**
 * 选择图片
 * <p/>
 * Author: duanlei
 * Date: 2016-01-04
 */
public class MainActivity extends AppCompatActivity {

    private ArrayList<GalleryItem> mItems = new ArrayList<>();
    private GalleryItemAdapter mAdapter;

    private GridView mGridView;
    private RefreshableViewScroll refreshableView;

    private boolean isRefresh, isLoadMore;

    private int pageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshableView = (RefreshableViewScroll) findViewById(R.id.refreshable_view);


        mGridView = (GridView) findViewById(R.id.gv_images);
        mAdapter = new GalleryItemAdapter(this, mItems, mGridView, new Handler(), refreshableView);

        mGridView.setAdapter(mAdapter);
        //item图片点击
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                intent.putExtra("url", mItems.get(position).getUrl());
                startActivity(intent);
            }
        });

        refreshableView.setOnRefreshListener(new RefreshableViewScroll.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                new FetchItemsTask().execute(1);
            }

            @Override
            public void onLoadMore() {
                isLoadMore = true;
                pageIndex ++;
                new FetchItemsTask().execute(pageIndex);
            }
        }, 1);

        //获取图片库数据
        new FetchItemsTask().execute(1);
    }


    private class FetchItemsTask extends AsyncTask<Integer, Void, ArrayList<GalleryItem>> {
        @Override
        protected ArrayList<GalleryItem> doInBackground(Integer... params) {
            return new TieTuKuFetcher().fetchItems(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> items) {
            if (!isLoadMore) {
                mItems.clear();
            }

            mItems.addAll(items);
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyLoad();

            if (isRefresh) {
                refreshableView.finishRefreshing();
                isRefresh = false;
            }

            if (isLoadMore) {
                if (items.size() > 0) {
                    refreshableView.finishLoadMore();
                } else {
                    refreshableView.onMoreData();
                }

                isLoadMore = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.quit();
        mAdapter.clearQueue();
    }
}


