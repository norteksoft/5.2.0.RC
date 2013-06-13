package com.norteksoft.product.mongo;

public class MongoFile {

	private String fileId;
	private String fileName;
	private String fileType;
	private long fileSize;

	public MongoFile(){}
	
	public MongoFile(String fileId, String fileName, long fileSize){
		this.fileId = fileId;
		this.fileName = fileName;
		this.fileSize = fileSize;
	}
	
	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("MongoFile[ {fileId:").append(fileId)
			.append(",fileName:").append(fileName).append(",fileSize:").append(fileSize).append("} ]").toString();
	}
	
}
