package com.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import bean.Todo;

public class TodoLabDataManager {
    private static final String TODO_DATABASE_NAME = "TodoListDataBase.db";
    private static final int TODO_DATABASE_VERSION = 1;

    private Context mContext;
    private TodoDatabaseHelper mTodoDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public TodoLabDataManager(Context context) {
        mContext = context;
        mTodoDatabaseHelper = new TodoDatabaseHelper(mContext, TODO_DATABASE_NAME, null, TODO_DATABASE_VERSION);
        mSQLiteDatabase = mTodoDatabaseHelper.getWritableDatabase();
    }

    public void saveTodo(Todo todo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("uuid", String.valueOf(todo.getId()));
        contentValues.put("title", todo.getTitle());
        contentValues.put("content", todo.getContent());
        contentValues.put("date", todo.getDate().getTime());
        contentValues.put("solved", todo.isSolved() ? 1 : 0);

        mSQLiteDatabase.insert("Todo", null, contentValues);
    }

    public void deleteTodo(Todo todo) {
        mSQLiteDatabase.delete("Todo", "uuid = ?", new String[]{String.valueOf(todo.getId())});
    }

    public void updateTodo(Todo todo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("uuid", String.valueOf(todo.getId()));
        contentValues.put("title", todo.getTitle());
        contentValues.put("content", todo.getContent());
        contentValues.put("date", todo.getDate().getTime());
        contentValues.put("solved", todo.isSolved() ? 1 : 0);

        mSQLiteDatabase.update("Todo", contentValues, "uuid = ?", new String[]{String.valueOf(todo.getId())});
    }

    public List<Todo> loadTodoList() {
        List<Todo> returnList = new ArrayList<>();

        Cursor cursor = mSQLiteDatabase.query("Todo", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                UUID uuid = UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid")));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                Date date = new Date(cursor.getLong(cursor.getColumnIndex("date")));
                boolean solved = cursor.getInt(cursor.getColumnIndex("solved")) == 1;

                Todo todo = new Todo(uuid, title, content, date, solved);
                returnList.add(todo);
            } while (cursor.moveToNext());
        }

        return returnList;
    }
}
