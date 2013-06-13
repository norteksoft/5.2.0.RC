package com.norteksoft.mms.base.utils;

import java.util.Date;

public class DataCompare implements Compare<Date>{

	public boolean compareGET(Date srcObj, Date distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.getTime()>=distObj.getTime();
	}

	public boolean compareGT(Date srcObj, Date distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.getTime()>distObj.getTime();
	}

	public boolean compareLET(Date srcObj, Date distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.getTime()<=distObj.getTime();
	}

	public boolean compareLT(Date srcObj, Date distObj) {
		if(srcObj == null || distObj == null) return false;
		return srcObj.getTime()<distObj.getTime();
	}

}
