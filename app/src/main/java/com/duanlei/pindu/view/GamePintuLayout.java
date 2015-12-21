package com.duanlei.pindu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
     * 交换item
     */
    private void exchangeView() {
        mFirst.setColorFilter(null);

        String firstTag = (String) mFirst.getTag();

        String secondTag = (String) mSecond.getTag();

        String[] firstParams = firstTag.split("_");
        String[] secondParams = secondTag.split("_");

        Bitmap firstBitmap = mItemBitmaps.get(Integer.valueOf(firstParams[0]))
                .getBitmap();

        Bitmap secondBitmap = mItemBitmaps.get(Integer.valueOf(secondParams[0]))
                .getBitmap();

        mSecond.setImageBitmap(firstBitmap);
        mFirst.setImageBitmap(secondBitmap);

        mFirst.setTag(secondTag);
        mSecond.setTag(firstTag);

        mFirst = mSecond = null;
    }
}

