package com.skylark.mobilesoft;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.skylark.mobilesoft.service.AddressService;
import com.skylark.mobilesoft.service.CallSmsSafeService;
import com.skylark.mobilesoft.ui.SettingClickView;
import com.skylark.mobilesoft.ui.SettingItemView;
import com.skylark.mobilesoft.utils.ServiceUtils;

public class SettingActivity extends Activity {

	// 设置是否开启自动更新
	private SettingItemView siv_update;
	// 用sharedpreferences来存储数据
	private SharedPreferences sp;
	// 设置是否开启显示号码归属地
	private SettingItemView siv_show_address;
	private Intent showAddress;
	//设置是否开启黑名单拦截
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	
	//设置号码归属地显示框背景
	private SettingClickView scv_changebg;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//服务状态的监听
		showAddress = new Intent(this, AddressService.class); //感觉这段代码放在这里是没有用的
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.skylark.mobilesoft.service.AddressService");
		//简化代码
		siv_show_address.setChecked(isServiceRunning);
		
//		if (isServiceRunning) {
//			//监听来电的服务是开启的
//			siv_show_address.setChecked(true);
//		}else {
//			siv_show_address.setChecked(false);
//		}
		
		boolean isCallSmsServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.skylark.mobilesoft.service.CallSmsSafeService");
		siv_callsms_safe.setChecked(isCallSmsServiceRunning);
		
		
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
		
		//黑名单拦截的设置
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		
		siv_callsms_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断是否有选中
				// 已经打开了
				if (siv_callsms_safe.isChecked()) {
					siv_callsms_safe.setChecked(false);
					stopService(callSmsSafeIntent);
				} else {
					// 没有打开
					siv_callsms_safe.setChecked(true);
					startService(callSmsSafeIntent);
				}
			}
		});
		
		//设置号码归属地的背景
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		final String [] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);
		
		scv_changebg.setTitle("归属地提示框风格");
		scv_changebg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int whichlast = sp.getInt("which", 0);
				
				// 弹出一个对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items, whichlast, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						//保存选择参数
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						
						scv_changebg.setDesc(items[which]);
						
						//选中单选框后 取消掉对话框
						dialog.dismiss();
						
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
				
			}
		});
	}

}
