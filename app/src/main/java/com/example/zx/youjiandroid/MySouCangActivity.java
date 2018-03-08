package com.example.zx.youjiandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.example.zx.Adapter.mySCAdapter;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.masonry.RecycleItemClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.Collect1;
import bean.User;

/**
 * Created by Administrator on 2017/10/25.
 */

public class MySouCangActivity extends Activity implements View.OnClickListener {
    private ImageView back_to_user2;
    private RecyclerView shoucangrw;
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    public static String URL;
    private static List<Collect1> collect1s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_soucang);
        initview();
    }

    private void initview() {
        back_to_user2= (ImageView) findViewById(R.id.back_to_user2);
        shoucangrw= (RecyclerView) findViewById(R.id.shoucangrw);
        if(User.getInstance().islogin()==true) {
            initdata();
        }
        back_to_user2.setOnClickListener(this);
    }

    /*
查询我的收藏
 */
    private void initdata() {
        MyConfig config = new MyConfig("CollectServlet", "selectALL1");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("UserId", User.getInstance().getUserId());
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
                    collect1s = JSON.parseArray(Result, Collect1.class);
                    Log.e("zzzzzzzzz",collect1s.get(0).getUpData());
                    recyclerview();
                }
                if(code.equals("0")){

                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(getApplicationContext(), URL, downloadListener);
    }


    private void recyclerview() {
        shoucangrw.setLayoutManager(new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL,false));
        RecycleItemClickListener itemClickListener = new RecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        };
        mySCAdapter adapter = new mySCAdapter(collect1s, itemClickListener);
        shoucangrw.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_to_user2:
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("main",4);
                startActivity(intent);
                finish();
                break;
        }
    }
}
