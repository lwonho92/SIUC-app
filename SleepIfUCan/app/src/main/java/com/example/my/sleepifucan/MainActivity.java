package com.example.my.sleepifucan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.my.sleepifucan.alarm.AlarmIntentService;
import com.example.my.sleepifucan.alarm.InitReceiver;
import com.example.my.sleepifucan.data.AlarmContract.AlarmEntry;

public class MainActivity extends AppCompatActivity implements
        AlarmAdapter.AlarmAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView mRecyclerView;
    private AlarmAdapter mAlarmAdapter;
    private Toast mToast;

    private int mPosition = RecyclerView.NO_POSITION;

    private static final int LOADER_ID = 0;
    private static final String INTENT_ACTION = "com.example.my.sleepifucan";

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

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_alarm);

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

                String stringId = Integer.toString(id);
                Uri uri = AlarmEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                Intent intent = new Intent(MainActivity.this, AlarmIntentService.class);
                intent.putExtra(AlarmIntentService.ALARM_ID, id);
                intent.putExtra(AlarmIntentService.ALARM_MILLIS, 0L);
                intent.setAction(AlarmIntentService.CANCEL_ACTION);
                startService(intent);

                getContentResolver().delete(uri, null, null);
                getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
            }
        }).attachToRecyclerView(mRecyclerView);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.setAction(DetailActivity.INSERT_ACTION);
                intent.setData(AlarmEntry.CONTENT_URI);
                startActivity(intent);
            }
        });

        if(!InitReceiver.isInit) {
            Intent intent = new Intent("com.example.my.sleepifucan.alarm.INIT_RECEIVER");
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
                Intent intent = new Intent(this, DetailActivity.class);
                intent.setAction(DetailActivity.INSERT_ACTION);
                intent.setData(AlarmEntry.CONTENT_URI);
                startActivity(intent);

                return true;
            case R.id.action_delete:
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        getContentResolver().delete(AlarmEntry.CONTENT_URI, null, null);

                        return null;
                    }
                }.execute();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}