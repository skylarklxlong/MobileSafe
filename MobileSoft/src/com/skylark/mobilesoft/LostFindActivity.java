package com.skylark.mobilesoft;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {

	private SharedPreferences sp;
	
	private TextView tv_savenumber;
	private ImageView iv_protecting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//判断一下,是否做过设置向导,如果没有就跳到设置向导页面,否则就留在当前页面
		Boolean configed = sp.getBoolean("configed", false);
		if (configed) {
			//就在手机防盗页面
			setContentView(R.layout.activity_lost_find);
			tv_savenumber = (TextView) findViewById(R.id.tv_savenumber);
			iv_protecting = (ImageView) findViewById(R.id.iv_protecting);
			//得到之前的安全号码
			String savenumber = sp.getString("savenumber", "");
			tv_savenumber.setText(savenumber);
			//得到防盗保护的状态
			boolean protecting = sp.getBoolean("protecting", false);
			if (protecting) {
				//防盗保护已经开启
				iv_protecting.setImageResource(R.drawable.lock);
			}else {
				//防盗保护没有开启
				iv_protecting.setImageResource(R.drawable.unlock);
			}
			
			
		}else {
			//还没有做过设置向导,跳到设置向导界面
			Intent intent = new Intent(LostFindActivity.this,Setup1Activity.class);
			startActivity(intent);
			//关闭当前手机防盗页面
			finish();
		}
		
	}
	public void reEnterSetup(View view){
		//重新进入设置向导
		Intent intent = new Intent(LostFindActivity.this,Setup1Activity.class);
		startActivity(intent);
		//关闭当前手机防盗页面
		finish();
	}
}
