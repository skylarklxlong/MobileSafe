package com.skylark.mobilesoft;

import com.skylark.mobilesoft.utils.MD5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	protected static final String TAG = "HomeActivity";
	private GridView list_home;
	private MyAdapter adapter;
	private static String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计",
			"手机杀毒", "缓存清理", "高级工具", "设置中心" };
	private static int[] ids = { R.drawable.safe, R.drawable.callmsgsafe,
			R.drawable.app, R.drawable.taskmanager, R.drawable.netmanager,
			R.drawable.trojan, R.drawable.sysoptimize, R.drawable.atools,
			R.drawable.settings };
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		list_home = (GridView) findViewById(R.id.list_home);
		adapter = new MyAdapter();
		list_home.setAdapter(adapter);
		list_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch (position) {
				case 0:// 进入手机防盗页面
					showLostFindDialog();
					break;
				case 1://进入黑名单拦截界面
					intent = new Intent(HomeActivity.this,
							CallSmsSafeActivity.class);
					startActivity(intent);
					break;
				case 2:
					

					break;
				case 3:

					break;
				case 4:

					break;
				case 5:

					break;
				case 6:

					break;
				case 7://进入高级工具
					intent = new Intent(HomeActivity.this,
							AtoolsActivity.class);
					startActivity(intent);
					break;
				case 8: // 进入设置中心
					intent = new Intent(HomeActivity.this,
							SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}
		});
	}

	protected void showLostFindDialog() {
		// 是否设置过密码
		if (isSetupPwd()) {
			// 已经设置过密码,弹出的是输入对话框
			showEnterDailog();
		} else {
			// 没有设置过密码,弹出的是设置对话框
			showSetupPwdDailog();
		}
	}

	private EditText et_setup_pwd;
	private EditText et_setup_confirm;
	private Button ok;
	private Button cancel;
	private AlertDialog dialog;

	/**
	 * 设置密码对话框
	 */
	private void showSetupPwdDailog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		// 自定义一个布局文件
		View view = View.inflate(HomeActivity.this,
				R.layout.dialog_setup_password, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 取出密码
				String password = et_setup_pwd.getText().toString().trim();
				String password_confirm = et_setup_confirm.getText().toString().trim();
				if (TextUtils.isEmpty(password)
						|| TextUtils.isEmpty(password_confirm)) {
					Toast.makeText(HomeActivity.this, "密码为空请重新输入", 0).show();
					return;
				}
				// 判断密码是否一致,然后保存
				if (password.equals(password_confirm)) {
					// 如果一致的话就保存密码,然后消掉对话框,并且进入手机防盗页面
					Editor editor = sp.edit();
					editor.putString("password", MD5Utils.md5Password(password));//保存加密后的密码
					editor.commit();
					dialog.dismiss();
					Log.i(TAG, "进入手机防盗页面");
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
				} else {

					Toast.makeText(HomeActivity.this, "密码不一致,请重新输入", 0).show();
					return;
				}
			}
		});

		builder.setView(view);
		dialog = builder.show();
		
		/**
		 * 如果要在低版本的安卓机上运行,则要考虑到一个界面有的细节
		 * 低版本默认的弹出窗口的背景都是黑色的
		 * 所以要现在两个布局文件dialog_enter_password和dialog_setuo_password
		 * 中添加android:background="#ffffff"
		 * 然后还要在showEnterDailog()和showSetupPwdDailog()两个方法中
		 * 把builder.setView(view);dialog = builder.show();替换为
		 * dialog = builder.create(); dialog.setView(view,0,0,0); dialog.show();
		 */

	}

	/**
	 * 输入密码对话框
	 */
	private void showEnterDailog() {

		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		// 自定义一个布局文件
		View view = View.inflate(HomeActivity.this,
				R.layout.dialog_enter_password, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 取出密码
				String password = et_setup_pwd.getText().toString().trim();
				String save_password = sp.getString("password", "");//取出加密后的密码
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "密码为空请重新输入", 0).show();
					return;
				}
				
				if (MD5Utils.md5Password(password).equals(save_password)) {
					//输入的密码是之前设置的密码 取消对话框  然后进入手机防盗页面
					dialog.dismiss();
					Log.i(TAG, "进入手机防盗页面");
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
				}else {
					
					Toast.makeText(HomeActivity.this, "密码错误,请重新输入", 0).show();
					et_setup_pwd.setText("");
					return;
				}
			}
		});

		builder.setView(view);
		dialog = builder.show();
		

	}

	/**
	 * 判断是否设置过密码
	 * 
	 * @return
	 */
	private Boolean isSetupPwd() {
		String password = sp.getString("password", null);

		// if (TextUtils.isEmpty(password)) {
		// return false;
		// }else {
		// return true;
		// }

		return !TextUtils.isEmpty(password);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return names.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = View.inflate(HomeActivity.this,
					R.layout.list_item_home, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
			tv_item.setText(names[position]);
			iv_item.setImageResource(ids[position]);
			return view;
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

	}
}
