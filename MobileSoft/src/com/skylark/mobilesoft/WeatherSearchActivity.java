package com.skylark.mobilesoft;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skylark.mobilesoft.utils.StreamTool;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 解析json格式的数据
 * 
 * @author XueLong_Li
 * 
 */

public class WeatherSearchActivity extends Activity {

	private EditText et_city;
	private TextView city_one;
	private TextView city_two;
	private TextView city_three;
	private TextView city_four;
	private TextView city_five;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_weather);

		et_city = (EditText) findViewById(R.id.et_city);
		city_one = (TextView) findViewById(R.id.city_one);
		city_two = (TextView) findViewById(R.id.city_two);
		city_three = (TextView) findViewById(R.id.city_three);
		city_four = (TextView) findViewById(R.id.city_four);
		city_five = (TextView) findViewById(R.id.city_five);

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			//关闭ProgressDialog
			dialog.dismiss();
			
			switch (msg.what) {
			case SUCCESS:

				try {
					JSONArray data = (JSONArray) msg.obj;
//					String day01 = data.getString(0);
//					String day02 = data.getString(1);

					JSONObject oj = data.getJSONObject(0);
					String date = oj.getString("date") + "\n";
					String high = oj.getString("high") + "---";
					String low = oj.getString("low") + "---";
					String fengli = oj.getString("fengli") + "---";
					String type = oj.getString("type")+ "\n";
					city_one.setText(date + high + low + fengli + type);
					
					JSONObject oj1 = data.getJSONObject(1);
					String date1 = oj1.getString("date") + "\n";
					String high1 = oj1.getString("high") + "---";
					String low1 = oj1.getString("low") + "---";
					String fengli1 = oj1.getString("fengli") + "---";
					String type1 = oj1.getString("type")+ "\n";
					city_two.setText(date1 + high1 + low1 + fengli1 + type1);
					
					JSONObject oj2 = data.getJSONObject(2);
					String date2 = oj2.getString("date") + "\n";
					String high2 = oj2.getString("high") + "---";
					String low2 = oj2.getString("low") + "---";
					String fengli2 = oj2.getString("fengli") + "---";
					String type2 = oj2.getString("type")+ "\n";
					city_three.setText(date2 + high2 + low2 + fengli2 + type2);
					
					JSONObject oj3 = data.getJSONObject(3);
					String date3 = oj3.getString("date") + "\n";
					String high3 = oj3.getString("high") + "---";
					String low3 = oj3.getString("low") + "---";
					String fengli3 = oj3.getString("fengli") + "---";
					String type3 = oj3.getString("type")+ "\n";
					city_four.setText(date3 + high3 + low3 + fengli3 + type3);
					
					JSONObject oj4 = data.getJSONObject(4);
					String date4 = oj4.getString("date") + "\n";
					String high4 = oj4.getString("high") + "---";
					String low4 = oj4.getString("low") + "---";
					String fengli4 = oj4.getString("fengli") + "---";
					String type4 = oj4.getString("type");
					city_five.setText(date4 + high4 + low4 + fengli4 + type4);
					
					
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case INVALID_CITY:

				Toast.makeText(WeatherSearchActivity.this, "您输入的城市有误!请重新输入", 0).show();
				break;

			case NETWORK_ERROR:
				Toast.makeText(WeatherSearchActivity.this, "网络连接错误!", 0).show();
				break;
			default:
				break;
			}
		};
	};

	// http://wthrcdn.etouch.cn/weather_mini?city=%E6%B7%B1%E5%9C%B3
	private final static String PATH = "http://wthrcdn.etouch.cn/weather_mini?city=";
	protected static final int SUCCESS = 0;
	protected static final int INVALID_CITY = 1;
	protected static final int NETWORK_ERROR = 2;
	private String city;
	private String urlall;
	private ProgressDialog dialog = null;
	public void searchCityWeather(View v) {

		city = et_city.getText().toString().trim();

		if (TextUtils.isEmpty(city)) {
			Toast.makeText(WeatherSearchActivity.this, "请输入城市地点", 0).show();
			return;
		}
		
		/**
		 * 这里加一个progressdialog
		 */
		dialog = new ProgressDialog(this);
		dialog.setMessage("正在玩命加载中...");
		dialog.show();
		
		
		new Thread() {
			@Override
			public void run() {

				try {
					// 那个网站接受的地点是转换后的编码
					urlall = PATH + URLEncoder.encode(city, "UTF-8");

					URL url = new URL(urlall);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					int code = conn.getResponseCode();

					if (code == 200) {
						InputStream in = conn.getInputStream();
						String alldata = StreamTool.decodeStream(in);
						// System.out.println(alldata);

						// 解析json格式数据
						// 获得一个json对象
						JSONObject jsonObject = new JSONObject(alldata);
						// 获得desc数据
						String result = jsonObject.getString("desc");

						if ("OK".equals(result)) {
							// 城市输入正确
							JSONObject dataObject = jsonObject
									.getJSONObject("data");
							JSONArray jsonArray = dataObject
									.getJSONArray("forecast");
							// String value1 = jsonArray.getString(0);
							// String value2 = jsonArray.getString(1);

							// System.out.println(value1);
							// 通知更新ui
							Message msg = Message.obtain();
							msg.obj = jsonArray;
							msg.what = SUCCESS;
							handler.sendMessage(msg);

						} else {
							// 输入错误
							// Toast.makeText(MainActivity.this,
							// "您输入的城市有误!请重新输入", 0).show();
							Message msg = Message.obtain();
							msg.what = INVALID_CITY;
							handler.sendMessage(msg);
							return;
						}
						in.close();
					}

				} catch (Exception e) {
					Message msg = Message.obtain();
					msg.what = NETWORK_ERROR;
					handler.sendMessage(msg);
					e.printStackTrace();
				}

			}
		}.start();

	}

}
