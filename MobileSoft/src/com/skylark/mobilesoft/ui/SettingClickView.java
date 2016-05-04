package com.skylark.mobilesoft.ui;

import com.skylark.mobilesoft.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 我们自定义的组合控件,它里面有两个TextView,还有一个ImageView,还有一个View
 * @author Xuelong
 *
 */
public class SettingClickView extends RelativeLayout {

	private TextView tv_title;
	private TextView tv_desc;
	
	private String desc_on;
	private String desc_off;
	
	/**
	 * 初始化布局文件
	 * @param context
	 */
	private void iniView(Context context) {
		//把一个布局文件转换成一个view,并且加载在SettingItemView中
		View.inflate(context, R.layout.setting_click_view, this);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
	}
	
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	/**
	 * 这个带有两个参数的构造方法,布局文件使用的时候调用
	 * @param context
	 * @param attrs
	 */
	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.skylark.mobilesoft", "title");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.skylark.mobilesoft", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.skylark.mobilesoft", "desc_off");
		tv_title.setText(title);
		setDesc(desc_off);

	}

	public SettingClickView(Context context) {
		super(context);
		iniView(context);
	}
	
	/**
	 * 设置组合控件的状态
	 */
	public void setChecked(boolean checked){
		if (checked) {
			setDesc(desc_on);
		}else {
			setDesc(desc_off);
		}
	}
	/**
	 * 设置组合框的描述信息
	 */
	public void setDesc(String desc){
		tv_desc.setText(desc);
	}
	
	/**
	 * 设置组合控件的标题
	 */
	public void setTitle(String title){
		tv_title.setText(title);
	}
}
