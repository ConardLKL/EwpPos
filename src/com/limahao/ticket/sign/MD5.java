package com.limahao.ticket.sign;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.impl.auth.UnsupportedDigestAlgorithmException;

import android.util.Log;

import com.limahao.ticket.config.Constants;
import com.limahao.ticket.utils.Utility;

/**
 * @Title: MD5.java
 * 
 * @author email: <a href="cj@12pai.cn">陈杰</a> 
 * @date 2013-5-16 上午09:50:21
 * @version :
 * @Description:
 */
public class MD5 {
	private static final String LOG_TAG = "MD5";
	private static final String ALGORITHM = "MD5";
	
	private static MessageDigest sDigest;

	static {
		try {
			sDigest = MessageDigest.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			Log.e(LOG_TAG, "Get MD5 Digest failed.");
			throw new UnsupportedDigestAlgorithmException(ALGORITHM, e);
		}
	}
	
	final public static String encode(String source) {
		byte[] btyes = source.getBytes();
		byte[] encodedBytes = sDigest.digest(btyes);

		return Utility.hexString(encodedBytes);
	}
	
	/**
	 * 
	 * @param md5
	 * @return
	 */
	final public static byte[] md5(String src) {
		try {
			sDigest.update(src.getBytes(Constants.DEFAULT_URL_ENCODING));
			byte[] digest = sDigest.digest();
			return digest;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

	}
		
	/**
	 * ----------------------------方法说明-----------------------
	 * 对content进行MD5 按照密钥key进行加密
	 *-----------------------------------------------------------
	 * @param content  明文
	 * @param key      密钥
	 * @return   返回加密后的字符串，如果加密异常，返回null
	 */
	final public static String md5ToStr(String content) {
		return md5ToStr(content, null);
	}
	
	/**
	 * ----------------------------方法说明-----------------------
	 * 对content进行MD5 按照密钥key进行加密
	 *-----------------------------------------------------------
	 * @param content  明文
	 * @param key      密钥
	 * @return   返回加密后的字符串，如果加密异常，返回null
	 */
	final public static String md5ToStr(String content, String key) {

		try {
			// 明文及密钥需要按照什么字符集进行解码
			sDigest.update(content.getBytes(Constants.DEFAULT_URL_ENCODING));

			String result = "";
			byte[] temp;
			// 明文及密钥需要按照什么字符集进行解码
			if(!Utility.isNullOrEmpty(key)) {
				temp = sDigest.digest(key.getBytes(Constants.DEFAULT_URL_ENCODING));
			} else {
				temp = sDigest.digest();
			}
			for (int i = 0; i < temp.length; i++) {
				result += Integer.toHexString(
						(0x000000ff & temp[i]) | 0xffffff00).substring(6);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ----------------------------方法说明-----------------------
	 *
	 *-----------------------------------------------------------
	 * @param content  明文
	 * @param ciphertext 密文
	 * @param key  密钥
	 * @param code 字符集
	 * @return 如果密文是明文按照密钥加密的结果，返回true
	 * 否则 返回false
	 */
	final public static Boolean isEqual(String content, String ciphertext ,String key){
		return ciphertext.equals(md5ToStr(content,key));
	}
}
