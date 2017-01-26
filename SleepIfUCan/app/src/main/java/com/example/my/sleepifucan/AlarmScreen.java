package com.example.my.sleepifucan;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class AlarmScreen extends AppCompatActivity {
    public static String ALARM_ID = "alarmId";
    public static String ALARM_DESCRIPTION = "alarmDescription";

    TextView screenTextView, currentTextView;
    CountDownTimer countDownTimer;
    Button alarmOffButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        screenTextView = (TextView) findViewById(R.id.tv_screen);
        currentTextView = (TextView) findViewById(R.id.tv_currentTime);
        alarmOffButton = (Button) findViewById(R.id.bt_alarmOff);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            int id = bundle.getInt(ALARM_ID);
            String des = bundle.getString(ALARM_DESCRIPTION);
            screenTextView.setText("Alarm id: ( " + id + " ) " + des);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

    }

    @Override
    protected void onResume() {
        super.onResume();

        countDownTimer = new CountDownTimer(1000000000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Calendar calendar = Calendar.getInstance();
                currentTextView.setText(calendar.get(Calendar.HOUR) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
            }

            @Override
            public void onFinish() { }
        };
        countDownTimer.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            int id = bundle.getInt(ALARM_ID);
            String des = bundle.getString(ALARM_DESCRIPTION);
            screenTextView.setText("Alarm id: ( " + id + " ) " + des);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        countDownTimer.cancel();
    }

    public void clickedAlarmOff(View view) {
        finish();
    }
}
