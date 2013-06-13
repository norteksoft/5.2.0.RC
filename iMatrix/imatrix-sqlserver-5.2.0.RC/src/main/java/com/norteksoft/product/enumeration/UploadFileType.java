package com.norteksoft.product.enumeration;

public enum UploadFileType {
	/**
	 * 服务器加密
	 */
	SERVERS_SECRET("servers.secret"),
	/**
	 * 服务器不加密
	 */
	SERVERS_NORMAL("servers.normal"),
	/**
	 * Mongo服务器
	 */
	MONGO_SERVERS("mongo.servers");
	
	public String code;
	
	UploadFileType(String code){
		this.code=code;
	}
	
	public int getIndex(){
		return this.ordinal();
	}
	
	public String getCode() {
		return code;
	}
}
