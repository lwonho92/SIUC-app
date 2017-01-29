package com.lwonho92.my.sleepifucan.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.lwonho92.my.sleepifucan.MainActivity;
import com.lwonho92.my.sleepifucan.utilities.TimeUtils;
import com.lwonho92.my.sleepifucan.data.AlarmContract.AlarmEntry;

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
                Uri uri = AlarmEntry.CONTENT_URI;
                String mSelection = AlarmEntry.COLUMN_SWITCH + "=?";
                String[] mSelectionArgs = new String[] {"1"};

                Cursor mCursor = mContext.getContentResolver().query(uri, null, mSelection, mSelectionArgs, null);

                if(mCursor == null)
                    return null;

                Calendar calendar = TimeUtils.getSetCalendar(0, 0);

                while(mCursor.moveToNext()) {
                    Intent intent = new Intent(mContext, AlarmIntentService.class);
                    intent.setAction(AlarmIntentService.RESERVE_ACTION);
                    int id = mCursor.getInt(MainActivity.INDEX_ID);
                    int hourOfDay = mCursor.getInt(MainActivity.INDEX_CLOCK);
                    int minute = mCursor.getInt(MainActivity.INDEX_MINUTE);
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    if(calendar.getTimeInMillis() < System.currentTimeMillis())
                        calendar.add(Calendar.DATE, 1);

                    intent.putExtra(AlarmIntentService.ALARM_ID, id);
                    intent.putExtra(AlarmIntentService.ALARM_MILLIS, calendar.getTimeInMillis());
                    intent.setAction(AlarmIntentService.RESERVE_ACTION);
                    mContext.startService(intent);
                }

                mCursor.close();
                return null;
            }
        }.execute();
    }
}
