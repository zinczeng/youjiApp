package com.example.zx.youjiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AnimationUtils;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.qiniu.android.storage.UploadManager;

public class MainActivity extends AppCompatActivity {
    // tab

    private TabHost tabHost;
    private RadioGroup radiogroup;
    private int menuid;
    public static UploadManager uploadManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menushow();
        //其他activity_跳转fragment
        uploadManager = new UploadManager();
        acti_to_frag();
    }

    private void acti_to_frag() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("main",-1);
        if(id==1){
            //加载fragment
            tabHost.setCurrentTabByTag("youji");
            //设置单选框
            radiogroup.check(R.id.main_youji);
        }
          if(id==2){
            tabHost.setCurrentTabByTag("gonglve");
              radiogroup.check(R.id.main_gonglve);
        }
          if(id==3){
            tabHost.setCurrentTabByTag("zhushou");
              radiogroup.check(R.id.main_gongju);
        }
          if(id==4){
            tabHost.setCurrentTabByTag("user");
              radiogroup.check(R.id.main_yonghu);
        }

    }


    private void menushow() {
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("youji").setIndicator("youji")
                .setContent(R.id.fragment_youji));
        tabHost.addTab(tabHost.newTabSpec("gonglve").setIndicator("gonglve")
                .setContent(R.id.fragment_gonglve));
        tabHost.addTab(tabHost.newTabSpec("zhushou").setIndicator("zhushou")
                .setContent(R.id.fragment_zhushou));
        tabHost.addTab(tabHost.newTabSpec("user").setIndicator("user")
                .setContent(R.id.fragment_user));
        radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                menuid = checkedId;
                int currentTab = tabHost.getCurrentTab();
                switch (checkedId) {
                    case R.id.main_youji:
                        //无动画效果
                        // tabHost.setCurrentTabByTag("youji");
                        //动画翻页效果
                        setCurrentTabWithAnim(currentTab,1, "youji");
                        break;
                    case R.id.main_gonglve:
                        //tabHost.setCurrentTabByTag("gonglve");
                        setCurrentTabWithAnim(currentTab, 2, "gonglve");
                        break;
                    case R.id.main_gongju:
                        //tabHost.setCurrentTabByTag("zhushou");
                        setCurrentTabWithAnim(currentTab, 3, "zhushou");
                        break;
                    case R.id.main_yonghu:
//                        if(UserId==null||PassWord==null){
//                            Intent intent = new Intent(BNMainActivity.this,LoginRegistActivity.class);
//                            startActivity(intent);
//                            break;
//                        }else {
                            // tabHost.setCurrentTabByTag("user");
                            setCurrentTabWithAnim(currentTab, 4, "user");
                            break;
//                        }
                }

            }
        });
    }

    private void setCurrentTabWithAnim(int now, int next, String tag) {
        if (now > next) {
            tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
            tabHost.setCurrentTabByTag(tag);
            tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
        } else {
            tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
            tabHost.setCurrentTabByTag(tag);
            tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        }
    }




}
