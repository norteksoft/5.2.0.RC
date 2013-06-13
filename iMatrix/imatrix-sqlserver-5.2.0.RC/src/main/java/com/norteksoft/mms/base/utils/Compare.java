package com.norteksoft.mms.base.utils;

public interface Compare<T> {
	
	boolean compareGT(T srcObj, T distObj);
	
	boolean compareGET(T srcObj, T distObj);
	
	boolean compareLT(T srcObj, T distObj);
	
	boolean compareLET(T srcObj, T distObj);
	
}
