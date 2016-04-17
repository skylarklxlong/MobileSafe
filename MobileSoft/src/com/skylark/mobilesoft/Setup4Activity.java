package com.skylark.mobilesoft;

import com.skylark.mobilesoft.recevier.MyAdmin;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Setup4Activity extends BaseSetupActivity {

	private static final int REQUEST_CODE_ENABLE_ADMIN = 0;
	private DevicePolicyManager dpm;
	private Button bt_dpm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		bt_dpm = (Button) findViewById(R.id.bt_dpm);

	}

	/**
	 * 下一步的点击事件
	 * 
	 * @param view
	 */
	@Override
	public void showNext() {

		Intent intent = new Intent(Setup4Activity.this, Setup5Activity.class);
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
		Intent intent = new Intent(Setup4Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		/**
		 * overridePendingTransition这个方法要求,必须在finish();或者startActivity(intent);
		 * 的后面
		 */
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}


	/**
	 * 设置设备管理员
	 */
	public void onClick(View view) {
		// 创建一个主键 激活 广播接受者
		ComponentName mDeviceAdminSample = new ComponentName(this,
						MyAdmin.class);
//		// 初始化远程对象
//		// 判断是否已经激活
//		if (dpm.isAdminActive(mDeviceAdminSample)) {
////			dpm.removeActiveAdmin(mDeviceAdminSample);
////			mDeviceAdminSample = null;
//			bt_dpm.setText("设置管理员已激活");
//		}else {
			//创建一个Intent 添加一个设备管理员
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			
	        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
	        //弹出激活页面 让用户选择是否激活
	        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"亲 开启我就可以一键锁屏哦!");
	        //启动意图
	        startActivity(intent);
	        
//	        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
	        
	        bt_dpm.setText("设置管理员已激活");
	        
//		}
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		ComponentName mDeviceAdminSample = new ComponentName(this,
//				MyAdmin.class);
//		if (resultCode == REQUEST_CODE_ENABLE_ADMIN) {
//			if (dpm.isAdminActive(mDeviceAdminSample)) {
//				bt_dpm.setText("设置管理员已激活");
//			}else {
//				bt_dpm.setText("设置管理员未激活");
//			}
//		}
//
//	}
}
