package com.example.my.sleepifucan.alarm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.my.sleepifucan.AlarmScreen;
import com.example.my.sleepifucan.data.AlarmContract;
import com.example.my.sleepifucan.utilities.DateUtils;

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

    @Override
    protected void onHandleIntent(Intent intent) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        String action = intent.getAction();
        int id = intent.getIntExtra(ALARM_ID, 0);
        long millis = intent.getLongExtra(ALARM_MILLIS, 0);

        Uri uri = AlarmContract.AlarmEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        Cursor mCursor = getContentResolver().query(uri, null, null, null, null);

        if(mCursor == null || !mCursor.moveToNext())
            return;

        Calendar calendar = Calendar.getInstance();

        int today = calendar.get(Calendar.DAY_OF_WEEK);
        int days = mCursor.getInt(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_DAY));

        switch(action) {
            case ALARM_ACTION:
                if(DateUtils.isSettedDay(days, today)) {
                    Intent screenIntent = new Intent(this, AlarmScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(AlarmScreen.ALARM_ID, id);
//                    TODO description test
                    String des = mCursor.getString(mCursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_DESCRIPTION));
                    bundle.putSerializable(AlarmScreen.ALARM_DESCRIPTION, des);

                    screenIntent.putExtras(bundle);
                    startActivity(screenIntent);
//                    매주 반복 체크 안되어 있을시 이 곳에서 업데이트.
//                    TODO 매주 반복 체크 관련 작업.
                }
                Log.d("IntentService", "Action alarm id: " + id );
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
                Log.d("IntentService", "Reserve alarm id: " + id );
                break;
            case CANCEL_ACTION:
                Intent cancelIntent = new Intent(this, AlarmIntentService.class);
                cancelIntent.putExtra(ALARM_ID, id);
                cancelIntent.putExtra(ALARM_MILLIS, millis);
                PendingIntent cancelPendingIntent = PendingIntent.getService(this, id, cancelIntent, PendingIntent.FLAG_NO_CREATE);

                if(cancelPendingIntent != null) {
                    alarmManager.cancel(cancelPendingIntent);
                    cancelPendingIntent.cancel();
                }

                break;
        }

        mCursor.close();
    }
}
