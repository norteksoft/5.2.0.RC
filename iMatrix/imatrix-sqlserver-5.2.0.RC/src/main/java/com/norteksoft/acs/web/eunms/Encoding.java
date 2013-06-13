package com.norteksoft.acs.web.eunms;

public enum Encoding {
	
	US_ASCII("US-ASCII"),
	ISO_8859_1("ISO-8859-1"),
	UTF_8("UTF-8"),
	UTF_16BE("UTF-16BE"),
	UTF_16LE("UTF-16LE"),
	UTF_16("UTF-16");
	
	private String code;
	
	Encoding(String code){
		this.code = code;
	}
	
	public String toString(){
		return this.code;
	}
	
	public String getCode(){
		return this.code;
	}
	
}
