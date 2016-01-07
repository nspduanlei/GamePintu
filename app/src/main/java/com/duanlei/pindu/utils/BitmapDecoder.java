package com.duanlei.pindu.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Author: duanlei
 * Date: 2015-12-04
 *
 * 封装先加载图片bound，计算出inSmallSize
 * 之后再加载图片的逻辑
 *
 */
public abstract class BitmapDecoder {

    /**
     * @param options
     * @return
     */
    public abstract Bitmap decodeBitmapWithOption(BitmapFactory.Options options);

    /**
     * @param width 图片的目标宽度
     * @param height 图片的目标高度
     * @return
     */
    public Bitmap decodeBitmap(int width, int height) {
        // 如果请求原图,则直接加载原图
        if (width <= 0 || height <= 0) {
            return decodeBitmapWithOption(null);
        }

        // 1、获取只加载Bitmap宽高等数据的Option, 即设置options.inJustDecodeBounds = true;
        BitmapFactory.Options options = getJustDecodeBoundsOptions();
        // 2、通过options加载bitmap，此时返回的bitmap为空,数据将存储在options中
        decodeBitmapWithOption(options);
        // 3、计算缩放比例, 并且将options.inJustDecodeBounds设置为false;
        calculateInSmall(options, width, height);
        // 4、通过options设置的缩放比例加载图片
        return decodeBitmapWithOption(options);
    }

    /**
     * 加载原图
     *
     * @return
     */
    public Bitmap decodeOriginBitmap() {
        return decodeBitmapWithOption(null);
    }

    /**
     * @return
     */
    private BitmapFactory.Options getJustDecodeBoundsOptions() {
        //
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        return options;
    }

    /**
     * @param options
     * @param width
     * @param height
     */
    protected void calculateInSmall(BitmapFactory.Options options, int width, int height) {
        // 设置缩放比例
        options.inSampleSize = computeInSmallSize(options, width, height);

        Log.d("", "$## inSampleSize = " + options.inSampleSize
                + ", width = " + width + ", height= " + height);
        // 图片质量
        //options.inPreferredConfig = Bitmap.Config.RGB_565;

        // 设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;

        //options.inPurgeable = true;
        //options.inInputShareable = true;
    }

    private int computeInSmallSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//            // Calculate ratios of height and width to requested height and
//            // width
//            final int heightRatio = Math.round((float) height / (float) reqHeight);
//            final int widthRatio = Math.round((float) width / (float) reqWidth);
//
//            // Choose the smallest ratio as inSampleSize value,
//            // this will guarantee a final image
//            // with both dimensions larger than or equal to the requested
//            // height and width.
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//
//            // This offers some additional logic in case the image has a strange
//            // aspect ratio. For example, a panorama may have a much larger
//            // width than height. In these cases the total pixels might still
//            // end up being too large to fit comfortably in memory, so we should
//            // be more aggressive with sample down the image (=larger
//            // inSampleSize).
//
//            final float totalPixels = width * height;
//
//            // Anything more than 2x the requested pixels we'll sample down
//            // further
//            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
//
//            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
//                inSampleSize++;
//            }
//        }
//        return inSampleSize;

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

    }
}
