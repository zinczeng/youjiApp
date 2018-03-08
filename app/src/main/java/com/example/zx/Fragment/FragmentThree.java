package com.example.zx.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.masonry.MasonryAdapter;
import com.example.zx.masonry.RecycleItemClickListener;
import com.example.zx.masonry.SpacesItemDecoration;
import com.example.zx.youjiandroid.LoginRegistActivity;
import com.example.zx.youjiandroid.ProductDetailActivity;
import com.example.zx.youjiandroid.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.SuiJi;
import bean.User;
import io.rong.imkit.RongIM;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by clevo on 2015/7/30.
 */
public class FragmentThree extends Fragment {
private static String UserId,YJGLId,UserPt,UserName;
    private TextView user_name,siliao;
    private ImageView userpt;
private RecyclerView recycler_youjiyonghu;
    private static List<SuiJi> suiJis;
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    public static String URL;
    public static FragmentThree newInstance(String Userid,String YJGLid,String Username,String Userpt){
        UserId=Userid;
        YJGLId = YJGLid;
        UserPt = Userpt;
        UserName = Username;
        return new FragmentThree();
    }


    public FragmentThree() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

      View  view=inflater.inflate(R.layout.detail_fragment_three, container, false);
        userpt = (ImageView) view.findViewById(R.id.shoupt);
        Glide.with(getContext()).load(UserPt)
                .bitmapTransform(new CropCircleTransformation(view.getContext()))
                .into(userpt);

        user_name = (TextView) view.findViewById(R.id.user_name);
        siliao = (TextView) view.findViewById(R.id.siliao);
        siliao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(User.getInstance().islogin()==false){
                    startActivity(new Intent(getContext(), LoginRegistActivity.class));
                } else if (User.getInstance().getUserId().equals(UserId)) {
                    Toast.makeText(getContext(),"您不能和自己私聊哦!",Toast.LENGTH_SHORT).show();
                }else {
                        RongIM.getInstance().startPrivateChat(getContext(), UserId, UserName);
                    }
                }

        });
        user_name.setText(UserName);
        initData();
        return   view;

    }

    private void recycler() {
        View view = getView();
        recycler_youjiyonghu = (RecyclerView) view.findViewById(R.id.recycler_youjiyonghu);
        recycler_youjiyonghu.setLayoutManager(new GridLayoutManager(getContext(),2));
        RecycleItemClickListener itemClickListener = new RecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent();
                intent.setClass(getActivity(),ProductDetailActivity.class);
                intent.putExtra("UserId",suiJis.get(position).getUserId());
                intent.putExtra("YJGLid",suiJis.get(position).getyJGLId());
                intent.putExtra("Dianzan",suiJis.get(position).getDianzan());
                intent.putExtra("Title",suiJis.get(position).getTitle());
                intent.putExtra("UpData",suiJis.get(position).getUpData());
                intent.putExtra("UserName",suiJis.get(position).getUserName());
                intent.putExtra("UserPT",suiJis.get(position).getUserPT());
                startActivity(intent);
            }
        };
        MasonryAdapter adapter = new MasonryAdapter(suiJis, itemClickListener);
        recycler_youjiyonghu.setAdapter(adapter);
            SpacesItemDecoration decoration = new SpacesItemDecoration(8);//设置边距  padding效果
        recycler_youjiyonghu.addItemDecoration(decoration);

    }



    private void initData() {

        MyConfig config = new MyConfig("YJGLServlet", "selectUser");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("types", 0);
        map.put("UserId", UserId);
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
                    String Result = map.get("result").toString();
                    suiJis = JSON.parseArray(Result, SuiJi.class);
                    recycler();
                }
                if(code.equals("0")){
                    Toast.makeText(getContext(),"该用户暂未有其他游记",Toast.LENGTH_SHORT).show();
                }

            }
        };
        taskHelper = new AsyncTaskHelper();
        taskHelper.downloadData(getContext(), URL, downloadListener);
    }
}
