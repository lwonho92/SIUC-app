package com.example.my.sleepifucan;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.sleepifucan.data.AlarmContract.AlarmEntry;
import com.example.my.sleepifucan.utilities.StringUtils;

/**
 * Created by MY on 2017-01-18.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmAdapterViewHolder>{
    private final Context mContext;
    private final AlarmAdapterOnClickHandler mHandler;
    private Cursor mCursor;

    class AlarmAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mTimeTextView;
        private final TextView mDesTextView;
        private final TextView mDayTextView;

        public AlarmAdapterViewHolder(View itemView) {
            super(itemView);

            mTimeTextView = (TextView) itemView.findViewById(R.id.tv_alarm_time);
            mDesTextView = (TextView) itemView.findViewById(R.id.tv_alarm_description);
            mDayTextView = (TextView) itemView.findViewById(R.id.tv_alarm_day);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int id = mCursor.getInt(MainActivity.INDEX_ID);

            mHandler.detail(id);
        }

        public void bind(int index) {
            mCursor.moveToPosition(index);

            int _id = mCursor.getInt(MainActivity.INDEX_ID);
            int clock = mCursor.getInt(MainActivity.INDEX_CLOCK);
            int minute = mCursor.getInt(MainActivity.INDEX_MINUTE);
            int day = mCursor.getInt(MainActivity.INDEX_DAY);
            int repeat = mCursor.getInt(MainActivity.INDEX_REPEAT);
            String des = mCursor.getString(MainActivity.INDEX_DESCRIPTION);
            int mSwitch = mCursor.getInt(MainActivity.INDEX_SWITCH);

            String tmp = String.format("%1$02d:%2$02d", clock, minute);

            itemView.setTag(_id);
            mTimeTextView.setText(tmp);
            mDesTextView.setText(des);
            mDayTextView.setText(Html.fromHtml(StringUtils.buildTextColor(1101101, true)));
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