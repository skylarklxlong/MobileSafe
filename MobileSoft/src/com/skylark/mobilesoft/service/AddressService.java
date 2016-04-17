package com.skylark.mobilesoft.service;

import com.skylark.mobilesoft.R;
import com.skylark.mobilesoft.db.dao.NumberAddressQueryUtils;

import android.R.integer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AddressService extends Service {

	/**
	 * 窗体管理者 他也是一个服务
	 */
	private WindowManager wm;
	private View view;

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
	 * 广播接受者可以在代码中注册 相当于服务里面的一个内部类
	 */
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// phone 就是我们拿到的用户拨出去的电话号码
			String phone = getResultData();
			// 查询数据库
			String address = NumberAddressQueryUtils.queryNumber(phone);
			// Toast.makeText(context, address, 1).show();
			/**
			 * 自定义土司
			 */
			myToast(address);
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
				// Toast.makeText(getApplicationContext(), address, 1).show();
				myToast(address);
				break;

			case TelephonyManager.CALL_STATE_IDLE:// 电话的空闲状态 : 挂电话 来电拒绝
				// 移除textView
				// 如果不为空才去移除
				if (view != null) {

					wm.removeView(view);
				}

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

		// 用代码注册广播接受者
		receiver = new OutCallReceiver();
		// 意图匹配器
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);

		// 实例化窗体管理者
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	/**
	 * 自定义土司
	 */
	public void myToast(String address) {

		// View = new TextView(getApplicationContext());
		// View.setText(address);
		// View.setTextSize(22);
		// View.setTextColor(Color.RED);

		// 使用自定义土司
		view = View.inflate(this, R.layout.address_show,
				null);
		TextView textview = (TextView) view.findViewById(R.id.tv_address);

		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] ids = { R.drawable.call_locate_white, R.drawable.call_locate_orange,
				R.drawable.call_locate_blue, R.drawable.call_locate_gray,
				R.drawable.call_locate_green };
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		//设置整体的背景
		view.setBackgroundResource(ids[sp.getInt("which", 0)]);
		//只设置文本的背景
		//textview.setBackgroundResource(ids[sp.getInt("which", 0)]);
		
		textview.setText(address);

		// 窗体管理的布局参数
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		// 窗体的宽和高
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		// 窗体的一些特性 没有焦点 不能触摸 保持锁屏存在
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		// 土司半透明
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;

		wm.addView(view, params);
	}

	public void onDestroy() {
		super.onDestroy();

		tm.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone = null;

		// 用代码取消注册广播接受者
		unregisterReceiver(receiver);
		receiver = null;
	}

}
