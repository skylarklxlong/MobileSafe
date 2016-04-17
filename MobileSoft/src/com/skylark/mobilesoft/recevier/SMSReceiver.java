package com.skylark.mobilesoft.recevier;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import com.skylark.mobilesoft.R;
import com.skylark.mobilesoft.service.GPSService;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";
	private SharedPreferences sp;
	private DevicePolicyManager dpm;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 写接受短信的代码
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

		for (Object b : objs) {
			// 具体的某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
			// 谁发来的短信
			String sender = sms.getOriginatingAddress();

			String savenumber = sp.getString("savenumber", "");

			// Toast.makeText(context, sender, 1).show();
			Log.i(TAG, "===sender===" + sender);
			// 得到短信的内容
			String body = sms.getMessageBody();

			if (sender.contains(savenumber)) {

				// 判断短信的内容
				if ("#*location*#".equals(body)) {
					// 得到手机的GPS
					Log.i(TAG, "得到手机的GPS");

					Intent i = new Intent(context, GPSService.class);
					context.startService(i);
					SharedPreferences sp = context.getSharedPreferences(
							"config", Context.MODE_PRIVATE);
					String lastlocation = sp.getString("lastlocation", null);
					if (TextUtils.isEmpty(lastlocation)) {
						// 位置没有得到
						SmsManager.getDefault().sendTextMessage(sender, null,
								"位置没有得到", null, null);
					} else {
						SmsManager.getDefault().sendTextMessage(sender, null,
								lastlocation, null, null);
					}

					// 把这个广播终止掉
					abortBroadcast();
				} else if ("#*alarm*#".equals(body)) {
					// 播放报警音乐
					Log.i(TAG, "播放报警音乐");
					MediaPlayer player = MediaPlayer
							.create(context, R.raw.ylzs);
					player.setLooping(false);// 若为true设置为循环播放报警音乐
					player.setVolume(1.0f, 1.0f);// 设置左右声道都为1.0f,都是最大
					player.start();
					abortBroadcast();
				} else if ("#*wipedata*#".equals(body)) {
					// 清除手机数据
					Log.i(TAG, "清除手机数据");
//					// 初始化四大组建的代理对象
//					// 第二个参数表示谁实现了当前的广播
//					ComponentName mDeviceAdminSample = new ComponentName(
//							context, MyAdmin.class);
//					// 判断是否激活设置管理员权限
//					if (dpm.isAdminActive(mDeviceAdminSample)) {
//						// 清理所有的数据 恢复出厂设置
//						dpm.wipeData(0);
//						// 清除手机SDK数据
//						// dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
//					}
					abortBroadcast();
				} else if ("#*lockscreen*#".equals(body)) {
					// 远程锁屏
					Log.i(TAG, "远程锁屏");
//					ComponentName mDeviceAdminSample = new ComponentName(
//							context, MyAdmin.class);
//					// 判断是否激活设置管理员权限
//					if (dpm.isAdminActive(mDeviceAdminSample)) {
//						// 设置锁屏密码 第一个参数中就填写密码
//						dpm.resetPassword("321", 0);
//						// 一键锁屏
//						dpm.lockNow();
//						
//					} else {
//						Toast.makeText(context, "请先开启管理员权限", 0).show();
//						return;
//					}
					abortBroadcast();
				}
			}
		}

	}

}
