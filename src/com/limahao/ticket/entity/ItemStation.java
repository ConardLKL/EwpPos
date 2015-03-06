package com.limahao.ticket.entity;

import java.util.List;

/**
 * @Title: ErrInfo.java
 * 
 * @author email: <a href="cj@ewppay.com">陈杰</a>
 * @date 2014-7-25 上午9:38:41
 * @version :
 * @Description: 站点列表
 */
public class ItemStation extends ItemBase {
	List<ItemCity> BUS_TRIP_LIST;

	public List<ItemCity> getBUS_TRIP_LIST() {
		return BUS_TRIP_LIST;
	}

	public void setBUS_TRIP_LIST(List<ItemCity> bUS_TRIP_LIST) {
		BUS_TRIP_LIST = bUS_TRIP_LIST;
	}

}
