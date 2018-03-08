package com.example.zx.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.zx.youjiandroid.MainActivity;
import com.example.zx.youjiandroid.R;

/**
 * Created by Administrator on 2017/12/10.
 */

public class WebActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView infoxiangqing_back;
    private static  String Url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initview();
    }

    private void initview() {
        infoxiangqing_back = ((ImageView) findViewById(R.id.infoxiangqing_back));
        infoxiangqing_back.setOnClickListener(this);
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(Url);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("main",2);
        startActivity(intent);
        this.finish();
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        Url=url;
        context.startActivity(intent);
    }
}
