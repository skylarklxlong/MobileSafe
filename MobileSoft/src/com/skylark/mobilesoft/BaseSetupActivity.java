package com.skylark.mobilesoft;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {

	// 1.定义一个手势识别器
	private GestureDetector detector;

	protected SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // 这个是第一个执行的
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 2.实例化这个手势识别器
		detector = new GestureDetector(this, new SimpleOnGestureListener() {
			/**
			 * 当我们的手指在上面滑动的时候回调 这个方法返回的是boolean类型,当返回true是说明不需要其他activity做其他的事情了
			 * 当返回false时说明其他activity还要做其他的事情
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// 屏蔽在x轴上滑动很慢的情况
				if (Math.abs(velocityX) < 200) {
					Toast.makeText(getApplicationContext(), "不能滑动太慢", 0).show();
					return true;
				}
				// 屏蔽斜着滑动屏幕这种情况
				if (Math.abs(e1.getRawY() - e2.getRawY()) > 100) {
					Toast.makeText(getApplicationContext(), "不能这样滑动屏幕", 0)
							.show();
					return true;
				}
				if ((e2.getRawX() - e1.getRawX()) > 200) {
					// 从左往右滑动,显示上一个页面
					showPre();
					return true;
				}
				if ((e1.getRawX() - e2.getRawX()) > 200) {
					// 从右往左滑动,显示下一个页面
					showNext();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	// 3.使用手势识别器
	/**
	 * onTouchEvent这个方法时,当手在屏幕上滑动时, 如果没有其他的view要处理的话,他就会把滑动的事件传递进来
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public abstract void showNext();

	/**
	 * 下一步的点击事件
	 * 
	 * @param view
	 */
	public void next(View view) {
		showNext();
	}

	public abstract void showPre();

	/**
	 * 上一步的点击事件
	 * 
	 * @param view
	 */
	public void pre(View view) {
		showPre();
	}

}
