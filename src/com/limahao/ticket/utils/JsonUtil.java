package com.limahao.ticket.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.limahao.ticket.log.DebugLog;

/**
 * json相关的一些操作类.
 * @author email: <a href="bentengwu@163.com">徐伟宏</a>
 * @version :
 * @Title: JsonUtil.java
 * @date 2013-5-27 下午05:27:16
 * @Description: json的支持类
 */
public class JsonUtil {
	 
    /**
     * 将流转化为JSONobject 如果不是JSON格式,则返回null.
     *<br />
     *@date 2013-11-27 上午11:33:06
     *@author <a href="bentengwu@163.com">伟宏</a>
     *@param data	
     *@return	不是JSON 返回 NULL.
     *			是JSON   返货JSONObject.
     */
    public static JSONObject getJSONObject(byte[] data){
    	try{
    		JSONObject json = new JSONObject(new String(data));
    		return json;
    	}catch(JSONException jsonEx){
    		return null;
    	}
    }
    
    /**
     * 将流转化为JSONArray 如果不是JSONArray格式,则返回null.
     *<br />
     *@date 2013-11-27 上午11:33:06
     *@author <a href="bentengwu@163.com">伟宏</a>
     *@param data	
     *@return		不是JSONArray 返回 NULL.
     *				是JSONArray   返货JSONArray.
     */
    public static JSONArray getJSONArray(byte[] data){
    	try{
    		JSONArray array = new JSONArray(new String(data));
    		return array;
	    }catch(JSONException jsonEx){
			return null;
		}
    }
    
    /**
     * 对JSONArray进行base64解码.
     *<br />
     *@date 2013-11-27 下午1:41:55
     *@author <a href="bentengwu@163.com">伟宏</a>
     *@param jsonArray
     *@return
     */
    private static JSONArray getBase64DecodeJSONArray(JSONArray jsonArray){
    	try{
    		JSONArray resArray = new JSONArray();
    		for(int j=0;j<jsonArray.length();j++){
    			Object obj = jsonArray.get(j);
    			if(obj!=null){
    				byte[] base64ArrayItemData = EncodeUtils.base64Decode(obj.toString());
    				JSONObject jsonObject = getJSONObject(base64ArrayItemData);
    				if(jsonObject!=null){
    					resArray.put(j, getBase64DecodeJSONObject(jsonObject));
    				}else{
    					JSONArray array = getJSONArray(base64ArrayItemData);
    					if(array!=null){
    						resArray.put(j,getBase64DecodeJSONArray(array));
    					}else{
    						resArray.put(j,new String(base64ArrayItemData));
    					}
    				}
    			}else{
    				resArray.put(j,"");
    			}
    		}
    		return resArray;
    	}catch(Exception jsonEx){
    		DebugLog.e(jsonEx.getMessage());
    		throw new RuntimeException("", jsonEx);
    	}
    }
    
    private static JSONArray getBase64EecodeJSONArray(JSONArray jsonArray){
    	try{
    		JSONArray resArray = new JSONArray();
    		for(int j=0;j<jsonArray.length();j++){
    			Object obj = jsonArray.get(j);
    			if(obj!=null){
    				byte[] base64ArrayItemData = obj.toString().getBytes();
    				JSONObject jsonObject = getJSONObject(base64ArrayItemData);
    				if(jsonObject!=null){
    					resArray.put(j, EncodeUtils.base64Encode(getBase64EecodeJSONObject(jsonObject).toString().getBytes()));
    				}else{
    					JSONArray array = getJSONArray(base64ArrayItemData);
    					if(array!=null){
    						resArray.put(j,EncodeUtils.base64Encode(getBase64EecodeJSONArray(array).toString().getBytes()));
    					}else{
    						resArray.put(j,EncodeUtils.base64Encode(base64ArrayItemData));
    					}
    				}
    			}else{
    				resArray.put(j,"");
    			}
    		}
    		return resArray;
    	}catch(Exception jsonEx){
    		DebugLog.e(jsonEx.getMessage());
    		throw new RuntimeException("", jsonEx);
    	}
    }
    
    
	public static JSONObject getBase64DecodeJSONObject(JSONObject json){
    	try{
    		JSONArray jsonArray  = json.names();
        	for(int i=0;i<jsonArray.length();i++){
        		Object base64Object = json.get(jsonArray.getString(i));
        		if(base64Object!=null){
        			byte[] base64Data = EncodeUtils.base64Decode(base64Object.toString());
        			JSONObject jsonObj = getJSONObject(base64Data);
        			if(jsonObj!=null){
        				/**********************值对象是JSON||2013-11-27********************/
        				json.put(jsonArray.getString(i), getBase64DecodeJSONObject(jsonObj));
        			}else{
        				JSONArray jsonArrays = getJSONArray(base64Data);
        				if(jsonArrays!=null){
        					/**********************是JSON数组||2013-11-27********************/
        					json.put(jsonArray.getString(i), getBase64DecodeJSONArray(jsonArrays));
        				}else{
        					/**********************值对象是字符串||2013-11-27********************/
            				json.put(jsonArray.getString(i), new String(base64Data));
        				}
        			}
        		}
        	}
        	return json;
    	}catch(Exception jsonEx){
    		DebugLog.e(jsonEx.getMessage());
    		throw new RuntimeException("BASE64解码JSON OBJECT 异常", jsonEx);
    	}
    }
	
	
	public static JSONObject getBase64EecodeJSONObject(JSONObject json){
		try{
			JSONArray jsonArray  = json.names();
			for(int i=0;i<jsonArray.length();i++){
				Object base64Object = json.get(jsonArray.getString(i));
				if(base64Object!=null){
					JSONObject jsonObj = getJSONObject(base64Object.toString().getBytes());
					if(jsonObj!=null){
						/**********************值对象是JSON||2013-11-27********************/
						json.put(jsonArray.getString(i), EncodeUtils.base64Encode(getBase64EecodeJSONObject(jsonObj).toString().getBytes()));
					}else{
						JSONArray jsonArrays = getJSONArray(base64Object.toString().getBytes());
						if(jsonArrays!=null){
							/**********************是JSON数组||2013-11-27********************/
							json.put(jsonArray.getString(i), EncodeUtils.base64Encode(getBase64EecodeJSONArray(jsonArrays).toString().getBytes()));
						}else{
							/**********************值对象是字符串||2013-11-27********************/
							json.put(jsonArray.getString(i), EncodeUtils.base64Encode(base64Object.toString().getBytes()));
						}
					}
				}else{
					json.put(jsonArray.getString(i), "");
				}
			}
			return json;
		}catch(Exception jsonEx){
			DebugLog.e(jsonEx.getMessage());
			throw new RuntimeException("BASE64解码JSON OBJECT 异常", jsonEx);
		}
	}
	
    public static void main(String[] args) throws JSONException {
    	String s = "{" +
    					"A:{A:[{A:123},{B:123}]}," +
    					"B:{A:B,C:D}" +
    				"}";
    	JSONObject jsono = new JSONObject(s); 
    	System.out.println("源JSON:");
    	System.out.println(jsono);
    	JSONObject json = getBase64EecodeJSONObject(jsono);
    	System.out.println(json);
    	System.out.println("base64 编码: ");
    	System.out.println(json.toString());
    	JSONObject josn1 = getBase64DecodeJSONObject(json);
    	System.out.println("base64 解码: ");
    	System.out.println(josn1);
	}
}


