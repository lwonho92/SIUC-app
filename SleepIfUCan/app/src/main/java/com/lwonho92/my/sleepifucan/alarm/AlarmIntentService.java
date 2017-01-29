package com.lwonho92.my.sleepifucan.alarm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.lwonho92.my.sleepifucan.AlarmScreen;
import com.lwonho92.my.sleepifucan.utilities.TimeUtils;
import com.lwonho92.my.sleepifucan.data.AlarmContract.AlarmEntry;

import java.util.Calendar;

/**
 * Created by MY on 2017-01-25.
 */

public class AlarmIntentService extends IntentService {

    public static final String ALARM_ACTION = "alarm_action";
    public static final String RESERVE_ACTION = "reserve_action";
    public static final String CANCEL_ACTION = "cancel_action";
    public static final String ALARM_ID = "alarm_id";
    public static final String ALARM_MILLIS = "alarm_millis";

    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    private static final String TAG = "Service Log";

    private static PowerManager.WakeLock mWakeLock;

    @Override
    protected void onHandleIntent(Intent intent) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int id = intent.getIntExtra(ALARM_ID, 0);
        long millis = intent.getLongExtra(ALARM_MILLIS, 0L);
        String action = intent.getAction();

        Calendar calendar = Calendar.getInstance();

        switch(action) {
            case ALARM_ACTION:
                Uri uri = AlarmEntry.buildAlarmUriWithId(id);
                Cursor mCursor = getContentResolver().query(uri, null, null, null, null);

                if(mCursor == null || !mCursor.moveToNext()) {
                    Log.e("Service", "return ;");
                    return;
                }

                int today = calendar.get(Calendar.DAY_OF_WEEK);
                int days = mCursor.getInt(mCursor.getColumnIndex(AlarmEntry.COLUMN_DAY));

                if(TimeUtils.isSettedDay(days, today)) {
                    Intent screenIntent = new Intent(this, AlarmScreen.class);
                    Bundle bundle = new Bundle();
                    String des = mCursor.getString(mCursor.getColumnIndex(AlarmEntry.COLUMN_DESCRIPTION));
                    bundle.putSerializable(AlarmScreen.ALARM_DESCRIPTION, des);
                    int volume = mCursor.getInt(mCursor.getColumnIndex(AlarmEntry.COLUMN_VOLUME));
                    bundle.putSerializable(AlarmScreen.ALARM_VOLUME, volume);
                    int type = mCursor.getInt(mCursor.getColumnIndex(AlarmEntry.COLUMN_TYPE));
                    bundle.putSerializable(AlarmScreen.ALARM_TYPE, type);

                    String path = mCursor.getString(mCursor.getColumnIndex(AlarmEntry.COLUMN_URI));

                    screenIntent.putExtras(bundle);
                    screenIntent.setData(Uri.parse(path));
                    screenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    if (mWakeLock != null) {
                        return;
                    }
                    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.ON_AFTER_RELEASE, "PowerManager");
                    mWakeLock.acquire();

                    if (mWakeLock != null) {
                        mWakeLock.release();
                        mWakeLock = null;
                    }

                    startActivity(screenIntent);

                    if(mCursor.getInt(mCursor.getColumnIndex(AlarmEntry.COLUMN_REPEAT)) == 0) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(AlarmEntry.COLUMN_DAY, TimeUtils.transOffDay(days, today));
                        getContentResolver().update(uri, contentValues, null, null);
                    }
                }
                mCursor.close();
                Log.e(TAG, "Action : " + id );
            case RESERVE_ACTION:
                calendar.setTimeInMillis(millis);
                if(calendar.getTimeInMillis() < System.currentTimeMillis())
                    calendar.add(Calendar.DATE, 1);

                Intent newIntent = new Intent(this, AlarmIntentService.class);
                newIntent.putExtra(AlarmIntentService.ALARM_ID, id);
                newIntent.putExtra(AlarmIntentService.ALARM_MILLIS, calendar.getTimeInMillis());
                newIntent.setAction(ALARM_ACTION);

                PendingIntent pendingIntent = PendingIntent.getService (this, id, newIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
                Log.e(TAG, "Reserve : " + id );
                break;
            case CANCEL_ACTION:
                Intent cancelIntent = new Intent(this, AlarmIntentService.class);
                cancelIntent.setAction(ALARM_ACTION);
                PendingIntent cancelPendingIntent = PendingIntent.getService(this, id, cancelIntent, PendingIntent.FLAG_NO_CREATE);

                if(cancelPendingIntent != null) {
                    alarmManager.cancel(cancelPendingIntent);
                    cancelPendingIntent.cancel();
                    Log.e(TAG, "Cancel : " + id );
                }
                break;
        }
    }
}
