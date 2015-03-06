package com.limahao.ticket.entity;

/**
 * @Title: ErrInfo.java
 * 
 * @author email: <a href="cj@ewppay.com">陈杰</a>
 * @date 2014-7-25 上午9:38:41
 * @version :
 * @Description: 用户个人信息
 */
public class ItemUserInfo extends ItemBase {
	
	/**
	 * 用户姓名
	 */
	String NAME = "";// 名字
	/**
	 * 性别  
	 */
	String SEX = ""; // 性别
	/**
	 * 手机号码 
	 */
	String PHONE = "";// 手机号码
	/**
	 * 身份证号码  
	 */
	String IDNUMBER = "";// 身份证号码
	
	/**
	 * 注册时间
	 */
	String REGDATE = "";// 注册时间
	
	/**
	 * 密码  
	 */
	String Password = "";

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public String getSEX() {
		return SEX;
	}

	public void setSEX(String sEX) {
		SEX = sEX;
	}

	public String getPHONE() {
		return PHONE;
	}

	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}

	public String getIDNUMBER() {
		return IDNUMBER;
	}

	public void setIDNUMBER(String iDNUMBER) {
		IDNUMBER = iDNUMBER;
	}
	
	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getREGDATE() {
		return REGDATE;
	}

	public void setREGDATE(String rEGDATE) {
		REGDATE = rEGDATE;
	}
	
}
