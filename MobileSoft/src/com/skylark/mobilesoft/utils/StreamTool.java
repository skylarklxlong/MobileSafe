package com.skylark.mobilesoft.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 将流转换成字符串的工具类
 * @param is输入流
 * @return String 返回的字符串
 * @throws IOException
 * @author XueLong_Li
 * 
 */
public class StreamTool {

	public static String decodeStream(InputStream is) throws IOException {

		// 任何数据都有一个底层流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 现在要做的就是把 in流中的数据 写入到baos流中 然后再将baos流转换成 字符串
		int length = 0;
		byte[] buffer = new byte[1024];

		while ((length = is.read(buffer)) > 0) {

			baos.write(buffer, 0, length);
		}
		is.close();
		String result = baos.toString();
		baos.close();
		return result;
	}

}
