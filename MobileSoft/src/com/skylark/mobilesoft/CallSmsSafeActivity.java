package com.skylark.mobilesoft;

import java.util.List;

import com.skylark.mobilesoft.db.dao.BlackNumberDao;
import com.skylark.mobilesoft.domain.BlackNumberInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 黑名单拦截还可以添加一些其他小功能
 * 比如：呼叫转移  可以设置黑名单用户打过来时停到的语音
 * 
 * 
* @ClassName: CallSmsSafeActivity 
* @Description(描叙): 
* @author Skylark 
* @date 2016年8月14日 下午10:06:42
 */
public class CallSmsSafeActivity extends Activity {
	private ListView lv_callsms_safe;
	private BlackNumberDao dao;
	private List<BlackNumberInfo> infos;
	private CallSmsSafeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		
		//准备数据
		dao = new BlackNumberDao(CallSmsSafeActivity.this);
		infos = dao.findAll(); // 拿到所有的黑名单数据，存在List<BlackNumberInfo>
		
		adapter = new CallSmsSafeAdapter();
		
		lv_callsms_safe.setAdapter(adapter);
	}
	/**
	 *listview 的 适配器 
	 */
	private class CallSmsSafeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			//一级优化  就是要控制listview的高为定值
			
			View view;
			//三级优化
			ViewHolder holder;
			//二级优化
			if(convertView == null){
				view = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
				holder = new ViewHolder();
				holder.tv_black_number = (TextView) view.findViewById(R.id.tv_black_number);
				holder.tv_black_mode = (TextView) view.findViewById(R.id.tv_black_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				view.setTag(holder);
			}else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			
			holder.tv_black_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			if ("1".equals(mode)) {
				holder.tv_black_mode.setText("电话拦截");
				
			}else if ("2".equals(mode)) {
				holder.tv_black_mode.setText("短信拦截");
			}else {
				holder.tv_black_mode.setText("全部拦截");
			}
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("警告");
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setMessage("确定要删除这条记录么？");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dao.delete(infos.get(position).getNumber());
							infos.remove(position);
							//通知listview数据适配器更新 这点非常重要 如果没有这个就会出现列表不会立即更新的现象
							adapter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});
			
			
			
			
			return view;
		}
		
	}
	static class ViewHolder{
		TextView tv_black_number;
		TextView tv_black_mode;
		ImageView iv_delete;
	}
	
	
	
	private EditText et_blacknumber;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button bt_ok;
	private Button bt_cancel;
	//添加黑名单用户
	
	public void addBlackNumber(View view){
		//自定义 一个dialog  来让用户添加黑名单用户
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		et_blacknumber = (EditText) contentView.findViewById(R.id.et_blacknumber);
		cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
		bt_cancel = (Button) contentView.findViewById(R.id.cancel);
		bt_ok = (Button) contentView.findViewById(R.id.ok);
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				if(TextUtils.isEmpty(blacknumber)){
					Toast.makeText(getApplicationContext(), "黑名单号码不能为空", 0).show();
					return;
				}
				String mode ;
				if(cb_phone.isChecked()&&cb_sms.isChecked()){
					//全部拦截
					mode = "3";
				}else if(cb_phone.isChecked()){
					//电话拦截
					mode = "1";
				}else if(cb_sms.isChecked()){
					//短信拦截
					mode = "2";
				}else{
					Toast.makeText(getApplicationContext(), "请选择拦截模式", 0).show();
					return;
				}
				//数据被加到数据库
				dao.add(blacknumber, mode);
				//更新listview集合里面的内容。
				BlackNumberInfo info = new BlackNumberInfo();
				info.setMode(mode);
				info.setNumber(blacknumber);
				//设置数据添加到第一个  为了更好的用户体验
				infos.add(0, info);
				//通知listview数据适配器数据更新了。
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		
	}
	
	
}
