package com.limahao.ticket.db;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

public class UserInfoColumn extends DatabaseColumn {

	public static final String TABLE_NAME = "user_info";
	public static final String Reg_Phone = "reg_phone";
	public static final String Reg_Time = "reg_time"; // 注册时间
	public static final String Password = "password";
	public static final String Name = "name";
	public static final String Sex = "sex";
	public static final String ID_Number = "id_number";// 身份证号

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);

	private static final Map<String, String> mColumnMap = new HashMap<String, String>();
	static {
		mColumnMap.put(_ID, "integer primary key autoincrement");
		mColumnMap.put(Reg_Phone, "text");
		mColumnMap.put(Reg_Time, "text");
		mColumnMap.put(Password, "text");
		mColumnMap.put(Name, "text");
		mColumnMap.put(Sex, "text");
		mColumnMap.put(ID_Number, "text");
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
