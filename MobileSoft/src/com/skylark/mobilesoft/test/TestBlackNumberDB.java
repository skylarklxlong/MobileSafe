package com.skylark.mobilesoft.test;

import com.skylark.mobilesoft.db.BlackNumberDBOpenHelper;
import android.test.AndroidTestCase;
/**
 * 数据库测试类
 * @author XueLong_Li
 *
 */
public class TestBlackNumberDB extends AndroidTestCase {

	public void testCreateDB() throws Exception{

		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
		helper.getWritableDatabase();
		
	}
	
}
