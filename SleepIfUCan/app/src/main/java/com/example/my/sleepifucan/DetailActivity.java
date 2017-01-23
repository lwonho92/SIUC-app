package com.example.my.sleepifucan;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.example.my.sleepifucan.data.AlarmContract.AlarmEntry;
import com.example.my.sleepifucan.utilities.TimePickerUtils;

import java.lang.reflect.Method;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 10;
    public static final String CODE_FLAG = "flag_detail";
    public static final int INSERT_ID = 1357;
    public static final int UPDATE_ID = 1470;

    private Uri mUri;
    private int flag;

    Switch switchType;
    EditText timeEditText, desEditText;
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        switchType = (Switch) findViewById(R.id.switchType);
        timeEditText = (EditText) findViewById(R.id.et_time);
        toggleButton = (ToggleButton) findViewById(R.id.toggle_monday);
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
        flag = intent.getIntExtra(CODE_FLAG, 0);
        mUri = intent.getData();

        if(flag == INSERT_ID)
            setTitle("Insert Alarm");
        else
            setTitle("Detail Alarm");

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    buttonView.getId();
                } else {
                    // The toggle is disabled
                }
            }
        });

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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

        String des = data.getString(data.getColumnIndex(AlarmEntry.COLUMN_DESCRIPTION));
        int type = data.getInt(data.getColumnIndex(AlarmEntry.COLUMN_TYPE));

        if(type == 0) {
            switchType.setChecked(true);
        } else {
            switchType.setChecked(false);
        }
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
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(AlarmEntry.COLUMN_CLOCK, 8);
                        contentValues.put(AlarmEntry.COLUMN_MINUTE, 18);
                        contentValues.put(AlarmEntry.COLUMN_DAY, 1101100);

                        contentValues.put(AlarmEntry.COLUMN_REPEAT, 0);
                        contentValues.put(AlarmEntry.COLUMN_TYPE, 0);
                        contentValues.put(AlarmEntry.COLUMN_PATH, "");
                        contentValues.put(AlarmEntry.COLUMN_VOLUME, 0);

                        contentValues.put(AlarmEntry.COLUMN_DESCRIPTION, "수정완료");
                        contentValues.put(AlarmEntry.COLUMN_SWITCH, 0);

                        if(flag == DetailActivity.INSERT_ID) {
                            getContentResolver().insert(mUri, contentValues);
                        } else {
                            getContentResolver().update(mUri, contentValues, null, null);
                        }

                        return null;
                    }
                }.execute();
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