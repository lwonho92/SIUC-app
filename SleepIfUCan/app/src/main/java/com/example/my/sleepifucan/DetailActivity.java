package com.example.my.sleepifucan;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.example.my.sleepifucan.alarm.AlarmIntentService;
import com.example.my.sleepifucan.data.AlarmContract.AlarmEntry;
import com.example.my.sleepifucan.utilities.TimeUtils;
import com.example.my.sleepifucan.utilities.TimePickerUtils;

import java.lang.reflect.Method;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 10;
    public static final String INSERT_ACTION = "insert_detail";
    public static final String UPDATE_ACTION = "update_detail";
    private Uri mUri;
    private String action;

    EditText timeEditText;
    ToggleButton[] dayToggleButtons;
    CheckBox repeatCheckBox;
    Switch typeSwitch;
    EditText pathEditText;
    SeekBar volumeSeekBar;
    EditText desEditText;

    public static final int INDEX_ID = 0;
    public static final int INDEX_CLOCK = 1;
    public static final int INDEX_MINUTE = 2;
    public static final int INDEX_DAY = 3;
    public static final int INDEX_REPEAT = 4;
    public static final int INDEX_TYPE = 5;
    public static final int INDEX_PATH = 6;
    public static final int INDEX_VOLUME = 7;
    public static final int INDEX_DESCRIPTION = 8;
    public static final int INDEX_SWITCH = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        timeEditText = (EditText) findViewById(R.id.et_time);
        dayToggleButtons = new ToggleButton[7];
        dayToggleButtons[0] = (ToggleButton) findViewById(R.id.tb_sunday);
        dayToggleButtons[1] = (ToggleButton) findViewById(R.id.tb_monday);
        dayToggleButtons[2] = (ToggleButton) findViewById(R.id.tb_tuesday);
        dayToggleButtons[3] = (ToggleButton) findViewById(R.id.tb_wednesday);
        dayToggleButtons[4] = (ToggleButton) findViewById(R.id.tb_thursday);
        dayToggleButtons[5] = (ToggleButton) findViewById(R.id.tb_friday);
        dayToggleButtons[6] = (ToggleButton) findViewById(R.id.tb_saturday);
        repeatCheckBox = (CheckBox) findViewById(R.id.cb_repeat);
        typeSwitch = (Switch) findViewById(R.id.sw_type);
        pathEditText = (EditText) findViewById(R.id.tv_path);
        volumeSeekBar = (SeekBar) findViewById(R.id.sb_volume);
        desEditText = (EditText) findViewById(R.id.et_description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            timeEditText.setShowSoftInputOnFocus(false);
        } else {
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus"
                        , new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(timeEditText, false);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        Intent intent = getIntent();
        action = intent.getAction();
        mUri = intent.getData();

        if(action == INSERT_ACTION)
            setTitle("Insert Alarm");
        else if(action == UPDATE_ACTION) {
            setTitle("Detail Alarm");
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LOADER_ID:
                return new CursorLoader(this, mUri, null, null, null, null);
            default:
                throw new RuntimeException("No Match Loader's Id");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null)
            return;
        data.moveToFirst();

        String formattedTime = TimeUtils.getFormattedTime(data.getInt(INDEX_CLOCK), data.getInt(INDEX_MINUTE));
        timeEditText.setText(formattedTime);
        int days = data.getInt(INDEX_DAY);
        for(ToggleButton toggleButton : dayToggleButtons) {
            toggleButton.setChecked((days%10)==1);
            days /= 10;
        }
        repeatCheckBox.setChecked(data.getInt(INDEX_REPEAT) == 1);
        typeSwitch.setChecked(data.getInt(INDEX_TYPE) == 1);
        pathEditText.setText(data.getString(INDEX_PATH));
        volumeSeekBar.setProgress(data.getInt(INDEX_VOLUME));
        desEditText.setText(data.getString(INDEX_DESCRIPTION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();

        switch(selectedItem) {
            case R.id.action_apply:
                ContentValues contentValues = new ContentValues();
                String formattedTime = timeEditText.getText().toString();
                int hourOfDay = Integer.parseInt(formattedTime.substring(0, 2));
                int minute = Integer.parseInt(formattedTime.substring(3, 5));
                contentValues.put(AlarmEntry.COLUMN_CLOCK, hourOfDay);
                contentValues.put(AlarmEntry.COLUMN_MINUTE, minute);
                int days = 0;
                for(int i = 6; i >= 0; i--) {
                    days *= 10;
                    if(dayToggleButtons[i].isChecked())
                        days += 1;
                }
                contentValues.put(AlarmEntry.COLUMN_DAY, days);

                contentValues.put(AlarmEntry.COLUMN_REPEAT, repeatCheckBox.isChecked() ? 1 : 0);
                contentValues.put(AlarmEntry.COLUMN_TYPE, typeSwitch.isChecked() ? 1 : 0);
                contentValues.put(AlarmEntry.COLUMN_PATH, pathEditText.getText().toString());
                contentValues.put(AlarmEntry.COLUMN_VOLUME, volumeSeekBar.getProgress());

                contentValues.put(AlarmEntry.COLUMN_DESCRIPTION, desEditText.getText().toString());
                contentValues.put(AlarmEntry.COLUMN_SWITCH, 1);

                int id = 0;

                if(action == DetailActivity.INSERT_ACTION) {
                    Uri insertedUri = getContentResolver().insert(mUri, contentValues);
                    id = Integer.parseInt(insertedUri.getLastPathSegment());
                } else if(action == DetailActivity.UPDATE_ACTION){
                    getContentResolver().update(mUri, contentValues, null, null);
                    id = Integer.parseInt(mUri.getLastPathSegment());
                }

                Calendar calendar = TimeUtils.getSetCalendar(hourOfDay, minute);
                if(calendar.getTimeInMillis() < System.currentTimeMillis())
                    calendar.add(Calendar.DATE, 1);

                Intent intent = new Intent(this, AlarmIntentService.class);
                intent.putExtra(AlarmIntentService.ALARM_ID, id);
                intent.putExtra(AlarmIntentService.ALARM_MILLIS, calendar.getTimeInMillis());
                intent.setAction(AlarmIntentService.RESERVE_ACTION);
                startService(intent);

                finish();
            case R.id.action_cancel:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void showTimePickerDialog(View v) {
        DialogFragment dialogFragment = new TimePickerUtils((EditText) v);
        dialogFragment.show(getSupportFragmentManager(), "TimePicker");
    }
}