package com.example.zx.youjiandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class TestActivity extends AppCompatActivity {

    private static final String URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ImageView imageView = (ImageView) findViewById(R.id.image3);

        /*Glide
        .with(context)//上下文
                .load(url)
                .placeholder(R.mipmap.defalut) //设置占位图
                .error(R.mipmap.error) //设置错误图片
                .crossFade() //设置淡入淡出效果，默认300ms，可以传参
                //.dontAnimate() //不显示动画效果
                .into(imageView);//显示图片的imageview*/
        Glide.with(this).load(getIntent().getStringExtra(URL)).into(imageView);
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, TestActivity.class);
        intent.putExtra(URL, url);
        context.startActivity(intent);
    }
}
