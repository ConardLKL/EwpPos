package com.limahao.ticket;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.limahao.ticket.config.Configs;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.db.provider.UserInfoDB;
import com.limahao.ticket.entity.ItemUserInfo;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.ui.SplashScreen;
//import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
//import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.nostra13.universalimageloader.core.assist.ImageScaleType;
//import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.limahao.ticket.utils.Utility;

public class BaseApplication extends Application implements  Thread.UncaughtExceptionHandler{

	private String TAG = "BaseApplication";
	private ItemUserInfo userInfo = null;
	private Map<String, Object> params = null;
	public Map<String, Object> getParams(){
		if(params == null) {
			params = new HashMap<String, Object>();
		}
		return params;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	//	initImageLoader(getApplicationContext());
		Thread.setDefaultUncaughtExceptionHandler(this); 
	}
	/**
	 *当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
     * 设置本地图片缓存
	 
	public  void initImageLoader(Context context) {
		DisplayImageOptions defaultsOptions=new DisplayImageOptions.
				Builder().cacheInMemory(true).cacheOnDisc(true)
				// 1在显示选项中使用。imageScaleType（ImageScaleType.IN_SAMPLE_INT）。或尝试。imageScaleType（ImageScaleType.EXACTLY）。
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT) 
				// 2使用。bitmapConfig（Bitmap.Config.RGB_565）在显示选项。在RGB_565位图占用比ARGB_88882倍的内存更少。
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.defaultDisplayImageOptions(defaultsOptions)
				// 4减少配置线程池的大小（。threadPoolSize（...））。 1 - 5建议。
				.threadPriority(Thread.NORM_PRIORITY - 2)
				//.denyCacheImageMultipleSizesInMemory()s
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 3易用。的MemoryCache（新WeakMemoryCache（））在内存配置或禁用缓存在所有的显示选项（不调用。cacheInMemory（））。
				.memoryCache(new WeakMemoryCache())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				//.enableLogging() // Not necessary in common//发布时请删除，有可能报Null mx2报错//1.8.6包，把这句删除
				//.discCache(LimitedAgeDiscCache())
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		
		// 避免使用RoundedBitmapDisplayer。它的工作过程中显示创建带有ARGB_8888配置新的Bitmap对象。
	}*/
	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}
	
	/**
	 * 是否有消息通知
	 * return 有ture 没有 false
	 */
	public boolean isNotice(){
		String key = "";
		// 跟帐号关联
		if(getUserInfo() != null){
			key = userInfo.getPHONE() + Constants.ParamKey.Key_Notice;
		}
		if(!Utility.isNullOrEmpty(Configs.getAppConfig(this).get(key))){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 设置消息通知
	 * param：通知 ture 取消通知 false
	 */
	public void setNotice(boolean isNotice){
		String key = "";
		// 跟帐号关联
		if(getUserInfo() != null){
			key = userInfo.getPHONE() + Constants.ParamKey.Key_Notice;
		}
		if(isNotice){
			Configs.getAppConfig(this).set(key,"1");
		} else {
			Configs.getAppConfig(this).remove(key);
		}
	}
	
	
	/**
	 * 判断是否登陆
	 * 
	 * @return
	 */
	public boolean isLogin() {
		if (getUserInfo() != null) {
			return true;
		}
		return false;
	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	public ItemUserInfo getUserInfo() {
		if(userInfo == null){
			initLogin();
		}
		return userInfo;
	}

	/**
	 * 初期化登录
	 */
	public void initLogin() {
		try {
			UserInfoDB db = new UserInfoDB(this);
			userInfo = db.queryUserInfo();
			db.dbClose();
			DebugLog.d("初期化登录状态： " + userInfo == null?"否":"是");
		} catch (Exception e) {
			e.printStackTrace();
			DebugLog.d(TAG, e.getMessage());
		}
	}

	/**
	 * 用户注销
	 * 
	 * @return
	 */
	public void LoginOut() {
		try {
			this.userInfo = null;
			UserInfoDB db = new UserInfoDB(this);
			db.deleteUserInfo();
			db.dbClose();
		} catch (Exception e) {
			e.printStackTrace();
			DebugLog.d(TAG, e.getMessage());
		}
	}

	/**
	 * 保存更新用户信息
	 * 
	 * @return
	 */
	public void UpdateUserInfo(ItemUserInfo userInfo) {
		try {
			if (userInfo != null) {
				UserInfoDB db = new UserInfoDB(this);
				db.updateUserInfo(userInfo);
				db.dbClose();
				initLogin(); //更新完毕重新读取用户信息
			}
		} catch (Exception e) {
			e.printStackTrace();
			DebugLog.d(TAG, e.getMessage());
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		 System.out.println("uncaughtException"); 
		 ex.printStackTrace();
		 Intent intent = new Intent(this, SplashScreen.class); 
         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
         Intent.FLAG_ACTIVITY_NEW_TASK); 
         startActivity(intent); 
         AppManager.getAppManager().AppExit(this);
	}
	
}
