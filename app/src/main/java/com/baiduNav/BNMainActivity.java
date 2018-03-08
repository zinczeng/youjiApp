package com.baiduNav;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.example.zx.youjiandroid.R;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType.GCJ02;
import static com.baidu.navisdk.adapter.PackageUtil.getSdcardDir;

public class BNMainActivity extends Activity implements OnClickListener {
	public static List<Activity> activityList = new LinkedList<Activity>();
	private MapView mMapView;
	private Button mBtnLocation;
	private BaiduMap mBaiduMap;
	private Button mBtnMockNav;
	private Button mBtnRealNav;


	private boolean hasInitSuccess = false;
	private boolean hasRequestComAuth = false;

	private String mSDCardPath = null;
	private final static int authBaseRequestCode = 1;
	private final static int authComRequestCode = 2;
	private final static String authBaseArr[] =
			{ Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION };
	private final static String authComArr[] = { Manifest.permission.READ_PHONE_STATE };
	private LocationClient mLocationClient;
	private boolean isFristLocation = true;
	private LatLng mLastLocationData;
	private LatLng mDestLocationData;
	private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";

	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityList.add(this);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.bncativity_main);
		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				Log.i("crug", name);
				delayTest();
			}
		
		}, 500);

		initLocation();

		mMapView = (MapView) findViewById(R.id.id_baiduMapView);
		mBtnLocation = (Button) findViewById(R.id.id_btn_location);
		mBtnMockNav = (Button) findViewById(R.id.id_btn_mocknav);
		mBtnRealNav = (Button) findViewById(R.id.id_btn_realnav);

		mBtnLocation.setOnClickListener(this);
		mBtnMockNav.setOnClickListener(this);
		mBtnRealNav.setOnClickListener(this);


		mBaiduMap = mMapView.getMap();

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
		mBaiduMap.setMapStatus(msu);

		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng arg0) {

				Toast.makeText(BNMainActivity.this, "设置目的地成功", Toast.LENGTH_SHORT).show();
				mDestLocationData = arg0;
				addDestInfoOverlay(arg0);
			}
		});


//初始化当行相关
		if (initDirs()) {
			initNavi();
		}
	}

	private void delayTest() {
	}


	private boolean hasBasePhoneAuth() {
		// TODO Auto-generated method stub

		PackageManager pm = this.getPackageManager();
		for (String auth : authBaseArr) {
			if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	private boolean hasCompletePhoneAuth() {
		// TODO Auto-generated method stub

		PackageManager pm = this.getPackageManager();
		for (String auth : authComArr) {
			if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	private boolean initDirs() {
		mSDCardPath = getSdcardDir();
		if (mSDCardPath == null) {
			return false;
		}
		File f = new File(mSDCardPath, APP_FOLDER_NAME);
		if (!f.exists()) {
			try {
				f.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	String authinfo = null;


		private void initNavi() {

			BNOuterTTSPlayerCallback ttsCallback = null;

			// 申请权限
			if (android.os.Build.VERSION.SDK_INT >= 23) {

				if (!hasBasePhoneAuth()) {

					this.requestPermissions(authBaseArr, authBaseRequestCode);
					return;

				}
			}

			BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
				@Override
				public void onAuthResult(int status, String msg) {
					if (0 == status) {
						authinfo = "key校验成功!";
					} else {
						authinfo = "key校验失败, " + msg;
					}
					BNMainActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(BNMainActivity.this, authinfo, Toast.LENGTH_LONG).show();
						}
					});
				}

				public void initSuccess() {
					Toast.makeText(BNMainActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
					hasInitSuccess = true;
				}

				public void initStart() {
					Toast.makeText(BNMainActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
				}

				public void initFailed() {
					Toast.makeText(BNMainActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
				}

			}, null, ttsHandler, ttsPlayStateListener);

		}

	/**
	 * 内部TTS播报状态回调接口
	 */
	private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

		@Override
		public void playEnd() {
			// showToastMsg("TTSPlayStateListener : TTS play end");
		}

		@Override
		public void playStart() {
			// showToastMsg("TTSPlayStateListener : TTS play start");
		}
	};

	/**
	 * 内部TTS播报状态回传handler
	 */
	private Handler ttsHandler = new Handler() {
		public void handleMessage(Message msg) {
			int type = msg.what;
			switch (type) {
				case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
					// showToastMsg("Handler : TTS play start");
					break;
				}
				case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
					// showToastMsg("Handler : TTS play end");
					break;
				}
				default:
					break;
			}
		}
	};





	private void routeplanToNavi(boolean mock) {
		BNRoutePlanNode.CoordinateType Type = GCJ02;
		if (!hasInitSuccess) {
			Toast.makeText(BNMainActivity.this, "还未初始化!", Toast.LENGTH_SHORT).show();
		}
		// 权限申请
		if (android.os.Build.VERSION.SDK_INT >= 23) {
			// 保证导航功能完备
			if (!hasCompletePhoneAuth()) {
				if (!hasRequestComAuth) {
					hasRequestComAuth = true;
					this.requestPermissions(authComArr, authComRequestCode);
					return;
				} else {
					Toast.makeText(BNMainActivity.this, "没有完备的权限!", Toast.LENGTH_SHORT).show();
				}
			}

		}
		BNRoutePlanNode sNode = null;
		BNRoutePlanNode eNode = null;

		sNode = new BNRoutePlanNode(mLastLocationData.longitude,
				mLastLocationData.latitude, "我的地点", null, Type);
		eNode = new BNRoutePlanNode(mDestLocationData.longitude,
				mDestLocationData.latitude, "目标终点", null, Type);


		if (sNode != null && eNode != null) {
			List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
			list.add(sNode);
			list.add(eNode);
			// 开发者可以使用旧的算路接口，也可以使用新的算路接口,可以接收诱导信息等
			//BaiduNaviManager.getInstance().launchNavigator(this, list, 1, mock, new DemoRoutePlanListener(sNode));
			BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode),
						eventListerner);
		}
	}

	BaiduNaviManager.NavEventListener eventListerner = new BaiduNaviManager.NavEventListener() {

		@Override
		public void onCommonEventCall(int what, int arg1, int arg2, Bundle bundle) {
			BNEventHandler.getInstance().handleNaviEvent(what, arg1, arg2, bundle);
		}
	};

	public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

	private BNRoutePlanNode mBNRoutePlanNode = null;

	public DemoRoutePlanListener(BNRoutePlanNode node) {
		mBNRoutePlanNode = node;
	}

	@Override
	public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
             */
		Intent intent = new Intent(BNMainActivity.this, BNDemoGuideActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ROUTE_PLAN_NODE,  mBNRoutePlanNode);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onRoutePlanFailed() {
		// TODO Auto-generated method stub
		Toast.makeText(BNMainActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
	}
}


	protected void addDestInfoOverlay(LatLng destInfo) {
		
		mBaiduMap.clear();
		OverlayOptions options = new MarkerOptions().position(destInfo)//
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.myloc))//
				.zIndex(5);
		mBaiduMap.addOverlay(options);
	}

	private void initLocation() {

		mLocationClient = new LocationClient(getApplicationContext());

		mLocationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {

				if (location == null || mMapView == null)
					return;

				MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())

						.latitude(location.getLatitude()).longitude(location.getLongitude()).build();

				mBaiduMap.setMyLocationData(locData);

				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				mLastLocationData = ll;
				if (isFristLocation) {
					isFristLocation = false;
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
					mBaiduMap.animateMapStatus(u);
				}
			}
		});
	}

	private void initLocationOps() {

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		int span = 1000;
		option.setScanSpan(span);
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		mLocationClient.setLocOption(option);
	}

	@Override
	public void onClick(View v)
	{

		switch (v.getId()) 
		{
		case R.id.id_btn_location:
			if (mLastLocationData != null) 
			{
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLng(mLastLocationData);
				mBaiduMap.animateMapStatus(u);
			}
			break;

			case R.id .id_btn_mocknav:

			{
				if (mDestLocationData == null){
					Toast.makeText(BNMainActivity.this,"长按地图设置目标地点", Toast.LENGTH_SHORT).show();
					return;
				}

				routeplanToNavi(false);
			}
				break;
			case R.id .id_btn_realnav:
				if (mDestLocationData == null){
					Toast.makeText(BNMainActivity.this,"长按地图设置目标地点", Toast.LENGTH_SHORT).show();
					return;
				}
				routeplanToNavi(true);
				break;

		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();

		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted())

			mLocationClient.start();
	}

	@Override
	protected void onStop() {
		super.onStop();

		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	private void initSetting() {
		// BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
		BNaviSettingManager
				.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
		BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
		// BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
		BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
		BNaviSettingManager.setIsAutoQuitWhenArrived(true);
		Bundle bundle = new Bundle();
		// 必须设置APPID，否则会静音
		bundle.putString(BNCommonSettingParam.TTS_APP_ID, "9717578");
		BNaviSettingManager.setNaviSdkParam(bundle);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == authBaseRequestCode) {
			for (int ret : grantResults) {
				if (ret == 0) {
					continue;
				} else {
					Toast.makeText(BNMainActivity.this, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			initNavi();
		} else if (requestCode == authComRequestCode) {
			for (int ret : grantResults) {
				if (ret == 0) {
					continue;
				}
			}
			routeplanToNavi(true);
		}

	}
}
