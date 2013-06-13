package com.norteksoft.bs.options.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.bs.options.enumeration.ApplyType;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.IdEntity;
/**
 * 定时任务
 * @author Administrator
 *
 */
@Entity
@Table(name="BS_TIMED_TASK")
public class TimedTask extends IdEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long systemId;//系统ID
	private String systemCode;//系统code
	private String code;//定时编号
	private String url; // 运行URL
	private String description;//备注
	private String runAsUser; // 运行身份当前用户登陆名
	private String runAsUserName; // 运行身份当前用户名
	private Integer timeout = 30; // 单位(秒)
	private DataState dataState=DataState.DRAFT;//状态
	private ApplyType applyType;
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRunAsUser() {
		return runAsUser;
	}

	public void setRunAsUser(String runAsUser) {
		this.runAsUser = runAsUser;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public DataState getDataState() {
		return dataState;
	}

	public void setDataState(DataState dataState) {
		this.dataState = dataState;
	}

	public String getRunAsUserName() {
		return runAsUserName;
	}

	public void setRunAsUserName(String runAsUserName) {
		this.runAsUserName = runAsUserName;
	}
	
	@Override
	public String toString() {
		return "id:"+this.getId()+"；系统编码："+this.systemCode+"；定时任务地址："+this.url;
	}

	public ApplyType getApplyType() {
		return applyType;
	}

	public void setApplyType(ApplyType applyType) {
		this.applyType = applyType;
	}

}
