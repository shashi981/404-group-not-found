package com.example.grocerymanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    //  ChatGPT Usage: Yes.
    public interface DatePickerListener {
        void onDateSet(int year, int month, int day);
    }

    //  ChatGPT Usage: Yes.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    //  ChatGPT Usage: Yes.
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Implement the DatePickerListener interface and send the selected date back to the activity
        DatePickerListener listener = (DatePickerListener) getActivity();
        if (listener != null) {
            listener.onDateSet(year, month, day);
        }
    }

}

