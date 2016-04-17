package com.skylark.mobilesoft;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {

	private EditText et_setup3_phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_setup3_phone = (EditText) findViewById(R.id.et_setup3_phone);
		//进到第三个页面后取出之前保存的安全号码
		et_setup3_phone.setText(sp.getString("savenumber", ""));
	}
	
	/**
	 * 下一步的点击事件
	 * @param view
	 */
	@Override
	public void showNext() {
		//判断输入框中是否有安全号码
		String phone = et_setup3_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "请设置安全号码", 0).show();
			return;
		}
		//应该要保存一下安全号码,才能接着进行下一步
		Editor editor = sp.edit();
		editor.putString("savenumber", phone);
		editor.commit();
		
		Intent intent = new Intent(Setup3Activity.this,Setup4Activity.class);
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
	 * @param view
	 */
	@Override
	public void showPre() {
		Intent intent = new Intent(Setup3Activity.this,Setup2Activity.class);
		startActivity(intent);
		finish();
		/**
		 * overridePendingTransition这个方法要求,必须在finish();或者startActivity(intent);
		 * 的后面
		 */
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	/**
	 * 选择联系人的点击事件
	 * @param view
	 */
	public void selectContact(View view){
		Intent intent = new Intent(Setup3Activity.this,SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		String phone = data.getStringExtra("phone").replace("-", "");
		et_setup3_phone.setText(phone);
		
	}
}
