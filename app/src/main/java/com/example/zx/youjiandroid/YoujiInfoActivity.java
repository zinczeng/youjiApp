package com.example.zx.youjiandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zx.Adapter.YJInfoAdapter;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.masonry.RecycleItemClickListener;
import com.example.zx.masonry.SpacesItemDecoration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.YJGLContent;

/**
 * Created by Administrator on 2017/12/12.
 */

public class YoujiInfoActivity  extends AppCompatActivity{
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    private boolean ispaddind=false;
    public static String URL;
    private static List<YJGLContent> yjglContents;
    private RecyclerView recyclerView;
    private static String YJGLId,Title;
    private Toolbar toolbar;
    private ImageView back_to_myyj;
    private TextView Title2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YJGLId =getIntent().getStringExtra("YJGLId");
        Title =getIntent().getStringExtra("Title");
        setContentView(R.layout.activity_youjiinfo);
        initData();
        initview();
    }

    private void initview() {
        back_to_myyj = (ImageView) findViewById(R.id.back_to_myyj);
        back_to_myyj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplication(),MyYouJiActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Title2 = (TextView) findViewById(R.id.Title2);
        Title2.setText(Title);
        toolbar = (Toolbar)findViewById(R.id.contol_yj);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void recyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        RecycleItemClickListener itemClickListener = new RecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

              //  showNormalDialog(yjglContents.get(position).getYJGLContentId().toString());

            }
        };
        YJInfoAdapter adapter = new YJInfoAdapter(yjglContents, itemClickListener);
        recyclerView.setAdapter(adapter);
        Log.e("ddddddddddddd","");
        if(ispaddind==false) {
            SpacesItemDecoration decoration = new SpacesItemDecoration(8);//设置边距  padding效果
            recyclerView.addItemDecoration(decoration);
            ispaddind=true;
        }
    }

    private void initData() {

            MyConfig config = new MyConfig("YJGLContentServlet", "selectallct");
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
                    if(!map.equals("")) {
                        String code = map.get("code").toString();
                        if (code.equals("1")) {

                            String Result = map.get("result").toString();
                            yjglContents = JSON.parseArray(Result, YJGLContent.class);
                            Log.e("dddd",yjglContents.get(0).getParagraphText().toString());
                            recyclerView();
                        }
                        if (code.equals("0")) {

                        }

                    }
                }
            };
            taskHelper = new AsyncTaskHelper();
            taskHelper.downloadData(getApplication(), URL, downloadListener);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.yj_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                Intent intent =new Intent(getApplicationContext(),YJContentActivity.class);
                intent.putExtra("creat","1");
                intent.putExtra("YJGLId",YJGLId);
                startActivity(intent);
                break;
            case R.id.menu_delete:
                if(!YJGLId.equals("")){
                    MyConfig config = new MyConfig("YJGLServlet", "delete");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("YJGLId", YJGLId);
                    String str = JSON.toJSONString(map);
                    try {
                        URL = config.getURL() + str;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("URL", URL);
                    downloadListener = new AsyncTaskHelper.OnDataDownloadListener() {
                        @Override

                        public void onDataDownload(byte[] result) {
                            String jsonString = new String(result); // 返回的字节数组转换为字符串
                            Map<String, Object> map = new HashMap<String, Object>();
                            map = FastJsonTools.getMap(jsonString);
                            String code = map.get("code").toString();
                            if (code.equals("1")) {
                                Toast.makeText(getApplicationContext(),"删除成功",Toast.LENGTH_SHORT).show();
                                Intent intent =new Intent(getApplication(),MyYouJiActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    };
                    taskHelper = new AsyncTaskHelper();
                    taskHelper.downloadData(getApplicationContext(), URL, downloadListener);
                }
                break;
        }
        return true;
    }


    private void showNormalDialog(String id){
        final String Id = id;
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getApplicationContext());
        normalDialog.setTitle("提示");
        normalDialog.setMessage("是否删除");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyConfig config = new MyConfig("YJGLContentServlet", "delete");
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("YJGLContentId", Id);
                        String str = JSON.toJSONString(map);
                        try {
                            URL = config.getURL() + str;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("URL", URL);
                        downloadListener = new AsyncTaskHelper.OnDataDownloadListener() {
                            @Override

                            public void onDataDownload(byte[] result) {
                                String jsonString = new String(result); // 返回的字节数组转换为字符串
                                Map<String, Object> map = new HashMap<String, Object>();
                                map = FastJsonTools.getMap(jsonString);
                                String code = map.get("code").toString();
                                if (code.equals("1")) {
                                    Toast.makeText(getApplicationContext(),"删除成功",Toast.LENGTH_SHORT).show();
                                    Intent intent =new Intent(getApplication(),MyYouJiActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        };
                        taskHelper = new AsyncTaskHelper();
                        taskHelper.downloadData(getApplicationContext(), URL, downloadListener);
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }


}
