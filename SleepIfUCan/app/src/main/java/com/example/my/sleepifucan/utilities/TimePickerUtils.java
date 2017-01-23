package com.example.my.sleepifucan.utilities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
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
    EditText edText;

    public TimePickerUtils(EditText edText){
        this.edText = edText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String text = edText.getText().toString();

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
        if(minute<10){
            sMin = "0"+minute;
        }else{
            sMin = String.valueOf(minute);
        }
        if(hourOfDay<10){
            sHour = "0"+hourOfDay;
        }else{
            sHour = String.valueOf(hourOfDay);
        }
        setSelectedTime(edText, sHour, sMin);
    }

    public void setSelectedTime(EditText edText, String hourOfDay,String minute) {
        edText.setText(hourOfDay + ":" + minute);
    }
}