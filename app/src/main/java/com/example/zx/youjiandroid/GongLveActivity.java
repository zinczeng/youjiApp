package com.example.zx.youjiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.example.zx.Adapter.myYJAdapter;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.masonry.RecycleItemClickListener;
import com.sortricheditor.ui.GLActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.SuiJi;
import bean.User;

/**
 * Created by Administrator on 2017/12/10.
 */

public class GongLveActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView back_to_user;
    private LinearLayout crevat_gl;
    private RecyclerView wodegl;
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    private LinearLayout crevat_gonglve;
    public static String URL;
    private static List<SuiJi> userYJGLs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gonglve);
        initview();
    }

    private void initview() {
        crevat_gl = (LinearLayout) findViewById(R.id.crevat_gl);
        back_to_user = (ImageView) findViewById(R.id.back_to_user4);
        wodegl = (RecyclerView) findViewById(R.id.wodegl);
        crevat_gl.setOnClickListener(this);
        back_to_user.setOnClickListener(this);
        if(User.getInstance().islogin()==true) {
            initdata();
        }
    }


    /*
查询我的游记
 */
    private void initdata() {
        MyConfig config = new MyConfig("YJGLServlet", "selectmy");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("types", 1);
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
                    userYJGLs = JSON.parseArray(Result, SuiJi.class);
                    recyclerview();
                }
                if(code.equals("0")){

                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(getApplicationContext(), URL, downloadListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.crevat_gl:
                if(User.getInstance().islogin()==false){
                    startActivity(new Intent(getApplication(),LoginRegistActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(this, GLActivity.class));
                }
                break;
            case R.id.back_to_user4:
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("main",4);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void recyclerview() {
        wodegl.setLayoutManager(new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL,false));
        RecycleItemClickListener itemClickListener = new RecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        };
        myYJAdapter adapter = new myYJAdapter(userYJGLs, itemClickListener);
        wodegl.setAdapter(adapter);
    }
}
