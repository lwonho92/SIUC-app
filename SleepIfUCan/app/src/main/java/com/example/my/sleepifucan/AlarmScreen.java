package com.example.my.sleepifucan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AlarmScreen extends AppCompatActivity {
    public static String ALARM_ID = "alarmId";
    public static String ALARM_DESCRIPTION = "alarmDescription";

    TextView screenTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        int id = getIntent().getExtras().getInt(ALARM_ID);
        String des = getIntent().getExtras().getString(ALARM_DESCRIPTION);

        screenTextView = (TextView) findViewById(R.id.screenTextView);
        screenTextView.setText("Alarm id: ( " + id + " ) " + des);
    }
}
