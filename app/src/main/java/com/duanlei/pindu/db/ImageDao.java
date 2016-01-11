package com.duanlei.pindu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.duanlei.pindu.model.GalleryItem;

/**
 * Created by duanlei on 16/1/10.
 */
public class ImageDao implements InterDao {

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    public ImageDao(Context context) {
        dbHelper = new PuzzleSQLiteHelper(context, "puzzle.db", null, 1);
        db = dbHelper.getWritableDatabase();
    }


    @Override
    public void add(GalleryItem galleryItem) {

        //判断url是否存在
        Cursor cursor = db.query(PuzzleSQLiteHelper.IMAGES_TABLE, null, "url = ?",
                new String[]{galleryItem.getUrl()},
                null, null, null);

        if(cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("url", galleryItem.getUrl());
            db.insert(PuzzleSQLiteHelper.IMAGES_TABLE, null, values);
        }
//        else {
//            cursor.moveToFirst();
//            int id = cursor.getInt(cursor.getColumnIndex("id"));
//            ContentValues valuesUpdate = new ContentValues();
//            valuesUpdate.put("id", id);
//            valuesUpdate.put("url", galleryItem.getUrl());
//            db.update(PuzzleSQLiteHelper.IMAGES_TABLE, valuesUpdate, "id = ?",
//                    new String[]{String.valueOf(id)});
//        }

        cursor.close();
    }

    public void close() {
        db.close();
    }


    @Override
    public void delete(int id) {
        db.delete(PuzzleSQLiteHelper.IMAGES_TABLE, "id = ?", new String[]{String.valueOf(id)});
    }

    @Override
    public GalleryItem read(int id) {
        GalleryItem galleryItem = new GalleryItem();
        Cursor cursor = db.query(PuzzleSQLiteHelper.IMAGES_TABLE, null, "id = ?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        String url = cursor.getString(cursor.getColumnIndex("url"));
        galleryItem.setUrl(url);

        cursor.close();

        return galleryItem;
    }

    @Override
    public void update(GalleryItem galleryItem) {
        ContentValues values = new ContentValues();
        values.put("url", galleryItem.getUrl());
        db.update(PuzzleSQLiteHelper.IMAGES_TABLE, values, "id = ?",
                new String[]{String.valueOf(galleryItem.getId())});
    }

    public int queryByUrl(String url) {
        Cursor cursor = db.query(PuzzleSQLiteHelper.IMAGES_TABLE, null, "url = ?",
                new String[]{url},
                null, null, null);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        cursor.close();

        return id;
    }

    public String getNextImage(String url) {
        int id = queryByUrl(url);
        GalleryItem galleryItem = read(id + 1);
        if (galleryItem != null) {
            return  galleryItem.getUrl();
        } else {
            return read(1).getUrl();
        }
    }


}
