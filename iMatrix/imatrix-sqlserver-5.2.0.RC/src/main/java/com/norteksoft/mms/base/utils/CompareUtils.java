package com.norteksoft.mms.base.utils;

import java.util.Date;

import com.norteksoft.mms.form.enumeration.DataType;

public class CompareUtils {
	
	public static boolean compareGT(DataType dt, Object src, Object dest){
		switch (dt) {
		case DATE: return new DataCompare().compareGT((Date)src, (Date)dest);
		case TIME: return new DataCompare().compareGT((Date)src, (Date)dest);
		case INTEGER: return new IntegerCompare().compareGT((Integer)src, (Integer)dest);
		case LONG: return new LongCompare().compareGT((Long)src, (Long)dest);
		case DOUBLE: return new DoubleCompare().compareGT((Double)src, (Double)dest);
		case FLOAT: return new FloatCompare().compareGT((Float)src, (Float)dest);
		}
		return false;
	}
	
	public static boolean compareGET(DataType dt, Object src, Object dest){
		switch (dt) {
		case DATE: return new DataCompare().compareGET((Date)src, (Date)dest);
		case TIME: return new DataCompare().compareGET((Date)src, (Date)dest);
		case INTEGER: return new IntegerCompare().compareGET((Integer)src, (Integer)dest);
		case LONG: return new LongCompare().compareGET((Long)src, (Long)dest);
		case DOUBLE: return new DoubleCompare().compareGET((Double)src, (Double)dest);
		case FLOAT: return new FloatCompare().compareGT((Float)src, (Float)dest);
		}
		return false;
	}
	
	public static boolean compareLT(DataType dt, Object src, Object dest){
		switch (dt) {
		case DATE: return new DataCompare().compareLT((Date)src, (Date)dest);
		case TIME: return new DataCompare().compareLT((Date)src, (Date)dest);
		case INTEGER: return new IntegerCompare().compareLT((Integer)src, (Integer)dest);
		case LONG: return new LongCompare().compareLT((Long)src, (Long)dest);
		case DOUBLE: return new DoubleCompare().compareLT((Double)src, (Double)dest);
		case FLOAT: return new FloatCompare().compareGT((Float)src, (Float)dest);
		}
		return false;
	}
	
	public static boolean compareLET(DataType dt, Object src, Object dest){
		switch (dt) {
		case DATE: return new DataCompare().compareLET((Date)src, (Date)dest);
		case TIME: return new DataCompare().compareLET((Date)src, (Date)dest);
		case INTEGER: return new IntegerCompare().compareLET((Integer)src, (Integer)dest);
		case LONG: return new LongCompare().compareLET((Long)src, (Long)dest);
		case DOUBLE: return new DoubleCompare().compareLET((Double)src, (Double)dest);
		case FLOAT: return new FloatCompare().compareGT((Float)src, (Float)dest);
		}
		return false;
	}
	
}
