package com.duanlei.pindu.utils;

import android.graphics.Bitmap;

/**
 * Author: duanlei
 * Date: 2015-12-21
 */
public class ImagePiece {
    private int index;
    private Bitmap mBitmap;

    public ImagePiece() {

    }

    public ImagePiece(int index, Bitmap bitmap) {
        this.index = index;
        mBitmap = bitmap;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    @Override
    public String toString() {
        return "ImagePiece{" +
                "index=" + index +
                ", mBitmap=" + mBitmap +
                '}';
    }
}
