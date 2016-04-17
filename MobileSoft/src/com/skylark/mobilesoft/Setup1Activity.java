package com.skylark.mobilesoft;

import android.content.Intent;
import android.os.Bundle;

public class Setup1Activity extends BaseSetupActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
	}


	@Override
	public void showNext() {
		Intent intent = new Intent(Setup1Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		/**
		 * 动画效果
		 * overridePendingTransition这个方法要求,
		 * 必须在finish();或者startActivity(intent);的后面
		 */
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		
	}


	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		
	}
	
	
}
