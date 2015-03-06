package com.limahao.ticket.entity;

/** 
 * @Title: BaseResponse.java 
 * 
 * @author email: <a href="cj@12pai.cn">陈杰</a> 
 * @date 2013-11-22 下午2:09:08 
 * @version :
 * @Description: 错误信息，错误代码
 */
public class ResponseBase {
	ItemErrInfo ERR_INFO;

	public ItemErrInfo getERR_INFO() {
		return ERR_INFO;
	}

	public void setERR_INFO(ItemErrInfo eRR_INFO) {
		ERR_INFO = eRR_INFO;
	}
}
