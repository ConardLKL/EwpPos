package com.limahao.ticket.entity;

import java.util.List;

public class ItemTikcerOrderStation extends ItemBase {
	List<ItemTickerOrder> Trade_Order_LIST;

	public List<ItemTickerOrder> getTrade_Order_LIST() {
		return Trade_Order_LIST;
	}

	public void setTrade_Order_LIST(List<ItemTickerOrder> trade_Order_LIST) {
		Trade_Order_LIST = trade_Order_LIST;
	}

}
