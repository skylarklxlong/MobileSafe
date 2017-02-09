package com.skylark.mobilesoft.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.skylark.mobilesoft.db.BlackNumberDBOpenHelper;
import com.skylark.mobilesoft.domain.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 黑名单数据库的增删改查业务类
 * @author XueLong_Li
 *
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private ArrayList<BlackNumberInfo> result;
	private ContentValues values;
	
	public BlackNumberDao(Context context){
		helper = new BlackNumberDBOpenHelper(context);
	}
	
	/**
	 * 查询黑名单号码是否存在
	 * @param number
	 * @return
	 */
	public boolean find(String number){
		boolean result = false;
		
		db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber where number = ?", new String[]{number});
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	/**
	 * 查询黑名单号码的拦截模式
	 * @param number
	 * @return 返回号码的拦截模式，如果不是黑名单号码就返回空，1 电话拦截  2短信拦截  3全部拦截
	 */
	public String findMode(String number){
		String result = null;
		db = helper.getReadableDatabase();
		cursor = db.rawQuery("select mode from blacknumber where number = ?", new String[]{number});
		if(cursor.moveToNext()){
			result = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}
	/**
	 * 查询全部黑名单号码
	 * @return
	 */
	public List<BlackNumberInfo> findAll(){
		result = new ArrayList<BlackNumberInfo>();
		db = helper.getReadableDatabase();
		//将查找到的结果 倒序排列   为了优化用户体验
		cursor = db.rawQuery("select number,mode from blacknumber order by _id desc", null);
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setNumber(number);
			info.setMode(mode);
			result.add(info);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 添加黑名单号码
	 * @param number
	 * @param mode 拦截模式： 1 电话拦截  2短信拦截  3全部拦截
	 */
	public void add(String number, String mode) {
		db = helper.getWritableDatabase();
		values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}

	/**
	 * 修改黑名单号码的拦截模式
	 * @param number
	 * @param newmode
	 */
	public void update(String number, String newmode) {
		db = helper.getWritableDatabase();
		values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values, "number = ?", new String[]{number});
		db.close();
	}

	/**
	 * 删除黑名单号码
	 * @param number
	 */
	public void delete(String number) {
		db = helper.getWritableDatabase();
		db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
	}

}
