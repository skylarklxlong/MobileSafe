package com.skylark.mobilesoft.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	/**
	 * 检验某个服务是否还活着
	 * servicename:传进来的服务的名称  包名!!!!
	 */
	public static boolean isServiceRunning(Context context, String servicename) {
		// 检验服务是否活着
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		//表示最多可以运行100个服务  如果正在运行的有5个  则返回5个
		List<RunningServiceInfo> infos = am.getRunningServices(100); 
		for (RunningServiceInfo info : infos) {
			String name = info.service.getClassName();
			if (name.equals(servicename)) {
				return true;
			}
		}
		
		return false;
	}

}
