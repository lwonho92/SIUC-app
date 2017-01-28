package com.example.my.sleepifucan;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class AlarmScreen extends AppCompatActivity {
    public static String ALARM_DESCRIPTION = "alarmDescription";
    public static String ALARM_VOLUME = "alarmVolume";
    public static String ALARM_TYPE = "alarmType";

    TextView currentTextView, mDesTextView;
    CountDownTimer countDownTimer;
    Button alarmOffButton;

    int id, mediaPlayerVolume, currentRingVolume, currentMusicVolume, type;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    Vibrator vibrator;
    Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alarm_screen);

        currentTextView = (TextView) findViewById(R.id.tv_currentTime);
        mDesTextView = (TextView) findViewById(R.id.tv_description_screen);
        alarmOffButton = (Button) findViewById(R.id.bt_alarmOff);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            String des = bundle.getString(ALARM_DESCRIPTION);
            mediaPlayerVolume = bundle.getInt(ALARM_VOLUME);
            type = bundle.getInt(ALARM_TYPE);
            if(des.length() >= 10)
                mDesTextView.setText(des.substring(0, 10) + " ...");
            else
                mDesTextView.setText(des);
        }

        if(type == 1) {
            mUri = getIntent().getData();
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            mediaPlayer = MediaPlayer.create(this, mUri);
            if (mediaPlayer == null) {
                mUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                mediaPlayer = MediaPlayer.create(getApplicationContext(), mUri);
            }
            mediaPlayer.setLooping(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(mediaPlayerVolume / 100.0f, mediaPlayerVolume / 100.0f);
        } else {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    @Override
    protected void onResume() {
        super.onResume();

        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Calendar calendar = Calendar.getInstance();
                currentTextView.setText(calendar.get(Calendar.HOUR) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
            }
            @Override
            public void onFinish() {
                finish();
            }
        };

        soundActive();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            String des = bundle.getString(ALARM_DESCRIPTION);
            mediaPlayerVolume = bundle.getInt(ALARM_VOLUME);
            type = bundle.getInt(ALARM_TYPE);
            if(des.length() >= 10)
                mDesTextView.setText(des.substring(0, 10) + " ...");
            else
                mDesTextView.setText(des);
        }
        if(type == 1) {
            mUri = getIntent().getData();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), mUri);
            if (mediaPlayer == null) {
                mUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                mediaPlayer = MediaPlayer.create(getApplicationContext(), mUri);
            }
            mediaPlayer.setLooping(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(mediaPlayerVolume / 100.0f, mediaPlayerVolume / 100.0f);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundInactive();
    }

    public void clickedAlarmOff(View view) {
        finish();
    }

    public void soundActive() {
        countDownTimer.start();

        if(type == 1) {
//            currentRingVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
            currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

            if (mediaPlayer != null)
                mediaPlayer.start();
        } else {
            if(vibrator.hasVibrator()) {
                vibrator.vibrate(new long[] {0, 3000, 1000}, 0);
            }
        }
    }

    public void soundInactive() {
        countDownTimer.cancel();

        if(type == 1) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentMusicVolume, 0);
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, currentRingVolume, 0);
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } else {
            vibrator.cancel();
        }
    }
}
