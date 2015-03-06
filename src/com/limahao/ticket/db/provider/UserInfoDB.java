package com.limahao.ticket.db.provider;

import com.limahao.ticket.db.DBHelper;
import com.limahao.ticket.db.UserInfoColumn;
import com.limahao.ticket.entity.ItemUserInfo;
import com.limahao.ticket.utils.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class UserInfoDB {
	private DBHelper dbHelper;

	public UserInfoDB(Context context) {
		dbHelper = DBHelper.getInstance(context);
	}

	/**
	 * 查询用户信息
	 * @return
	 */
	public ItemUserInfo queryUserInfo(){
		Cursor cursor = querySQL();
		if (cursor.moveToFirst()) { //查询
			ItemUserInfo item = new ItemUserInfo();
			// 手机号码 
			item.setPHONE(cursor.getString(cursor
					.getColumnIndex(UserInfoColumn.Reg_Phone)));
			
			// 用户姓名
			item.setNAME(cursor.getString(cursor
					.getColumnIndex(UserInfoColumn.Name)));
			
			// 性别
			item.setSEX(cursor.getString(cursor
					.getColumnIndex(UserInfoColumn.Sex)));
			
			// 身份证号码 
			item.setIDNUMBER(cursor.getString(cursor
					.getColumnIndex(UserInfoColumn.ID_Number)));
			
			// 密码
			item.setPassword(cursor.getString(cursor
					.getColumnIndex(UserInfoColumn.Password)));
			
			// 注册时间
			item.setREGDATE(cursor.getString(cursor
					.getColumnIndex(UserInfoColumn.Reg_Time)));
			
			cursor.close();
			return item;
		} 
		cursor.close();
		return null;
	}
	
	/**
	 * 更新用户信息
	 * 
	 * @param ItemUserInfo
	 * @return
	 */
	public void updateUserInfo(ItemUserInfo item){
		Cursor cursor = querySQL();
		if (cursor.moveToFirst()) {
			// 更新
			int id = cursor.getInt(cursor
					.getColumnIndex(UserInfoColumn._ID));
			updateSQL(item, id);
		} else {
			insertSQL(item);
		}
		cursor.close();
	}
	
	/**
	 * 删除数据库中存放的用户信息
	 */
	public void deleteUserInfo(){
		Cursor cursor = querySQL();
		if (cursor.moveToFirst()) {
			// 更新
			int id = cursor.getInt(cursor
					.getColumnIndex(UserInfoColumn._ID));
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
	 * @param item
	 */
	private void insertSQL(ItemUserInfo item) {
		String format = "insert into %s(%s, %s, %s, %s, %s, %s) values('%s','%s','%s', '%s','%s','%s')";
		String SQL = String.format(format, UserInfoColumn.TABLE_NAME,
				UserInfoColumn.Reg_Phone, UserInfoColumn.Reg_Time, UserInfoColumn.Name,
				UserInfoColumn.Sex, UserInfoColumn.ID_Number,
				UserInfoColumn.Password, item.getPHONE(), item.getREGDATE(),item.getNAME(),
				item.getSEX(), item.getIDNUMBER(), item.getPassword());
		// String SQL1 = "insert into " + PreDateColumn.TABLE_NAME + "("
		// + PreDateColumn.Pre_Date + "," + PreDateColumn.Max_Ticket + ","
		// + PreDateColumn.Cur_Date + ") values('" + item.getPreDate()
		// + "','" + item.getMaxTicket() + "','" + item.getCurDate()
		// + "')";
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
	private int updateSQL(ItemUserInfo item, int id) {
		ContentValues values = new ContentValues();
		if(!Utility.isNullOrEmpty(item.getPHONE())){
			values.put(UserInfoColumn.Reg_Phone, item.getPHONE());
		}
		if(!Utility.isNullOrEmpty(item.getREGDATE())){
			values.put(UserInfoColumn.Reg_Time, item.getREGDATE());
		}
		if(!Utility.isNullOrEmpty(item.getNAME())){
			values.put(UserInfoColumn.Name, item.getNAME());
		}
		if(!Utility.isNullOrEmpty(item.getSEX())){
			values.put(UserInfoColumn.Sex, item.getSEX());
		}
		if(!Utility.isNullOrEmpty(item.getIDNUMBER())){
			values.put(UserInfoColumn.ID_Number, item.getIDNUMBER());
		}
		if(!Utility.isNullOrEmpty(item.getPassword())){
			values.put(UserInfoColumn.Password, item.getPassword());
		}

		return dbHelper.update(UserInfoColumn.TABLE_NAME, values,
				UserInfoColumn._ID + "=?", new String[] { id + "" });
	}

	private int deleteSQL(int id) {
		return dbHelper.delete(UserInfoColumn.TABLE_NAME, id);
	}

	private Cursor querySQL() {
		// String SQL = "select * from " + PreDateColumn.TABLE_NAME + " where "
		// + PreDateColumn.URL + "='" + url + "'";
		// 只有一条数据
		String SQL = "select * from " + UserInfoColumn.TABLE_NAME;
		return dbHelper.rawQuery(SQL, null);
	}

}
