package com.skylark.mobilesoft;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

public class AtoolsActivity extends Activity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}

	/**
	 * 进入号码归属地查询界面
	 * 
	 * @param view
	 */
	public void numberQuery(View view) {

		Intent intent = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intent);
	}

	public void weatherSearch(View view) {

		Intent intent = new Intent(this, WeatherSearchActivity.class);
		startActivity(intent);

	}

	public void takePhoto(View view) {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		// 请求code
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	public void takeVideo() {

		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

		File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".mp4");

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);//设置录像质量 1:高清
		// 请求code
		startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//拍照的
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			// 给你数据的activity设置的结果标记
			switch (resultCode) {
			case Activity.RESULT_OK:
				System.out.println("确认拍照");
				break;
			case Activity.RESULT_CANCELED:
				System.out.println("取消拍照");
				break;
			default:
				break;
			}
		}
		//录像的
		if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
			// 给你数据的activity设置的结果标记
			switch (resultCode) {
			case Activity.RESULT_OK:
				System.out.println("确认录像");
				break;
			case Activity.RESULT_CANCELED:
				System.out.println("取消录像");
				break;
			default:
				break;
			}
		}

	}

}
