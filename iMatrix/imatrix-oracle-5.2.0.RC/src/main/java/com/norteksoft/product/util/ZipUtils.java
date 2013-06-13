package com.norteksoft.product.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.util.zip.ZipEntry;
import com.norteksoft.product.util.zip.ZipFile;
import com.norteksoft.product.util.zip.ZipOutputStream;

  
public class ZipUtils {  
      /**
       * 压缩文件入口
       * @param zipFileName
       * @param inputFileName
       * @throws Exception
       */
    public static void zipFile(String zipFileName, String inputFileName)  
            throws Exception {  
        ZipOutputStream out = new ZipOutputStream(  
                new FileOutputStream(zipFileName));  
        out.setEncoding("utf-8");  
        File inputFile = new File(inputFileName);  
        zipIt(out, inputFile, "", true);  
        out.close();  
    }  
      
    /* 
     * 能支持中文的压缩 参数base 开始为"" first 开始为true 
     */  
    public static void zipIt(ZipOutputStream out, File file,  
            String base, boolean first) throws Exception {  
        if (file.isDirectory()) {  
            File[] fiels = file.listFiles();  
            if (first) {  
                first = false;  
            } else {  
                base = base + "/";  
            }  
            for (int i = 0; i < fiels.length; i++) {  
                zipIt(out, fiels[i], base + fiels[i].getName(), first);  
            }  
        } else {  
            if (first) {  
                base = file.getName();  
            }  
            out.putNextEntry(new ZipEntry(base));  
            FileInputStream in = new FileInputStream(file);  
            int b;  
            while ((b = in.read()) != -1) {  
                out.write(b);  
            }  
            in.close();  
        }  
    }  
  
    public static String unZipFile(String unZipFileName, String unZipPath)  
            throws Exception {  
        ZipFile zipFile = new ZipFile(  
                unZipFileName,prexEncoding(unZipFileName));  
        unZipFileByOpache(zipFile, unZipPath); 
        return unZipPath;
    }  
      
    /* 
     * 解压文件 unZip为解压路径 
     */  
    @SuppressWarnings("unchecked")
	public static void unZipFileByOpache(ZipFile zipFile,  
            String unZipRoot) throws Exception, IOException {  
       Enumeration e = zipFile.getEntries();  
        ZipEntry zipEntry;  
        while (e.hasMoreElements()) {  
            zipEntry = (ZipEntry) e.nextElement();  
            if (zipEntry.isDirectory()) {  
            } else {  
            	 InputStream fis = zipFile.getInputStream(zipEntry);  
                File file = new File(unZipRoot + File.separator  
                        + zipEntry.getName());  
                File parentFile = file.getParentFile();  
                parentFile.mkdirs();  
                FileOutputStream fos = new FileOutputStream(file);  
                byte[] b = new byte[1024];  
                int len;  
                while ((len = fis.read(b, 0, b.length)) != -1) {  
                    fos.write(b, 0, len);  
                }  
                fos.close();  
                fis.close();  
            }  
        } 
        zipFile.close();
    }  
    
//    /**
//     * 解析文件编码格试
//     * @param fileName
//     * @return
//     */
//    public static String prexEncoding(String fileName){//d:/1.txt
//    	java.io.File f=new java.io.File(fileName);  
//    	try{  
//    	  java.io.InputStream ios=new java.io.FileInputStream(f);  
//    	  byte[] b=new byte[3];  
//    	  ios.read(b);  
//    	  ios.close();  
//    	  if(b[0]==-17&&b[1]==-69&&b[2]==-65)  
//    		  return "utf-8"; 
//    	  else 
//    		  return "GBK";
//    	}catch(Exception e){  
//    	   e.printStackTrace();  
//    	}
//    	return "utf-8";
//    }
    
    /**
     * 解析文件编码格试
     * @param fileName
     * @return
     */
    public static String prexEncoding(String fileName){
    	String code="GBK";
 	   try {
 		   CharsetDetector charDect = new CharsetDetector();
 		   FileInputStream input = new FileInputStream(new File(fileName));
 		   String[] probableSet = charDect.detectChineseCharset(input);
 		   StringBuilder bu =new StringBuilder();
 		   for (String charset : probableSet)
 		   {
 			   bu.append(charset+",");
 		   }
 		   if(StringUtils.isNotEmpty(bu.toString())){
 			   if(StringUtils.containsIgnoreCase(bu.toString(), "GB2312")||StringUtils.containsIgnoreCase(bu.toString(), "GBK")){
 				   return "GB2312";
 			   }else if(StringUtils.containsIgnoreCase(bu.toString(), "GB18030")){
 				   return "GB2312";
 			   }else if(StringUtils.containsIgnoreCase(bu.toString(), "UTF-8")){
 				   return "UTF-8";
 			   }
 		   }
 		} catch (Exception e) {
 			e.printStackTrace(); 
 		}
 	   return code;
    }
    
    /**
     * 解析文件夹
     */
    public static void prexFolder(File dir){//d:/1.txt
    	File[] files =dir.listFiles(); 
    	if(files.length != 0){
    		for (File file : files) {
				if(file.isDirectory()){ 
	                System.out.println(file.getName());
	                prexFolder(file);
	            }else{
	            	System.out.println(file.getName());
	            }
			}
    	}
    }
    public static void zipFolder(String exportRootPath,OutputStream fileOut){
    	//将生成的文件夹打成zip包且删除暂时文件夹
    	try {
    		File baseFile = new File(exportRootPath); 
    		ZipOutputStream out = new ZipOutputStream(fileOut);  
    		out.setEncoding("gbk");  
    		zipIt(out, baseFile, "", true);
    		out.close();
    	} catch (Exception e) {
    		e.printStackTrace(); 
    	}
    }
  
//    public static void main(String[] args) throws Exception {  
//        zipFile("d:/temp/folders.zip", "D:/temp/folders");//压缩入口  
//        unZipFile("d:/测试.zip","e:/a/");//解压入口 
//    	prexFolder(new File("e:/a"));
//    }  
} 
