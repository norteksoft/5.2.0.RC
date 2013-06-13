package com.norteksoft.acs.web.eunms;

public enum AddOrRomoveState {

	ADD(0), ROMOVE(1);
	public int code; 
	
	AddOrRomoveState(int code){
		this.code=code;
	}
	
	@Override
	public String toString() {
		return String.valueOf(code);
	}
	
}
