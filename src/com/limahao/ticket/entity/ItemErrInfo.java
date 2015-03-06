package com.limahao.ticket.entity;

/**
 * @Title: ErrInfo.java
 * 
 * @author email: <a href="cj@ewppay.com">陈杰</a>
 * @date 2014-7-25 上午9:38:41
 * @version :
 * @Description:
 */
public class ItemErrInfo extends ItemBase {
	/**
	 * 返回码
	 */
	String ERROR_CODE;

	/**
	 * 错误描述信息
	 */
	String ERROR_MSG;

	public String getERROR_CODE() {
		return ERROR_CODE;
	}

	public void setERROR_CODE(String eRROR_CODE) {
		ERROR_CODE = eRROR_CODE;
	}

	public String getERROR_MSG() {
		return ERROR_MSG;
	}

	public void setERROR_MSG(String eRROR_MSG) {
		ERROR_MSG = eRROR_MSG;
	}
}
