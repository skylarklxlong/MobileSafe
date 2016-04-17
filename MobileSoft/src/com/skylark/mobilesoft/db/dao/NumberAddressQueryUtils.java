package com.skylark.mobilesoft.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {

	// 拷贝数据库的初始化操作在SplashActivity页面中完成
	private static String path = "data/data/com.skylark.mobilesoft/files/address.db";

	/**
	 * select location from data2 where id = (select outkey from data1 where id
	 * = ? ) 级联查询语句 用到的是小米的数据库
	 */

	/**
	 * 传一个号码进来,返回一个归属地
	 * 
	 * @param number
	 * @return
	 */
	public static String queryNumber(String number) {
		String address = number;
		// assets目录下的文件只有webview 才可以读取 ,
		// 所以我们要把address.db 拷贝到 data/data/<包名>/files/address.db

		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		/**
		 * 手机号码一般都是13* 14* 15* 16* 17* 18* 所以用正则表达式来 ^为开头 $为结尾 \d为数字字符匹配。等效于
		 * [0-9]。 {n}为n 是非负整数。正好匹配 n 次 ^1[345678]\d{9}$
		 */
		if (number.matches("^1[345678]\\d{9}$")) {
			// 是手机号码

			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id = (select outkey from data1 where id = ? )",
							new String[] { number.substring(0, 7) }); // 一共需要7位数就够了，这里0——7表示7的前一位
			while (cursor.moveToNext()) {
				String location = cursor.getString(0);
				address = location;
			}
			cursor.close();
		} else {
			// 其他非11位手机号码
			switch (number.length()) {
			case 3: // 110 匪警号码
				address = "匪警号码";
				break;
			case 4: // 5554 模拟器号码
				address = "模拟器号码";
				break;
			case 5: // 10086 客服电话
				address = "客服电话";
				break;
			case 7: // 本地号码
				address = "本地号码";
				break;
			case 8: // 本地号码
				address = "本地号码";
				break;

			default:
				// 处理长途电话 一般大于10位 并且开头是0
				if (number.length() > 10 && number.startsWith("0")) {
					//区号为3位  010-88888888
					Cursor cursor = database.rawQuery(
							"select location from data2 where area = ?",
							new String[]{number.substring(1,3)}); //截取第一位和第二位
					while (cursor.moveToNext()) {
						String location = cursor.getString(0);
						//只显示地区名 不显示运营商
						address = location.substring(0, location.length() - 2);
					}
					cursor.close();
					
					//区号为4为  0855-88888888
					cursor = database.rawQuery(
							"select location from data2 where area = ?",
							new String[]{number.substring(1,4)}); //截取第一位和第三位
					while (cursor.moveToNext()) {
						String location = cursor.getString(0);
						//只显示地区名 不显示运营商
						address = location.substring(0, location.length() - 2);
					}
					cursor.close();
					
				}
				break;
			}

		}
		return address;
	}
}
