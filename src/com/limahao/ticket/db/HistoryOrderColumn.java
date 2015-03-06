package com.limahao.ticket.db;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

public class HistoryOrderColumn extends DatabaseColumn {

	public static final String TABLE_NAME = "history_Order";
	public static final String Order_Phone = "order_phone";// 订单手机号
	public static final String Timestamp = "timestamp";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);

	private static final Map<String, String> mColumnMap = new HashMap<String, String>();
	static {
		mColumnMap.put(_ID, "integer primary key autoincrement");
		mColumnMap.put(Order_Phone, "text");
		mColumnMap.put(Timestamp, "integer");
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return TABLE_NAME;
	}

	@Override
	public Uri getTableContent() {
		// TODO Auto-generated method stub
		return CONTENT_URI;
	}

	@Override
	protected Map<String, String> getTableMap() {
		// TODO Auto-generated method stub
		return mColumnMap;
	}

}
