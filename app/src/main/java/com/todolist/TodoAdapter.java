package com.todolist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.zx.youjiandroid.R;

import java.util.List;

import bean.Todo;


public class TodoAdapter extends ArrayAdapter {
    private int mResourceId;

    public TodoAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        mResourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Todo todo = TodoLab.getInstance(getContext()).getTodoList().get(position);

        View view = LayoutInflater.from(getContext()).inflate(mResourceId, null);

        TextView todoTitleTextView = (TextView) view.findViewById(R.id.todo_title_textView);
        todoTitleTextView.setText(todo.getTitle());

        TextView todoDateTextView = (TextView) view.findViewById(R.id.todo_date_textView);
        todoDateTextView.setText(DateFormat.getDateFormat(getContext()).format(todo.getDate()));

        CheckBox todoSolvedCheckBox = (CheckBox) view.findViewById(R.id.todo_solved_checkBox);
        todoSolvedCheckBox.setChecked(todo.isSolved());

        return view;
    }
}
