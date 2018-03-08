package com.example.zx.youjiandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.wx.goodview.GoodView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.User;
import bean.YJGLContent;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Administrator on 2017/10/25.
 */

public class GongLveXiangXiActvity extends Activity implements View.OnClickListener {
    private ImageView back_to_gonglve,gongluezhe,tubiaodianzande;
    private boolean isdianzan=false;
    private LinearLayout gonglve_pinglun,dianzanchufa;
    private TextView shoucang,yonghumingzi,gonglve_biaoti,gonglveshijian,dianzanshu_gonglve;
    public static String URL;
    private LinearLayout gonglvexianshi;
    private AsyncTaskHelper taskHelper;
   private String YJGLId,Dianzan,Title,UpData,UserName,UserPT;
    private List<YJGLContent> yjglContents;
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private GoodView mGoodView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        mGoodView = new GoodView(this);
        YJGLId  =intent.getStringExtra("YJGLid");
        Dianzan  =intent.getStringExtra("Dianzan");
        Title  =intent.getStringExtra("Title");
        UpData  =intent.getStringExtra("UpData");
        UserName  =intent.getStringExtra("UserName");
        UserPT  =intent.getStringExtra("UserPT");
    setContentView(R.layout.activuty_gonglve_xiangxi);
        initview();
        gencontent();
        selectdianzanxinxi();
    }
/*
查询是否点过赞
 */
    private void selectdianzanxinxi() {
        if (User.getInstance().islogin() == false) {
            tubiaodianzande.setImageResource(R.drawable.dianzanhui);
        } else {
            MyConfig config = new MyConfig("LikesServlet", "otherlikesinfo");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("YJGLId", YJGLId);
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
                        tubiaodianzande.setImageResource(R.drawable.dianzancheng);
                        isdianzan=true;
                    }
                    if (code.equals("0")) {
                        tubiaodianzande.setImageResource(R.drawable.dianzanhui);
                    }
                }
            };
            taskHelper = new AsyncTaskHelper();
            taskHelper.downloadData(this, URL, downloadListener);
            Log.e("bbbbbbbbbbbbbb",URL);
        }
    }
    /*
    拿取攻略内容
     */
    private void gencontent() {
        MyConfig config = new MyConfig("YJGLContentServlet", "selectallct");
        Map<String, Object> map = new HashMap<String, Object>();
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
                    String Result = map.get("result").toString();
                    yjglContents=JSON.parseArray(Result, YJGLContent.class);
                    LayoutInflater inflater = LayoutInflater.from(GongLveXiangXiActvity.this);
                    for(int i=0;i<yjglContents.size();i++) {
                        TextView tv = (TextView) inflater.inflate(R.layout.layout2, gonglvexianshi, false);

                        tv.setText(yjglContents.get(i).getParagraphText());
                        tv.setTextColor(Color.parseColor("#3194D8"));
                        gonglvexianshi.addView(tv);
                        String pt = yjglContents.get(i).getYJGLContentPT();
                        Boolean b = pt.contains(",");
                        if (b == true) {
                           // StringTokenizer tokener = new StringTokenizer(pt, ",");
                           String[] result1 = pt.split(",");
                           // String[] sArray=pt.Split('c') ;
                             for(int j =0;j<result1.length;j++){
                                 ImageView iv = (ImageView) inflater.inflate(R.layout.layout3, gonglvexianshi, false);
                                 Log.e("xxx",result1[j].toString());
                            Glide.with(getApplication()).load(result1[j])
                                    .into(iv);
                                 gonglvexianshi.addView(iv);
                              }
                        }else {
                            ImageView iv = (ImageView) inflater.inflate(R.layout.layout3, gonglvexianshi, false);
                            Glide.with(getApplication()).load(pt)
                                    .into(iv);
                            gonglvexianshi.addView(iv);
                        }

                    }
                }
                if (code.equals("0")) {
                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(this, URL, downloadListener);
    }



    private void initview() {
        int length = UpData.length();
        UpData = UpData.substring(0,length-11);
        back_to_gonglve= (ImageView) findViewById(R.id.back_to_gonglve);
        tubiaodianzande= (ImageView) findViewById(R.id.tubiaodianzande);
        gongluezhe= (ImageView) findViewById(R.id.gongluezhe);
        Glide.with(getApplication()).load(UserPT)
                .bitmapTransform(new CropCircleTransformation(getApplication()))
                .into(gongluezhe);
        shoucang= (TextView) findViewById(R.id.gonglveshoucang);
        yonghumingzi= (TextView) findViewById(R.id.yonghumingzi);
        yonghumingzi.setText(UserName);
        dianzanshu_gonglve= (TextView) findViewById(R.id.dianzanshu_gonglve);
        dianzanshu_gonglve.setText(Dianzan);
        gonglveshijian= (TextView) findViewById(R.id.gonglveshijian2);
        gonglveshijian.setText(UpData);
        gonglve_biaoti= (TextView) findViewById(R.id.gonglve_biaoti2);
        gonglve_biaoti.setText(Title);
        gonglve_pinglun= (LinearLayout) findViewById(R.id.gonglve_pinglun);
        dianzanchufa= (LinearLayout) findViewById(R.id.dianzanchufa);
        gonglvexianshi= (LinearLayout) findViewById(R.id.gonglvexianshi);
        back_to_gonglve.setOnClickListener(this);
        shoucang.setOnClickListener(this);
        gonglve_pinglun.setOnClickListener(this);
        dianzanchufa.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_to_gonglve:
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("main",2);
                startActivity(intent);
                finish();
                break;
            case R.id.gonglve_pinglun:
                Intent intent1 = new Intent(this,GLReviewActivity.class);
                intent1.putExtra("YJGLId",YJGLId);
                startActivity(intent1);
                break;
            case R.id.gonglveshoucang:
                if(User.getInstance().islogin()==false){
                    startActivity(new Intent(this,LoginRegistActivity.class));
                    finish();
                }
              else if  (User.getInstance().islogin()==true) {
                shoucang1();
            }
                break;
            case R.id.dianzanchufa:
                if(User.getInstance().islogin()==false){
        startActivity(new Intent(this,LoginRegistActivity.class));
                }else {
                    if(isdianzan==false){
                        charu();
                        tubiaodianzande.setImageResource(R.drawable.dianzancheng);
                        mGoodView.setText("+1");
                        mGoodView.show(view);
                        String j=String.valueOf(Integer.valueOf(Dianzan)+1);
                        dianzanshu_gonglve.setText(j);
                        isdianzan=true;
                    }else {
                        Toast.makeText(getApplication(),"您已经点赞过了哟(*^__^*)",Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }
/*
上传点赞信息
 */
    private void charu() {

        MyConfig config = new MyConfig("LikesServlet", "otherlikes");
        Map<String, Object> map = new HashMap<String, Object>();
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
                }
                if (code.equals("0")) {
                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(this, URL, downloadListener);

    }

    /*
    收藏攻略
     */
    private void shoucang1() {

            MyConfig config = new MyConfig("CollectServlet", "addct");
            Map<String, Object> map = new HashMap<String, Object>();
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
                        Toast.makeText(getApplication(), "收藏成功", Toast.LENGTH_SHORT).show();
                    }
                    if (code.equals("0")) {
                        Toast.makeText(getApplication(), "添加收藏失败,你可能已经收藏", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            taskHelper = new AsyncTaskHelper();
            taskHelper.downloadData(this, URL, downloadListener);
        }
}
