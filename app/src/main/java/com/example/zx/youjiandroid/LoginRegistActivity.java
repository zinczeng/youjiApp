package com.example.zx.youjiandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.AsyncTaskHelper.OnDataDownloadListener;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.User;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;


/**
 * Created by Administrator on 2017/9/5.
 */

public class LoginRegistActivity extends Activity implements View.OnClickListener, RongIM.UserInfoProvider {
    private View view1, view2;
    private ViewPager viewPager;
    private List<View> ListView;
    private TextView textView1, textView2;
    private ImageView imageView, login_go_main;// 动画图片
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    String UserId, UserPW;
    private Button regist, login;
    private EditText zhanghao, mima, lgzhanghao, lgmima;
    public static String URL;
    private OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;


    protected static final String TAG = "LoginRegistActivity";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_regist);
        InitImageView();
        InitTextView();
        InitViewPager1();
        init();

    }

    private void init() {
        login_go_main = (ImageView) findViewById(R.id.login_go_main);
        login_go_main.setOnClickListener(this);
        regist = (Button) view2.findViewById(R.id.regist);
        login = (Button) view1.findViewById(R.id.sure_login);
        zhanghao = (EditText) view2.findViewById(R.id.regist_id);
        lgzhanghao = (EditText) view1.findViewById(R.id.lg_userid);
        mima = (EditText) view2.findViewById(R.id.regist_pw);
        lgmima = (EditText) view1.findViewById(R.id.lg_userpw);
        regist.setOnClickListener(this);
        login.setOnClickListener(this);

    }

    private void InitViewPager1() {
        viewPager = ((ViewPager) findViewById(R.id.vPager));
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.login1, null);
        view2 = inflater.inflate(R.layout.login2, null);
        ListView = new ArrayList<View>();
        ListView.add(view1);
        ListView.add(view2);
        viewPager.setAdapter(new MyViewPagerAdapter(ListView));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

    }

    private void InitTextView() {
        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        textView1.setOnClickListener(new MyOnClickListener(0));
        textView2.setOnClickListener(new MyOnClickListener(1));

    }

    private void InitImageView() {
        imageView = (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        imageView.setImageMatrix(matrix);// 设置动画初始位置

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_go_main:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                this.finish();
                break;
            case R.id.regist:
                regist1();
                break;
            case R.id.sure_login:
                login1();
                break;
        }

    }

    /*
    实现登录功能
     */
    private void login1() {
        UserId = lgzhanghao.getText().toString();
        UserPW = lgmima.getText().toString();
        if (!UserId.equals("") && !UserPW.equals("")) {
            MyConfig config = new MyConfig("UserServlet", "login");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("UserId", UserId);
            map.put("UserPW", UserPW);
            String str = JSON.toJSONString(map);
            try {
                URL = config.getURL() + str;
            } catch (Exception e) {
                e.printStackTrace();
            }
            downloadListener = new OnDataDownloadListener() {
                @Override

                public void onDataDownload(byte[] result) {
                    String jsonString = new String(result); // 返回的字节数组转换为字符串
                    Map<String, Object> map = new HashMap<String, Object>();
                    map = FastJsonTools.getMap(jsonString);
                    String code = map.get("code").toString();
                    if (code.equals("1")) {
                        String Result = map.get("result").toString();
                        List<User> userList = FastJsonTools.getBeans(Result, User.class);
                        User.getInstance().setUserAddress(userList.get(0).getUserAddress());
                        User.getInstance().setUserPT(userList.get(0).getUserPT());
                        User.getInstance().setUserName(userList.get(0).getUserName());
                        User.getInstance().setSex(userList.get(0).getSex());
                        User.getInstance().setSign(userList.get(0).getSign());
                        User.getInstance().setUserId(userList.get(0).getUserId());
                        User.getInstance().setUserAge(userList.get(0).getUserAge());
                        User.getInstance().setUserPW(userList.get(0).getUserPW());
                        User.getInstance().setToken(userList.get(0).getToken());
                        User.getInstance().setIslogin(true);
                        //登录融云
                        connectRongServer(User.getInstance().getToken());
                                Intent intent = new Intent(LoginRegistActivity.this, MainActivity.class);
                                intent.putExtra("main", 4);
                                startActivity(intent);
                        finish();
                    } else if (code.equals("0")) {
                        Toast.makeText(LoginRegistActivity.this, "登录失败！",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };
            taskHelper = new AsyncTaskHelper();
            taskHelper.downloadData(this, URL, downloadListener);
        } else {
            Toast.makeText(LoginRegistActivity.this, "密码或账号不能为空", Toast.LENGTH_SHORT).show();
        }


    }

    /*
    实现注册功能
     */
    private void regist1() {
        UserPW = mima.getText().toString();
        UserId = zhanghao.getText().toString();
        if (!UserId.equals("") && !UserPW.equals("")) {
            MyConfig config = new MyConfig("UserServlet", "regist");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("UserId", UserId);
            map.put("UserPW", UserPW);
            map.put("UserAddress", "");
            map.put("UserAge", "0");
            map.put("UserName", "新用户" + UserId);
            map.put("sex", "0");
            map.put("UserPT", "http://oibc7l5du.bkt.clouddn.com/Fl8nX3iBeixcphQ4eH28afeXCZB_");
            map.put("Sign", "这个人很懒，什么都没有留下");
            map.put("Token", "wzVnuvcSWYozcMLl9nkg9h3PuO87lu/0awyT3Ro0mbzpH+35ylB2ZZPSJqH7piEJ4VVnw2ngrc/q1HkJGRhThg==");
            String str = JSON.toJSONString(map);
            try {
                URL = config.getURL() + str;

            } catch (Exception e) {
                e.printStackTrace();
            }

            downloadListener = new OnDataDownloadListener() { // 访问服务器后获取返回数据

                @Override
                public void onDataDownload(byte[] result) {
                    String jsonString = new String(result); // 返回的字节数组转换为字符串
                    Map<String, Object> map = new HashMap<String, Object>();
                    map = FastJsonTools.getMap(jsonString);
                    String code = map.get("code").toString();
                    if (code.equals("1")) {
                        Toast.makeText(LoginRegistActivity.this, "注册成功！",
                                Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(0);
                            }
                        }, 2000);//2秒后执行Runnable中的run方法
                    } else if (code.equals("0")) {
                        Toast.makeText(LoginRegistActivity.this, "注册失败！该账号已存在",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };
            taskHelper = new AsyncTaskHelper();
            taskHelper.downloadData(getApplicationContext(),
                    URL,
                    downloadListener); // 发起服务器的访问

        } else {
            Toast.makeText(LoginRegistActivity.this, "密码或账号不能为空", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public UserInfo getUserInfo(String s) {
        return new UserInfo(User.getInstance().getUserId(), User.getInstance().getUserName(), Uri.parse(User.getInstance().getUserPT()));
    }


    /**
     * 头标点击监听 3
     */
    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;

        }

        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }

    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

        public void onPageScrollStateChanged(int arg0) {


        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {


        }

        public void onPageSelected(int arg0) {
            Animation animation = new TranslateAnimation(one * currIndex, one * arg0, 0, 0);//显然这个比较简洁，只有一行代码。
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            imageView.startAnimation(animation);
        }



    }

    /**
     * 连接融云服务器
     *
     * @param token
     */
    private void connectRongServer(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {

               RongIM.setUserInfoProvider(LoginRegistActivity.this, true);
                RongIM.getInstance().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
                    @Override
                    public boolean onReceived(Message message, int arg1) {
                        if (message.getContent() instanceof ImageMessage) {
                            ImageMessage imageMessage = (ImageMessage) message.getContent();
                            Log.e(TAG, "缩略路径为：" + imageMessage.getThumUri());
                        }
                        return false;
                    }

                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e(TAG,
                        "connect failure errorCode is :" + errorCode.getValue());
            }


            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "token is error , please check token and appkey ");
            }
        });

        RongIM.getInstance().getRongIMClient().setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
            @Override
            public void onChanged(ConnectionStatus status) {
                if (ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT == status) {
                    Log.e(TAG, "被顶号");
                }
                Log.e(TAG, "网络状态已经变化");
            }
        });



        /**
         * 设置消息体内是否携带用户信息。
         * @param state 是否携带用户信息，true 携带，false 不携带。
         */
        RongIM.getInstance().setMessageAttachedUserInfo(true);
    }


}
