package com.duanlei.pindu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.duanlei.pindu.R;
import com.duanlei.pindu.utils.ImagePiece;
import com.duanlei.pindu.utils.ImageSplitterUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author: duanlei
 * Date: 2015-12-21
 */
public class GamePintuLayout extends RelativeLayout implements View.OnClickListener {

    private int mColum = 3;

    /**
     * 容器的内边距
     */
    private int mPadding;

    /**
     * 每张小图之间的距离（横，纵） dp
     */
    private int mMargin = 3;


    private ImageView[] mGamePintuItmes;

    private int mItemWidth;

    /**
     * 游戏的图片
     */
    private Bitmap mBitmap;

    private List<ImagePiece> mItemBitmaps;

    private boolean once;

    /**
     * 游戏面板宽度
     */
    private int mWidth;

    private GamePintuListener mGamePintuListener;

    /**
     * 重置游戏
     */
    public void gameReSet() {
        removeAllViews();
        initBitmap();
        initItem();

        setUpAnimLayout();

        mFirst = null;
        mSecond = null;
    }

    public interface GamePintuListener {
        void nextLevel(int nextLevel);
        void timeChanged(int currentTime);
        void gameOver();
        void step();
    }

    public void setGamePintuListener(GamePintuListener gamePintuListener) {
        mGamePintuListener = gamePintuListener;
    }

    public void setColum(int colum) {
        mColum = colum;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public GamePintuLayout(Context context) {
        this(context, null);
    }

    public GamePintuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GamePintuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //取宽高中的最小值作为容器的宽度
        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());

        if (!once) {
            initBitmap();
            initItem();
            once = true;
        }

        setMeasuredDimension(mWidth, mWidth);
    }

    private void init() {
        //将dp转化为px
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                3, getResources().getDisplayMetrics());

        mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(),
                getPaddingBottom());
    }

    /**
     * 获取多个参数的最小值
     */
    private int min(int... params) {

        int min = params[0];
        for (int param : params) {
            if (param < min) {
                min = param;
            }
        }
        return min;
    }

    /**
     * 设置ImageView（item）的宽高等属性
     */
    private void initItem() {
        mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColum - 1)) / mColum;
        mGamePintuItmes = new ImageView[mColum * mColum];

        //生成item，设置Rule
        for (int i = 0; i < mGamePintuItmes.length; i ++) {
            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);
            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());

            mGamePintuItmes[i] = item;
            item.setId(i + 1);
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth,
                    mItemWidth);

            //设置item间的横向间隙

            //不是第一列
            if (i % mColum != 0) {
                lp.leftMargin = mMargin;
                lp.addRule(RelativeLayout.RIGHT_OF, mGamePintuItmes[i-1].getId());
            }

            //设置纵向的间隙

            //不是第一行
            if ((i + 1) > mColum) {
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW, mGamePintuItmes[i-mColum].getId());
            }

            addView(item, lp);
        }

    }

    /**
     * 进行切图和排序
     */
    private void initBitmap() {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon1);
        }

        mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColum);

        //使用sort完成乱序
        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece a, ImagePiece b) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }


    private ImageView mFirst;
    private ImageView mSecond;

    @Override
    public void onClick(View v) {
        if (isAniming)
            return;

        //两次点击同一个item
        if (mFirst == v) {
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }

        if (mFirst == null) {
            mFirst = (ImageView)v;
            mFirst.setColorFilter(Color.parseColor("#55FF0000"));
        } else {
            mSecond = (ImageView) v;

            exchangeView();
        }
    }


    /**
     * 动画层
     */
    private RelativeLayout mAnimLayout;

    private boolean isAniming;


    /**
     * 交换item
     */
    private void exchangeView() {
        mFirst.setColorFilter(null);

        setUpAnimLayout();

        final String firstTag = (String) mFirst.getTag();

        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = mItemBitmaps.get(getImageIdByTag(firstTag)).getBitmap();

        first.setImageBitmap(firstBitmap);
        LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);


        final String secondTag = (String) mSecond.getTag();

        ImageView second = new ImageView(getContext());

        final Bitmap secondBitmap = mItemBitmaps.get(getImageIdByTag(secondTag)).getBitmap();
        second.setImageBitmap(secondBitmap);
        LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        //设置动画

        TranslateAnimation animation = new TranslateAnimation(0,
                mSecond.getLeft() - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());

        animation.setDuration(300);
        animation.setFillAfter(true);
        first.startAnimation(animation);

        TranslateAnimation animationSecond = new TranslateAnimation(0,
                mFirst.getLeft() - mSecond.getLeft(), 0, mFirst.getTop() - mSecond.getTop());

        animationSecond.setDuration(300);
        animationSecond.setFillAfter(true);
        second.startAnimation(animationSecond);

        //动画监听
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFirst.setVisibility(View.INVISIBLE);
                mSecond.setVisibility(View.INVISIBLE);
                isAniming = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSecond.setImageBitmap(firstBitmap);
                mFirst.setImageBitmap(secondBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(VISIBLE);
                mSecond.setVisibility(VISIBLE);

                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();

                //判断游戏是否成功
                checkSuccess();

                mGamePintuListener.step();

                isAniming = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 判断用户游戏是否成功
     */
    private void checkSuccess() {
        boolean isSuccess = true;

        for (int i = 0; i < mGamePintuItmes.length; i ++) {
            ImageView imageView = mGamePintuItmes[i];

            if (getImageIndexByTag((String) imageView.getTag()) != i) {
                isSuccess = false;
            }
        }

        if (isSuccess) {
            Toast.makeText(getContext(), "恭喜拼图成功！", Toast.LENGTH_LONG).show();
            mGamePintuListener.gameOver();
        }
    }


    /**
     * 根据tag获取item的id
     * @param tag
     * @return
     */
    public int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.valueOf(split[0]);
    }


    public int getImageIndexByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.valueOf(split[1]);
    }


    /**
     * 构造动画层
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }
    }
}

