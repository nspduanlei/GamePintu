package com.duanlei.pindu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by duanlei on 16/1/10.
 */
public class PuzzleSQLiteHelper extends SQLiteOpenHelper {

    public static final String IMAGES_TABLE = "images";

    /**
     * 创建图片表格
     */
    private static final String CREATE_IMAGES = "create table images (" +
            "id integer primary key autoincrement, " +
            "url text)";


    public PuzzleSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
