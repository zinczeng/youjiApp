package com.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TodoDatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_TODO = "create table Todo "
            + "("
            + "_id integer primary key autoincrement, "
            + "uuid, "
            + "title, "
            + "content, "
            + "date, "
            + "solved"
            + ")";
    private Context mContext;

    public TodoDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
