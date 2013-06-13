package com.norteksoft.mms.base.utils;


public class IntegerCompare implements Compare<Integer>{

	public boolean compareGET(Integer srcObj, Integer distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.intValue() >= distObj.intValue();
	}

	public boolean compareGT(Integer srcObj, Integer distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.intValue() > distObj.intValue();
	}

	public boolean compareLET(Integer srcObj, Integer distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.intValue() <= distObj.intValue();
	}

	public boolean compareLT(Integer srcObj, Integer distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.intValue() < distObj.intValue();
	}

}
