package com.example.my.sleepifucan;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.example.my.sleepifucan.data.AlarmContract.AlarmEntry;
import com.example.my.sleepifucan.utilities.TimePickerUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 10;
    private Uri mUri;

    Switch switchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        switchType = (Switch) findViewById(R.id.switchType);

        Intent intent = getIntent();
        mUri = intent.getData();

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
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void showTimePickerDialog(View v) {
        DialogFragment dialogFragment = new TimePickerUtils((EditText) v);
        dialogFragment.show(getSupportFragmentManager(), "TimePicker");
    }
}