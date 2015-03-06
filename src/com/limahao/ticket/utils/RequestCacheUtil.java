package com.limahao.ticket.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;

import com.limahao.ticket.config.Configs;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.config.Urls;
import com.limahao.ticket.db.DBHelper;
import com.limahao.ticket.db.RequestCacheColumn;
import com.limahao.ticket.https.HttpUtils;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.sign.MD5;

//整个调用方法就是异步，这里不需要异步
public class RequestCacheUtil {
	private static final String TAG = "RequestCacheUtil";

	public static class Cache{
		public Cache(long t, String r){
			time = t;
			result = r;
		}
		long time;
		String result;
	}
	
	private static LinkedHashMap<String, SoftReference<Cache>> RequestCache = new LinkedHashMap<String, SoftReference<Cache>>(
			20);
	
/**
 * -------------------------------------------分割线----------------------------------------------------------------	
 */
	
	// [start] 公有方法
	public static String getRequestContent(Context context, String RequestUrl,
			String source_type, String content_type, boolean UseCache) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		String md5 = MD5.encode(RequestUrl);
																						// 缓存目录
		if (!CommonUtil.sdCardIsAvailable())/* true 为可用 */{
			String cachePath = context.getCacheDir().getAbsolutePath() + "/" + md5; // data里的缓存
			return getCacheRequest(context, RequestUrl, cachePath, source_type,
					content_type, dbHelper, UseCache);
		} else {
			String imagePath = getExternalCacheDir(context) + File.separator + md5; // sd卡
			return getCacheRequest(context, RequestUrl, imagePath, source_type,
					content_type, dbHelper, UseCache);
		}
	}
	
	public static String getCachDir(Context context){
		
		if (!CommonUtil.sdCardIsAvailable())/* true 为可用 */{
			return context.getCacheDir().getAbsolutePath() + File.separator ; // data里的缓存
		} else {
			return getExternalCacheDir(context) + File.separator ; // sd卡
		}
		 
	}
	
	// [end]

	// [start] 私有方法

	/**
	 * 获得程序在sd开上的cahce目录
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	@SuppressLint("NewApi")
	public static String getExternalCacheDir(Context context) {
		// android 2.2 以后才支持的特性
		if (hasExternalCacheDir()) {
			return context.getExternalCacheDir().getPath() + File.separator
					+ "request";
		}

		// Before Froyo we need to construct the external cache dir ourselves
		// 2.2以前我们需要自己构造
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/request/";
		return Environment.getExternalStorageDirectory().getPath() + cacheDir;
	}

	private static boolean hasExternalCacheDir() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}
	
	private static String getCacheRequest(Context context, String requestUrl,
			String requestPath, String source_type, String content_type,
			DBHelper dbHelper, boolean useCache) {
		// TODO Auto-generated method stub
		String result = "";
		if (useCache) {
			result = getStringFromSoftReference(requestUrl, content_type);
			if (!result.equals(null) && !result.equals("")) {
				return result;
			}
			result = getStringFromLocal(requestPath, requestUrl, dbHelper);
			if (!result.equals(null) && !result.equals("")) {
				putStringForSoftReference(requestUrl, result);
				return result;
			}
		}
		result = getStringFromWeb(context, requestPath, requestUrl,
				source_type, content_type, dbHelper);
		
		return result;
	}

	private static void putStringForSoftReference(String requestUrl,
			String result) {
		long timestamp = System.currentTimeMillis();
		Cache cache = new Cache(timestamp, result);
		SoftReference<Cache> referece = new SoftReference<Cache>(cache);
		RequestCache.put(requestUrl, referece);
	}

	/**
	 * 清除request请求缓存
	 * @return
	 */
	public static void clearCacheRequest(Context context, String RequestUrl){
		DBHelper dbHelper = DBHelper.getInstance(context);
		String md5 = MD5.encode(RequestUrl);
		String requestPath = "";
		
		// 清楚内存缓存
		deleteStringFromSoftReference(RequestUrl);
		
		// 清楚本地缓存文件
		// 取得缓存路径
		if (!CommonUtil.sdCardIsAvailable())/* true 为可用 */{
			 requestPath = context.getCacheDir().getAbsolutePath() + "/" + md5; // data里的缓存
		} else {
			 requestPath = getExternalCacheDir(context) + File.separator + md5; // sd卡
		}
		deleteStringFromLocal(requestPath, RequestUrl, dbHelper);
	}
	
	private static String getStringFromWeb(Context context, String requestPath,
			String requestUrl, String source_type, String content_type,
			DBHelper dbHelper) {
		// TODO Auto-generated method stub
		String result = "";
		try {
			result = HttpUtils.getByHttpClient(context, requestUrl);
			if (result.equals(null) || result.equals("")) {
				return result;
			}
			/**
			 * 请求出错的时候不缓存
			 *  "ERR_INFO": { "ERROR_CODE": "00000000","ERROR_MSG": "提交数据成功"},
  			 *	"RET_INFO": {}
			 */
			if (result.indexOf(Urls.REQ_SUCESS) == -1) {
				DebugLog.d(TAG, "请求出错的时候不缓存");
				return result;
			}
			// 更新数据库
			Cursor cursor = getStringFromDB(requestUrl, dbHelper);
			updateDB(cursor, requestUrl, source_type, content_type, dbHelper);
			saveFileByRequestPath(requestPath, result);
			putStringForSoftReference(requestUrl, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static void saveFileByRequestPath(String requestPath, String result) {
		// TODO Auto-generated method stub
		deleteFileFromLocal(requestPath);
		saveFileForLocal(requestPath, result);
	}

	private static void saveFileForLocal(String requestPath, String result) {
		// TODO Auto-generated method stub
		File file = new File(requestPath);
		if (!file.exists()) {
			try {
				File parentFile = file.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				file.createNewFile();
				FileOutputStream fout = new FileOutputStream(file);
				byte[] buffer = result.getBytes();
				fout.write(buffer);
				fout.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void updateDB(Cursor cursor, String requestUrl,
			String source_type, String content_type, DBHelper dbHelper) {
		if (cursor.moveToFirst()) {
			// 更新
			int id = cursor.getInt(cursor
					.getColumnIndex(RequestCacheColumn._ID));
			long timestamp = System.currentTimeMillis();
			String SQL = "update " + RequestCacheColumn.TABLE_NAME + " set "
					+ RequestCacheColumn.Timestamp + "=" + timestamp
					+ " where " + RequestCacheColumn._ID + "=" + id;
			dbHelper.ExecSQL(SQL);
		} else {
			// 添加
			String SQL = "insert into " + RequestCacheColumn.TABLE_NAME + "("
					+ RequestCacheColumn.URL + ","
					+ RequestCacheColumn.SOURCE_TYPE + ","
					+ RequestCacheColumn.Content_type + ","
					+ RequestCacheColumn.Timestamp + ") values('" + requestUrl
					+ "','" + source_type + "','" + content_type + "','"
					+ System.currentTimeMillis() + "')";
			dbHelper.ExecSQL(SQL);
		}
	}

	private static String getStringFromSoftReference(String requestUrl, String content_type) {
		if (RequestCache.containsKey(requestUrl)) {
			SoftReference<Cache> reference = RequestCache.get(requestUrl);
			Cache cache = (Cache) reference.get();
			if (cache != null && !Utility.isNullOrEmpty(cache.result)) {
				long span = getSpanTimeFromConfigs(content_type);
				long nowTime = System.currentTimeMillis();
				if ((nowTime - cache.time) > span * 1000 ) {
					// 过期 删除
					RequestCache.remove(requestUrl);
					// 没过期 
				} else {
					return cache.result;
				}
			}
		}
		return "";
	}
	
	/**
	 * 清楚内存缓存
	 * @param requestUrl
	 * @return
	 */
	private static void deleteStringFromSoftReference(String requestUrl) {
		if (RequestCache.containsKey(requestUrl)) {
//			SoftReference<Cache> reference = RequestCache.get(requestUrl);
//			Cache cache = (Cache) reference.get();
//			if (cache != null) {
//				reference.clear();
//			}
			RequestCache.remove(requestUrl);
		}
	}
	

	private static String getStringFromLocal(String requestPath,
			String requestUrl, DBHelper dbHelper) {
		String result = "";
		Cursor cursor = getStringFromDB(requestUrl, dbHelper);
		if (cursor.moveToFirst()) {
			Long timestamp = cursor.getLong(cursor
					.getColumnIndex(RequestCacheColumn.Timestamp));
			String strContentType = cursor.getString(cursor
					.getColumnIndex(RequestCacheColumn.Content_type));
			long span = getSpanTimeFromConfigs(strContentType);
			long nowTime = System.currentTimeMillis();
			if ((nowTime - timestamp) > span * 1000 ) {
				// 过期
				deleteFileFromLocal(requestPath);
			} else {
				// 没过期
				result = getFileFromLocal(requestPath);
			}
		}
		return result;
	}
	
	/**
	 * 清楚本地缓存
	 * @param requestPath
	 * @param requestUrl
	 * @param dbHelper
	 */
	private static void deleteStringFromLocal(String requestPath,
			String requestUrl, DBHelper dbHelper){
		
		// 删除数据库记录
		deleteStringFromDB(requestUrl, dbHelper);
		// 删除本地缓存文件
		deleteFileFromLocal(requestPath);
	}
	
	/**
	 * 从db中删除数据
	 * 
	 * @param requestUrl
	 * @param dbHelper
	 * @return DELETE FROM Person WHERE LastName = 'Wilson' 
	 */
	private static void deleteStringFromDB(String requestUrl, DBHelper dbHelper) {
		String SQL = "delete from " + RequestCacheColumn.TABLE_NAME 
				+ " where "+ RequestCacheColumn.URL + "='" + requestUrl + "'";
		dbHelper.ExecSQL(SQL);
	}
	
	/**
	 * 从db中查找数据
	 * 
	 * @param requestUrl
	 * @param dbHelper
	 * @return
	 */
	private static Cursor getStringFromDB(String requestUrl, DBHelper dbHelper) {
		String SQL = "select * from " + RequestCacheColumn.TABLE_NAME
				+ " where " + RequestCacheColumn.URL + "='" + requestUrl + "'";
		Cursor cursor = dbHelper.rawQuery(SQL, new String[] {});
		return cursor;
	}

	private static String getFileFromLocal(String requestPath) {
		// TODO Auto-generated method stub
		File file = new File(requestPath);
		String result = "";
		if (file.exists()) {
			FileInputStream fileIn;
			try {
				fileIn = new FileInputStream(file);

				int length = fileIn.available();
				byte[] buffer = new byte[length];
				fileIn.read(buffer);
				fileIn.close();
				result = EncodingUtils.getString(buffer, "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
		return "";
	}

	private static void deleteFileFromLocal(String requestPath) {
		// TODO Auto-generated method stub
		File file = new File(requestPath);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 根据类型获取缓存时间
	 * 
	 * @param str
	 * @return
	 */
	private static long getSpanTimeFromConfigs(String str) {
		long span = 0;
		if (str.equals(Constants.DBContentType.Content_list)) {
			span = Configs.Content_ListCacheTime;
		} else if (str.equals(Constants.DBContentType.Content_content)) {
			span = Configs.Content_ContentCacheTime;
		} else if (str.equals(Constants.DBContentType.Content_initdata)) {
			span = Configs.Content_InitDataCacheTime;
		} else if (str.equals(Constants.DBContentType.Discuss)) {
			span = Configs.DiscussCacheTime;
		} else if (str.equals(Constants.DBContentType.Content_search)){
	    	span = Configs.Content_SearchCacheTime;
		} else {
			span = Configs.Content_DefaultCacheTime;
		}
		return span;
	}
	// [end]

}
