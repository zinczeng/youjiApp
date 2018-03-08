package com.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.zx.youjiandroid.R;

import java.util.Date;
import java.util.UUID;

import bean.Todo;

public class TodoFragment extends Fragment {
    private static final String TAG = "TodoFragment";
    private static final String ARGUMENTS_TODO_ID = "TodoId";
    private static final String EXTRA_TODO_DATE = "TodoDate";
    private static final int REQUEST_CODE_TODO_DATE = 0;

    private Toolbar toolbar;
    private EditText mTodoTitleEditText;
    private EditText mTodoContentEditText;
    private CheckBox mTodoSolvedCheckBox;
    private Button mTodoDateButton;
private ImageView back_to_todolist;
    private Todo mTodo;

    public static TodoFragment createFragment(UUID uuid) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGUMENTS_TODO_ID, uuid);
        TodoFragment todoFragment = new TodoFragment();
        todoFragment.setArguments(arguments);
        return todoFragment;
    }

    public static Intent createIntent(Date date) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TODO_DATE, date);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        setHasOptionsMenu(true);
        back_to_todolist = (ImageView) view.findViewById(R.id.back_to_todolist);
        back_to_todolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),TodoListActivity.class));
                getActivity().finish();
            }
        });
        toolbar = (Toolbar) view.findViewById(R.id.tb_beiwanglu_delete);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        UUID todoId = (UUID) getArguments().getSerializable(ARGUMENTS_TODO_ID);
        mTodo = TodoLab.getInstance(getContext()).getTodo(todoId);

        mTodoTitleEditText = (EditText) view.findViewById(R.id.todo_title_editText);
        mTodoTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTodo.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mTodoContentEditText = (EditText) view.findViewById(R.id.todo_content_editText);
        mTodoContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTodo.setContent(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mTodoSolvedCheckBox = (CheckBox) view.findViewById(R.id.todo_solved_checkBox);
        mTodoSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mTodo.setSolved(b);
            }
        });

        mTodoDateButton = (Button) view.findViewById(R.id.todo_date_button);
        mTodoDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, mTodo.toString());
                TodoDatePickerDialogFragment todoDatePickerDialogFragment = new TodoDatePickerDialogFragment();
                todoDatePickerDialogFragment.setTargetFragment(TodoFragment.this, REQUEST_CODE_TODO_DATE);
                todoDatePickerDialogFragment.show(getFragmentManager(), "TodoDatePickerFragment");
            }
        });

        updateUI();

        return view;
    }

    private void updateUI() {
        mTodoTitleEditText.setText(mTodo.getTitle());
        mTodoContentEditText.setText(mTodo.getContent());
        mTodoSolvedCheckBox.setChecked(mTodo.isSolved());
        mTodoDateButton.setText(DateFormat.getDateFormat(getContext()).format(mTodo.getDate()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_TODO_DATE:
                mTodo.setDate((Date) data.getSerializableExtra(EXTRA_TODO_DATE));
                updateUI();
                break;
            default:
                break;
        }
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_todo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_todo:
                TodoLab.getInstance(getContext()).removeTodo(mTodo);
                getActivity().finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TodoLab.getInstance(getContext()).updateTodo(mTodo);
    }
}
