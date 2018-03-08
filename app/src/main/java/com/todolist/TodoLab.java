package com.todolist;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bean.Todo;

public class TodoLab {
    private static TodoLab INSTANCE = null;
    private static List<Todo> mTodoList = new ArrayList<>();
    private Context mContext;
    private TodoLabDataManager mTodoLabDataManager;

    private TodoLab(Context context) {
        mContext = context;
        mTodoLabDataManager = new TodoLabDataManager(mContext);
        mTodoList = mTodoLabDataManager.loadTodoList();
    }

    public static TodoLab getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TodoLab(context);
        }
        return INSTANCE;
    }

    public Todo getTodo(UUID uuid) {
        for (Todo todo : mTodoList) {
            if (todo.getId().equals(uuid)) {
                return todo;
            }
        }
        return null;
    }

    public List<Todo> getTodoList() {
        return mTodoList;
    }

    public void addTodo(Todo todo) {
        mTodoList.add(todo);
        mTodoLabDataManager.saveTodo(todo);
    }

    public void removeTodo(Todo todo) {
        mTodoList.remove(todo);
        mTodoLabDataManager.deleteTodo(todo);
    }

    public void updateTodo(Todo todo) {
        mTodoLabDataManager.updateTodo(todo);
    }
}
