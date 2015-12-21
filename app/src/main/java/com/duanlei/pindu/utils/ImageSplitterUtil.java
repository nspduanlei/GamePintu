package com.duanlei.pindu.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: duanlei
 * Date: 2015-12-21
 * 图片分离工具
 *
 */
public class ImageSplitterUtil {


    /**
     * @param bitmap
     * @param piece 切成 piece * piece 块
     * @return 返回List<ImagePiece>
     */
    public static List<ImagePiece> splitImage(Bitmap bitmap, int piece) {

        List<ImagePiece> imagePieces = new ArrayList<>();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int pieceWidth = Math.min(width, height) / piece;

        for (int i = 0; i < piece; i++) {
            for (int j = 0; j < piece; j++) {
                ImagePiece imagePiece = new ImagePiece();

                imagePiece.setIndex(j + i * piece);

                int x = j * pieceWidth;
                int y = i * pieceWidth;

                imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceWidth));


                imagePieces.add(imagePiece);
            }
        }


        return imagePieces;

    }
}
