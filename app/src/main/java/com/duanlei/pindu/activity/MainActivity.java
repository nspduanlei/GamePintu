package com.duanlei.pindu.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.duanlei.pindu.R;
import com.duanlei.pindu.adapter.GalleryItemAdapter;
import com.duanlei.pindu.model.GalleryItem;
import com.duanlei.pindu.network.TieTuKuFetcher;
import com.duanlei.pindu.view.RefreshableViewGridView;

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
    private RefreshableViewGridView refreshableView;
    private boolean isRefresh, isLoadMore;
    private LinearLayout loadMoreFooter;
    private int pageIndex = 1;

    private TextView tvLoadMore;
    private ProgressBar pbLoadMore;

    private MyHandler mMyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMyHandler = new MyHandler();

        refreshableView = (RefreshableViewGridView) findViewById(R.id.refreshable_view);
        loadMoreFooter = (LinearLayout) findViewById(R.id.ll_refresh_footer);
        mGridView = (GridView) findViewById(R.id.gv_images);

        tvLoadMore = (TextView) findViewById(R.id.tv_load_more_footer);
        pbLoadMore = (ProgressBar) findViewById(R.id.progress_bar_footer);

        mAdapter = new GalleryItemAdapter(this, mItems, mGridView, new Handler());


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

        refreshableView.setOnRefreshListener(new RefreshableViewGridView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                new FetchItemsTask().execute(1);
            }

            @Override
            public void onLoadMore() {
                isLoadMore = true;

                showLoadMoreUI();

                pageIndex ++;
                new FetchItemsTask().execute(pageIndex);
            }
        }, 1);

        //获取图片库数据
        new FetchItemsTask().execute(1);
    }

    /**
     * 加载更多时，在底部显示，加载更多的ui
     */
    private void showLoadMoreUI() {
        ViewGroup.MarginLayoutParams marginLayoutParams =
                (ViewGroup.MarginLayoutParams) refreshableView.getLayoutParams();
        marginLayoutParams.bottomMargin =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getResources().getDisplayMetrics());

        refreshableView.setLayoutParams(marginLayoutParams);

        loadMoreFooter.setVisibility(View.VISIBLE);
        tvLoadMore.setText(getResources().getString(R.string.load_more));
        pbLoadMore.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏加载更多的ui
     */
    private void hideLoadMoreUI() {
        ViewGroup.MarginLayoutParams marginLayoutParams =
                (ViewGroup.MarginLayoutParams) refreshableView.getLayoutParams();
        marginLayoutParams.bottomMargin = 0;
        refreshableView.setLayoutParams(marginLayoutParams);

        loadMoreFooter.setVisibility(View.GONE);
    }

    /**
     * 没有更多数据时的ui变化
     */
    private void setOnMoreData() {
        loadMoreFooter.setVisibility(View.VISIBLE);

        tvLoadMore.setText(getResources().getString(R.string.on_more_data));
        findViewById(R.id.progress_bar_footer).setVisibility(View.GONE);

        mMyHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoadMoreUI();
            }
        }, 1000);
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

            if (isRefresh) {
                mAdapter.notifyLoad();
                refreshableView.finishRefreshing();
                isRefresh = false;
            }

            if (isLoadMore) {
                if (items.size() > 0) {
                    refreshableView.finishLoadMore();
                    hideLoadMoreUI();
                } else {
                    setOnMoreData();
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

    static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}


