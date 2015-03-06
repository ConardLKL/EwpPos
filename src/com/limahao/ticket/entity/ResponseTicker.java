package com.limahao.ticket.entity;

/** 
 * @Title: BaseResponse.java 
 * 
 * @author email: <a href="cj@12pai.cn">陈杰</a> 
 * @date 2014-07-25 下午2:09:08 
 * @version :
 * @Description: 获取到站列表
 */
public class ResponseTicker extends ResponseBase{
	ItemTikcerStation RET_INFO;

	public ItemTikcerStation getRET_INFO() {
		return RET_INFO;
	}

	public void setRET_INFO(ItemTikcerStation rET_INFO) {
		RET_INFO = rET_INFO;
	}
}
