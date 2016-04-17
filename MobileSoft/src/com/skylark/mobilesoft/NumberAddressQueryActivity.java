package com.skylark.mobilesoft;

import com.skylark.mobilesoft.db.dao.NumberAddressQueryUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressQueryActivity extends Activity {
	
	private static final String TAG = "NumberAddressQueryActivity";
	private EditText ed_phone;
	private TextView result;
	/**
	 * 震动服务
	 */
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address);
		ed_phone = (EditText) findViewById(R.id.ed_phone);
		result = (TextView) findViewById(R.id.result);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		/**
		 * TextChangedListener这个方法非常使用
		 */
		ed_phone.addTextChangedListener(new TextWatcher() {
			
			/**
			 * 当文本发生变化的时候回调
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s != null&&s.length() >= 3) {
					//查询数据库,并且显示结果
					String address = NumberAddressQueryUtils.queryNumber(s.toString());
					result.setText(address);
				}
			}
			/**
			 * 当文本发生变化之前回调
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			/**
			 * 当文本发生变化之后回调
			 */
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void numberAddressQuery(View view){
		//trim()方法是去空格
		String phone = ed_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "号码为空", 0).show();
			
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			ed_phone.startAnimation(shake);
			
			//当输入的电话号码为空时震动
//			vibrator.vibrate(1000); //单位是毫秒  震动一秒
			
			//停0.2秒 震动0.2秒 停0.3秒 震动0.3秒  停1秒 震动1秒
			long[] pattern = {200,200,300,300,1000,1000};
			//  -1:不循环   0:从0位置开始往后循环   1:从1位置开始往后循环
			vibrator.vibrate(pattern, -1); //有规律的震动
			
			return;
		}else {
			//去数据库查询号码归属地
			//两种方法1.联网查询 2.本地数据库查询
			//写一个工具类
			String address = NumberAddressQueryUtils.queryNumber(phone);
			result.setText(address);
			Log.i(TAG, "您要查询的电话号码=="+phone);
		}
	}
}
