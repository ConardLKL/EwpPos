package com.limahao.ticket.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchInfo;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.LocalSearchInfo;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.limahao.ticket.BaseActivity;
import com.limahao.ticket.R;
import com.limahao.ticket.log.DebugLog;

/**
 * @Title: Website.java
 * 
 * @author email: <a href="cj@ewppay.com">陈杰</a>
 * @date 2014-8-18 上午10:42:23
 * @version :
 * @Description: 周边网点
 */
public class Website extends BaseActivity {
	private final String TAG = "Website";
	private LinearLayout layoutBack, right_layout;

	private SDKReceiver mReceiver;

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			DebugLog.d(TAG, "action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				// 调试用
				DebugLog.e(TAG, "key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				DebugLog.e(TAG, getString(R.string.httpError));
				showLongToast(getString(R.string.httpError));
			}
		}
	}

	// 地图相关
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private BitmapDescriptor mCurrentMarker;

	// 定位相关
	private LocationClient mLocClient;
	private LocationMode mCurrentMode;
	private MyLocationListenner myListener = new MyLocationListenner();

	// poi搜索
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	private MyOnGetPoiSearchResultListener myPsrListenner = new MyOnGetPoiSearchResultListener();
	private MyOnGetSuggestionResultListener mySrListenner = new MyOnGetSuggestionResultListener();

	// LBS云搜索
	private MyCloudListener myCloudListener;
	private String ak = "DUMFuMgqicdc6KB8FtufLXPY"; // 就是百度申请的key， 从配置文件读取
	private int geoTableId = 76357; // 后台服务导数据是产生 76357
	private MyLBSMarkerClick myLBSMarkerClick = new MyLBSMarkerClick();
	private MyOnMapStatusChangeListener myOnMapStatusChangeListener = new MyOnMapStatusChangeListener();

	// 检索相关设定
	private String keyword = "limahao"; // limahao 搜索关键字
	private int radius = 2000; // 搜索半径
	private int pageNum = 0; // 第一页
	private int pageCapacity = 10; // 一页条数
	private BitmapDescriptor[] bdArray = new BitmapDescriptor[10]; // 标记点的图标 10个
	
	private boolean isLbsChage = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");

		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);

		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.websit);
		iniTitle();
		initHttpFailView();
		findView();
		init();
	}

	/**
	 * 标题设置
	 */
	private void iniTitle() {
		layoutBack = (LinearLayout) findViewById(R.id.include_left_layout);
		layoutBack.setVisibility(View.VISIBLE);
		layoutBack.setOnClickListener(this);
		LinearLayout center_layout = (LinearLayout) findViewById(R.id.include_center_layout);
		center_layout.setVisibility(View.VISIBLE);
		TextView center_tv = (TextView) findViewById(R.id.include_center_tv);
		center_tv.setVisibility(View.VISIBLE);
		center_tv.setText(getString(R.string.include_tick_websit_txt));

		right_layout = (LinearLayout) findViewById(R.id.include_right_layout);
		right_layout.setVisibility(View.VISIBLE);
		right_layout.setOnClickListener(this);
		TextView right_tv = (TextView) findViewById(R.id.include_right_tv);
		right_tv.setText(this.getResources().getString(
				R.string.include_tick_websit_location_txt));
		right_tv.setVisibility(View.VISIBLE);
	}

	@Override
	protected void findView() {
		mMapView = (MapView) findViewById(R.id.websit_bmapView);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 退出键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 如果键盘打开则关闭
			if (imm != null && imm.isActive()) {
				// imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
				// InputMethodManager.HIDE_NOT_ALWAYS);
			}
			setResult(RESULT_OK);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		if (v == layoutBack) {
			// 如果键盘打开则关闭
			if (imm != null && imm.isActive()) {
				// imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
				// InputMethodManager.HIDE_NOT_ALWAYS);
			}
			setResult(RESULT_OK);
			finish();
		} else if (v == right_layout) {
			mLocClient.start(); // 重新定位
		}
	}

	/**
	 * 初期话设置
	 * 
	 */
	private void init() {
		// 地图初始化
		mBaiduMap = mMapView.getMap();
		mBaiduMap.clear();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		// 设置模式 目前为普通:LocationMode.NORMAL 跟随：LocationMode.FOLLOWING，
		// 罗盘：LocationMode.COMPASS
		mCurrentMode = LocationMode.NORMAL;
		// 修改为自定义marker
		mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_geo);
		// 自定义的图标高分辨率被拉伸 暂时不设置了
		// mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
		// mCurrentMode, true, mCurrentMarker));

		// 设定中心点坐标 杭州
		LatLng cenpt = new LatLng(30.283774, 120.149287);
		// 定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(15)
				.build(); // 设置地图缩放比例：17级100米
		// 16级200米
		// 15级500米

		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		mBaiduMap.setMapStatus(mMapStatusUpdate);

		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setPriority(LocationClientOption.GpsFirst); // 设置gps优先
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000 * 5); // 设置发起定位请求的间隔时间单位毫秒 5秒定位一次
		mLocClient.setLocOption(option);

		// poi初始化
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(myPsrListenner);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(mySrListenner);

		// LBS云检索初始化
		// try {
		// // 获取百度密钥
		// ApplicationInfo appInfo = this.getPackageManager()
		// .getApplicationInfo(getPackageName(),
		// PackageManager.GET_META_DATA);
		// ak = appInfo.metaData.getString("com.baidu.lbsapi.API_KEY");
		// } catch (NameNotFoundException e) {
		// DebugLog.d(TAG, "AndroidManifest.xml读取从百度key失败");
		// e.printStackTrace();
		// }
		myCloudListener = new MyCloudListener();
		CloudManager.getInstance().init(myCloudListener);
		// 初始化10个标记图标
		bdArray[0] = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_marka);
		bdArray[1] = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_markb);
		bdArray[2] = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_markc);
		bdArray[3] = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_markd);
		bdArray[4] = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_marke);
		bdArray[5] = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_markf);
		bdArray[6] = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_markg);
		bdArray[7] = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_markh);
		bdArray[8] = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_marki);
		bdArray[9] = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_markj);

		// 最后开始定位
		mLocClient.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);

		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy(); // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView = null;
		// 销毁poi查询相关
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();

		// LBS云检索相关
		CloudManager.getInstance().destroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	/**
	 * poi搜索
	 * 
	 * @param location
	 */
	private void searchPoi(BDLocation location) {
		// 附近周报搜索方式 指定半径 经纬度
		PoiNearbySearchOption serchOption = new PoiNearbySearchOption();
		LatLng lat = new LatLng(location.getLatitude(), location.getLongitude());
		// 经度，维度
		serchOption.location(lat);
		serchOption.radius(radius);
		serchOption.keyword(keyword);
		serchOption.pageCapacity(pageCapacity);
		serchOption.pageNum(pageNum);
		mPoiSearch.searchNearby(serchOption);
	}

	/**
	 * LBS云搜索
	 * 
	 * @param longitude 纬度
	 *  @param latitude 经度
	 */
	private void searchLBS(double longitude, double latitude) {
		DebugLog.d(TAG, "LBS云搜索被调用");
		// 城市搜索
//		LocalSearchInfo info1 = new LocalSearchInfo();
//		info1.ak = ak;
//		info1.geoTableId = geoTableId;
//		// info.q = keyword;
//		info1.region = "杭州"; // 检索区域名称，必选。市或区的名字，如北京市，海淀区。最长25个字符。
//		DebugLog.d(TAG, "LBS云搜索城市： " + info1.region);
//		DebugLog.d(TAG, "定位城市： " + location.getCity());
//		CloudManager.getInstance().localSearch(info1);

		// 附近周报搜索方式 指定半径 经纬度
		 NearbySearchInfo info = new NearbySearchInfo();
		 info.ak = ak; // access_key（必须），最大长度50
		 info.geoTableId = geoTableId; // geo table 表主键（必须）
		 // info.q = keyword; // 检索关键字，可选。最长45个字符。。
		 // info.pageSize = pageCapacity; // 分页数量，可选，默认为10，最多为50
		 // info.pageIndex = pageNum; // 分页索引，可选，默认为0
		 // info.tags = "立马好"; // 标签，可选，空格分隔的多字符串，最长45个字符，样例：美食 小吃
		 info.radius = radius; // 半径
		 info.location = longitude + "," + latitude ; // 经纬度
		 DebugLog.d(TAG, info.location);
		 CloudManager.getInstance().nearbySearch(info); // "116.403689,39.914957"
	}

	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			mLocClient.stop(); // 搜索一次后停止
			DebugLog.d(TAG, "定位一次onReceiveLocation");
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(radius / 2)
					// location.getRadius()
					// 半径 设置1000米
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(0).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			
			// 地图状态变化设置监听 进行lbs检索
			mBaiduMap.setOnMapStatusChangeListener(myOnMapStatusChangeListener); 
			// 定位地图
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}

		public void onReceivePoi(BDLocation poiLocation) {

		}
	}

	/**
	 * LBS云搜索相关
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyCloudListener implements CloudListener {

		@Override
		public void onGetDetailSearchResult(DetailSearchResult result, int error) {
			showShortToast("LBS云搜索被调用   onGetDetailSearchResult");
			if (result != null) {
				if (result.poiInfo != null) {
					showShortToast(result.poiInfo.title);
				} else {
					showShortToast("状态:" + result.status);
				}
			}
		}

		@Override
		public void onGetSearchResult(CloudSearchResult result, int error) {
			if (result != null && result.poiList != null
					&& result.poiList.size() > 0) {
				DebugLog.d("LBS云搜索被调用   onGetSearchResult");
				DebugLog.d(TAG, "onGetSearchResult, result length: "
						+ result.poiList.size());
				mBaiduMap.clear();
				BitmapDescriptor bd = BitmapDescriptorFactory
						.fromResource(R.drawable.icon_gcoding);
				LatLng ll;
				LatLngBounds.Builder builder = new Builder();
				int i = 0;
				for (CloudPoiInfo info : result.poiList) {
					// 1-10个的标记图标
					if (i < bdArray.length) {
						bd = bdArray[i];
					}
					i++;
					ll = new LatLng(info.latitude, info.longitude);
					OverlayOptions oo = new MarkerOptions().icon(bd)
							.position(ll).title("[" + info.title + "]" + info.address);
					mBaiduMap.addOverlay(oo);
					builder.include(ll);
				}
				mBaiduMap.setOnMarkerClickListener(myLBSMarkerClick);
				//取消地图定位
//				LatLngBounds bounds = builder.build();
//				MapStatusUpdate u = MapStatusUpdateFactory
//						.newLatLngBounds(bounds);
//				mBaiduMap.animateMapStatus(u);
				
			} else {
				DebugLog.d("LBS云搜索被调用   onGetSearchResult  结果空");
			}
		}

	}

	/**
	 * 创建POI检索监听者
	 */
	private class MyOnGetPoiSearchResultListener implements
			OnGetPoiSearchResultListener {

		@Override
		public void onGetPoiDetailResult(PoiDetailResult result) {
			// 获取Place详情页检索结果
			if (result.error != SearchResult.ERRORNO.NO_ERROR) {
				showShortToast("抱歉，未找到结果");
			} else {
				showLongToast(result.getName() + ": " + result.getAddress());
			}

		}

		@Override
		public void onGetPoiResult(PoiResult result) {
			// 获取POI检索结果
			if (result == null
					|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				mBaiduMap.clear();
				PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(overlay);
				overlay.setData(result);
				overlay.addToMap();
				overlay.zoomToSpan();
				return;
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
				// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
				String strInfo = "在";
				for (CityInfo cityInfo : result.getSuggestCityList()) {
					strInfo += cityInfo.city;
					strInfo += ",";
				}
				strInfo += "找到结果";
				showLongToast(strInfo);
			}
		}

	}

	/**
	 * 创建在线建议查询监听者
	 * 
	 */
	private class MyOnGetSuggestionResultListener implements
			OnGetSuggestionResultListener {

		@Override
		public void onGetSuggestionResult(SuggestionResult res) {
			// 获取在线建议检索结果
			if (res == null || res.getAllSuggestions() == null) {
				return;
				// 未找到相关结果
			}
			// sugAdapter.clear();
			// for (SuggestionResult.SuggestionInfo info :
			// res.getAllSuggestions()) {
			// if (info.key != null)
			// sugAdapter.add(info.key);
			// }
			// sugAdapter.notifyDataSetChanged();
		}

	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}

	private class MyLBSMarkerClick implements OnMarkerClickListener {

		public MyLBSMarkerClick() {
		}

		@Override
		public boolean onMarkerClick(Marker mark) {
			// DetailSearchInfo info = new DetailSearchInfo();
			// info.ak = ak;
			// info.geoTableId = geoTableId;
			// //info.uid = poi.;
			// CloudManager.getInstance().detailSearch(info);
			showLongToast(mark.getTitle()); // 显示地址
			return true;
		}
	}
	
	private class MyOnMapStatusChangeListener implements OnMapStatusChangeListener{

		@Override
		public void onMapStatusChange(MapStatus arg0) {
			DebugLog.d(TAG, "onMapStatusChange");
		}

		@Override
		public void onMapStatusChangeFinish(MapStatus mapStatus) {
			DebugLog.d(TAG, "onMapStatusChangeFinish");
			if(!isLbsChage){ // 如果状态的变化不是 云搜索引起的则进行云搜索 否则说明正在搜索中
				isLbsChage = true; //标记正在云搜索
				searchLBS(mapStatus.target.longitude, mapStatus.target.latitude); // 周边云检索
			} else {
				isLbsChage = false; //标记云搜索结束
			}
		}

		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {
			DebugLog.d(TAG, "onMapStatusChangeStart");
		}
		
	}
}
