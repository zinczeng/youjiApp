package com.sortricheditor.ui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.test.espresso.core.deps.guava.base.Joiner;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.dialog.Dialogs;
import com.example.zx.youjiandroid.GongLveActivity;
import com.example.zx.youjiandroid.R;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.sortricheditor.view.editor.SEditorData;
import com.sortricheditor.view.editor.SortRichEditor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.SuiJi;
import bean.User;
import biaoqian.AddBiaoQianActivity;
import biaoqian.FlowLayout;

public class GLActivity extends AppCompatActivity implements View.OnClickListener {
    private List<SEditorData> list =new ArrayList<>();
    private List<SEditorData> list2 =new ArrayList<>();
    ArrayList<String> list3;
    LayoutInflater mInflater;
    private FlowLayout mFlowLayout;
    public static final int REQUEST_CODE_PICK_IMAGE = 1023;
    public static final int REQUEST_CODE_CAPTURE_CAMEIA = 1022;
    private static List<SuiJi> suiJis;
    private UploadManager uploadManager;
    private boolean next =true;
    private boolean isgl=true;
    public static String URL;
    public static String url;
    public static String YJGLId;
    private AsyncTaskHelper taskHelper;// 网络加载对象
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;// 接口回调对象
    private List<String> l=new ArrayList<>();
    private List<String> upimg_key_list=new ArrayList<>();
    private static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    private String Title=null;
    private String context=null;
    private ProgressDialog pd;
    private TextView tvSort,fanhui_mgl;

    private SortRichEditor editor;

    private ImageView ivGallery, ivCamera;

    private Button btnPosts;

    private File mCurrentPhotoFile;// 照相机拍照得到的图片

    public Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gl);
        list3 = new ArrayList<>();
        pd = new ProgressDialog(this);
        mFlowLayout = (FlowLayout) findViewById(R.id.id_flowlayout);
        tvSort = (TextView) findViewById(R.id.tv_sort);
        fanhui_mgl = (TextView) findViewById(R.id.fanhui_mgl);
        editor = (SortRichEditor) findViewById(R.id.richEditor);
        ivGallery = (ImageView) findViewById(R.id.iv_gallery);
        ivCamera = (ImageView) findViewById(R.id.iv_camera);
        btnPosts = (Button) findViewById(R.id.btn_posts);

        tvSort.setOnClickListener(this);
        fanhui_mgl.setOnClickListener(this);
        ivGallery.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
        btnPosts.setOnClickListener(this);
        mInflater = LayoutInflater.from(this);
        updateData();
    }

    /**
     * 负责处理编辑数据提交等事宜，请自行实现
     */
    private void dealEditData(List<SEditorData> editList) {

        int i;
        for(i=0;i<editList.size();i++) {
            if (editList.get(i).getImagePath() != null || editList.get(i).getInputStr() != null) {
                list.add(editList.get(i));
            }
        }
        for (int j=0;j<list.size();j++) {
            int k=1;
            if(list.get(j).getInputStr()!=null){
                if(context==null){
                    context=list.get(j).getInputStr();
                }else {
                    context = context + "\n" + list.get(j).getInputStr();
                }
            }if(list.get(j).getImagePath()!=null){
                    l.add(list.get(j).getImagePath());
            }
            if(list.size()-1>j) {
                if (list.get(j).getImagePath() != null && list.get(k).getInputStr() != null) {
                    qiniu(l);

                    for(int l=0;i<editList.size()-j;l++) {
                            list2.add(editList.get(l));
                    }
                   break;
                }
            }else {
                qiniu(l);
            }
            k++;
        }
    }

    private void openCamera() {
        try {
            PHOTO_DIR.mkdirs();// 创建照片的存储目录
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
        } catch (ActivityNotFoundException e) {
        }
    }

    /**
     * 用当前时间给取得的图片命名
     */
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK) return;

        if (editor.isSort()) tvSort.setText("排序");
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            String[] photoPaths = data.getStringArrayExtra(PhotoPickerActivity.INTENT_PHOTO_PATHS);
            editor.addImageArray(photoPaths);
        } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            editor.addImage(mCurrentPhotoFile.getAbsolutePath());
        }

        if (data != null) {
            if (requestCode == 1) {
                list3 = data.getStringArrayListExtra("bg");
                updateData();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sort:
                if (editor.sort()) {
                    tvSort.setText("完成");
                } else {
                    tvSort.setText("排序");
                }
                break;
            case R.id.iv_gallery:
                startActivityForResult(new Intent(this, PhotoPickerActivity.class), REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.iv_camera:
                openCamera();
                break;
            case R.id.btn_posts:
                if(editor.getTitleData().equals("")||editor.getContext().equals("")){
                    Toast.makeText(this,"标题和内容都不能为空！",Toast.LENGTH_SHORT).show();
                }else {
                    if(list3.size()!=0){
                    Title=editor.getTitleData();
                  pd = ProgressDialog.show(GLActivity.this, "上传攻略", "上传中，图片上传耗时请稍等……");
                    List<SEditorData> editList = editor.buildEditData();
                    // 下面的代码可以上传、或者保存，请自行实现
                    dealEditData(editList);}
                    else {
                        final  AlertDialog.Builder normalDialog =
                                new AlertDialog.Builder(GLActivity.this);
                        normalDialog.setTitle("提示：添加标签更容易被搜索");
                        normalDialog.setMessage("是否继续上传");
                        normalDialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pd = ProgressDialog.show(GLActivity.this, "上传攻略", "上传中，图片上传耗时请稍等……");
                                        List<SEditorData> editList = editor.buildEditData();
                                        // 下面的代码可以上传、或者保存，请自行实现
                                        dealEditData(editList);
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
                break;
            case R.id.fanhui_mgl:
                Dialogs.showNormalDialog(GLActivity.this,"提示","你确定要放弃编辑吗");
                break;
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法

            upglcontent();
        }
    };

    private void qiniu(final List imagePath){
        new Thread() {
            public void run() {
                next=true;
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
                                    up_gl();
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
/*
上传攻略
 */
    private void up_gl() {
        if(isgl==true){
            MyConfig config = new MyConfig("YJGLServlet", "addyl");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("UserId", User.getInstance().getUserId());
            map.put("Title", Title);
            map.put("Types", 1);
            map.put("ContentPt1", url);
            map.put("Content1", context);
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
                        for(int m=0;m<list3.size();m++) {
                            addbiaoqian(list3.get(m));
                        }
                    }
                }
            };
            taskHelper = new AsyncTaskHelper();
            taskHelper.downloadData(getApplicationContext(), URL, downloadListener);
            isgl=false;
        }
    }

    private void addbiaoqian(String tag) {

        MyConfig config = new MyConfig("TagServlet", "addtg");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("TagName",tag);
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

    private void upglcontent() {
     String   pt = Joiner.on(",").join(upimg_key_list);
        MyConfig config = new MyConfig("YJGLContentServlet", "addContent");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("YJGLId", YJGLId);
        map.put("ParagraphText", context);
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
                    next=false;
                    l=new ArrayList<>();
                    context=null;
                    if(list2.size()!=0) {
                        dealEditData(list2);
                        list2=new ArrayList<>();
                    }else{
                        pd.dismiss();// 关闭ProgressDialog
                        startActivity(new Intent(GLActivity.this, GongLveActivity.class));
                        finish();
                    }
                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(getApplicationContext(), URL, downloadListener);
    }

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
    private void updateData() {
        mFlowLayout.removeAllViews();
        for (int i = 0; i < list3.size(); i++) {
            TextView view = (TextView) mInflater.inflate(R.layout.item_flow, mFlowLayout, false);
            view.setText(list3.get(i));
            mFlowLayout.addView(view);
        }

        ImageView iv = (ImageView) mInflater.inflate(R.layout.add_item_flow, mFlowLayout, false);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(GLActivity.this, AddBiaoQianActivity.class).putStringArrayListExtra("bq", list3).putExtra("Activity","GLActivity"), 1);
            }
        });
        mFlowLayout.addView(iv);

    }



}