package com.example.zx.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.masonry.MasonryAdapter;
import com.example.zx.masonry.RecycleItemClickListener;
import com.example.zx.masonry.SpacesItemDecoration;
import com.example.zx.youjiandroid.ProductDetailActivity;
import com.example.zx.youjiandroid.R;
import com.example.zx.youjiandroid.SearchYouJiActivity;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.SuiJi;

public class YouJiFragment extends Fragment{
    private boolean ispaddind=false;
    private boolean isshuaxin=true;
    private static List<SuiJi> suiJis;
    private AsyncTaskHelper.OnDataDownloadListener downloadListener;
    private AsyncTaskHelper taskHelper;
    public static String URL;
    private RecyclerView recyclerView;
    protected View mMainView=null, LinearLayout;
    private RollPagerView mViewPager;
    private FrameLayout search_youji;
    private SwipeRefreshLayout swipeRefresh;
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public YouJiFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_youji, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainView = getView();
        init(mMainView);
        SwipeRefresh();
        recyclerView();
        /*
广告轮播
 */
        mViewPager = (RollPagerView) mMainView.findViewById(R.id.view_pager);
        mViewPager.setAdapter(new YouJiFragment.ImageNormalAdapter());
    }
//刷新
    private void SwipeRefresh() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isshuaxin = true;
                initData();
            }
    });
    }

    private void init(View mMainView) {
        swipeRefresh = (SwipeRefreshLayout)mMainView.findViewById(R.id.swipe_refresh2);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        search_youji=(FrameLayout) mMainView.findViewById(R.id.search_youji_go) ;
        search_youji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getContext(), SearchYouJiActivity.class);
                startActivity(intent);
            }
        });
    }



    /*
    瀑布流实现
     */
    private void recyclerView() {
        recyclerView = (RecyclerView) mMainView.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        initData();
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
        recyclerView.setAdapter(adapter);
        if(ispaddind==false) {
            SpacesItemDecoration decoration = new SpacesItemDecoration(8);//设置边距  padding效果
            recyclerView.addItemDecoration(decoration);
            ispaddind=true;
        }
    }

    private void initData() {
        if(isshuaxin == true){
            MyConfig config = new MyConfig("YJGLServlet", "selectrand");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("types", 0);
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
                    if(!map.equals("")) {
                        String code = map.get("code").toString();
                        if (code.equals("1")) {
                            String Result = map.get("result").toString();
                            suiJis = JSON.parseArray(Result, SuiJi.class);
                            isshuaxin = false;
                            recyclerView();
                        }
                        if (code.equals("0")) {

                        }
                        swipeRefresh.setRefreshing(false);
                    }
                }
            };
            taskHelper = new AsyncTaskHelper();
            taskHelper.downloadData(getContext(), URL, downloadListener);
        }

    }

    /*
 广告轮播的实现 依赖rollviewpager
  */
    class ImageNormalAdapter extends StaticPagerAdapter {
        int[] imgs = new int[]{
                R.drawable.youji001,
                R.drawable.youji002,
                R.drawable.youji003,
                R.drawable.youji004,
        };

        /**
         * SetScaleType(ImageView.ScaleType.CENTER);
         * 按图片的原来size居中显示，当图片长/宽超过View的长/宽，则截取图片的居中部分显示
         * <p>
         * SetScaleType(ImageView.ScaleType.CENTER_CROP);
         * 按比例扩大图片的size居中显示，使得图片长(宽)等于或大于View的长(宽)
         * <p>
         * setScaleType(ImageView.ScaleType.CENTER_INSIDE);
         * 将图片的内容完整居中显示，通过按比例缩小或原来的size使得图片长/宽等于或小于View的长/宽
         * <p>
         * setScaleType(ImageView.ScaleType.FIT_CENTER);
         * 把图片按比例扩大/缩小到View的宽度，居中显示
         */
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);// 按比例扩大图片的size居中显示，使得图片长(宽)等于或大于View的长(宽)
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setImageResource(imgs[position]);
            return view;

        }

        public int getCount() {
            return imgs.length;
        }
    }

}
