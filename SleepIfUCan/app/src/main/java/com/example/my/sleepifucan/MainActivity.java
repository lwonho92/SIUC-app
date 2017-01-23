package com.example.my.sleepifucan;

import android.content.ContentValues;
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

import com.example.my.sleepifucan.data.AlarmContract.AlarmEntry;

public class MainActivity extends AppCompatActivity implements
        AlarmAdapter.AlarmAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView mRecyclerView;
    private AlarmAdapter mAlarmAdapter;
    private Toast mToast;

    private int mPosition = RecyclerView.NO_POSITION;

    private static final int LOADER_ID = 0;

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

                getContentResolver().delete(uri, null, null);
                getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
            }
        }).attachToRecyclerView(mRecyclerView);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
//                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
//                startActivity(addTaskIntent);
                Toast.makeText(view.getContext(), "Add Button Clicked.", Toast.LENGTH_SHORT).show();
            }
        });

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void detail(int id) {
        Intent intent = new Intent(this, DetailActivity.class);
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
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(AlarmEntry.COLUMN_CLOCK, 7);
                        contentValues.put(AlarmEntry.COLUMN_MINUTE, 17);
                        contentValues.put(AlarmEntry.COLUMN_DAY, 1101101);

                        contentValues.put(AlarmEntry.COLUMN_REPEAT, 0);
                        contentValues.put(AlarmEntry.COLUMN_TYPE, 0);
                        contentValues.put(AlarmEntry.COLUMN_PATH, "");
                        contentValues.put(AlarmEntry.COLUMN_VOLUME, 0);

                        contentValues.put(AlarmEntry.COLUMN_DESCRIPTION, "기상하세요");
                        contentValues.put(AlarmEntry.COLUMN_SWITCH, 0);

                        getContentResolver().insert(AlarmEntry.CONTENT_URI, contentValues);

                        return null;
                    }
                }.execute();
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

    public void androidClicked(View view) {
        Toast.makeText(this, "Android Clicked", Toast.LENGTH_SHORT).show();
    }
}