package com.duanlei.pindu.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duanlei.pindu.R;
import com.duanlei.pindu.cache.DoubleCache;
import com.duanlei.pindu.db.ImageDao;
import com.duanlei.pindu.utils.ImageUtil;
import com.duanlei.pindu.utils.ScreenUtils;
import com.duanlei.pindu.view.GamePintuLayout;

import java.util.Timer;
import java.util.TimerTask;

public class PuzzleActivity extends AppCompatActivity implements
        GamePintuLayout.GamePintuListener, View.OnClickListener {

    private String url;
    private DoubleCache mDoubleCache;
    private static final String CACHE_SUFFIX = "_big";

    private TextView tvStep, tvTimer;
    private int stepCount;

    private MyHandler mMyHandler;
    private Timer mTimer;

    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        initView();

        //得到参数
        url = getIntent().getStringExtra("url");
        initTimer();
        setImage();
    }

    private void setImage() {
        //得到缓存对象
        mDoubleCache = DoubleCache.getDoubleCache(this);

        //如果图片缓存存在，则填充
        mBitmap = mDoubleCache.get(url + CACHE_SUFFIX);
        if (mBitmap != null) {
            setBitmap();
        } else {
            new GetImageTask().execute();
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //初始化时间显示和步数显示控件
        tvStep = (TextView) findViewById(R.id.tv_step_num);
        tvTimer = (TextView) findViewById(R.id.tv_timer);
        tvStep.setText(String.format(getResources().getString(R.string.step_num), 0));
        tvTimer.setText(String.format(getResources().getString(R.string.timer_str), 0));

        findViewById(R.id.btn_show_image).setOnClickListener(this);
        findViewById(R.id.btn_reset).setOnClickListener(this);
        findViewById(R.id.btn_next_page).setOnClickListener(this);

        loading_view = findViewById(R.id.ll_loading);
        loading_view.setOnClickListener(this);
    }

    private TimerTask timerTask;

    /**
     * 初始化计时器
     */
    private void initTimer() {
        mMyHandler = new MyHandler();
        //为timer提供一个定时执行的任务，在Timer线程中无法直接操作UI线程
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mMyHandler.obtainMessage(MESSAGE_TIMER).sendToTarget();
            }
        };

        mTimer = new Timer(true);
    }



    @Override
    public void nextLevel(int nextLevel) {

    }

    @Override
    public void timeChanged(int currentTime) {

    }

    @Override
    public void gameOver() {
        //退出计时器
        //mTimer.cancel();
        setNextImage();
    }

    @Override
    public void step() {
        stepCount++;
        tvStep.setText(String.format(getResources().getString(R.string.step_num), stepCount));
    }

    //图片加载时显示的控件
    private View loading_view;

    private boolean isNext;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_image: //显示原图

                showImageView();

                break;
            case R.id.btn_reset://重置

                gameReSet();
                break;

            case R.id.btn_next_page: //下一张图片

                setNextImage();

                break;
            case R.id.ll_loading: //隐藏原图显示
                hideImageView();
                break;
        }
    }


    private void setNextImage() {
        ImageDao imageDao = new ImageDao(this);
        url = imageDao.getNextImage(url);
        if (url == null) {
            Toast.makeText(this, "没有下一张了！", Toast.LENGTH_SHORT).show();
            return;
        }
        isNext = true;
        setImage();
    }

    /**
     * 重置游戏
     */
    public void gameReSet() {
        tvStep.setText(String.format(getResources().getString(R.string.step_num), 0));
        tvTimer.setText(String.format(getResources().getString(R.string.timer_str), 0));
        secondCount = 0;
        stepCount = 0;

        puzzleView.gameReSet();
    }

    /**
     * 显示原图
     */
    public void showImageView() {
        //显示动画

        Animator animator =
                AnimatorInflater.loadAnimator(this, R.animator.image_show_anim);
        animator.setTarget(mImageView);
        animator.start();

        mImageView.setVisibility(View.VISIBLE);
        loading_view.setVisibility(View.VISIBLE);
        loading_view.findViewById(R.id.pb_loading).setVisibility(View.GONE);
    }

    /**
     * 隐藏原图
     */
    public boolean hideImageView() {
        if (mImageView != null && mImageView.getVisibility() == View.VISIBLE) {

            //TODO 隐藏动画
            Animator animator =
                    AnimatorInflater.loadAnimator(this, R.animator.image_hide_anim);
            animator.setTarget(mImageView);
            animator.start();

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mImageView.setVisibility(View.GONE);
                    loading_view.setVisibility(View.GONE);
                    loading_view.findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


            return true;
        }

        return false;
    }

    private class GetImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading_view.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return ImageUtil.getBitmapWithUrl(url,
                    ScreenUtils.getScreenWidth(PuzzleActivity.this),
                    ScreenUtils.getScreenWidth(PuzzleActivity.this));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //图片获取成功
            loading_view.setVisibility(View.GONE);
            mDoubleCache.put(url + CACHE_SUFFIX, bitmap);
            mBitmap = bitmap;
            setBitmap();
        }
    }

    private GamePintuLayout puzzleView;

    private void setBitmap() {
        if (!isNext) {
            puzzleView =
                    (GamePintuLayout) ((ViewStub) findViewById(R.id.stub_import)).inflate();
            puzzleView.setGamePintuListener(this);
            relativeLayout = (RelativeLayout) findViewById(R.id.rl_main);
            puzzleView.setBitmap(mBitmap);
            addImageView();

            //1s执行一次
            mTimer.schedule(timerTask, 1000, 1000);
        } else {
            mImageView.setImageBitmap(mBitmap);
            puzzleView.setBitmap(mBitmap);
            gameReSet();
        }
    }

    private static final int MESSAGE_TIMER = 0;
    private int secondCount;

    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TIMER:
                    secondCount++;
                    tvTimer.setText(String.format(getResources().getString(R.string.timer_str), secondCount));
                    break;
            }
        }
    }

    private ImageView mImageView;
    private RelativeLayout relativeLayout;

    /**
     * 添加显示原图View
     */
    private void addImageView() {
        mImageView = new ImageView(this);
        mImageView.setImageBitmap(mBitmap);

        int width = ScreenUtils.getScreenWidth(this);

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(width, width);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        mImageView.setLayoutParams(params);
        relativeLayout.addView(mImageView);
        mImageView.setVisibility(View.GONE);
    }

    /**
     * 返回按钮监听
     */
    @Override
    public void onBackPressed() {
        if (!hideImageView()) {
            super.onBackPressed();
        }
    }
}
