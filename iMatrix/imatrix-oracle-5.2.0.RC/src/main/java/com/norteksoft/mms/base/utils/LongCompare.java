package com.norteksoft.mms.base.utils;

public class LongCompare implements Compare<Long>{

	public boolean compareGET(Long srcObj, Long distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.longValue() >= distObj.longValue();
	}

	public boolean compareGT(Long srcObj, Long distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.longValue() >= distObj.longValue();
	}

	public boolean compareLET(Long srcObj, Long distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.longValue() >= distObj.longValue();
	}

	public boolean compareLT(Long srcObj, Long distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.longValue() >= distObj.longValue();
	}

}
