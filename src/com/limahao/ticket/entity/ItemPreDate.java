package com.limahao.ticket.entity;

/**
 * @Title: ErrInfo.java
 * 
 * @author email: <a href="cj@ewppay.com">陈杰</a>
 * @date 2014-7-25 上午9:38:41
 * @version :
 * @Description: 获取预售天数和单订单售票张数
 */
public class ItemPreDate extends ItemBase {
	/**
	 * 预售天数:5
	 */
	String PreDate;

	/**
	 * 单订单售票张数:10
	 */
	String MaxTicket;
	
	/**
	 * 当天日期：yyyyMMdd 20140728  
	 */
	String CurDate;

	public String getPreDate() {
		return PreDate;
	}

	public void setPreDate(String preDate) {
		PreDate = preDate;
	}

	public String getMaxTicket() {
		return MaxTicket;
	}

	public void setMaxTicket(String maxTicket) {
		MaxTicket = maxTicket;
	}

	public String getCurDate() {
		return CurDate;
	}

	public void setCurDate(String curDate) {
		CurDate = curDate;
	}
}
