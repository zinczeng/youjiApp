package com.example.zx.youjiandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zx.Adapter.GLRWAdaptar;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.masonry.RecycleItemClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.Review1;
import bean.User;

/**
 * Created by Administrator on 2017/11/14.
 */

public class GLReviewActivity extends Activity implements View.OnClickListener {
    private TextView shangchuan,neirong;
    private ImageView fanhui;
    private boolean isshuaxin=true;
    private RecyclerView xiangqing;
    private SwipeRefreshLayout swipeRefresh;
    public static String URL;
    private static List<Review1> review1s;
    private String pinglunneirong="",YJGLId;
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glreview);
        Intent intent = getIntent();
         YJGLId = intent.getStringExtra("YJGLId");
        initview();        selectreview();
        SwipeRefresh();

    }

    private void initview() {
        shangchuan= (TextView) findViewById(R.id.shangchuanpinglun);
        neirong= (TextView) findViewById(R.id.pinglunneirong);
        fanhui= (ImageView) findViewById(R.id.back_to_gonglvexiangqing);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id .swipe_refresh4);
        xiangqing= (RecyclerView) findViewById(R.id.pinglunxiangqin);
        fanhui.setOnClickListener(this);
        shangchuan.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shangchuanpinglun:
                pinglunneirong=neirong.getText().toString();
                if(User.getInstance().islogin()==false){
                    startActivity(new Intent(this,LoginRegistActivity.class));
                    finish();
                }else {
                    if (pinglunneirong.equals("")) {
                        Toast.makeText(getBaseContext(), "请先评论哦", Toast.LENGTH_SHORT).show();
                    } else if (!pinglunneirong.equals("")) {
                        review();
                    }
                }
                break;
            case R.id.back_to_gonglvexiangqing:
                finish();
                break;
        }
    }

    private void recyclerView() {
        xiangqing.setLayoutManager(new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL,false));
        RecycleItemClickListener itemClickListener = new RecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        };
        GLRWAdaptar adapter = new GLRWAdaptar(review1s, itemClickListener);
        xiangqing.setAdapter(adapter);
    }
    //刷新
    private void SwipeRefresh() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isshuaxin = true;
                selectreview();
            }
        });
    }
/*
查询评论
 */
    private void selectreview(){
        if(isshuaxin==true) {
            MyConfig config = new MyConfig("ReviewServlet", "selectpl");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("YJGLId",YJGLId);
            String str = JSON.toJSONString(map);
            try {
                URL = config.getURL() + str;
            } catch (Exception e) {
                e.printStackTrace();
            }
            downloadListener = new AsyncTaskHelper.OnDataDownloadListener() {
                @Override

                public void onDataDownload(byte[] result) {
                    String jsonString = new String(result); // 返回的字节数组转换为字符串
                    Map<String, Object> map = new HashMap<String, Object>();
                    map = FastJsonTools.getMap(jsonString);
                    String code = map.get("code").toString();
                    if (code.equals("1")) {
                        String Result = map.get("result").toString();
                        review1s = JSON.parseArray(Result, Review1.class);
                        isshuaxin = false;
                        recyclerView();
                    }
                    if (code.equals("0")) {
                        Toast.makeText(getApplication(), "暂未评论信息", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefresh.setRefreshing(false);
                }
            };
            taskHelper = new AsyncTaskHelper();
            taskHelper.downloadData(this, URL, downloadListener);
        }
    }
/*
评论
 */
    private void review() {


        MyConfig config = new MyConfig("ReviewServlet", "addrw");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Content", pinglunneirong);
        map.put("UserId", User.getInstance().getUserId());
        map.put("YJGLId", YJGLId);
        String str = JSON.toJSONString(map);
        try {
            URL = config.getURL() + str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        downloadListener = new AsyncTaskHelper.OnDataDownloadListener() {
            @Override

            public void onDataDownload(byte[] result) {
                String jsonString = new String(result); // 返回的字节数组转换为字符串
                Map<String, Object> map = new HashMap<String, Object>();
                map = FastJsonTools.getMap(jsonString);
                String code = map.get("code").toString();
                if (code.equals("1")) {
                    Toast.makeText(getApplication(),"评论成功",Toast.LENGTH_LONG).show();
                    SwipeRefresh();
                }
                if(code.equals("0")){

                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(this, URL, downloadListener);
    }
    }

