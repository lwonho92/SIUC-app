package com.example.my.sleepifucan.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.my.sleepifucan.data.AlarmContract.AlarmEntry;

/**
 * Created by MY on 2017-01-18.
 */

public class AlarmDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "alarm.db";
    public static final int DATABASE_VERSION = 1;

    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" +
                AlarmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                AlarmEntry.COLUMN_CLOCK + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_MINUTE + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_DAY + " INTEGER NOT NULL, " +

                AlarmEntry.COLUMN_REPEAT + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_TYPE + " INTEGER NOT NULL, " +
                AlarmEntry.COLUMN_PATH + " TEXT NOT NULL, " +
                AlarmEntry.COLUMN_VOLUME + " INTEGER NOT NULL, " +

                AlarmEntry.COLUMN_DESCRIPTION + " TEXT, " +
                AlarmEntry.COLUMN_SWITCH + " INTEGER NOT NULL);";
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

//    DB Scheme 변경 시 MainActivity.class, DetailActivity.class의 COLUMN_INDEX 변경사항 확인 하여야함.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + AlarmEntry.TABLE_NAME);
        onCreate(db);
    }
}
