package com.limahao.ticket.utils;

import java.io.InputStream;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.Xml;
import com.limahao.ticket.BaseApplication;
import com.limahao.ticket.config.Constants;
//import com.ewp.pos.entity.CityEntity;
//import com.ewp.pos.entity.ProviceEntity;
import com.limahao.ticket.sign.MD5;

public class Utility {
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	private static final int MAX_NONCE = 0 + 10;

	private static final String LABEL_App_sign = "api_sign";
	private static final String LABEL_TIME = "timestamp";
	private static final String LABEL_NONCE = "nonce";
	private static final String LABEL_UID = "uid";

	private static final SecureRandom sRandom = new SecureRandom();

	private static char sHexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String getNonce() {
		byte[] bytes = new byte[MAX_NONCE / 2];
		sRandom.nextBytes(bytes);
		return hexString(bytes);
	}

	public static String hexString(byte[] source) {
		if (source == null || source.length <= 0) {
			return "";
		}

		final int size = source.length;
		final char str[] = new char[size * 2];
		int index = 0;
		byte b;
		for (int i = 0; i < size; i++) {
			b = source[i];
			str[index++] = sHexDigits[b >>> 4 & 0xf];
			str[index++] = sHexDigits[b & 0xf];
		}
		return new String(str);
	}

	private static long getTimestamp() {
		Date date = new Date();
		long i = date.getTime();
		return i;
	}

	private static String getAPIsig(String key, long timestamp, String nonce,
			String uid) {
		// api_sig =
		// MD5("api_key"+@api_key+"nonce"+@nonce+"timestamp"+@timestamp)
		String result = null;
		StringBuilder builder = new StringBuilder();
		synchronized (builder) {
			builder.append(key);
			builder.append(timestamp);
			builder.append(nonce);
			builder.append(uid);
			result = MD5.encode(builder.toString());
			builder.delete(0, builder.length());
		}
		return result;
	}

	/**
	 * &…………………………
	 * 
	 * @param key
	 * @return
	 */
	public static String getParams(String key) {
		String result = "";
		try {
			String[] temp = key.split(":");
			long timestamp = getTimestamp();
			String nonce = getNonce();
			String api_sign = getAPIsig(key, timestamp, nonce, temp[1]);

			StringBuilder builder = new StringBuilder();

			synchronized (result) {
				builder.append(String.format("&" + LABEL_UID + "=%s", temp[1]));
				builder.append(String.format("&" + LABEL_NONCE + "=%s", nonce));
				builder.append(String.format("&" + LABEL_TIME + "=%s",
						timestamp));
				builder.append(String.format("&" + LABEL_App_sign + "=%s",
						api_sign));
				result = builder.toString();
				builder.delete(0, builder.length());
			}
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public static String getScreenParams(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return "&screen="
				+ (dm.heightPixels > dm.widthPixels ? dm.widthPixels + "*"
						+ dm.heightPixels : dm.heightPixels + "*"
						+ dm.widthPixels);
	}

	/**
	 * 检查字符串是null或者空
	 * 
	 * @param str
	 * @return true | false
	 */
	/*
	 * public static Boolean isNullOrEmpty(String str) { if (str == null ||
	 * "".equals(str) || "null".equals(str)) { return true; } return false; }
	 */
	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isNullOrEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public static int getNetworkType(Context context) {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!isNullOrEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	// /**
	// * 解析得到城市列表 xml pull解析
	// *
	// * @param context
	// * @return ProvinceEntity 省
	// */
	// public static List<ProviceEntity> getCityAndProvince(Context context) {
	//
	// Map<String, List<String>> dataMap = new HashMap<String, List<String>>();
	// AssetManager am = null;
	// am = context.getAssets();
	//
	// List<ProviceEntity> provices = null;
	// try {
	// InputStream inStream = am.open("city.xml");
	// XmlPullParser parser = Xml.newPullParser();
	// parser.setInput(inStream, "UTF-8");
	// int eventType = parser.getEventType();
	//
	// List<CityEntity> citys = null;
	// CityEntity currentCity = null;
	// ProviceEntity currentProvice = null;
	// while (eventType != XmlPullParser.END_DOCUMENT) {
	// switch (eventType) {
	// case XmlPullParser.START_DOCUMENT:
	// provices = new ArrayList<ProviceEntity>();
	// break;
	// case XmlPullParser.START_TAG:
	// //MyLog.i("Log"+ parser.getName());
	// String name = parser.getName();
	// if (name.equalsIgnoreCase("element")) {
	// currentProvice = new ProviceEntity();
	// } else if (currentProvice != null) {
	// if (name.equalsIgnoreCase("name")
	// && currentCity == null) {
	// currentProvice.setName(parser.nextText());// 如果后面是Text节点,即返回它的值
	// } else if (name.equalsIgnoreCase("value")
	// && currentCity == null) {
	// currentProvice.setValue(parser.nextText());
	// } else if (name.equalsIgnoreCase("children")) {
	// citys = new ArrayList<CityEntity>();
	// } else if (name.equalsIgnoreCase("child")) {
	// currentCity = new CityEntity();
	// } else if (currentCity != null) {
	// if (name.equalsIgnoreCase("name")) {
	// currentCity.setName(parser.nextText());
	// } else if (name.equalsIgnoreCase("value")) {
	// currentCity.setValue(parser.nextText());
	// }
	// }
	// }
	//
	// /*
	// * if (name.equalsIgnoreCase("person")) { currentPerson =
	// * new Person(); currentPerson.setId(new
	// * Integer(parser.getAttributeValue(null, "id"))); } else if
	// * (currentPerson != null) { if
	// * (name.equalsIgnoreCase("name")) {
	// * currentPerson.setName(parser.nextText());//
	// * 如果后面是Text节点,即返回它的值 } else if
	// * (name.equalsIgnoreCase("age")) { currentPerson.setAge(new
	// * Short(parser.nextText())); } }
	// */
	// break;
	// case XmlPullParser.END_TAG:
	// if (parser.getName().equalsIgnoreCase("element")
	// && currentProvice != null) {
	// provices.add(currentProvice);
	// currentProvice = null;
	// } else if (parser.getName().equalsIgnoreCase("child")
	// && currentProvice != null) {
	// citys.add(currentCity);
	// currentCity = null;
	// } else if (parser.getName().equalsIgnoreCase("children")) {
	// currentProvice.setChild(citys);
	// }
	// break;
	// }
	// eventType = parser.next();
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return provices;
	//
	// }

	// 获取系统时间
	public static String GetSystime() {
		String systime;
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		Date date = c.getTime();
		systime = df.format(date);
		return systime;
	}

	/**
	 * 获取货币类型格式
	 * 
	 * @return
	 */
	public static NumberFormat getMoneyFormt() {
		NumberFormat currency = NumberFormat.getNumberInstance();
		// currency.setMaximumIntegerDigits(6); // 设置数的整数部分所允许的最大位数 显示返回的数据上限不设置
		currency.setMinimumFractionDigits(2); // 设置数的小数部分所允许的最小位数(如果不足后面补0)
		currency.setMaximumFractionDigits(2); // 设置数的小数部分所允许的最大位数(如果超过会四舍五入)
		return currency;
	}

	/**
	 * 获取手机唯一标识 imei码 需要权限 <uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 * 
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
}
