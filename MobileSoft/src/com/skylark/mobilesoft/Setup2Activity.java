package com.skylark.mobilesoft;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.skylark.mobilesoft.ui.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView siv_setup2_sim;
	// 读取手机的sim卡的信息
	private TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		siv_setup2_sim = (SettingItemView) findViewById(R.id.siv_setup2_sim);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String sim = sp.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			// 没有绑定
			siv_setup2_sim.setChecked(false);
		} else {
			// 已经绑定
			siv_setup2_sim.setChecked(true);
		}
		siv_setup2_sim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();

				if (siv_setup2_sim.isChecked()) {
					siv_setup2_sim.setChecked(false);
					editor.putString("sim", null);
				} else {
					siv_setup2_sim.setChecked(true);
					// 保存SIM卡的序列号
					String sim = tm.getSimSerialNumber();
					editor.putString("sim", sim);
				}
				editor.commit();
			}
		});
	}

	/**
	 * 下一步的点击事件
	 * 
	 * @param view
	 */
	@Override
	public void showNext() {
		
		//取出是否绑定SIM卡
		String sim = sp.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			//没有绑定
			Toast.makeText(this, "SIM卡没有绑定", 1).show();
			return;
		}
		
		Intent intent = new Intent(Setup2Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		/**
		 * overridePendingTransition这个方法要求,必须在finish();或者startActivity(intent);
		 * 的后面
		 */
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	/**
	 * 上一步的点击事件
	 * 
	 * @param view
	 */
	@Override
	public void showPre() {
		Intent intent = new Intent(Setup2Activity.this, Setup1Activity.class);
		startActivity(intent);
		finish();
		/**
		 * overridePendingTransition这个方法要求,必须在finish();或者startActivity(intent);
		 * 的后面
		 */
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

}
