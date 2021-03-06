package com.limahao.ticket.db;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

public class PreDateColumn extends DatabaseColumn {

	public static final String TABLE_NAME = "pre_info";
	public static final String Pre_Date = "pre_date";
	public static final String Max_Ticket = "max_ticket";
	public static final String Cur_Date = "cur_date";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);

	private static final Map<String, String> mColumnMap = new HashMap<String, String>();
	static {
		mColumnMap.put(_ID, "integer primary key autoincrement");
		mColumnMap.put(Pre_Date, "text");
		mColumnMap.put(Max_Ticket, "text");
		mColumnMap.put(Cur_Date, "text");
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
