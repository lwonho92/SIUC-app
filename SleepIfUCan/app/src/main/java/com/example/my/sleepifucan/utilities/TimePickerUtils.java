package com.example.my.sleepifucan.utilities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.my.sleepifucan.R;

import java.util.Calendar;

/**
 * Created by MY on 2017-01-23.
 */

public class TimePickerUtils extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    EditText mEditText;

    public TimePickerUtils() {
    }

    /*public TimePickerUtils(EditText editText){
        this.mEditText = editText;
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        FragmentActivity fragmentActivity = getActivity();

        mEditText = (EditText) fragmentActivity.findViewById(R.id.et_time);
        String text = mEditText.getText().toString();

        if(text.equals(getResources().getString(R.string.default_time)))
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        else
            return new TimePickerDialog(getActivity(), this, Integer.parseInt(text.substring(0, 2)), Integer.parseInt(text.substring(3, 5)), DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Log.d("TimePicker", view.toString());
        String sHour, sMin;

        sHour = String.format("%1$02d", hourOfDay);
        sMin = String.format("%1$02d", minute);

        setSelectedTime(mEditText, sHour, sMin);
    }

    public void setSelectedTime(EditText edText, String hourOfDay,String minute) {
        edText.setText(hourOfDay + ":" + minute);
    }
}