package com.limahao.ticket.db.provider;

import com.limahao.ticket.db.DBHelper;
import com.limahao.ticket.db.PreDateColumn;
import com.limahao.ticket.entity.ItemPreDate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class PreDateDB {
	private DBHelper dbHelper;

	public PreDateDB(Context context) {
		dbHelper = DBHelper.getInstance(context);
	}

	/**
	 * 查询预售天信息
	 * @return
	 */
	public ItemPreDate queryPreDate(){
		Cursor cursor = querySQL();
		if (cursor.moveToFirst()) { //查询
			ItemPreDate item = new ItemPreDate();
			// 预售天数
			item.setPreDate(cursor.getString(cursor
					.getColumnIndex(PreDateColumn.Pre_Date)));
			
			// 预售票数
			item.setMaxTicket(cursor.getString(cursor
					.getColumnIndex(PreDateColumn.Max_Ticket)));
			
			// 当天日期
			item.setCurDate(cursor.getString(cursor
					.getColumnIndex(PreDateColumn.Cur_Date)));
			
			cursor.close();
			return item;
		} 
		cursor.close();
		return null;
	}
	
	/**
	 * 更新预售票信息
	 * 
	 * @param ItemPreDate
	 * @return
	 */
	public void updatePreDate(ItemPreDate item){
		Cursor cursor = querySQL();
		if (cursor.moveToFirst()) { //存在则更新否则进行插入操作
			// 更新
			int id = cursor.getInt(cursor
					.getColumnIndex(PreDateColumn._ID));
			updateSQL(item, id);
		} else{
			insertSQL(item);
		}
		cursor.close();
	}
	
	/**
	 * 删除数据库中存放的预售票信息
	 */
	public void deletePreDate(){
		Cursor cursor = querySQL();
		if (cursor.moveToFirst()) {
			// 更新
			int id = cursor.getInt(cursor
					.getColumnIndex(PreDateColumn._ID));
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
	 * 插入预售票信息
	 * @param item
	 */
	private void insertSQL(ItemPreDate item) {
		String format = "insert into %s(%s, %s, %s) values('%s','%s','%s')";
		String SQL = String.format(format, PreDateColumn.TABLE_NAME,
				PreDateColumn.Pre_Date, PreDateColumn.Max_Ticket,
				PreDateColumn.Cur_Date, item.getPreDate(), item.getMaxTicket(),
				item.getCurDate());
//		String SQL1 = "insert into " + PreDateColumn.TABLE_NAME + "("
//				+ PreDateColumn.Pre_Date + "," + PreDateColumn.Max_Ticket + ","
//				+ PreDateColumn.Cur_Date + ") values('" + item.getPreDate()
//				+ "','" + item.getMaxTicket() + "','" + item.getCurDate()
//				+ "')";
		dbHelper.ExecSQL(SQL);
	}
	
	/**
	 * 更新预售票信息
	 * 
	 * @param ItemPreDate
	 * @param id 数据库索引id
	 * @return
	 */
	private int updateSQL(ItemPreDate item, int id) {
		ContentValues values = new ContentValues();
		values.put(PreDateColumn.Pre_Date, item.getPreDate());
		values.put(PreDateColumn.Max_Ticket, item.getMaxTicket());
		values.put(PreDateColumn.Cur_Date, item.getCurDate());
		return dbHelper.update(PreDateColumn.TABLE_NAME, values,
				PreDateColumn._ID + "=?", new String[] { id + "" });
	}

	private int deleteSQL(int id) {
		return dbHelper.delete(PreDateColumn.TABLE_NAME, id);
	}

	private Cursor querySQL() {
//		String SQL = "select * from " + PreDateColumn.TABLE_NAME + " where "
//				+ PreDateColumn.URL + "='" + url + "'";
		// 只有一条数据
		String SQL = "select * from " + PreDateColumn.TABLE_NAME;
		return dbHelper.rawQuery(SQL, null);
	}
	
}
