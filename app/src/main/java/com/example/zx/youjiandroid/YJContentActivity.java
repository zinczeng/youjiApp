package com.example.zx.youjiandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.test.espresso.core.deps.guava.base.Joiner;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.dialog.Dialogs;
import com.example.zx.gridimageview.GridImageView;
import com.example.zx.gridimageview.GridImageViewAdapter;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.SuiJi;
import bean.User;
import biaoqian.FlowLayout;

/**
 * Created by Administrator on 2017/11/20.
 */

public class YJContentActivity extends AppCompatActivity implements View.OnClickListener {
    private GridImageView<String> mGridImageView;
private static String creat ;
    private TextView shownan,next_nwancheng;
    private ImageView back_to_biaoti;
    private EditText youjiwenzi;
    private static List<SuiJi> suiJis;
    private ProgressDialog pd;
    ArrayList<String> list3;
    private FlowLayout mFlowLayout;
    private List<String> l;
    private List<String> upimg_key_list;
    private String Title,pt1,pt,YJGLId,Content;
    public static String URL;
    private UploadManager uploadManager;
    LayoutInflater mInflater;
    public static String url;
    private AsyncTaskHelper taskHelper;// 网络加载对象
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;// 接口回调对象

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Title=intent.getStringExtra("Title");
        setContentView(R.layout.activity_yj_content);
        creat=getIntent().getStringExtra("creat");
        pd = new ProgressDialog(this);
        pd .setCancelable(false);

        upimg_key_list=new ArrayList<>();
        l=new ArrayList<>();
        initview();

        showchage();
    }

    private void initview() {
//        mFlowLayout = (FlowLayout) findViewById(R.id.d_flowlayout);
        youjiwenzi = (EditText) findViewById(R.id.youjiwenzi);
        shownan = (TextView) findViewById(R.id.shownan);
        back_to_biaoti = (ImageView) findViewById(R.id.back_to_biaoti);
        back_to_biaoti.setOnClickListener(this);
//        mInflater = LayoutInflater.from(this);
        mGridImageView= (GridImageView<String>) this.findViewById(R.id.gridImageView);
        mGridImageView.setAdapter(new GridImageViewAdapter<String>(){
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, String path) {
                Picasso.with(context).load("file://"+path).centerCrop().resize(400,400).into(imageView);
            }

            @Override
            protected void onAddClick(Context context, List<String> list) {
                Intent intent=new Intent(YJContentActivity.this,SelectorActivity.class);
                startActivityForResult(intent,1234);
            }

            @Override
            protected int getShowStyle() {
                return GridImageView.STYLE_GRID;
            }

            @Override
            protected void onItemImageClick(Context context, int index, List<String> list) {
                super.onItemImageClick(context, index, list);
                Toast.makeText(getApplicationContext(),"--->"+index,Toast.LENGTH_SHORT).show();
            }
        } );
    }
    /*
实现文字还剩余多少字可添加
 */
    private void showchage() {

        youjiwenzi.addTextChangedListener(new TextWatcher() {
            private   int num =255;
            private CharSequence temp;

            private int selectionStart;

            private int selectionEnd;

            @Override

            public void onTextChanged(CharSequence s, int start, int before,

                                      int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,

                                          int after) {

                temp = s;

            }

            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub

                int number = num - s.length();

                shownan.setText("可输入 " + "" + number + " 字");

                selectionStart = youjiwenzi.getSelectionStart();

                selectionEnd = youjiwenzi.getSelectionEnd();

                if (temp.length() > num) {

                    s.delete(selectionStart - 1, selectionEnd);

                    int tempSelection = selectionEnd;

                    youjiwenzi.setText(s);

                    youjiwenzi.setSelection(tempSelection);// 设置光标在最后

                }

            }

        });

    }

    public void wancheng(View view) {
        Content = youjiwenzi.getText().toString();
        if (creat.equals("0")) {
            if (Content.equals("")) {
                Toast.makeText(getApplication(), "内容不能为空哦", Toast.LENGTH_SHORT).show();
            } else {
                if (l.size() != 0) {
                    pd = ProgressDialog.show(YJContentActivity.this, "上传游记", "上传中，图片上传耗时请稍等……");
                    qiniu(l);
                } else {
                    Content = youjiwenzi.getText().toString();
                    pt1 = "http://oibc7l5du.bkt.clouddn.com/yj.jpg";
                    MyConfig config = new MyConfig("YJGLServlet", "addyl");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("UserId", User.getInstance().getUserId());
                    map.put("Title", Title);
                    map.put("Types", 0);
                    map.put("ContentPt1", pt1);
                    map.put("Content1", Content);
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
                                String Result = map.get("result").toString();
                                suiJis = JSON.parseArray(Result, SuiJi.class);
                                YJGLId = suiJis.get(0).getyJGLId();
                                charu();
                                upcontent();
                            }
                        }
                    };
                    taskHelper = new AsyncTaskHelper();
                    taskHelper.downloadData(getApplicationContext(), URL, downloadListener);

                }
            }
        } else if(creat.equals("1")){
            YJGLId=getIntent().getStringExtra("YJGLId");
            if (Content.equals("")) {
                Toast.makeText(getApplication(), "内容不能为空哦", Toast.LENGTH_SHORT).show();
            } else {
                if (l.size() != 0) {
                    pd = ProgressDialog.show(YJContentActivity.this, "上传游记", "上传中，图片上传耗时请稍等……");
                    qiniu(l);
                } else {
                    upcontent();
                }
            }
        }
    }
    private void upcontent() {

        MyConfig config = new MyConfig("YJGLContentServlet", "addContent");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("YJGLId", YJGLId);
        map.put("ParagraphText", Content);
        map.put("YJGLContentPT", pt);
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

                    Intent intent =new Intent(getApplication(),YoujiInfoActivity.class);
                    intent.putExtra("YJGLId",YJGLId);
                    intent.putExtra("Title",Title);
                    startActivity(intent);
                    finish();
                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(getApplicationContext(), URL, downloadListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1) {
                list3 = data.getStringArrayListExtra("bg");
//                updateData();
            }
        }

        switch (requestCode) {
            case 1234:
                List<String> list = data.getStringArrayListExtra("list");
                //  PictureUtil.cropPhoto(this, Uri.parse("file://"+list.get(0)));
                mGridImageView.setImageData(list,false);
                 l=mGridImageView.getImgDataList();

                break;

        }
    }

  private void qiniu(final List imagePath){
      new Thread() {
          public void run() {

              for(int i=0;i<imagePath.size();i++){
                  // 图片上传到七牛 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
                  uploadManager = new UploadManager();
                  String key = null;
                  // token是每个注册账号可以建立多个，用“QiNiuGenertorToken”这个项目去生成自己的token
                  String token = "6WhBXBouf6QghARZBLJiPaEgV3dclqjbMHwdJ15R:jRELqCfbbhaY8R0sIVVAWtiqclo=:eyJzY29wZSI6Ind1eHVhbmJhY2tldCIsImRlYWRsaW5lIjozMjg0NTg4MTY3fQ==";
                  uploadManager.put(imagePath.get(i).toString(), key, token, new UpCompletionHandler() {

                      @Override
                      public void complete(String s, ResponseInfo responseInfo,
                                           JSONObject jsonObject) {

                          // TODO Auto-generated method stub
                          System.out.println("complete");
                          if (responseInfo.statusCode == 200) {
                              String headerUrl = "";
                              try {
                                  headerUrl = jsonObject.getString("key");// 得到回传的头像url
                                  System.out.println(headerUrl);
                                  url = "http://oibc7l5du.bkt.clouddn.com/" + headerUrl;
                                  upimg_key_list.add(url);//将七牛返回图片的文件名添加到list集合中
                                  if(upimg_key_list.size()==l.size()) {
                                      handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
                                  }
                              } catch (JSONException e) {
                                  e.printStackTrace();
                              }
                          }
                      }
                  }, null);
              }


              }

      }.start();

  }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_to_biaoti:
                Dialogs.showNormalDialog(YJContentActivity.this,"提示","你确定要放弃编辑吗");
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog

            Content = youjiwenzi.getText().toString();
            if (creat.equals("0")) {
                if (Content.equals("")) {
                    Toast.makeText(getApplication(), "内容不能为空哦", Toast.LENGTH_SHORT).show();
                }else
                if(!Content.equals("")) {
                    pt1 = upimg_key_list.get(0);
                    pt = Joiner.on(",").join(upimg_key_list);
                    MyConfig config = new MyConfig("YJGLServlet", "addyl");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("UserId", User.getInstance().getUserId());
                    map.put("Title", Title);
                    map.put("Types", 0);
                    map.put("ContentPt1", pt1);
                    map.put("Content1", Content);
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
                                String Result = map.get("result").toString();
                                suiJis = JSON.parseArray(Result, SuiJi.class);
                                YJGLId = suiJis.get(0).getyJGLId();
                                charu();
                                upcontent();
                            }
                        }
                    };
                    taskHelper = new AsyncTaskHelper();
                    taskHelper.downloadData(getApplicationContext(), URL, downloadListener);

                }
            }else if(creat.equals("1")){
                YJGLId =getIntent().getStringExtra("YJGLId");
                pt = Joiner.on(",").join(upimg_key_list);
                if (Content.equals("")) {
                    Toast.makeText(getApplication(), "内容不能为空哦", Toast.LENGTH_SHORT).show();
                }else {
                    upcontent();
                }
            }
        }


    };
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
//    private void updateData() {
//        mFlowLayout.removeAllViews();
//        for (int i = 0; i < list3.size(); i++) {
//            TextView view = (TextView) mInflater.inflate(R.layout.item_flow, mFlowLayout, false);
//            view.setText(list3.get(i));
//            mFlowLayout.addView(view);
//        }
//
//        ImageView iv = (ImageView) mInflater.inflate(R.layout.add_item_flow, mFlowLayout, false);
//        iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                startActivityForResult(new Intent(YJContentActivity.this, AddBiaoQianActivity.class).putStringArrayListExtra("bq", list3).putExtra("Activity","YJContentActivity"), 1);
//            }
//        });
//        mFlowLayout.addView(iv);
//
//    }
}
