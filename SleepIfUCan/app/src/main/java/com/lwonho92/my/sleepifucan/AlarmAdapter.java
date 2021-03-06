package com.lwonho92.my.sleepifucan;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwonho92.my.sleepifucan.alarm.AlarmIntentService;
import com.lwonho92.my.sleepifucan.data.AlarmContract.AlarmEntry;
import com.lwonho92.my.sleepifucan.utilities.TimeUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MY on 2017-01-18.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmAdapterViewHolder>{
    private  final Context mContext;
    private final AlarmAdapterOnClickHandler mHandler;
    private Cursor mCursor;

    class AlarmAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_alarm_time) TextView mTimeTextView;
        @BindView(R.id.tv_alarm_description) TextView mDesTextView;
        @BindView(R.id.tv_alarm_day) TextView mDayTextView;
        private final ImageView mCycleView, mSwitchView;

        public AlarmAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mCycleView = (ImageView) itemView.findViewById(R.id.im_cycle);
            mSwitchView = (ImageView) itemView.findViewById(R.id.im_switch);
            mSwitchView.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int id = mCursor.getInt(MainActivity.INDEX_ID);

            switch(view.getId()) {
                case R.id.im_switch:
                    Uri mUri = AlarmEntry.buildAlarmUriWithId((int)itemView.getTag());

                    int hourOfDay = mCursor.getInt(MainActivity.INDEX_CLOCK);
                    int minute = mCursor.getInt(MainActivity.INDEX_MINUTE);
                    int mSwitch = mCursor.getInt(MainActivity.INDEX_SWITCH);
                    mSwitch = 1 - mSwitch;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(AlarmEntry.COLUMN_SWITCH, mSwitch);
                    mContext.getContentResolver().update(mUri, contentValues, null, null);

                    Calendar calendar = TimeUtils.getSetCalendar(hourOfDay, minute);
                    if(calendar.getTimeInMillis() < System.currentTimeMillis())
                        calendar.add(Calendar.DATE, 1);

                    Intent intent = new Intent(mContext, AlarmIntentService.class);
                    intent.putExtra(AlarmIntentService.ALARM_ID, id);
                    intent.putExtra(AlarmIntentService.ALARM_MILLIS, calendar.getTimeInMillis());

                    if(mSwitch == 1) {
                        setViewAlpha(true);
                        intent.setAction(AlarmIntentService.RESERVE_ACTION);
                    }
                    else {
                        setViewAlpha(false);
                        intent.setAction(AlarmIntentService.CANCEL_ACTION);
                    }
                    mContext.startService(intent);
                    break;
                default:
                    mHandler.detail(id);

                    break;
            }
        }

        public void bind(int index) {
            mCursor.moveToPosition(index);

            int _id = mCursor.getInt(MainActivity.INDEX_ID);
            int hourOfDay = mCursor.getInt(MainActivity.INDEX_CLOCK);
            int minute = mCursor.getInt(MainActivity.INDEX_MINUTE);
            int day = mCursor.getInt(MainActivity.INDEX_DAY);
            int repeat = mCursor.getInt(MainActivity.INDEX_REPEAT);
            String des = mCursor.getString(MainActivity.INDEX_DESCRIPTION);
            int mSwitch = mCursor.getInt(MainActivity.INDEX_SWITCH);

            itemView.setTag(_id);

            String formattedTime = TimeUtils.getFormattedTime(hourOfDay, minute);
            mTimeTextView.setText(formattedTime);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mDayTextView.setText(Html.fromHtml(TimeUtils.buildTextColor(day, mSwitch==1), Html.FROM_HTML_MODE_LEGACY));
            } else {
                mDayTextView.setText(Html.fromHtml(TimeUtils.buildTextColor(day, mSwitch==1)));
            }

            if(repeat == 1)
                mCycleView.setVisibility(View.VISIBLE);
            else
                mCycleView.setVisibility(View.GONE);

            if(des.length() >= 10)
                mDesTextView.setText(des.substring(0, 10) + " ...");
            else
                mDesTextView.setText(des);

            if(mSwitch == 1) {
                setViewAlpha(true);
            }
            else {
                setViewAlpha(false);
            }
        }

        public void setViewAlpha(boolean mSwitch) {
            if(mSwitch) {
                mTimeTextView.setAlpha(1.0f);
                mDesTextView.setAlpha(1.0f);
                mDayTextView.setAlpha(1.0f);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mCycleView.setImageAlpha(255);
                    mSwitchView.setImageAlpha(255);
                }
            } else {
                mTimeTextView.setAlpha(0.5f);
                mDesTextView.setAlpha(0.5f);
                mDayTextView.setAlpha(0.5f);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mCycleView.setImageAlpha(128);
                    mSwitchView.setImageAlpha(128);
                }
            }
        }
    }

    interface AlarmAdapterOnClickHandler {
        public void detail(int id);
    }

    public AlarmAdapter(Context context, AlarmAdapterOnClickHandler handler) {
        mContext = context;
        mHandler = handler;
    }

    @Override
    public AlarmAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int listId = R.layout.alarm_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean immdy = false;

        View view = layoutInflater.inflate(listId, viewGroup, immdy);

        return new AlarmAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mCursor != null)
            return mCursor.getCount();
        else
            return 0;
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;

        notifyDataSetChanged();
    }
}