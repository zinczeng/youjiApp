package com.example.zx.youjiandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zx.Fragment.FragmentOne;
import com.example.zx.Fragment.FragmentThree;
import com.example.zx.Fragment.FragmentTwo;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.wx.goodview.GoodView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import bean.User;
import bean.YJGLContent;


/**
 * Created by clevo on 2015/7/30.
 */
public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    private LinearLayout frontView, bottomView;
    private RollPagerView youjitupian;
    private FloatingActionButton fab;
    private ImageView youji_userpt2,youji_dianzan;
    private AnimatorSet showAnim,hiddenAnim;
    private EditText youjipinglunneirong;
    private boolean isdianzan=false;
    public static String[] Urls=new String[20];
    private List<YJGLContent> yjglContents;
    private long fWidth,fHeight, bHeight;
    private TextView tvCloseBottom,youjibiaoti,youjipinglunfasong,shoucang_youji;
    private String YJGLId,Dianzan,Title,UpData,UserName,UserPT,neirong,UserId;
    private static String URL2;
     private  GoodView mGoodView;
    private LruCache<String,Bitmap> mLruCaches;    //使用LruCahe缓存，可以节省流量，速度也快
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        mGoodView = new GoodView(this);
        final Intent intent=getIntent();
        YJGLId  =intent.getStringExtra("YJGLid");
        Dianzan  =intent.getStringExtra("Dianzan");
        Title  =intent.getStringExtra("Title");
        UpData  =intent.getStringExtra("UpData");
        UserName  =intent.getStringExtra("UserName");
        UserPT  =intent.getStringExtra("UserPT");
        UserId  =intent.getStringExtra("UserId");
        youjibiaoti = (TextView) findViewById(R.id.youjibiaoti);
        Toolbar tb= (Toolbar) findViewById(R.id.tb_detail );
        tb.setNavigationIcon(R.mipmap.ic_arrow_back_white);
            tb.setTitle("");
        youjibiaoti.setText(Title);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
                intent1.putExtra("main",1);
                finish();
            }
        });
        youjipinglunneirong = (EditText) findViewById(R.id.youjipinglunneirong);
        youji_dianzan = (ImageView) findViewById(R.id.youji_dianzan);
        youjipinglunfasong = (TextView) findViewById(R.id.youjipinglunfasong);
        shoucang_youji = (TextView) findViewById(R.id.shoucang_youji);
        youji_dianzan.setOnClickListener(this);
        youjipinglunfasong.setOnClickListener(this);
        shoucang_youji.setOnClickListener(this);
        youjitupian = (RollPagerView) findViewById(R.id.youjitupian);


        //设置缓存
        int maxMemory= (int) Runtime.getRuntime().maxMemory();
        int cacheSize=maxMemory/4;
        mLruCaches=new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        tvCloseBottom= (TextView) findViewById(R.id.tv_close_bottom);
        tvCloseBottom.setOnClickListener(this);

        ViewPager viewPager= (ViewPager) findViewById(R.id.view_pager_detail );
        MyPagerAdapter adapter=new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout= (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        selectdianzanxinxi();
        initView();
        gencontent();
    }

    /*
    查询是否点过赞
     */
    private void selectdianzanxinxi() {
        if (User.getInstance().islogin() == false) {
            youji_dianzan.setImageResource(R.drawable.dianzan_huise);
        } else {
            MyConfig config = new MyConfig("LikesServlet", "otherlikesinfo");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("YJGLId", YJGLId);
            map.put("UserId", User.getInstance().getUserId());
            String str = JSON.toJSONString(map);
            try {
                URL2 = config.getURL() + str;
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
                        youji_dianzan.setImageResource(R.drawable.dianzan_huang);
                        isdianzan=true;
                    }
                    if (code.equals("0")) {
                        youji_dianzan.setImageResource(R.drawable.dianzan_huise);
                    }
                }
            };
            taskHelper = new AsyncTaskHelper();
            taskHelper.downloadData(this, URL2, downloadListener);

        }
    }
    /*
    图片轮播
     */
    class  ImageNormalAdapter extends StaticPagerAdapter{
        private String urls[] ;


        public ImageNormalAdapter(String Urls[]) {
                this.urls=Urls;
        }

    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        view.setImageResource(R.drawable.jiazai);   //先加载一个默认的图片，当网络不给力时，有个过度效果
        Bitmap bitmap=getBitmapFromCache(urls[position]);   //先从缓存中读取图片
        if(bitmap!=null){
            view.setImageBitmap(bitmap);  //如果缓存中有图片，就直接放上去行了
        }else {
            //缓存中没有，只要去异步下载了
            Log.e("1","");
            AddbBitmapToView addbBitmapToView=new AddbBitmapToView(view);
            addbBitmapToView.execute(urls[position]);
        }
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    public int getCount() {
       return urls==null?0:urls.length;
    }

}
    //这里使用Asyn异步的方式加载网络图片
    class AddbBitmapToView extends AsyncTask<String,Void,Bitmap> {
        private ImageView imageView;
        public AddbBitmapToView(ImageView  imageView){
            this.imageView=imageView;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap);
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap=getPicture(params[0]);
            if(bitmap!=null){
                addBitmapToCache(params[0],bitmap);
            }
            return bitmap;
        }
    }
    //将图片增加到缓存中
    private void addBitmapToCache(String url,Bitmap bitmap){
        if(getBitmapFromCache(url)==null){   //如果没有缓存，则增加到缓存中
            mLruCaches.put(url,bitmap);
            Log.e("2","");
        }
    }
    //从缓存中获取图片
    private Bitmap getBitmapFromCache(String url){
        return mLruCaches.get(url);
    }
    //网络地址获取图片
    private Bitmap getPicture(String path){
        Bitmap bm=null;
        InputStream is;
        try{
            URL url=new URL(path);
            URLConnection connection=url.openConnection();
            connection.connect();
            is=connection.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;//图片宽高都为原来的二分之一，即图片为原来的四分之一
            bm= BitmapFactory.decodeStream(is,null,options);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }

    private void initView() {
        frontView = (LinearLayout) findViewById(R.id.front);
        ViewTreeObserver vto= frontView.getViewTreeObserver();//view事件的观察者。要注意的是它的初始化就是调用View.getViewTreeObserver()。


        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                frontView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //但是需要注意的是OnGlobalLayoutListener可能会被多次触发，因此在得到了高度之后，要将OnGlobalLayoutListener注销掉。
                fWidth = frontView.getMeasuredWidth();
                fHeight = frontView.getMeasuredHeight();
            }
        });
        bottomView = (LinearLayout) findViewById(R.id.bottom );
        ViewTreeObserver sVto= bottomView.getViewTreeObserver();
        sVto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bottomView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //但是需要注意的是OnGlobalLayoutListener可能会被多次触发，因此在得到了高度之后，要将OnGlobalLayoutListener注销掉。
                bHeight = bottomView.getMeasuredHeight();
                initShowAnim();
                initHiddenAnim();
            }
        });

    }

    private void initShowAnim(){
        ObjectAnimator fViewScaleXAnim=ObjectAnimator.ofFloat(frontView,"scaleX",1.0f,0.8f);
        fViewScaleXAnim.setDuration(350);
        ObjectAnimator fViewScaleYAnim=ObjectAnimator.ofFloat(frontView,"scaleY",1.0f,0.8f);
        fViewScaleYAnim.setDuration(350);
        ObjectAnimator fViewAlphaAnim=ObjectAnimator.ofFloat(frontView,"alpha",1.0f,0.5f);
        fViewAlphaAnim.setDuration(350);
        ObjectAnimator fViewRotationXAnim = ObjectAnimator.ofFloat(frontView, "rotationX", 0f, 10f);
        fViewRotationXAnim.setDuration(200);
        ObjectAnimator fViewResumeAnim = ObjectAnimator.ofFloat(frontView, "rotationX", 10f, 0f);
        fViewResumeAnim.setDuration(150);
        fViewResumeAnim.setStartDelay(200);
        ObjectAnimator fViewTransYAnim=ObjectAnimator.ofFloat(frontView,"translationY",0,-0.1f* fHeight);
        fViewTransYAnim.setDuration(350);
        ObjectAnimator sViewTransYAnim=ObjectAnimator.ofFloat(bottomView,"translationY", bHeight,0);
        sViewTransYAnim.setDuration(350);
        sViewTransYAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                youji_dianzan.setVisibility(View.INVISIBLE);
                bottomView.setVisibility(View.VISIBLE);
            }
        });
        showAnim=new AnimatorSet();
        showAnim.playTogether(fViewScaleXAnim,fViewRotationXAnim,fViewResumeAnim,fViewTransYAnim,fViewAlphaAnim,fViewScaleYAnim,sViewTransYAnim);
    }

    private void initHiddenAnim(){
        ObjectAnimator fViewScaleXAnim=ObjectAnimator.ofFloat(frontView,"scaleX",0.8f,1.0f);
        fViewScaleXAnim.setDuration(350);
        ObjectAnimator fViewScaleYAnim=ObjectAnimator.ofFloat(frontView,"scaleY",0.8f,1.0f);
        fViewScaleYAnim.setDuration(350);
        ObjectAnimator fViewAlphaAnim=ObjectAnimator.ofFloat(frontView,"alpha",0.5f,1.0f);
        fViewAlphaAnim.setDuration(350);
        ObjectAnimator fViewRotationAnim = ObjectAnimator.ofFloat(frontView, "rotationX",0f, 10f);
        fViewRotationAnim.setDuration(150);
        ObjectAnimator fViewResumeAnim = ObjectAnimator.ofFloat(frontView, "rotationX",10f, 0f);
        fViewResumeAnim.setDuration(200);
        fViewResumeAnim.setStartDelay(150);
        ObjectAnimator fViewTransYAnim=ObjectAnimator.ofFloat(frontView,"translationY",-fHeight *0.1f,0);
        fViewTransYAnim.setDuration(350);
        ObjectAnimator sViewTransYAnim=ObjectAnimator.ofFloat(bottomView,"translationY",0, bHeight);
        sViewTransYAnim.setDuration(350);
        sViewTransYAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bottomView.setVisibility(View.INVISIBLE);
             //   youji_userpt2.setVisibility(View.VISIBLE);
                youji_dianzan.setVisibility(View.VISIBLE);
            }
        });
        hiddenAnim=new AnimatorSet();
        hiddenAnim.playTogether(fViewScaleXAnim, fViewAlphaAnim,fViewRotationAnim,fViewResumeAnim, fViewScaleYAnim,fViewTransYAnim, sViewTransYAnim);
        hiddenAnim.setDuration(350);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.youjipinglunfasong:
                neirong = youjipinglunneirong.getText().toString();
                if (User.getInstance().islogin() == false) {
                    startActivity(new Intent(this, LoginRegistActivity.class));
                    finish();
                } else {
                    if (neirong.equals("")) {
                        Toast.makeText(getBaseContext(), "请先评论哦", Toast.LENGTH_SHORT).show();
                    } else if (!neirong.equals("")) {
                        review();
                    }
                }
                break;
            case R.id.fab:
                showAnim.start();
                break;
            case R.id.tv_close_bottom:
                hiddenAnim.start();

                break;
            case R.id.shoucang_youji:
                if (User.getInstance().islogin() == false) {
                    startActivity(new Intent(this, LoginRegistActivity.class));
                } else {
                    shoucang1();
                }
                break;
            case R.id.youji_dianzan:
                if (User.getInstance().islogin() == false) {
                    startActivity(new Intent(this, LoginRegistActivity.class));
                } else {
                    if (isdianzan == false) {
                        charu();
                        youji_dianzan.setImageResource(R.drawable.dianzan_huang);
                        mGoodView.setText("+1");
                        mGoodView.show(v);
                        isdianzan = true;
                    } else {
                        Toast.makeText(getApplication(), "您已经点赞过了哟(*^__^*)", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
        }
    }
/*
插入点赞信息
 */

    private void charu() {
        MyConfig config = new MyConfig("LikesServlet", "otherlikes");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("UserId", User.getInstance().getUserId());
        map.put("YJGLId", YJGLId);
        String str = JSON.toJSONString(map);
        try {
            URL2 = config.getURL() + str;
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
        taskHelper.downloadData(this, URL2, downloadListener);

    }
/*
评论
 */
    private void review() {
        MyConfig config = new MyConfig("ReviewServlet", "addrw");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Content", neirong);
        map.put("UserId", User.getInstance().getUserId());
        map.put("YJGLId", YJGLId);
        String str = JSON.toJSONString(map);
        try {
            URL2 = config.getURL() + str;
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
                    Toast.makeText(getApplication(),"评论成功",Toast.LENGTH_LONG).show();
                    hiddenAnim.start();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(youjipinglunneirong.getWindowToken(),0);
                }
                if(code.equals("0")){

                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(this, URL2, downloadListener);
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
            URL2 = config.getURL() + str;
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
                    hiddenAnim.start();
                }
                if (code.equals("0")) {
                    Toast.makeText(getApplication(), "添加收藏失败,你可能已经收藏", Toast.LENGTH_SHORT).show();
                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(this, URL2, downloadListener);
    }


    /*
拿取游记内容
*/
    private void gencontent() {
        MyConfig config = new MyConfig("YJGLContentServlet", "selectallct");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("YJGLId", YJGLId);
        String str = JSON.toJSONString(map);
        try {
            URL2 = config.getURL() + str;
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
                    yjglContents = JSON.parseArray(Result, YJGLContent.class);
                    int k=0;
                    for(int i=0;i<yjglContents.size();i++) {
                        String pt = yjglContents.get(i).getYJGLContentPT();
                        Boolean b = pt.contains(",");
                        if (b == true) {
                            StringTokenizer tokener = new StringTokenizer(pt, ",");
                            String[] result1 = new String[tokener.countTokens()];
                            for(int j =0;j<result1.length;j++){
                                Urls[k]=result1[j];
                                k++;
                            }
                        }else {
                            Urls[k]=pt;
                            k++;
                        }
                    }
                    String URls[] = new String[k];
                    for(int m=0;m<k;m++){
                        URls[m]=Urls[m];
                    }
                    youjitupian.setAdapter(new ImageNormalAdapter(URls));
                }
            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(this, URL2, downloadListener);

    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {


        private MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return FragmentOne.newInstance(YJGLId);
                case 1:
                    return FragmentTwo.newInstance(YJGLId);
                case 2:
                    return FragmentThree.newInstance(UserId,YJGLId,UserName,UserPT);
                default:
                    return FragmentOne.newInstance(YJGLId);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:return "评论";
                case 1:return "游记详情";
                case 2:return "其他";
                default:return "评论";
            }
        }
    }
}
