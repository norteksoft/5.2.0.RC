package com.norteksoft.product.util;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * 从jar文件中读取文件
 * @author wurong
 */
public class JarFileUtils{
	/**
	 * 从jar文件中读取文件
	 * @param url 该文件在jar中的路径
	 * @return 文件内容
	 */
	public static <T> String readFile(Class<T> clazz ,String url) throws Exception{
		String currentJarPath = URLDecoder.decode(clazz.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8");
		JarFile currentJar = new JarFile(currentJarPath);
		JarEntry dbEntry = currentJar.getJarEntry(url);
		InputStream in = currentJar.getInputStream(dbEntry);
		 byte[] bs;
		StringBuffer buffer = new StringBuffer();
		int count = 1;
		while (count > 0) {
			bs = new byte[1024];
			count = in.read(bs, 0, 1024);
			buffer.append(new String(bs, "UTF-8"));
		}
		return  buffer.toString().trim();
	}

}
