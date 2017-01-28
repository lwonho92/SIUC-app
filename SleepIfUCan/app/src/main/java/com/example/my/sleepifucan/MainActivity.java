package com.example.my.sleepifucan;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.my.sleepifucan.alarm.AlarmIntentService;
import com.example.my.sleepifucan.alarm.InitReceiver;
import com.example.my.sleepifucan.data.AlarmContract.AlarmEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
                                            AlarmAdapter.AlarmAdapterOnClickHandler,
                                            LoaderManager.LoaderCallbacks<Cursor>,
                                            View.OnClickListener {
    @BindView(R.id.recyclerview_alarm) public RecyclerView mRecyclerView;
    @BindView(R.id.iv_picture) public ImageView mPictureImageView;
    private AlarmAdapter mAlarmAdapter;

    private int mPosition = RecyclerView.NO_POSITION;

    private static final int LOADER_ID = 0;
    private static final String INTENT_ACTION = "com.example.my.sleepifucan.alarm.INIT_RECEIVER";

    public static final String[] DESIRED_COLUMNS = {
            AlarmEntry._ID,
            AlarmEntry.COLUMN_CLOCK,
            AlarmEntry.COLUMN_MINUTE,
            AlarmEntry.COLUMN_DAY,
            AlarmEntry.COLUMN_REPEAT,
            AlarmEntry.COLUMN_DESCRIPTION,
            AlarmEntry.COLUMN_SWITCH
    };

    public static final int INDEX_ID = 0;
    public static final int INDEX_CLOCK = 1;
    public static final int INDEX_MINUTE = 2;
    public static final int INDEX_DAY = 3;
    public static final int INDEX_REPEAT = 4;
    public static final int INDEX_DESCRIPTION = 5;
    public static final int INDEX_SWITCH = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAlarmAdapter = new AlarmAdapter(this, this);

        mRecyclerView.setAdapter(mAlarmAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (int) viewHolder.itemView.getTag();

                Intent intent = new Intent(MainActivity.this, AlarmIntentService.class);
                intent.putExtra(AlarmIntentService.ALARM_ID, id);
                intent.putExtra(AlarmIntentService.ALARM_MILLIS, 0L);
                intent.setAction(AlarmIntentService.CANCEL_ACTION);
                startService(intent);

                Uri uri = AlarmEntry.buildAlarmUriWithId(id);
                getContentResolver().delete(uri, null, null);
                getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
            }
        }).attachToRecyclerView(mRecyclerView);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(this);

        if(!InitReceiver.isInit) {
            Intent intent = new Intent(INTENT_ACTION);
            this.sendBroadcast(intent);
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void detail(int id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.setAction(DetailActivity.UPDATE_ACTION);
        Uri uri = AlarmEntry.buildAlarmUriWithId(id);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LOADER_ID:
                Uri uri = AlarmEntry.CONTENT_URI;
                String sortOrder = AlarmEntry._ID + " ASC";
                return new CursorLoader(this, uri, DESIRED_COLUMNS, null, null, sortOrder);
            default:
                throw new RuntimeException("No Match Loader's Id");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAlarmAdapter.setCursor(data);
        if(mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);

        if(data.moveToNext()) {
            showAlarm();
        } else {
            showBackground();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAlarmAdapter.setCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();

        switch(selectedItem) {
            case R.id.action_insert:
                newAlarm();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void newAlarm() {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.setAction(DetailActivity.NEW_ACTION);
        intent.setData(AlarmEntry.CONTENT_URI);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        newAlarm();
    }

    public void showAlarm() {
        mPictureImageView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    public void showBackground() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mPictureImageView.setVisibility(View.VISIBLE);
    }
}