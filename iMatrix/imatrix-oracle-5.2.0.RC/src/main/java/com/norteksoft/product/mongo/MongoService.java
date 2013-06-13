package com.norteksoft.product.mongo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoService {
	private String host;
	private int port;
	private String dbname;
	private String username;
	private String password;

	private Log logger = LogFactory.getLog(MongoService.class);
	private DB db = null;
	
	public DB getDb(){
		if(db == null) db = getSingleDb();
		return db;
	}
	
	public synchronized DB getSingleDb(){
		try {
			Mongo mongo =  new Mongo(host, port);
			DB db = mongo.getDB( dbname );
			boolean auth = db.authenticate(username, password.toCharArray());
			logger.debug("Mongo authenticate result : "+auth);
			if(auth) return db;
			throw new MongoException(" Can not connect to Mongo ...");
		} catch (UnknownHostException e) {
			throw new MongoException(" unknown host ...", e);
		} catch (MongoException e) {
			throw new MongoException(" Mongo exception ...", e);
		}
	}
	
	/**
	 * 保存文件
	 * @param file 文件
	 * @param fileName 文件名称
	 * @return 文件ID
	 * @throws IOException
	 */
	public MongoFile saveFile(File file, String fileName) throws IOException {
		FileInputStream in = new FileInputStream(file);
		return saveFile(in, fileName);
	}
	
	/**
	 * 保存文件
	 * @param in 文件输入流
	 * @param fileName 文件名称
	 * @return 文件ID
	 * @throws IOException
	 */
	public MongoFile saveFile(InputStream in, String fileName) throws IOException {
		GridFS fs = new GridFS(getDb());
		GridFSInputFile gridFile = fs.createFile(in, true);
		gridFile.setFilename(fileName);
		long chunkSize = GridFS.DEFAULT_CHUNKSIZE;
		if(in.available()>10*1024*1024){
			chunkSize = 1024*1024;
		}
		gridFile.save(chunkSize);
		IOUtils.closeQuietly(in);
		MongoFile file = new MongoFile(gridFile.getId().toString(), gridFile.getFilename(), gridFile.getLength());
		file.setFileType(gridFile.getContentType());
		return file;
	}
	
	public MongoFile saveFile(byte[] fileBody, String fileName) throws IOException {
		return saveFile(new ByteArrayInputStream(fileBody), fileName);
	}
	
	public byte[] getFile(String fileId){
		GridFS fs = new GridFS(getDb());
		GridFSDBFile queryFile = fs.find(new ObjectId(fileId));
		return getFile(queryFile);
	}
	
	private byte[] getFile(GridFSDBFile queryFile){
		byte[] file = null;
		if(queryFile != null){
			ByteArrayOutputStream bao = null;
			try {
				bao = new ByteArrayOutputStream();
				queryFile.writeTo(bao);
				file = bao.toByteArray();
			} catch (IOException e) {
				
			} finally{
				IOUtils.closeQuietly(bao);
			}
		}
		return file;
	}
	
	public MongoFile copyFile(String fileId){
		GridFS fs = new GridFS(getDb());
		GridFSDBFile queryFile = fs.find(new ObjectId(fileId));
		MongoFile file = null;
		if(queryFile != null){
			logger.debug("query file info, fileId:"+queryFile.getId().toString());
			GridFSInputFile gridFile = fs.createFile(getFile(queryFile));
			gridFile.save();
			file = new MongoFile(gridFile.getId().toString(), gridFile.getFilename(), gridFile.getLength());
			logger.debug("copy file info : "+file);
		}
		return file;
	}
	
	public void writeTo(String fileId, OutputStream out) throws IOException{
		GridFS fs = new GridFS(getDb());
		GridFSDBFile queryFile = fs.find(new ObjectId(fileId));
		//fs.findOne( new BasicDBObject( "_id" , new ObjectId("4ed5ef3a54c6baba1fb31a12")) );
		logger.debug("query file info : "+queryFile);
		if(queryFile != null) queryFile.writeTo(out);
	}
	
	public void deleteFile(String fileId){
		GridFS fs = new GridFS(getDb());
		fs.remove(new ObjectId(fileId));
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setLogger(Log logger) {
		this.logger = logger;
	}
}
