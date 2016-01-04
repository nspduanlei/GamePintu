package com.duanlei.pindu.activity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewStub;

import com.duanlei.pindu.R;
import com.duanlei.pindu.utils.ImageUtil;
import com.duanlei.pindu.utils.ScreenUtils;
import com.duanlei.pindu.view.GamePintuLayout;

public class PuzzleActivity extends AppCompatActivity implements GamePintuLayout.GamePintuListener {

    private GamePintuLayout mPuzzleView;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        //获取图片
//        mPuzzleView = (GamePintuLayout) findViewById(R.id.gpl_view);
//        mPuzzleView.setGamePintuListener(this);

        url = getIntent().getStringExtra("url");
        new GetImageTask().execute();
    }

    @Override
    public void nextLevel(int nextLevel) {

    }

    @Override
    public void timeChanged(int currentTime) {

    }

    @Override
    public void gameOver() {

    }


    private class GetImageTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            return ImageUtil.getBitmapWithUrl(url, PuzzleActivity.this,
                    ScreenUtils.getScreenWidth(PuzzleActivity.this),
                    ScreenUtils.getScreenWidth(PuzzleActivity.this));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //图片获取成功
            mPuzzleView = (GamePintuLayout) ((ViewStub)findViewById(R.id.stub_import)).inflate();
            mPuzzleView.setBitmap(bitmap);


        }
    }
}
