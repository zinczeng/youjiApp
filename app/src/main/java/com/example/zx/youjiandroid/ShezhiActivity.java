package com.example.zx.youjiandroid;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import bean.User;

/**
 * Created by Administrator on 2017/12/8.
 */

public class ShezhiActivity  extends FragmentActivity implements View.OnClickListener {
    private ImageView shezhijieshu;
    private LinearLayout xiugaizhiliao,yijianfankui,xiugaimima,lianxizuozhe,shezhi;
    private TextView tuichuzhanghao;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shezhi);
        initview();
    }

    private void initview() {

        shezhijieshu = (ImageView) findViewById(R.id.shezhijieshu);
        xiugaizhiliao = (LinearLayout) findViewById(R.id.xiugaizhiliao);
        yijianfankui = (LinearLayout) findViewById(R.id.yijianfankui);
        xiugaimima = (LinearLayout) findViewById(R.id.xiugaimima);
        lianxizuozhe = (LinearLayout) findViewById(R.id.lianxizuozhe);
        shezhi = (LinearLayout) findViewById(R.id.shezhi);
        tuichuzhanghao = (TextView) findViewById(R.id.tuichuzhanghao);
        shezhijieshu.setOnClickListener(this);
        xiugaizhiliao.setOnClickListener(this);
        yijianfankui.setOnClickListener(this);
        xiugaimima.setOnClickListener(this);
        lianxizuozhe.setOnClickListener(this);
        tuichuzhanghao.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shezhijieshu:
                finish();
                break;
            case R.id.xiugaizhiliao:
                if(User.getInstance().islogin()==false){
                    Toast.makeText(this,"抱歉，你还未登陆", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(this, ChangeInfoActivity.class));
                }
                break;
            case R.id.yijianfankui:

                    startActivity(new Intent(this, FanKuiActivity.class));

                break;
            case R.id.xiugaimima:
                if(User.getInstance().islogin()==false){
                    Toast.makeText(this,"抱歉，你还未登陆", Toast.LENGTH_SHORT).show();
                }else {
                startActivity(new Intent(this,ChangepwActivity.class));
                }
                break;
            case R.id.lianxizuozhe:
                if (checkApkExist(this,"com.tencent.mobileqq")){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="+"1217072253"+"&version=1")));
                }else{
                    Toast.makeText(this,"本机未安装QQ应用", Toast.LENGTH_SHORT).show();
                }
            break;
            case R.id.tuichuzhanghao:
                if( User.getInstance().islogin()==true) {
                    User.getInstance().setIslogin(false);
                    Toast.makeText(this, "退出当前账号成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ShezhiActivity.this, MainActivity.class);
                    intent.putExtra("main", 1);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "你还未登录", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
/*
判断是否安装QQ
 */
    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


}
