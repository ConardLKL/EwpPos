package com.limahao.ticket.utils;

import java.io.UnsupportedEncodingException;

import com.limahao.ticket.config.Constants;

/** 
 * @Title: EncodeUtils.java 
 * 
 * @author email: <a href="cj@12pai.cn">陈杰</a> 
 * @date 2013-11-27 下午3:53:55 
 * @version :
 * @Description: 
 */
public class EncodeUtils {
	/**
     * utf-8字符集，Base64编码
     * @param src
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String base64Encode(String src) throws UnsupportedEncodingException {
    	return base64Encode( src.getBytes(Constants.DEFAULT_URL_ENCODING) );
    }
    
	/**
     * Base64编码
     * @param src
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String base64Encode(byte[] src) throws UnsupportedEncodingException {
    	return android.util.Base64.encodeToString(src, android.util.Base64.URL_SAFE);
    }
    
    /**
     * utf-8字符集，Base64解
     * @param src
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String base64DecodeToBytes(String src) throws UnsupportedEncodingException{
    	return new String(base64Decode(src), Constants.DEFAULT_URL_ENCODING);
    }
    
    /**
     * Base64解
     * @param src
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] base64Decode(String src) throws UnsupportedEncodingException{
    	return android.util.Base64.decode(src, android.util.Base64.DEFAULT);
    }
}
