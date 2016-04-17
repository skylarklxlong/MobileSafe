package com.skylark.mobilesoft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.skylark.mobilesoft.utils.StreamTools;

public class SplashActivity extends Activity {
	
	protected static final String TAG = "SplashActivity";
	protected static final int ENTER_HOME = 1;
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private String discription;
	private TextView tv_update_info;
	//新版本下载地址
	private String apkurl;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本号:"+ getVersionName());
		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		
		Boolean update = sp.getBoolean("update", false);
		
		//拷贝数据库
		copyDB();
		
		if (update) {
			//检查升级
			checkUpdate();
		}else {
			//自动升级已经关闭
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					//进入主页面
					enterHome();
				}
				//延迟两秒进入
			}, 2000);
		}
		
		//两个界面之间产生动画效果
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		//设置动画时长500ms
		aa.setDuration(500);
		findViewById(R.id.rl_root_splash).setAnimation(aa);
	}
	
	/**
	 * 把address.db 拷贝到 data/data/<包名>/files/address.db
	 */
	private void copyDB() {
		//只要这个数据库拷贝过一次就不需要再次拷贝了
		try {
			//文件
			File file = new File(getFilesDir(), "address.db");
			//判断 如果文件存在并且不为空 那么就不需要拷贝了
			if (file.exists()&&file.length() > 0) {
				//正常,不需要拷贝了
				Log.i(TAG, "数据库拷贝成功");
			}else {
				
				//输入流
				InputStream is = getAssets().open("address.db");
				//文件输出流
				FileOutputStream fos = new FileOutputStream(file);
				//缓冲区
				byte[] buffer = new byte[1024];
				//长度
				int length = 0;
				//读值读到文件的末尾是-1
				while ((length = is.read(buffer)) != -1) {
					//从缓冲区写到文件中
					fos.write(buffer, 0, length);
				}
				//释放掉流
				is.close();
				fos.close();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG: //显示升级的对话框
				Log.i(TAG, "显示升级的对话框");
				showUpdateDialog();
				break;
			case ENTER_HOME: //进入主界面
				enterHome();
				break;
			case URL_ERROR: //url错误
				enterHome();
				Toast.makeText(getApplicationContext(), "url错误", 0).show();
				break;
			case NETWORK_ERROR: //网络错误
				enterHome();
				Toast.makeText(SplashActivity.this, "服务器网络错误", 0).show();
				break;
			case JSON_ERROR: //json解析错误
				enterHome();
				Toast.makeText(SplashActivity.this, "json解析错误", 0).show();
				break;
			default:
				break;
			}
			
		}
		
	};
	
	/**
	 * 检查是否有新版本,如果有就更新
	 */
	
	private void checkUpdate() {

		new Thread(){
			@Override
			public void run() {
				Message mes = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {
					
					//URL
					URL url = new URL(getString(R.string.serverurl));
					//联网，打开一个输入流
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					//链接请求方式
					conn.setRequestMethod("GET");
					//请求超时时间4s
					conn.setConnectTimeout(4000);
					//得到相应码
					int code = conn.getResponseCode();
					if (code == 200) {
						//联网成功
						InputStream is = conn.getInputStream();
						//把流转换成string
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, "联网成功了" + result);
						//json解析
						JSONObject obj = new JSONObject(result);
						//得到服务器的版本信息
						String version = (String) obj.get("version");
						
						discription = (String) obj.get("discription");
						apkurl = (String) obj.get("apkurl");
						//校验新版本,比较当前版本与服务器版本是否相同
						if (getVersionName().equals(version)) {
							//版本一致，没有新版本,进入主页面
							mes.what = ENTER_HOME;
						}else {
							//有新版本,弹出一个升级对话框
							mes.what = SHOW_UPDATE_DIALOG;
						}
						
					}
					
				} catch (MalformedURLException e) {
					mes.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					mes.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					mes.what = JSON_ERROR;
					e.printStackTrace();
				}finally{
					
					long endTime = System.currentTimeMillis();
					//花了多长时间
					long dTime = endTime - startTime;
					//规定让他停留2000ms
					if (dTime < 2000) {
						try {
							Thread.sleep(2000 - dTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					handler.sendMessage(mes);
				}
			};
		}.start();
	}

	/**
	 * 弹出升级对话框
	 */
	protected void showUpdateDialog() {
		//用this和Activity.this都可以,一般都用Activity.this  getApplicationContext生命周期长,只要应用还存活着他就存在
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
		builder.setTitle("升级提示");
		//体验效果不好 builder.setCancelable(false);//设置为false后,点击对话框以外的区域和点击返回键就没有效果了.也就是让你强制升级
		//一般用
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//进入主页面
				enterHome();
				dialog.dismiss();
				
			}
		});
		
		builder.setMessage(discription);
		builder.setPositiveButton("立刻升级", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//下载apk，并且替换安装
				//一般我们下载的apk都放在sdk里面，所以要首先判断sdk是否存在
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					//sdk存在
					//afinal 断点下载
					FinalHttp finalhttp = new FinalHttp();
					finalhttp.download(apkurl, 
							Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobilesoft.2.0.apk"
							, new AjaxCallBack<File>() {

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									t.printStackTrace();
									Toast.makeText(getApplicationContext(), "下载失败", 1).show();
									
									
									super.onFailure(t, errorNo, strMsg);
								}

								@Override
								public void onLoading(long count, long current) {
									super.onLoading(count, current);
									//当调用这个方法时显示下载进度
									tv_update_info.setVisibility(View.VISIBLE);
									//当前下载百分比
									int progress = (int) (current * 100 / count);
									tv_update_info.setText("下载进度" + progress + "%");
									
								}

								@Override
								public void onSuccess(File t) {
									
									super.onSuccess(t);
									installApk(t);
								}

								/**
								 * 安装apk
								 * @param t
								 */
								private void installApk(File t) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
									
									startActivity(intent);
								}
						
						
							});
				}else {
					//sdk不存在，不存在时要提示用户
					Toast.makeText(getApplicationContext(), "未发现sdk,请安装sdk后再尝试", 0).show();
					return;
				}
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//消失对话框
				dialog.dismiss();
				//进入主界面
				enterHome();
			}
		});
		
		//要将对话框显示出来
		builder.show();
		
	}

	protected void enterHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		//关闭当前页面，finish掉splash界面
		finish();
	}

	/*
	 * 得到应用程序的版本名称
	 */
	private String getVersionName(){
		//用来管理手机的apk
		PackageManager pm =getPackageManager();
		
		try {
			//得到知道APK的功能清单文件
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
		
	}
}
