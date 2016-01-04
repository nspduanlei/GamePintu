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

import java.util.ArrayList;

/**
 *
 * 选择图片
 *
 * Author: duanlei
 * Date: 2016-01-04
 */
public class MainActivity extends AppCompatActivity {

    private ArrayList<GalleryItem> mItems = new ArrayList<>();
    private GalleryItemAdapter mAdapter;

    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView) findViewById(R.id.gv_images);
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

        //获取图片库数据
        new FetchItemsTask().execute();
    }


    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {
        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            return new TieTuKuFetcher().fetchItems();
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> items) {
            mItems.clear();
            mItems.addAll(items);
            mAdapter.notifyDataSetChanged();
            mAdapter.notifyLoad();
        }
    }

}


