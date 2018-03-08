package com.todolist;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.example.zx.youjiandroid.R;

import java.util.Date;
import java.util.GregorianCalendar;


public class TodoDatePickerDialogFragment extends DialogFragment {
    private DatePicker mTodoDatePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_todo_date_picker_dialog, null);
        mTodoDatePicker = (DatePicker) view.findViewById(R.id.todo_date_picker_datePicker);

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = mTodoDatePicker.getYear();
                        int month = mTodoDatePicker.getMonth();
                        int day = mTodoDatePicker.getDayOfMonth();

                        Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(date);
                    }
                })
                .create();
    }

    private void sendResult(Date date) {
        Intent intent = TodoFragment.createIntent(date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
