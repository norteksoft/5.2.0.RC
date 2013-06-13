package com.norteksoft.product.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

import com.ibm.icu.text.SimpleDateFormat;

/*字符串 DESede(3DES) 加密*/
public class ThreeDes {
	/**
	 * @param args在java中调用sun公司提供的3DES加密解密算法时
	 *            ，需要使 用到$JAVA_HOME/jre/lib/目录下如下的4个jar包： jce.jar
	 *            security/US_export_policy.jar security/local_policy.jar
	 *            ext/sunjce_provider.jar
	 */
	private static final String Algorithm = "DESede"; // 定义加密算法,可用
														// DES,DESede,Blowfish

	// keybyte为加密密钥，长度为24字节
	// src为被加密的数据缓冲区（源）
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			// 加密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);// 在单一方面的加密或解密
		} catch (java.security.NoSuchAlgorithmException e1) {
			// TODO: handle exception
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	// keybyte为加密密钥，长度为24字节
	// src为加密后的缓冲区
	public static byte[] decryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			// 解密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			// TODO: handle exception
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 文件加密
	 * @param systemPath系统默认路径
	 * @param file要加密的文件
	 * @return
	 * @throws Exception
	 */
	public static String encryptFile(String systemPath,String file) throws Exception{
		final byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10,
				0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD,
				0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36,
				(byte) 0xE2 }; // 24字节的密钥
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		systemPath=systemPath+"/"+format.format(new Date())+"/"+UUID.randomUUID().toString()+"/";
		File f = new File(systemPath);
		if(!f.exists())
			f.mkdirs();
		File newFile = new File(file);
		int byteSize=1024*1024;
		long fileLong=newFile.length();
		if(fileLong<1024*1024*10){
			byteSize=1024*1024;
		}if(fileLong<1024*1024*100){
			byteSize=byteSize*2;
		}if(fileLong<1024*1024*1000){
			byteSize=byteSize*4;
		}else{
			byteSize=byteSize*8;
		}
		BufferedInputStream bis = null;
		FileOutputStream out = null;
		try {
				bis = new BufferedInputStream(new FileInputStream(newFile));
				byte[]  buffer = new byte[byteSize];
				byte[]  newbuffer = null;
				int num=0;
				int size=0;
				while ((size=bis.read(buffer, 0, buffer.length)) != -1) {
					if(size!=buffer.length){
						newbuffer=new byte[size];
						System.arraycopy(buffer, 0, newbuffer, 0, size);
					}else{
						newbuffer =buffer;
					}
					out = new FileOutputStream(systemPath+num+"_"+num+"_"+num);
					byte[] newByte = encryptMode(keyBytes, newbuffer);
					out.write(newByte, 0,newByte.length);
					num++;
				}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			out.close();
			bis.close();
		}
		return systemPath;
	}
	/**
	 * 文件加密
	 * @param systemPath系统默认路径
	 * @param file要加密的文件
	 * @return
	 * @throws Exception
	 */
	public static String encryptFile(String systemPath,byte[] file) throws Exception{
		final byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10,
				0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD,
				0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36,
				(byte) 0xE2 }; // 24字节的密钥
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		systemPath=systemPath+"/"+format.format(new Date())+"/"+UUID.randomUUID().toString()+"/";
		File f = new File(systemPath);
		if(!f.exists())
			f.mkdirs();
		BufferedInputStream bis = null;
		FileOutputStream out = null;
		
		try {
			bis = new BufferedInputStream(new ByteArrayInputStream(file));
			byte[]  buffer = new byte[1024*1024];
			byte[]  newbuffer = null;
			int num=0;
			int size=0;
			while ((size=bis.read(buffer, 0, buffer.length)) != -1) {
				if(size!=buffer.length){
					newbuffer=new byte[size];
					System.arraycopy(buffer, 0, newbuffer, 0, size);
				}else{
					newbuffer =buffer;
				}
				out = new FileOutputStream(systemPath+num+"_"+num+"_"+num);
				byte[] newByte = encryptMode(keyBytes, newbuffer);
				out.write(newByte, 0,newByte.length);
				num++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			out.close();
			bis.close();
		}
		return systemPath;
	}
	
	/**
	 * 文件解密
	 * @param keyBytes密钥
	 * @param fileFoulder加密文件夹路径
	 * @param file解密后的文件
	 * @throws IOException
	 */
	public static void decryptFile(String fileFoulder,String file) throws IOException{
		final byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10,
				0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD,
				0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36,
				(byte) 0xE2 }; // 24字节的密钥
		File foulder = new File(fileFoulder);
		File[] files=foulder.listFiles();
		if(files!=null&&files.length!=0){
			int fileSize=files.length;
			FileOutputStream out = new FileOutputStream(file);
			for (int i=0;i<fileSize;i++) {
				byte[] oldByte=FileUtils.readFileToByteArray(new File(fileFoulder+"/"+i+"_"+i+"_"+i));
				byte[] newByte = decryptMode(keyBytes, oldByte);
				out.write(newByte, 0, newByte.length);
			}
			out.close();
		}
	}
	/**
	 * 文件解密
	 * @param keyBytes密钥
	 * @param fileFoulder加密文件夹路径
	 * @param out解密后的文件输出流
	 * @throws IOException
	 */
	public static void decryptFile(String fileFoulder,OutputStream out) throws IOException{
		final byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10,
				0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD,
				0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36,
				(byte) 0xE2 }; // 24字节的密钥
		File foulder = new File(fileFoulder);
		File[] files=foulder.listFiles();
		if(files!=null&&files.length!=0){
			int fileSize=files.length;
			for (int i=0;i<fileSize;i++) {
				byte[] oldByte=FileUtils.readFileToByteArray(new File(fileFoulder+"/"+i+"_"+i+"_"+i));
				byte[] newByte = decryptMode(keyBytes, oldByte);
				out.write(newByte, 0, newByte.length);
			}
			out.close();
		}
	}
	/**
	 * 文件解密
	 * @param keyBytes密钥
	 * @param fileFoulder加密文件夹路径
	 * @throws IOException
	 */
	public static byte[] decryptFile(String fileFoulder) throws IOException{
		final byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10,
				0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD,
				0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36,
				(byte) 0xE2 }; // 24字节的密钥
		ByteArrayOutputStream  out = new ByteArrayOutputStream();
		File foulder = new File(fileFoulder);
		File[] files=foulder.listFiles();
		if(files!=null&&files.length!=0){
			int fileSize=files.length;
			for (int i=0;i<fileSize;i++) {
				byte[] oldByte=FileUtils.readFileToByteArray(new File(fileFoulder+"/"+i+"_"+i+"_"+i));
				byte[] newByte = decryptMode(keyBytes, oldByte);
				out.write(newByte, 0, newByte.length);
			}
			out.close();
		}
		return out.toByteArray();
	}
	
	
	/**
	 * 文件加密并上传
	 * @param systemPath 系统默认路径
	 * @return
	 * @throws Exception
	 */
	public static String encryptAndUploadFile(Long userId,String systemPath) throws Exception{
		final byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10,
				0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD,
				0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36,
				(byte) 0xE2 }; // 24字节的密钥
		
		HttpServletRequest request=ServletActionContext.getRequest();
		File filePath=((MultiPartRequestWrapper)request).getFiles("Filedata")[0];
		systemPath=systemPath+"/"+userId+"/"+UUID.randomUUID().toString()+"/";
		File f = new File(systemPath);
		if(!f.exists())
			f.mkdirs();
		int byteSize=1024*1024;
		long fileLong=filePath.length();
		if(fileLong<1024*1024*10){
			byteSize=1024*1024;
		}if(fileLong<1024*1024*100){
			byteSize=byteSize*2;
		}if(fileLong<1024*1024*1000){
			byteSize=byteSize*4;
		}else{
			byteSize=byteSize*8;
		}
		BufferedInputStream bis = null;
		FileOutputStream out = null;
		try {
				bis = new BufferedInputStream(new FileInputStream(filePath));
				byte[]  buffer = new byte[byteSize];
				byte[]  newbuffer = null;
				int num=0;
				int size=0;
				while ((size=bis.read(buffer, 0, buffer.length)) != -1) {
					if(size!=buffer.length){
						newbuffer=new byte[size];
						System.arraycopy(buffer, 0, newbuffer, 0, size);
					}else{
						newbuffer =buffer;
					}
					out = new FileOutputStream(systemPath+num+"_"+num+"_"+num);
					byte[] newByte = encryptMode(keyBytes, newbuffer);
					out.write(newByte, 0,newByte.length);
					num++;
				}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			out.close();
			bis.close();
		}
		return systemPath;
	}
	
	/**
	 * 文件解密并下载
	 * @param keyBytes密钥
	 * @param fileFoulder加密文件夹路径
	 * @param file解密后的文件
	 * @throws IOException
	 */
	public static void decryptAndDownloadFile(String fileFoulder,String fileName) throws IOException{
		final byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10,
				0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD,
				0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36,
				(byte) 0xE2 }; // 24字节的密钥
		File foulder = new File(fileFoulder);
		File[] files=foulder.listFiles();
		if(files!=null&&files.length!=9){
			OutputStream out=null;
			HttpServletResponse response=ServletActionContext.getResponse();
			response.reset();
			try {
				int fileSize=files.length;
				for (int i=0;i<fileSize;i++) {
					byte[] oldByte=FileUtils.readFileToByteArray(new File(fileFoulder+"/"+i+"_"+i+"_"+i));
					byte[] newByte = decryptMode(keyBytes, oldByte);
					response.setContentType("application/x-download");
					byte[] byname=fileName.getBytes("gbk");
					fileName=new String(byname,"8859_1");
					response.addHeader("Content-Disposition", "attachment;filename="+fileName);
					out=response.getOutputStream();
					out.write(newByte, 0, newByte.length);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				out.close();
			}
		}
	}
	
	

//	public static void main(String[] args) throws Exception {
//		long a = System.currentTimeMillis();
//			String path=ThreeDes.encryptFile(123l,"d:/", "d:/a.rmvb");
//		long b=System.currentTimeMillis();
//		System.out.println((b-a)/1000);
//		long c = System.currentTimeMillis();
//			ThreeDes.decryptFile(path,"d:/b.rmvb" );
//		long d = System.currentTimeMillis();
//		System.out.println((d-c)/1000);
//	}
	
}