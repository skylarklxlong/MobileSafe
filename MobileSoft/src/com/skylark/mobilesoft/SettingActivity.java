package com.skylark.mobilesoft;

import com.skylark.mobilesoft.service.AddressService;
import com.skylark.mobilesoft.ui.SettingItemView;
import com.skylark.mobilesoft.utils.ServiceUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	// 设置是否开启自动更新
	private SettingItemView siv_update;
	// 用sharedpreferences来存储数据
	private SharedPreferences sp;
	// 设置是否开启显示号码归属地
	private SettingItemView siv_show_address;
	private Intent showAddress;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//服务状态的监听
		showAddress = new Intent(this, AddressService.class);
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.skylark.mobilesoft.service.AddressService");
		
		if (isServiceRunning) {
			//监听来电的服务是开启的
			siv_show_address.setChecked(true);
		}else {
			siv_show_address.setChecked(false);
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 设置是否开启自动更新
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		// 取出上次保存的信息
		Boolean update = sp.getBoolean("update", false);
		if (update) {
			// 自动升级已经开启
			siv_update.setChecked(true);
			// siv_update.setDesc("自动升级已经开启");
		} else {
			// 自动升级已经关闭
			siv_update.setChecked(false);
			// siv_update.setDesc("自动升级已经关闭");
		}
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// 判断是否有选中
				// 已经打开了自动升级
				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					// siv_update.setDesc("自动升级已经关闭");
					editor.putBoolean("update", false);
				} else {
					// 没有打开自动升级
					siv_update.setChecked(true);
					// siv_update.setDesc("自动升级已经开启");
					editor.putBoolean("update", true);
				}
				// 一定记得要提交
				editor.commit();
			}
		});

		// 设置是否显示号码归属地
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		showAddress = new Intent(this, AddressService.class);
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.skylark.mobilesoft.service.AddressService");
		if (isServiceRunning) {
			//监听来电的服务是开启的
			siv_show_address.setChecked(true);
		}else {
			siv_show_address.setChecked(false);
		}
		siv_show_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断是否有选中
				// 已经打开了显示号码归属地
				if (siv_show_address.isChecked()) {
					siv_show_address.setChecked(false);
					stopService(showAddress);
				} else {
					// 没有打开显示号码归属地
					siv_show_address.setChecked(true);
					startService(showAddress);
				}
			}
		});
	}

}
