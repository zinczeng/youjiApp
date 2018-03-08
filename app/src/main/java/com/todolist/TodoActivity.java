package com.todolist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class TodoActivity extends SingleFragmentActivity {
    private static final String EXTRA_TODO_ID = "TodoId";

    public static Intent createIntent(Context context, UUID uuid) {
        Intent intent = new Intent(context, TodoActivity.class);
        intent.putExtra(EXTRA_TODO_ID, uuid);
        return intent;
    }

    @Override
    protected Fragment getFragment() {
        UUID todoId = (UUID) getIntent().getSerializableExtra(EXTRA_TODO_ID);
        return TodoFragment.createFragment(todoId);
    }
}
