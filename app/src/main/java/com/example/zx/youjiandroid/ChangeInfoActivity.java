package com.example.zx.youjiandroid;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bean.User;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.zx.youjiandroid.MainActivity.uploadManager;

/**
 * Created by Administrator on 2017/12/8.
 */

public class ChangeInfoActivity extends Activity implements View.OnClickListener {
    private ImageView bianjijieshu, touxiang;
    private TextView baocunxiugai;
    private RadioGroup radiogroup2;
    private EditText xiugainame, xiugaiqianming;
    public static String touxiang1 = User.getInstance().getUserPT();
    public static String URL;
    public static String url;//图片七牛地址
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private final int IMAGE_CODE = 0; // 这里的IMAGE_CODE是自己任意定义的
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    public static String xinbie = User.getInstance().getSex();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
        initview();
    }

    private void initview() {
        bianjijieshu = (ImageView) findViewById(R.id.bianjijieshu);
        touxiang = (ImageView) findViewById(R.id.touxiang);
        Glide.with(getApplicationContext()).load(User.getInstance().getUserPT())
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .into(touxiang);
        baocunxiugai = (TextView) findViewById(R.id.baocunxiugai);
        radiogroup2 = (RadioGroup) findViewById(R.id.radiogroup2);
        if (User.getInstance().getSex().equals("0")) {
            radiogroup2.check(R.id.nan);
        } else if (User.getInstance().getSex().equals("1")) {
            radiogroup2.check(R.id.nv);
        }
        xiugainame = (EditText) findViewById(R.id.xiugainame);
        xiugainame.setText(User.getInstance().getUserName());
        xiugaiqianming = (EditText) findViewById(R.id.xiugaiqianming);
        xiugaiqianming.setText(User.getInstance().getSign());
        bianjijieshu.setOnClickListener(this);
        touxiang.setOnClickListener(this);
        baocunxiugai.setOnClickListener(this);


        radiogroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.nan:
                        xinbie = "0";
                        break;
                    case R.id.nv:
                        xinbie = "1";
                        break;
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bianjijieshu:
                finish();
                break;
            case R.id.touxiang:
                setuserpt();
                break;
            case R.id.baocunxiugai:
                if (xiugainame.getText().toString().equals("")) {
                    Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
                } else {

                    MyConfig config = new MyConfig("UserServlet", "updateinfo");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("UserId", User.getInstance().getUserId());
                    map.put("UserName", xiugainame.getText().toString());
                    map.put("UserPT", touxiang1);
                    map.put("Sex", xinbie);
                    map.put("Sign", xiugaiqianming.getText().toString());
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
                                Toast.makeText(ChangeInfoActivity.this, "修改成功！",
                                        Toast.LENGTH_SHORT).show();
                                User.getInstance().setUserPT(touxiang1);
                                User.getInstance().setSex(xinbie);
                                User.getInstance().setSign(xiugaiqianming.getText().toString());
                                User.getInstance().setUserName(xiugainame.getText().toString());
                                Intent intent = new Intent(ChangeInfoActivity.this, MainActivity.class);
                                intent.putExtra("main", 4);
                                startActivity(intent);
                            } else if (code.equals("0")) {
                                Toast.makeText(ChangeInfoActivity.this, "修改失败！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    taskHelper = new AsyncTaskHelper();
                    taskHelper.downloadData(this, URL, downloadListener);


                }
                break;
        }
    }


    private void setuserpt() {
        Intent getAlbum = new Intent(Intent.ACTION_PICK);

        getAlbum.setType(IMAGE_UNSPECIFIED);

        startActivityForResult(getAlbum, IMAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bm = null;

        // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口

        ContentResolver resolver = getApplication().getContentResolver();

        if (requestCode == IMAGE_CODE) {

            try {

                Uri originalUri = data.getData(); // 获得图片的uri
                Log.e("sssssssssssssssss", originalUri + "");
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                touxiang.setImageBitmap(ThumbnailUtils.extractThumbnail(bm,
                        100, 100)); // 使用系统的一个工具类，参数列表为 Bitmap Width,Height
                // 这里使用压缩后显示，否则在华为手机上ImageView 没有显示
                // 显得到bitmap图片
                // imageView.setImageBitmap(bm);
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor;
                // 好像是android多媒体数据库的封装接口，具体的看Android文档
                CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), originalUri, null, null,
                        null, null);
                cursor = cursorLoader.loadInBackground();
                // 按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                // 最后根据索引值获取图片路径
                String path = cursor.getString(column_index);
                //  tv.setText(path);

                String key = null;
                // token是每个注册账号可以建立多个，用“QiNiuGenertorToken”这个项目去生成自己的token
                String token = "6WhBXBouf6QghARZBLJiPaEgV3dclqjbMHwdJ15R:jRELqCfbbhaY8R0sIVVAWtiqclo=:eyJzY29wZSI6Ind1eHVhbmJhY2tldCIsImRlYWRsaW5lIjozMjg0NTg4MTY3fQ==";
                Log.e("ss", token);
                uploadManager.put(byteArray, key, token, new UpCompletionHandler() {

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
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (headerUrl != null && !headerUrl.equals("")) {
                                touxiang1 = url;

                                //圆头像
                                Glide.with(getApplicationContext()).load(touxiang1)
                                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                        .into(touxiang);
                            } else {
                                Toast.makeText(getApplicationContext(), "上传失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                }, null);

            } catch (IOException e) {
                Log.e("TAG-->Error", e.toString());

            } finally {
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
