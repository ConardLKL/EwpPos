package com.limahao.ticket.db.provider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.limahao.ticket.db.DBHelper;
import com.limahao.ticket.db.HistoryOrderColumn;
import com.limahao.ticket.utils.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class HistoryOrderDB {
	private DBHelper dbHelper;

	public HistoryOrderDB(Context context) {
		dbHelper = DBHelper.getInstance(context);
	}

	/**
	 * 查询预售天信息
	 * 
	 * @return
	 */
	public List<String> queryHistory() {
		Cursor cursor = querySQL(true);  // 时间降序
		List<String> list = new ArrayList<String>();
		if (cursor.moveToFirst()) { // 查询
			// 手机号码
			list.add(cursor.getString(cursor
					.getColumnIndex(HistoryOrderColumn.Order_Phone)));
			
			while(cursor.moveToNext()){
				// 手机号码
				list.add(cursor.getString(cursor
						.getColumnIndex(HistoryOrderColumn.Order_Phone)));
			}
		}
		
		cursor.close();
		return list;
	}

	/**
	 * 更新预售票信息
	 * 
	 * @param ItemUserInfo
	 * @return
	 */
	public void updateHistory(String phone) {
		Cursor cursor = querySQL(phone);
		if (cursor.moveToFirst()) {
			// 更新
			int id = cursor.getInt(cursor.getColumnIndex(HistoryOrderColumn._ID));
			updateSQL(id, null); // 只更新时间
		} else {
			cursor.close();
			cursor = querySQL(false); // 时间升序
			// 最多保存5条记录
			if(cursor.getCount() < 5){
				insertSQL(phone); //小于5条记录则增加
			} else {
				// 已经有5条记录了则修改最老的那条记录
				if(cursor.moveToFirst()){
					int id = cursor.getInt(cursor.getColumnIndex(HistoryOrderColumn._ID));
					updateSQL(id, phone); // 只更新时间,历史记录手机号
				}
			}
			
		}
		cursor.close();
	}

	/**
	 * 删除数据库中存放的预售票信息
	 */
	public void deleteHistory(String phone) {
		Cursor cursor = querySQL(phone);
		if (cursor.moveToFirst()) {
			// 更新
			int id = cursor.getInt(cursor.getColumnIndex(HistoryOrderColumn._ID));
			// 删除
			deleteSQL(id);
		}
		cursor.close();
	}

	/**
	 * 关闭数据库
	 */
	public void dbClose() {
		dbHelper.closeDb();
	}

	/**
	 * 插入用户信息
	 * 
	 * @param item
	 */
	private void insertSQL(String phone) {
		String format = "insert into %s(%s, %s) values('%s','%s')";
		String SQL = String.format(format, HistoryOrderColumn.TABLE_NAME,
				HistoryOrderColumn.Order_Phone, HistoryOrderColumn.Timestamp,
				phone, Calendar.getInstance().getTimeInMillis());

		dbHelper.ExecSQL(SQL);
	}

	/**
	 * 更改用户信息
	 * 
	 * @param ItemUserInfo
	 * @param id
	 *            数据库索引id
	 * @return
	 */
	private int updateSQL(int id, String phone) {
		ContentValues values = new ContentValues();
		// 更新时间
		values.put(HistoryOrderColumn.Timestamp, Calendar.getInstance()
				.getTimeInMillis());
		
		if(!Utility.isNullOrEmpty(phone)){
			values.put(HistoryOrderColumn.Order_Phone, phone);
		}

		return dbHelper.update(HistoryOrderColumn.TABLE_NAME, values,
				HistoryOrderColumn._ID + "=?", new String[] { id + "" });
	}

	private int deleteSQL(int id) {
		return dbHelper.delete(HistoryOrderColumn.TABLE_NAME, id);
	}

	private Cursor querySQL(String phone) {
		// String SQL = "select * from " + PreDateColumn.TABLE_NAME + " where "
		// + PreDateColumn.URL + "='" + url + "'";
		// 只有一条数据
		String SQL = "select * from " + HistoryOrderColumn.TABLE_NAME
				+ " where " + HistoryOrderColumn.Order_Phone + "='" + phone
				+ "'";
		return dbHelper.rawQuery(SQL, null);
	}

	private Cursor querySQL(boolean isDesc) {
		String order = " desc";
		if(!isDesc){
			order = " asc";
		}
		
		// 最多5条数据 按时间降序
		String SQL = "select * from " + HistoryOrderColumn.TABLE_NAME
				+ " order by " + HistoryOrderColumn.Timestamp + order;
		return dbHelper.rawQuery(SQL, null);
	}

}
