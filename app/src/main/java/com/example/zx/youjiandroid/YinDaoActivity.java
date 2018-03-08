package com.example.zx.youjiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.iflytek.YouDao.CreatApplication;
import com.yingdao.ViewPagerAdapter;
import com.yingdao.fragment.Fragment1;
import com.yingdao.fragment.Fragment2;
import com.yingdao.fragment.Fragment3;

import java.util.ArrayList;
import java.util.List;

public class YinDaoActivity extends FragmentActivity {

    private ViewPager viewPager;
    private List<View> listImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CreatApplication.preferences.getString("YJYinDao","2").equals("1")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent, 1);
        } else {
            setContentView(R.layout.yindao);
            initView();
        }


    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), showView()));
        listImg = new ArrayList<View>();
        listImg.add(findViewById(R.id.y1));
        listImg.add(findViewById(R.id.y2));
        listImg.add(findViewById(R.id.y3));
        viewPager.setOnPageChangeListener(showPageChange);
    }


    ViewPager.OnPageChangeListener showPageChange = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < listImg.size(); i++) {
                if (i == arg0) {
                    listImg.get(arg0).setBackgroundResource(R.drawable.y_focused);
                } else {
                    listImg.get(i).setBackgroundResource(R.drawable.y_normal);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                finish();
            }
        }
    }


    private List<Fragment> showView() {
        List<Fragment> listView = new ArrayList<Fragment>();
        listView.add(new Fragment1());
        listView.add(new Fragment2());
        listView.add(new Fragment3());
        return listView;
    }
}
