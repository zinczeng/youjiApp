package com.example.zx.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.crazysunj.cardslideview.CardViewPager;
import com.example.zx.Adapter.GLAdaptar;
import com.example.zx.Util.AsyncTaskHelper;
import com.example.zx.Util.FastJsonTools;
import com.example.zx.Util.MyConfig;
import com.example.zx.cardview.MyCardHandler;
import com.example.zx.masonry.RecycleItemClickListener;
import com.example.zx.youjiandroid.GongLveXiangXiActvity;
import com.example.zx.youjiandroid.R;
import com.example.zx.youjiandroid.SearchGongLveActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.MyBean;
import bean.SuiJi;


public class GongLveFragment extends Fragment implements View.OnClickListener {
	private boolean isshuaxin=true;
	private  RecyclerView gonglve_lt;
	private TextView go_to_search_;
	public static String URL;
	private CardViewPager viewPager;
	private AsyncTaskHelper.OnDataDownloadListener downloadListener;
	private AsyncTaskHelper taskHelper;
	private SwipeRefreshLayout swipeRefresh;
	private static View view;
	private static List<SuiJi> suiJis;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_gonglve, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		 view = getView();
		initData();
		gonglve_lt = (RecyclerView) view.findViewById(R.id.gonglve_lt);
		viewPager = (CardViewPager) view.findViewById(R.id.cardviewpager);
		go_to_search_ = (TextView) view.findViewById(R.id.go_to_search_);
		swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id .swipe_refresh3);
		go_to_search_.setOnClickListener(this);
		SwipeRefresh();
		cardview();
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
	private void recyclerView() {
		gonglve_lt.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL,false));
		RecycleItemClickListener itemClickListener = new RecycleItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Intent intent=new Intent(getActivity(),GongLveXiangXiActvity.class);
				intent.putExtra("YJGLid",suiJis.get(position).getyJGLId());
				intent.putExtra("Dianzan",suiJis.get(position).getDianzan());
				intent.putExtra("Title",suiJis.get(position).getTitle());
				intent.putExtra("UpData",suiJis.get(position).getUpData());
				intent.putExtra("UserName",suiJis.get(position).getUserName());
				intent.putExtra("UserPT",suiJis.get(position).getUserPT());
				startActivity(intent);
			}
		};
			GLAdaptar adapter = new GLAdaptar(suiJis, itemClickListener);
			gonglve_lt.setAdapter(adapter);
	}
/*
获取攻略
 */
	private void initData() {
		if(isshuaxin == true){
			MyConfig config = new MyConfig("YJGLServlet", "selectrand");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("types", 1);
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
	实现攻略卡片效果
	MyCardHandler()，实现卡片切换监听
	notifyUI， 通知刷新UI线程
	 */
	private void cardview() {
		List<MyBean> list = new ArrayList<MyBean>();
		list.add(new MyBean("http://oibc7l5du.bkt.clouddn.com/lushan.jpg","http://m.cncn.com/jingdian/6709"));
		list.add(new MyBean("http://oibc7l5du.bkt.clouddn.com/20171210231318.jpg","http://m.cncn.com/jingdian/9412"));
		list.add(new MyBean("http://oibc7l5du.bkt.clouddn.com/gulangyu.jpg","http://m.cncn.com/jingdian/272"));
		list.add(new MyBean("http://oibc7l5du.bkt.clouddn.com/xihu.jpg","http://m.cncn.com/jingdian/5193"));
		list.add(new MyBean("http://oibc7l5du.bkt.clouddn.com/lijiang.jpg","http://m.cncn.com/jingdian/10263"));
		list.add(new MyBean("http://oibc7l5du.bkt.clouddn.com/wuzheng.jpg","http://m.cncn.com/jingdian/5562"));
		viewPager.bind(getFragmentManager(), new MyCardHandler(), list);
		viewPager.setCardTransformer(180, 0.3f);
		viewPager.setCardPadding(30);
		viewPager.setCardMargin(5);
		viewPager.notifyUI(CardViewPager.MODE_CARD);
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.go_to_search_:
				startActivity(new Intent(getContext(), SearchGongLveActivity.class));
				break;
		}
	}
}

