package com.skylark.mobilesoft.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service {

	//用到位置服务
	private LocationManager lm;
	private MyLocationListener listener;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		//获取手机可用的定位的方式
//		List<String> provider = lm.getAllProviders();
//		for (String l: provider) {
//			System.out.println(l);
//		}
		
		listener = new MyLocationListener();
		//注册监听位置服务
		//给位置提供者设置条件
		Criteria criteria = new Criteria();
		String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(provider, 0, 0, listener);
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消监听服务
		lm.removeUpdates(listener);
		listener = null;
	}
	
	class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			
			String longitude = "j:" + location.getLongitude() + "\n";
			String latitde = "w:" + location.getLatitude() + "\n";
			String accuracy = "a:" + location.getAccuracy() + "\n";
			// 把标准的GPS坐标转换成火星坐标
//			InputStream is;
//			try {
//				is = getAssets().open("axisoffset.dat");
//				ModifyOffset offset = ModifyOffset.getInstance(is);
//				PointDouble double1 = offset.s2c(new PointDouble(location
//						.getLongitude(), location.getLatitude()));
//				longitude ="j:" + offset.X+ "\n";
//				latitude =  "w:" +offset.Y+ "\n";
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			//把位置信息保存到起来 然后给安全号码发短信
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastlocation", longitude+latitde+accuracy);
			editor.commit();
			
//			TextView textview = new TextView(GPSService.this);
//			textview.setText(longitude + "\n" + latitde + "\n" + accuracy);
//			setContentView(textview);
		}

		/**
		 * 某一个位置提供者不可以使用了
		 */
		@Override
		public void onProviderDisabled(String provider) {
			
		}

		/**
		 * 某一个位置提供者可以使用了
		 */
		@Override
		public void onProviderEnabled(String provider) {
			
		}

		/**
		 * 当状态发生改变的时候回调    开启--关闭,关闭--开启
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
	}

}
