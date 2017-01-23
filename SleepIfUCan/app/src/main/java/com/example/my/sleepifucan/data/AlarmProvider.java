package com.example.my.sleepifucan.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.my.sleepifucan.data.AlarmContract.AlarmEntry;

/**
 * Created by MY on 2017-01-18.
 */

public class AlarmProvider extends ContentProvider {
    AlarmDbHelper mAlarmDbHelper;

    public static final int CODE_ALARM = 100;
    public static final int CODE_ALARM_WITH_ID = 101;

    public static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AlarmContract.CONTENT_AUTHORITY, AlarmContract.PATH_ALARM, CODE_ALARM);
        uriMatcher.addURI(AlarmContract.CONTENT_AUTHORITY, AlarmContract.PATH_ALARM + "/#", CODE_ALARM_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mAlarmDbHelper = new AlarmDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase sqLiteDatabase = mAlarmDbHelper.getReadableDatabase();

        Cursor retCursor;

        int code = sUriMatcher.match(uri);
        switch(code) {
            case CODE_ALARM:
                retCursor = sqLiteDatabase.query(AlarmEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case CODE_ALARM_WITH_ID:
                String item = uri.getLastPathSegment();
                String mSelection = AlarmEntry._ID + "=?";
                String[] mSelectionArgs = new String[]{item};

                retCursor = sqLiteDatabase.query(AlarmEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);

                Log.d("Provider log", "queried item : " + item);

                break;

            default:
                throw new UnsupportedOperationException("No match Uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase sqLiteDatabase = mAlarmDbHelper.getWritableDatabase();

        Uri retUri;

        int code = sUriMatcher.match(uri);
        switch(code) {
            case CODE_ALARM:
                long id = sqLiteDatabase.insert(AlarmEntry.TABLE_NAME, null, values);
                if(id > 0) {
                    retUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI, id);
                    Log.d("Provider log", "inserted item : " + id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                Log.d("SQLite Debug ", retUri.toString());
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = mAlarmDbHelper.getWritableDatabase();

        int deletedAlarm;

        int code = sUriMatcher.match(uri);
        switch(code) {
            case CODE_ALARM_WITH_ID:
//                String id = uri.getPathSegments().get(1);
                String id = uri.getLastPathSegment();
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                deletedAlarm = sqLiteDatabase.delete(AlarmEntry.TABLE_NAME, mSelection, mSelectionArgs);
                Log.d("Provider log", "deleted item : " + id);
                break;
//            for db test with CODE_ALARM.
            case CODE_ALARM:
                deletedAlarm = sqLiteDatabase.delete(AlarmEntry.TABLE_NAME, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(deletedAlarm != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return deletedAlarm;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mAlarmDbHelper.getWritableDatabase();

        int updatedAlarm;

        int code = sUriMatcher.match(uri);
        switch(code) {
            case CODE_ALARM:
                if(insert(uri, values) != null) {
                    updatedAlarm = 1;
                } else {
                    updatedAlarm = 0;
                }
                break;
            case CODE_ALARM_WITH_ID:
                String id = uri.getLastPathSegment();
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                updatedAlarm = sqLiteDatabase.update(AlarmEntry.TABLE_NAME, values, mSelection, mSelectionArgs);
                Log.d("Provider log", "updated item : " + id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(updatedAlarm != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return updatedAlarm;
    }

    @Override
    public String getType(Uri uri) {
        throw new RuntimeException("We are not implementing getType in Sunshine.");
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mAlarmDbHelper.close();
        super.shutdown();
    }
}