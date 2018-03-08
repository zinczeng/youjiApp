package com.example.zx.youjiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;

import java.util.HashMap;
import java.util.Map;

import bean.User;

/**
 * Created by Administrator on 2017/12/8.
 */

public class ChangepwActivity extends AppCompatActivity implements View.OnClickListener {
    public static String URL;
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    private EditText mima1, mima2;
    private TextView tijiao;
    private ImageView fanhui;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepw);
        initview();
    }

    private void initview() {
        mima1 = (EditText) findViewById(R.id.xiugai1);
        mima2 = (EditText) findViewById(R.id.xiugai2);
        tijiao = (TextView) findViewById(R.id.sure_gaimi);
        fanhui = (ImageView) findViewById(R.id.xiugaijieshu);
        fanhui.setOnClickListener(this);
        tijiao.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.xiugaijieshu:
                finish();
                break;
            case R.id.sure_gaimi:
                if (mima1.getText().toString().equals("") || mima2.getText().toString().equals("")) {
                    Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                }  if (!mima1.getText().toString().equals("") &&!mima2.getText().toString().equals("")) {
                    String gaimi = mima1.getText().toString();
                    String gaimi2 = mima2.getText().toString();
                    if (gaimi.equals(gaimi2)) {
                        MyConfig config = new MyConfig("UserServlet", "updatepw");
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("UserId", User.getInstance().getUserId());
                        map.put("UserPW", gaimi);
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
                                    Toast.makeText(ChangepwActivity.this, "修改成功！",
                                            Toast.LENGTH_SHORT).show();
                                    User.getInstance().setIslogin(false);
                                    startActivity(new Intent(ChangepwActivity.this, LoginRegistActivity.class));
                                } else if (code.equals("0")) {
                                    Toast.makeText(ChangepwActivity.this, "修改失败！",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        };
                        taskHelper = new AsyncTaskHelper();
                        taskHelper.downloadData(this, URL, downloadListener);
                    } else {
                        Toast.makeText(this, "两次密码不一致！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
