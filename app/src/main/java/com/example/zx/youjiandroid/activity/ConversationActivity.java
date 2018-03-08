package com.example.zx.youjiandroid.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.TextView;

import com.example.zx.youjiandroid.R;


public class ConversationActivity extends FragmentActivity {
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.conversation_activity);
        mTextView = (TextView) findViewById(R.id.title);
        mTextView.setText(getIntent().getData().getQueryParameter("title"));
    }
}
