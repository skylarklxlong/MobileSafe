package com.skylark.mobilesoft.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {

		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			// 开启防盗保护才执行 下面的通知事件

			tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			// 读取之前保存的SIM卡信息
			String saveSim = sp.getString("sim", "");
			// 读取当前的SIM卡信息
			String realSim = tm.getSimSerialNumber();
			// 比较两次是否一样
			if (saveSim.equals(realSim)) {
				// sim卡没有变更,还是同一个SIM卡

			} else {
				// SIM卡已经变更,发一个短信给安全号码
				Toast.makeText(context, "SIM卡已经变更", 1);
				String savenumber = sp.getString("savenumber", "");
				SmsManager.getDefault().sendTextMessage(savenumber, null,
						"SIM卡已经变更......", null, null);
			}
		}

	}

}
