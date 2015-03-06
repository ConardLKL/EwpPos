package com.limahao.ticket.config;

public class Urls {
	// "http://192.168.2.250:8291/TermServ"
	// http://192.168.199.28:8080/term/ 赵光路地址
	// http://115.236.88.38:8291 外网
	// http://term.limahao.com 生产环境
	public static final String BASIC_URL = "http://term.limahao.com";
	public static final String BASIC_IMG_URL = "";

	/**
	 * 业务接口
	 */
	public static final String SERVER = BASIC_URL + "/TermServ/ChargeServlet";

	/**
	 * 关于我们
	 */
	public static final String MORE_ABOUT = BASIC_URL + "/app/about.html";

	/**
	 * 用户指南
	 */
	public static final String MORE_NOTICE = BASIC_URL + "/app/userguide.html";
	
	/**
	 * 免责声明
	 */
	public static final String MORE_PROTOCOL = BASIC_URL + "/app/state.html";

	/**
	 * 请求成功code "ERROR_CODE": "00000000"
	 */
	public static final String REQ_SUCESS = "\"" + "ERROR_CODE" + "\"" + ": "
			+ "\"" + "00000000" + "\"";
}
