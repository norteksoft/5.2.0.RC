package com.norteksoft.mms.base.utils.view;

import java.util.List;

/**
 * 导出数据
 * @author Administrator
 *
 */
public class ExportData {
	private List<Object> head;//表头数据
	private List<List<Object>> bodyData;//表体数据
	private String[] format;//列的格式设置
	private String[] dataType;//列的数据类型
	private String[] valueSet;//列的值设置
	
	public List<Object> getHead() {
		return head;
	}
	public void setHead(List<Object> head) {
		this.head = head;
	}
	public List<List<Object>> getBodyData() {
		return bodyData;
	}
	public void setBodyData(List<List<Object>> bodyData) {
		this.bodyData = bodyData;
	}
	public String[] getFormat() {
		return format;
	}
	public void setFormat(String[] format) {
		this.format = format;
	}
	public String[] getDataType() {
		return dataType;
	}
	public void setDataType(String[] dataType) {
		this.dataType = dataType;
	}
	public String[] getValueSet() {
		return valueSet;
	}
	public void setValueSet(String[] valueSet) {
		this.valueSet = valueSet;
	}
}
