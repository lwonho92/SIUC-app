package com.example.my.sleepifucan;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.my.sleepifucan.alarm.AlarmIntentService;
import com.example.my.sleepifucan.data.AlarmContract.AlarmEntry;
import com.example.my.sleepifucan.utilities.TimeUtils;
import com.example.my.sleepifucan.utilities.TimePickerUtils;

import java.lang.reflect.Method;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 10;
    public static final int PICK_FROM_FILE = 1234;
    public static final String NEW_ACTION = "new_detail";
    public static final String UPDATE_ACTION = "update_detail";
    private Uri mUri;
    private String sPath = "";
    private String action;

    EditText timeEditText;
    ToggleButton[] dayToggleButtons;
    CheckBox repeatCheckBox;
    Switch typeSwitch;
    TextView pathTextView;
    Button pathButton;
    SeekBar volumeSeekBar;
    EditText desEditText;

    public static final int INDEX_ID = 0;
    public static final int INDEX_CLOCK = 1;
    public static final int INDEX_MINUTE = 2;
    public static final int INDEX_DAY = 3;
    public static final int INDEX_REPEAT = 4;
    public static final int INDEX_TYPE = 5;
    public static final int INDEX_URI = 6;
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
        typeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    typeSwitch.setText("알람음");
                } else {
                    typeSwitch.setText("진동");
                }
            }
        });
        pathTextView = (TextView) findViewById(R.id.tv_path);
        pathButton = (Button) findViewById(R.id.bt_path);
        pathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for alarm:");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM | RingtoneManager.TYPE_RINGTONE);

                startActivityForResult(intent, PICK_FROM_FILE);
            }
        });
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

        if(action == NEW_ACTION)
            setTitle("New Alarm");
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
        sPath = data.getString(INDEX_URI);
        pathTextView.setText(getFileName());
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
                contentValues.put(AlarmEntry.COLUMN_URI, sPath);
                contentValues.put(AlarmEntry.COLUMN_VOLUME, volumeSeekBar.getProgress());

                contentValues.put(AlarmEntry.COLUMN_DESCRIPTION, desEditText.getText().toString());
                contentValues.put(AlarmEntry.COLUMN_SWITCH, 1);

                int id = 0;

                if(action == DetailActivity.NEW_ACTION) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == PICK_FROM_FILE) && (resultCode == RESULT_OK)){
            if (data != null) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (uri != null) {
                    sPath = uri.toString();
                    pathTextView.setText(getFileName());
                }
            }
        }
    }

    public String getFileName() {
        Uri uri = Uri.parse(sPath);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        String fileName = "기본 알람음";

        if(cursor == null) {
            return fileName;
        }
        if (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            if(index > 0)
                fileName = cursor.getString(index);
        }
        return fileName;
    }
}