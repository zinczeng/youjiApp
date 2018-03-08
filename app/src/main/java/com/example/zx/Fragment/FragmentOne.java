package com.example.zx.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zx.Adapter.YJRWAdapter;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.masonry.RecycleItemClickListener;
import com.example.zx.youjiandroid.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.Review1;


/**
 * Created by clevo on 2015/7/30.
 */
public class FragmentOne extends Fragment {
    private SwipeRefreshLayout swipeRefresh;
    public static String URL;
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    private static  String YJGLId;
    private RecyclerView recyclerView;
    private boolean isshuaxin=true;
    private static List<Review1> review1s;
    public static FragmentOne newInstance(String YJGLid){
        YJGLId=YJGLid;
        return new FragmentOne();
    }


    public FragmentOne() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.detail_fragment_one,container,false );
         recyclerView= (RecyclerView) view.findViewById(R.id.recycler_fragment);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id .swipe_refresh5);
        SwipeRefresh();
        selectreview();
        return view;
    }

    //刷新
    private void SwipeRefresh() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isshuaxin = true;
                selectreview();
            }
        });
    }
    /*
    查询评论
     */
    private void selectreview(){
        if(isshuaxin==true) {
            MyConfig config = new MyConfig("ReviewServlet", "selectpl");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("YJGLId",YJGLId);
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
                        review1s = JSON.parseArray(Result, Review1.class);
                        isshuaxin = false;
                        recyclerView1();
                    }
                    if (code.equals("0")) {
                        Toast.makeText(getContext(), "暂未评论信息", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefresh.setRefreshing(false);
                }
            };
            taskHelper = new AsyncTaskHelper();
            taskHelper.downloadData(getContext(), URL, downloadListener);
        }
    }
/*

 */
    private void recyclerView1() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecycleItemClickListener itemClickListener = new RecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        };
        YJRWAdapter adapter=new YJRWAdapter(review1s, itemClickListener);
        recyclerView.setAdapter(adapter);
    }

}
