package com.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.zx.youjiandroid.R;

import java.util.UUID;

import bean.Todo;

public class TodoPagerActivity extends AppCompatActivity {
    private static final String EXTRA_TODO_ID = "TodoId";

    private ViewPager mTodoViewPager;
    private TodoLab mTodoLab;

    public static Intent createIntent(Context context, UUID uuid) {
        Intent intent = new Intent(context, TodoPagerActivity.class);
        intent.putExtra(EXTRA_TODO_ID, uuid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_pager);

        mTodoLab = TodoLab.getInstance(TodoPagerActivity.this);
        UUID todoId = (UUID) getIntent().getSerializableExtra(EXTRA_TODO_ID);

        mTodoViewPager = (ViewPager) findViewById(R.id.todo_viewPager);
        mTodoViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Todo todo = mTodoLab.getTodoList().get(position);
                Fragment fragment = TodoFragment.createFragment(todo.getId());
                return fragment;
            }

            @Override
            public int getCount() {
                return mTodoLab.getTodoList().size();
            }
        });

        int todoPosition = 0;
        for (Todo todo : mTodoLab.getTodoList()) {
            if (todo.getId().equals(todoId)) {
                mTodoViewPager.setCurrentItem(todoPosition);
            }
            todoPosition++;
        }
    }
}
