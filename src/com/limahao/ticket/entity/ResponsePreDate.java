package com.limahao.ticket.entity;

/** 
 * @Title: BaseResponse.java 
 * 
 * @author email: <a href="cj@12pai.cn">陈杰</a> 
 * @date 2014-07-25 下午2:09:08 
 * @version :
 * @Description: 3.4.	获取预售天数和单订单售票张数
 */
public class ResponsePreDate extends ResponseBase{
	ItemPreDate RET_INFO;

	public ItemPreDate getRET_INFO() {
		return RET_INFO;
	}

	public void setRET_INFO(ItemPreDate rET_INFO) {
		RET_INFO = rET_INFO;
	}
	
}
