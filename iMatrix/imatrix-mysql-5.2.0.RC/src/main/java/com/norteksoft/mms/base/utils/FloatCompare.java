package com.norteksoft.mms.base.utils;

public class FloatCompare implements Compare<Float>{
	public boolean compareGET(Float srcObj, Float distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.floatValue() >= distObj.floatValue();
	}

	public boolean compareGT(Float srcObj, Float distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.floatValue() > distObj.floatValue();
	}

	public boolean compareLET(Float srcObj, Float distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.floatValue() <= distObj.floatValue();
	}

	public boolean compareLT(Float srcObj, Float distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.floatValue() < distObj.floatValue();
	}
}
