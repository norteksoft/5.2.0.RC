package com.norteksoft.product.api.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.ibm.icu.text.SimpleDateFormat;
import com.norteksoft.product.api.FileService;
import com.norteksoft.product.enumeration.UploadFileType;
import com.norteksoft.product.mongo.MongoFile;
import com.norteksoft.product.mongo.MongoService;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreeDes;

public class FileServiceImpl implements FileService {
	private MongoService mongoService;
	
	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}

	public String saveFile(File file) {
		
		String uploadFileType=PropUtils.getProp("application.properties","upload.file.type");
		if(StringUtils.isEmpty(uploadFileType)){
			uploadFileType=PropUtils.getProp("applicationContent.properties","upload.file.type");
		}
		String filePath="";
		try {
			switch (UploadFileType.valueOf(uploadFileType)) {
			case SERVERS_SECRET:
				filePath=uploadSecret(file);
				break;
			case SERVERS_NORMAL:
				filePath=uploadNormal(file);
				break;
			case MONGO_SERVERS:
				MongoFile mongoFile = mongoService.saveFile(file, "");
				filePath=mongoFile.getFileId();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}
	
	private String uploadSecret(File file)throws Exception{
		String path=PropUtils.getProp("application.properties","upload.file.path");
		if(StringUtils.isEmpty(path)){
			path=PropUtils.getProp("applicationContent.properties","upload.file.path");
		}
		return ThreeDes.encryptFile(path,file.getPath());
	}
	
	private String uploadNormal(File file)throws Exception{
		String path=PropUtils.getProp("application.properties","upload.file.path");
		if(StringUtils.isEmpty(path)){
			path=PropUtils.getProp("applicationContent.properties","upload.file.path");
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		path = cretaFolder(path+"/"+format.format(new Date())+"/");
		path += UUID.randomUUID().toString();
		return uploadFile(file, path);
	}
	
	public String saveFile(byte[] file) {
		String uploadFileType=PropUtils.getProp("application.properties","upload.file.type");
		if(StringUtils.isEmpty(uploadFileType)){
			uploadFileType=PropUtils.getProp("applicationContent.properties","upload.file.type");
		}
		String filePath="";
		try {
			switch (UploadFileType.valueOf(uploadFileType)) {
			case SERVERS_SECRET:
				filePath=uploadSecret(file);
				break;
			case SERVERS_NORMAL:
				filePath=uploadNormal(file);
				break;
			case MONGO_SERVERS:
				MongoFile mongoFile = mongoService.saveFile(file, "");
				filePath=mongoFile.getFileId();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}
	
	private String uploadSecret(byte[] file)throws Exception{
		String path=PropUtils.getProp("application.properties","upload.file.path");
		if(StringUtils.isEmpty(path)){
			path=PropUtils.getProp("applicationContent.properties","upload.file.path");
		}
		return ThreeDes.encryptFile(path,file);
	}
	
	private String uploadNormal(byte[] file)throws Exception{
		String path=PropUtils.getProp("application.properties","upload.file.path");
		if(StringUtils.isEmpty(path)){
			path=PropUtils.getProp("applicationContent.properties","upload.file.path");
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		path = cretaFolder(path+"/"+format.format(new Date())+"/");
		path += UUID.randomUUID().toString();
		BufferedInputStream bis = null;
		FileOutputStream out = null;
		try {
			bis = new BufferedInputStream(new ByteArrayInputStream(file));
			byte[]  buffer = new byte[1024*1024];
			int size=0;
			out = new FileOutputStream(path);
			while ((size=bis.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0,size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			out.close();
			bis.close();
		}
		return path;
	}
	
	/**
	 * 创建文件夹
	 * @param path
	 * @return
	 */
	private String cretaFolder(String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return path;
	}
	
	/**
	 * 上传文件
	 */
	private String uploadFile(File path,String serverPath)throws Exception{
		FileUtils.copyFile(path, new File(serverPath));
		return serverPath;
	}

	public byte[] getFile(String filePath) {
		String uploadFileType=PropUtils.getProp("application.properties","upload.file.type");
		if(StringUtils.isEmpty(uploadFileType)){
			uploadFileType=PropUtils.getProp("applicationContent.properties","upload.file.type");
		}
		byte[] file=null;
		try {
			switch (UploadFileType.valueOf(uploadFileType)) {
			case SERVERS_SECRET:
				file =ThreeDes.decryptFile(filePath);
				break;
			case SERVERS_NORMAL:
				file = FileUtils.readFileToByteArray(new File(filePath));
				break;
			case MONGO_SERVERS:
				file = mongoService.getFile(filePath);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public void writeTo(String filePath,OutputStream out) {
		String uploadFileType=PropUtils.getProp("application.properties","upload.file.type");
		if(StringUtils.isEmpty(uploadFileType)){
			uploadFileType=PropUtils.getProp("applicationContent.properties","upload.file.type");
		}
		try {
			switch (UploadFileType.valueOf(uploadFileType)) {
			case SERVERS_SECRET:
				ThreeDes.decryptFile(filePath,out);
				break;
			case SERVERS_NORMAL:
				byte[] file=FileUtils.readFileToByteArray(new File(filePath));
				out.write(file);
				out.close();
				break;
			case MONGO_SERVERS:
				mongoService.writeTo(filePath, out);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteFile(String filePath){
		String uploadFileType=PropUtils.getProp("application.properties","upload.file.type");
		if(StringUtils.isEmpty(uploadFileType)){
			uploadFileType=PropUtils.getProp("applicationContent.properties","upload.file.type");
		}
		try {
			switch (UploadFileType.valueOf(uploadFileType)) {
			case SERVERS_SECRET:
				FileUtils.deleteDirectory(new File(filePath));
				break;
			case SERVERS_NORMAL:
				FileUtils.deleteQuietly(new File(filePath));
				break;
			case MONGO_SERVERS:
				mongoService.deleteFile(filePath);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
