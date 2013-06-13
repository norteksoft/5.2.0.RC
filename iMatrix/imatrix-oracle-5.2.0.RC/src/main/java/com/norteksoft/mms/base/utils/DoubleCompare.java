package com.norteksoft.mms.base.utils;

public class DoubleCompare implements Compare<Double>{

	public boolean compareGET(Double srcObj, Double distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.doubleValue() >= distObj.doubleValue();
	}

	public boolean compareGT(Double srcObj, Double distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.doubleValue() > distObj.doubleValue();
	}

	public boolean compareLET(Double srcObj, Double distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.doubleValue() <= distObj.doubleValue();
	}

	public boolean compareLT(Double srcObj, Double distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.doubleValue() < distObj.doubleValue();
	}

}
