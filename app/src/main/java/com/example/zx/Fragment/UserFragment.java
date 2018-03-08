package com.example.zx.Fragment;

import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.ImageDownloadHelper;
import com.example.zx.Util.MyConfig;
import com.example.zx.youjiandroid.GongLveActivity;
import com.example.zx.youjiandroid.LoginRegistActivity;
import com.example.zx.youjiandroid.MySouCangActivity;
import com.example.zx.youjiandroid.MyYouJiActivity;
import com.example.zx.youjiandroid.R;
import com.example.zx.youjiandroid.ShezhiActivity;
import com.example.zx.youjiandroid.fragment.LoadConversationListFragment;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bean.User;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.zx.youjiandroid.LoginRegistActivity.URL;
import static com.example.zx.youjiandroid.MainActivity.uploadManager;


public class UserFragment extends Fragment implements View.OnClickListener {
    private ImageView blurImageView, xingbie;
    protected static final String TAG = "LoginRegistActivity";
    private ImageView avatarImageView;
    private TextView User_Fragment_Id, user_SIGN;
    private LinearLayout my_youji, my_soucang,myfrind,shezhi2,my_gl;
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    private View view = null;
    private Button tuichu;
    private RelativeLayout login;
    public static String url;//图片七牛地址
    private ImageDownloadHelper downloadHelper;// 图片加载对象
    private ImageDownloadHelper.OnImageDownloadListener imageDownloadListener;// 图片加载回调接口的对象

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private final int IMAGE_CODE = 0; // 这里的IMAGE_CODE是自己任意定义的

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);

        downloadHelper = new ImageDownloadHelper();
        initview(view);

        setinfo();
        return view;
    }

    /*
    设置用户信息
     */
    private void setinfo() {
        if (User.getInstance().islogin() == true) {
            User_Fragment_Id.setText(User.getInstance().getUserName());
            user_SIGN.setText(User.getInstance().getSign());
            if (User.getInstance().getSex().equals("0")) {
                xingbie.setImageDrawable(getResources().getDrawable((R.drawable.nan)));
            } else if (User.getInstance().getSex().equals("1")) {
                xingbie.setImageDrawable(getResources().getDrawable((R.drawable.nv)));
            }
            //毛玻璃实现
            Glide.with(this).load(User.getInstance().getUserPT())
                    .bitmapTransform(new BlurTransformation(getContext(), 4), new CenterCrop(getContext()))
                    .into(blurImageView);
            //圆头像
            Glide.with(this).load(User.getInstance().getUserPT())
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into(avatarImageView);
        }
        //毛玻璃实现
        if (User.getInstance().islogin() == false) {
            Glide.with(this).load(R.drawable.background_2)
                    .bitmapTransform(new BlurTransformation(getContext(), 4), new CenterCrop(getContext()))
                    .into(blurImageView);
            //圆头像
            Glide.with(this).load(R.drawable.background_2)
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into(avatarImageView);
        }

    }

    private void initview(View view) {
        User_Fragment_Id = (TextView) view.findViewById(R.id.User_Fragment_Id);
        blurImageView = (ImageView) view.findViewById(R.id.iv_blur);

        avatarImageView = (ImageView) view.findViewById(R.id.iv_avatar);
        xingbie = (ImageView) view.findViewById(R.id.xingbie);
        my_youji = (LinearLayout) view.findViewById(R.id.my_youji);
        my_soucang = (LinearLayout) view.findViewById(R.id.my_shoucang);
        my_gl = (LinearLayout) view.findViewById(R.id.my_gl);
        shezhi2 = (LinearLayout) view.findViewById(R.id.shezhi2);
        myfrind = (LinearLayout) view.findViewById(R.id.myfrind);
        user_SIGN = (TextView) view.findViewById(R.id.user_SIGN);
        login = (RelativeLayout) view.findViewById(R.id.chose_login);
        my_youji.setOnClickListener(this);
        my_soucang.setOnClickListener(this);
        myfrind.setOnClickListener(this);
        my_gl.setOnClickListener(this);
        shezhi2.setOnClickListener(this);
        avatarImageView.setOnClickListener(this);
        login.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_shoucang:
                if (User.getInstance().islogin() == false) {
                    startActivity(new Intent(getContext(), LoginRegistActivity.class));
                } else if (User.getInstance().islogin() == true) {
                    startActivity(new Intent(getContext(), MySouCangActivity.class));
                }
                break;
            case R.id.my_youji:
                startActivity(new Intent(getContext(), MyYouJiActivity.class));
                break;
            case R.id.chose_login:
                if (User.getInstance().islogin() == false) {
                    startActivity(new Intent(getContext(), LoginRegistActivity.class));
                    getActivity().finish();
                }
                break;

            case R.id.iv_avatar:
                if (User.getInstance().islogin() == false) {
                    startActivity(new Intent(getContext(), LoginRegistActivity.class));
                    getActivity().finish();
                } else if (User.getInstance().islogin() == true) {
                    setuserpt();
                }
                break;
            case R.id.myfrind:
                if(User.getInstance().islogin()==false){
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(getContext(), LoadConversationListFragment.class));
                }
                break;
            case R.id.shezhi2:
                startActivity(new Intent(getContext(), ShezhiActivity.class));
                break;
            case R.id.my_gl:
                startActivity(new Intent(getContext(), GongLveActivity.class));
                break;

        }
    }

    private void updatapt() {
        if (url != null) {
            MyConfig config = new MyConfig("UserServlet", "updatept");
            Map<String, Object> map = new HashMap<String, Object>();
            String pt = url;
            map.put("UserId", User.getInstance().getUserId());
            map.put("UserPT", pt);
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
                        User.getInstance().setUserPT(url);

                        //毛玻璃实现
                        Glide.with(getContext()).load(User.getInstance().getUserPT())
                                .bitmapTransform(new BlurTransformation(getContext(), 4), new CenterCrop(getContext()))
                                .into(blurImageView);
                        //圆头像
                        Glide.with(getContext()).load(User.getInstance().getUserPT())
                                .bitmapTransform(new CropCircleTransformation(getContext()))
                                .into(avatarImageView);
                    }
                }
            };
            taskHelper = new AsyncTaskHelper();
            taskHelper.downloadData(getContext(), URL, downloadListener);
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

        ContentResolver resolver = getActivity().getContentResolver();

        if (requestCode == IMAGE_CODE) {

            try {

                Uri originalUri = data.getData(); // 获得图片的uri
                Log.e("sssssssssssssssss", originalUri + "");
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                avatarImageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bm,
                        100, 100)); // 使用系统的一个工具类，参数列表为 Bitmap Width,Height
                // 这里使用压缩后显示，否则在华为手机上ImageView 没有显示
                // 显得到bitmap图片
                // imageView.setImageBitmap(bm);
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor;
                // 好像是android多媒体数据库的封装接口，具体的看Android文档
                CursorLoader cursorLoader = new CursorLoader(getContext(), originalUri, null, null,
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
                                Log.e("aaaaaaaaaaaaaaa", url);
                                //img_url.setText(url);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (headerUrl != null && !headerUrl.equals("")) {
                                //传入数据库
                                updatapt();
                            } else {
                                Toast.makeText(getContext(), "上传失败",
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
