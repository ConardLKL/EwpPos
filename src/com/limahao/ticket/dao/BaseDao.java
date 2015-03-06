package com.limahao.ticket.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;

import com.limahao.ticket.config.Configs;
//import com.limahao.ticket.plugein.PayPlugIn;
//import com.limahao.ticket.plugein.PayPlugIn.State;
import com.limahao.ticket.config.Constants;
import com.limahao.ticket.entity.ItemTicker;
import com.limahao.ticket.entity.ItemTickerOrder;
import com.limahao.ticket.entity.ResponseTicker;
import com.limahao.ticket.entity.ResponseTickerOrder;
import com.limahao.ticket.log.DebugLog;
import com.limahao.ticket.sign.MD5;
import com.limahao.ticket.utils.JsonUtil;
import com.limahao.ticket.utils.RequestCacheUtil;
import com.limahao.ticket.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BaseDao {
	protected Gson gson;
	protected boolean isBase64;
	// protected PayPlugIn sign;
	protected Activity mActivity;
	protected static String ime;

	// public BaseDao(){
	// gson = getGson();
	// };

	public BaseDao(Activity activity) {
		mActivity = activity;
		gson = getGson();
		// sign = PayPlugIn.getInstance(activity);
	}

	private Gson getGson() {

		GsonBuilder builder = new GsonBuilder();
		// 不进行html escape转义 针对base64编码
		builder.disableHtmlEscaping();
		// 所有的字符串都进行base64处理
		// base64额外进行处理，这里不做适配
		// builder.registerTypeAdapter(String.class, new StringGsonAdaptpsor());

		// base64方法激活设置
		isBase64 = false;
		// return builder.create();
		return new Gson();
	}

	/**
	 * 拼接 url参数
	 * 
	 * @return 返回小写的url get方式的拼接参数
	 */
	public String getParamUrl(HashMap<String, String> params) {
		StringBuilder sb = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			int i = 0;
			for (Map.Entry<String, String> param : params.entrySet()) {
				// key不当参数传
				if ("key".equals(param.getKey())) {
					continue;
				}
				if (i > 0) {
					sb.append("&");
				}
				try {
					// 传入的参数名称转小写 参数值用urlencoder编码
					sb.append(String.format("%s=%s", param.getKey()
							.toLowerCase(), URLEncoder.encode(param.getValue(),
							Constants.DEFAULT_URL_ENCODING)));
				} catch (UnsupportedEncodingException e) {
					DebugLog.d(e.getMessage());
					e.printStackTrace();
				}
				i++;
			}
		}
		return sb.toString();
	}

	/**
	 * url签名
	 * 
	 * @param source
	 * @return
	 */
	public String sign(HashMap<String, String> params, String... order) {
		StringBuilder sb = new StringBuilder();
		String paramUrl = "";
		// 参数不为空
		if (params != null && !params.isEmpty() && order != null
				&& order.length > 0) {
			for (int i = 0; i < order.length; i++) {
				if (i > 0) {
					sb.append("&");
				}
				sb.append(String.format("%s=%s", order[i], params.get(order[i])));
			}
		}
		// 签名的时候key和value都要小写，所以整串转小写
		paramUrl = sb.toString().toLowerCase();
		DebugLog.d("签名的url:" + paramUrl);
		if (!Utility.isNullOrEmpty(paramUrl)) {
			String sign = MD5.encode(paramUrl);
			return "&sign=" + sign;
		}
		return "";
	}

	/**
	 * 设置默认参数
	 * 
	 * @param params
	 */
	private void setDefaultParams(HashMap<String, String> params) {
		if (params != null) {
			params.put("terminalid", Configs.terminalid);
			params.put("agentid", Configs.agentid);
			params.put("datatype", Configs.datatype);
			params.put("key", Configs.key);
			params.put("devid", getIme());
		}
	}

	/**
	 * 获取手机唯一识别码
	 * 
	 * @return
	 */
	private String getIme() {
		if (ime == null) {
			ime = Utility.getDeviceId(mActivity);
		}
		return ime;
	}

	/**
	 * 参数以get网络请求
	 * 
	 * @param param
	 *            ParamBase 类型 参数
	 * @param url
	 *            请求地址
	 * @param respCls
	 *            返回的数据封装类型
	 * @param useCache
	 *            是否进行缓存true 是 false 否
	 * @param content_type
	 *            请求的内容形式 跟缓存时间有关
	 * @return 数据封装类型
	 */

	public <T> T get(String url, Class<T> respCls, boolean useCache,
			String content_type, HashMap<String, String> params,
			String... order) {
		T reponse = null;
		try {
			setDefaultParams(params);
			String paramUrl = getParamUrl(params);
			// 参数不为空
			if (!Utility.isNullOrEmpty(paramUrl)) {
				paramUrl = paramUrl + sign(params, order); // 签名
				url = url + "?" + paramUrl;
			}
			DebugLog.d("请求前的url:" + url);
			String result = RequestCacheUtil.getRequestContent(mActivity, url,
					Constants.WebSourceType.Json, content_type, useCache);
			DebugLog.d("请求返回的结果:" + result);
			if (Utility.isNullOrEmpty(result)) {
				return null;
			}

			result = base64DecodeJson(result);

			// 查询订单列表时当数据只有一条是返回的json格式不是数组，比较坑，所以只能写这些恶心的代码了
			/**
			 * 多条格式
			 *  "RET_INFO": {"Trade_Order_LIST":   [
        	 * 								{    },
        	 * 								{    }  
        	 * 			]}
        	 * 
        	 * 单条格式
        	 * 
        	 * "RET_INFO": {"Trade_Order_LIST": {"TradeOrder":   {}}
  			 *				}
  			 *				
  			 * 正常单条格式	
  			 *		
  			 * "RET_INFO": {"Trade_Order_LIST": [{    }]
  			 *				}	
  			 *
  			 *{\"TradeOrder\":   {
			 */
			if (ResponseTickerOrder.class.equals(respCls)) {
				String replaceReg = "{\"TradeOrder\":   {";
				// 找到需要替换的
				if(result.indexOf(replaceReg) != -1){
					result = result.replace(replaceReg, "[{");
					result = result.replace("}}", "}]");
				}
				DebugLog.d("ResponseTickerOrder单条：" + result);
				// 查询车次列表时当数据只有一条是返回的json格式不是数组，比较坑，所以只能写这些恶心的代码了
			} else if (ResponseTicker.class.equals(respCls)) { 
				/**
				 *    "RET_INFO": {"BUS_TRIP_LIST": {"BUSTRIP":   {
  				 *		}}}
				 */
				String replaceReg = " {\"BUSTRIP\":   {";
				// 找到需要替换的
				if(result.indexOf(replaceReg) != -1){
					result = result.replace(replaceReg, "[{");
					result = result.replace("}}", "}]");
				}
				DebugLog.d("ResponseTicker单条：" + result);
			}
			
			reponse = gson.fromJson(result, respCls);
			return reponse;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return reponse;
	}

	// /**
	// * 参数以json格式的post网络请求
	// * @param param ParamBase 类型 参数
	// * @param url 请求地址
	// * @param respCls 返回的数据封装类型
	// * @param useCache 是否进行缓存true 是 false 否
	// * @param content_type 请求的内容形式 跟缓存时间有关
	// * @return 数据封装类型
	// */
	//
	// public <T> T post(ParamBase param, String url, Class<T> respCls,
	// boolean useCache, String content_type) {
	// T reponse = null;
	// try {
	// String jsonParam = "";
	//
	// if (param != null) {
	// jsonParam = gson.toJson(param);
	// jsonParam = base64EncodeJson(jsonParam);
	// }
	//
	// String result = RequestCacheUtil.getRequestContent(mActivity, url,
	// jsonParam, Constants.WebSourceType.Json,
	// content_type, useCache);
	//
	// if (Utility.isNullOrEmpty(result)) {
	// return null;
	// }
	//
	// result = base64DecodeJson(result);
	// reponse = gson.fromJson(result, respCls);
	//
	// return reponse;
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return reponse;
	// }

	/**
	 * 参数以加密json格式的post网络请求
	 * 
	 * @param param
	 *            ParamBase 类型 参数
	 * @param url
	 *            请求地址
	 * @param respCls
	 *            返回的数据封装类型
	 * @param useCache
	 *            是否进行缓存true 是 false 否
	 * @param content_type
	 *            请求的内容形式 跟缓存时间有关
	 * @return 数据封装类型
	 */
	// public <T> T encryptPost(ParamBase param, String url, Class<T> respCls,
	// boolean useCache, String content_type) {
	// T reponse = null;
	// String jsonParam = gson.toJson(param);
	// // 加密
	// jsonParam = encrypt(jsonParam);
	// try {
	// String result = RequestCacheUtil.getRequestContent(mActivity, url,
	// jsonParam, Constants.WebSourceType.Json,
	// content_type, useCache);
	//
	// if (Utility.isNullOrEmpty(result)) {
	// return null;
	// }
	//
	// try {
	// // json解析 没发生异常 说明返回的是json格式登录出错
	// new JSONObject(result);
	// result = base64DecodeJson(result);
	// reponse = gson.fromJson(result, respCls);
	// } catch (JSONException jsonEx) {
	// // json格式发生异常 说明是加密
	// result = decrypt(result);
	// }
	//
	// reponse = gson.fromJson(result, respCls);
	//
	// return reponse;
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return reponse;
	// }

	/**
	 * base64编码
	 * 
	 * @param json
	 * @return
	 */
	public String base64EncodeJson(String json) {
		if (isBase64 && !Utility.isNullOrEmpty(json)) {
			String result = "";
			JSONObject jsono;
			try {
				jsono = new JSONObject(json);
				DebugLog.d("base64编码前源JSON:" + json);
				// base64编码
				result = JsonUtil.getBase64EecodeJSONObject(jsono).toString();
				DebugLog.d("base64编码码后JSON:" + result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
		return json;
	}

	/**
	 * base64解码
	 * 
	 * @param json
	 * @return
	 */
	public String base64DecodeJson(String json) {
		if (isBase64 && !Utility.isNullOrEmpty(json)) {
			String result = "";
			JSONObject jsono;
			try {
				jsono = new JSONObject(json);
				DebugLog.d("base64解码前源JSON:" + json);
				// base64解码
				result = JsonUtil.getBase64DecodeJSONObject(jsono).toString();
				DebugLog.d("base64解码前后JSON:" + result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
		return json;
	}

	/**
	 * rsa+3des加密 （和解密一起使用必须并在一个线程内）
	 * 
	 * @param source
	 *            json格式
	 * @return rsa+3des加密 过后并用base64编码过的json格式
	 */
	// public String encrypt(String source) {
	// if (Utility.isNullOrEmpty(source)) {
	// return source;
	// }
	// String encrypt = "";
	//
	// // 初期化
	// if (sign.initEncryptSo() != State.INIT_SUCCESS) {
	// DebugLog.d("加密插件初期化失败");
	// return encrypt;
	// }
	// // base64编码过后的
	// String base64json = base64EncodeJson(source);
	//
	// // 加密
	// encrypt = sign.doEncodeHttpMessage(base64json);
	//
	// DebugLog.d("加密后格式：" + encrypt);
	//
	// return encrypt;
	// }

	/**
	 * 解密rsa+3des （和加密一起使用必须并在一个线程内）
	 * 
	 * @param encrypt
	 *            rsa+3des加密的密文
	 * @return 解密后的数据
	 */
	// public String decrypt(String encrypt) {
	// if (Utility.isNullOrEmpty(encrypt)) {
	// return encrypt;
	// }
	//
	// String decrypt = "";
	// DebugLog.d("解密前格式：" + encrypt);
	// decrypt = sign.doDecodeHttpMessage(encrypt);
	// DebugLog.d("解密后格式：" + decrypt);
	// sign.exitEncryptSo();
	//
	// return base64DecodeJson(decrypt);
	// }
}
