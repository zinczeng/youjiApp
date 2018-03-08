package com.example.zx.youjiandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zx.Adapter.GLAdaptar;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.View.FlowLayout;
import com.example.zx.masonry.RecycleItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.SuiJi;
import bean.Tag;

import static com.example.zx.youjiandroid.LoginRegistActivity.URL;

/**
 * Created by Administrator on 2017/10/25.
 */

public class SearchGongLveActivity extends Activity implements View.OnClickListener {
    private ImageView searchback_to_gonglve;
    private TextView select_gl;
    private EditText gl;
    private FlowLayout mFlowlayout;
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    private RecyclerView tag_gl;
private List<Tag> tag = new ArrayList<>();
private List<SuiJi> suiJis = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_gonglve);
        initview();
        mFlowlayout = (FlowLayout)findViewById(R.id.Flow_biaoqian2);
        tag_gl = (RecyclerView) findViewById(R.id.tag_gl);
        select_gl = (TextView) findViewById(R.id.select_gl);
        select_gl.setOnClickListener(this);
        gl = (EditText) findViewById(R.id.gl);
        initretag();
    }

    private void initretag() {
        MyConfig config = new MyConfig("TagServlet", "selectrd");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("types", 1);
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
                    tag = FastJsonTools.getBeans(Result,Tag.class);
                    LayoutInflater inflater = LayoutInflater.from(SearchGongLveActivity.this);
                            for(int i=0;i<tag.size();i++){
                                TextView tv =(TextView) inflater.inflate(R.layout.layout2,mFlowlayout,false);
                                tv.setText(tag.get(i).getTagName());
                                tv.setTextColor(Color.parseColor("#3194D8"));
                                mFlowlayout.addView(tv);
                                final int j=i ;
                                mFlowlayout.findViewById(R.id.tvi).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    selectgl(tag.get(j).getTagName());
                                    }
                                });
                    }

                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(this, URL, downloadListener);

    }

    private void initview() {
        searchback_to_gonglve = (ImageView) findViewById(R.id.searchback_to_gonglve);
        searchback_to_gonglve.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.searchback_to_gonglve:
                Intent intent =new Intent(this,MainActivity.class);
                intent.putExtra("main",2);
                startActivity(intent);
                finish();
                break;
            case R.id.select_gl:
                if(!gl.getText().toString().equals("")){
                selectgl(gl.getText().toString());
                }else {
                    Toast.makeText(SearchGongLveActivity.this,"输入内容不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void selectgl(String Tagname) {

        MyConfig config = new MyConfig("TagServlet", "selectsearch");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("TagName",Tagname);
        map.put("Types", 1);
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
                    suiJis = JSON.parseArray(Result, SuiJi.class);
                    recyclerView();
                    }
                else if(code.equals("0")){
                    Toast.makeText(SearchGongLveActivity.this,"暂未查询到攻略",Toast.LENGTH_SHORT).show();
                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(this, URL, downloadListener);

    }

    private void recyclerView() {
        tag_gl.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
        RecycleItemClickListener itemClickListener = new RecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(SearchGongLveActivity.this,GongLveXiangXiActvity.class);
                intent.putExtra("YJGLid",suiJis.get(position).getyJGLId());
                intent.putExtra("Dianzan",suiJis.get(position).getDianzan());
                intent.putExtra("Title",suiJis.get(position).getTitle());
                intent.putExtra("UpData",suiJis.get(position).getUpData());
                intent.putExtra("UserName",suiJis.get(position).getUserName());
                intent.putExtra("UserPT",suiJis.get(position).getUserPT());
                startActivity(intent);
            }
        };
        GLAdaptar adapter = new GLAdaptar(suiJis, itemClickListener);
        tag_gl.setAdapter(adapter);
    }
}
