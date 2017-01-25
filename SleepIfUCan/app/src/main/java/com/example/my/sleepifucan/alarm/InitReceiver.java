package com.example.my.sleepifucan.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.example.my.sleepifucan.data.AlarmContract;

import java.util.Calendar;

/**
 * Created by MY on 2017-01-23.
 */

public class InitReceiver extends BroadcastReceiver {
    public static boolean isInit = false;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        isInit = true;
        mContext = context;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Uri uri = AlarmContract.AlarmEntry.CONTENT_URI;
                String mSelection = AlarmContract.AlarmEntry.COLUMN_SWITCH + "=?";
                String[] mSelectionArgs = new String[] {"1"};

                Cursor mCursor = mContext.getContentResolver().query(uri, null, mSelection, mSelectionArgs, null);
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

                if(mCursor == null)
                    return null;

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                while(mCursor.moveToNext()) {
                    Intent intent = new Intent(mContext, AlarmIntentService.class);
                    intent.setAction(AlarmIntentService.RESERVE_ACTION);
                    int id = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry._ID));
                    int hourOfDay = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_CLOCK));
                    int minute = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_MINUTE));
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    if(calendar.getTimeInMillis() < System.currentTimeMillis())
                        calendar.add(Calendar.DATE, 1);

                    intent.putExtra(AlarmIntentService.ALARM_ID, id);
                    intent.putExtra(AlarmIntentService.ALARM_MILLIS, calendar.getTimeInMillis());
                    intent.setAction(AlarmIntentService.RESERVE_ACTION);
                    mContext.startService(intent);
                }

                return null;
            }
        }.execute();
    }
}
