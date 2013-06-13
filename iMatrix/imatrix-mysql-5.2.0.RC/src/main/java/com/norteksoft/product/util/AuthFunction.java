package com.norteksoft.product.util;

import java.io.Serializable;

public class AuthFunction implements Serializable{
	private static final long serialVersionUID = 1L;
	private String functionPath;
	private String functionId;
	public String getFunctionPath() {
		return functionPath;
	}
	public void setFunctionPath(String functionPath) {
		this.functionPath = functionPath;
	}
	public String getFunctionId() {
		return functionId;
	}
	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}
	
}
