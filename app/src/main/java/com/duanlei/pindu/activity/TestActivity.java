package com.duanlei.pindu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.duanlei.pindu.R;
import com.duanlei.pindu.view.RefreshableView;

/**
 * Author: duanlei
 * Date: 2016-01-05
 */
public class TestActivity extends Activity {

    RefreshableView refreshableView;
    ListView listView;
    ArrayAdapter<String> adapter;
    String[] items = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P"
            , "Q"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_test);

        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        listView = (ListView) findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        refreshableView.setOnRefreshListener(
                new RefreshableView.PullToRefreshListener() {
                    @Override
                    public void onRefresh() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        refreshableView.finishRefreshing();
                    }

                }, 0);
    }

}
