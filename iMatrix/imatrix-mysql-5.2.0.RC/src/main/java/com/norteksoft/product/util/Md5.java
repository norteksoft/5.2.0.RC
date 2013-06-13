package com.norteksoft.product.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {
	public static String toMessageDigest(String str) {
		MessageDigest messageDigest = null;   
		try {  
	            messageDigest = MessageDigest.getInstance("MD5");  
	  
	            messageDigest.reset();  
	  
	            messageDigest.update(str.getBytes("UTF-8"));  
	        } catch (NoSuchAlgorithmException e) {  
	            throw new RuntimeException("No such algorithm!");  
	        } catch (UnsupportedEncodingException e) {  
	        	throw new RuntimeException("encoding exception!");  
	        }  
	  
	        byte[] byteArray = messageDigest.digest();  
	  
	        StringBuffer md5StrBuff = new StringBuffer();  
	  
	        for (int i = 0; i < byteArray.length; i++) {              
	            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
	                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
	            else  
	                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
	        }  
	  
	        return md5StrBuff.toString();  

	}
	
//	public static void main(String[] args) {
//		String s="123";
//		String aa=Md5.toMessageDigest(s);
//		System.out.println(aa.equals("202cb962ac59075b964b07152d234b70"));
////		Md5.toMessageDigest(s);
//	}

}
