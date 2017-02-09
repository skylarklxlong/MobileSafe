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
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.StrictMode.VmPolicy;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
/**
* @ClassName: AddressService 
* @Description(描叙): 归属地查询的服务，里面还实现了来电归属地位置的显示框
* @author Skylark 
* @date 2017年2月5日 下午8:01:36
 */
public class AddressService extends Service {

	protected static final String TAG = "AddressService";
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
	// 要经常使用 所以就把它定义成类的成员变量
	private WindowManager.LayoutParams params;
	private SharedPreferences sp;
	
	long[] mHits = new long[2];//双击事件

	/**
	* @MethodName: myToast 
	* @Description(描叙): 
	* @author Skylark 
	* @param @param address 
	* @return void
	* @throws
	* @date 2017年2月5日 下午8:04:57
	 */
	public void myToast(String address) {

		// View = new TextView(getApplicationContext());
		// View.setText(address);
		// View.setTextSize(22);
		// View.setTextColor(Color.RED);

		// 使用自定义土司
		view = View.inflate(this, R.layout.address_show, null);
		TextView textview = (TextView) view.findViewById(R.id.tv_address);
		/**
		 * 实现双击 居中
		 */
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//这两句就做了一件事,将数组左移一位
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
					//双击居中
					params.x = wm.getDefaultDisplay().getWidth()/2 - view.getWidth()/2;
					params.y = wm.getDefaultDisplay().getHeight()/2 - view.getHeight()/2;
					wm.updateViewLayout(view, params);
					//记录位置
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();
				}
				
			}
		});

		// 给view对象设置一个触摸监听器
		view.setOnTouchListener(new OnTouchListener() {

			// 定义手指初始化的位置
			int startX;
			int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: // 手指按下屏幕
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.i(TAG, "开始的位置" + startX + "," + startY);

					break;
				case MotionEvent.ACTION_MOVE: // 手指在屏幕上移动
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					Log.i(TAG, "新的位置" + newX + "," + newY);
					int dx = newX - startX;
					int dy = newY - startY;
					Log.i(TAG, "手指的偏移量" + dx + "," + dy);
					Log.i(TAG, "更新imageview在窗体的位置" + dx + "," + dy);
					// 用代码实现
					params.x += dx;
					params.y += dy;

					// 考虑边界问题
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.x > (wm.getDefaultDisplay().getWidth() - view
							.getWidth())) {
						params.x = (wm.getDefaultDisplay().getWidth() - view
								.getWidth());
					}
					if (params.y > (wm.getDefaultDisplay().getHeight() - view
							.getHeight())) {
						params.y = (wm.getDefaultDisplay().getHeight() - view
								.getHeight());
					}

					// 更新imageview
					wm.updateViewLayout(view, params);
					// 重新初始化手指的开始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP: // 手指离开屏幕一瞬间
					// 记录控件位置举例屏幕左上角的位置
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();

					break;
				}

//				return true; // 事件处理完毕了,不要让父控件 父布局响应触摸事件了.
				return false;
			}
		});

		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] ids = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 设置整体的背景
		view.setBackgroundResource(ids[sp.getInt("which", 0)]);
		// 只设置文本的背景
		// textview.setBackgroundResource(ids[sp.getInt("which", 0)]);

		textview.setText(address);

		// 窗体管理的布局参数
		params = new WindowManager.LayoutParams();
		// 窗体的宽和高
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;

		// 与窗体左上角对其
		params.gravity = Gravity.TOP + Gravity.LEFT;
		// 指定窗体距离左边100 上边100个像素
		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);

		// 窗体的一些特性 没有焦点 不能触摸 保持锁屏存在
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		// 土司半透明
		params.format = PixelFormat.TRANSLUCENT;
		// params.type = WindowManager.LayoutParams.TYPE_TOAST;
		/**
		 * android系统里面具有电话优先级的一种窗体类型,记得添加权限
		 * android.permission.SYSTEM_ALERT_WINDOW
		 */
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

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
