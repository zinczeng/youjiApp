package com.todolist;

import android.support.v4.app.Fragment;

public class TodoListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return new TodoListFragment();
    }
}
