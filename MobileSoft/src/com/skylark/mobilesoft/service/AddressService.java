package com.skylark.mobilesoft.service;

import com.skylark.mobilesoft.db.dao.NumberAddressQueryUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class AddressService extends Service {

	/**
	 * 电话服务
	 */
	private TelephonyManager tm;
	private MyListenerPhone listenerPhone;
	
	private OutCallReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}
	/**
	 * 广播接受者可以在代码中注册
	 *	相当于服务里面的一个内部类
	 */
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//phone 就是我们拿到的用户拨出去的电话号码
			String phone = getResultData();
			//查询数据库
			String address = NumberAddressQueryUtils.queryNumber(phone);
			Toast.makeText(context, address, 1).show();
		}

	}

	
	
	private class MyListenerPhone extends PhoneStateListener {

		/**
		 * 当呼叫状态发生改变时回调该方法
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// state：状态，incomingNumber：来电号码
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 来电铃声响起
				String address = NumberAddressQueryUtils
						.queryNumber(incomingNumber);// 查询数据库的操作
				Toast.makeText(getApplicationContext(), address, 1).show();
				break;

			default:
				break;
			}

		}

	}

	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		// 监听来电
		listenerPhone = new MyListenerPhone();
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);
		
		//用代码注册广播接受者
		receiver = new OutCallReceiver();
		//意图匹配器
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
	}

	public void onDestroy() {
		super.onDestroy();

		tm.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone = null;
		
		//用代码取消注册广播接受者
		unregisterReceiver(receiver);
		receiver = null;
	}

}
