package com.norteksoft.bs.options.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.bs.options.enumeration.TimingType;
import com.norteksoft.product.orm.IdEntity;
/**
 * 定时器
 * @author Administrator
 *
 */
@Entity
@Table(name="BS_TIMER")
public class Timer extends IdEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Enumerated(EnumType.STRING)
	private TimingType timingType;//定时类型
	@Column(length=2000)
	private String dateTime;  //日期
	private String weekTime;  //星期
	private String corn; // 时间
	private String appointTime;//指定时间
	private String appointSet;//高级设置
	private Long jobId;//定时id
	
	@Transient
	private TimedTask jobInfo;

	public String getCorn() {
		return corn;
	}

	public void setCorn(String corn) {
		this.corn = corn;
	}

	public TimingType getTimingType() {
		return timingType;
	}

	public void setTimingType(TimingType timingType) {
		this.timingType = timingType;
	}

	public String getAppointTime() {
		return appointTime;
	}

	public void setAppointTime(String appointTime) {
		this.appointTime = appointTime;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getWeekTime() {
		return weekTime;
	}

	public void setWeekTime(String weekTime) {
		this.weekTime = weekTime;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getAppointSet() {
		return appointSet;
	}

	public void setAppointSet(String appointSet) {
		this.appointSet = appointSet;
	}

	public TimedTask getJobInfo() {
		return jobInfo;
	}

	public void setJobInfo(TimedTask jobInfo) {
		this.jobInfo = jobInfo;
	}
	
	@Override
	public String toString() {
		return "id:"+this.getId()+"；工作任务id："+this.jobId+"；定时类型："+this.timingType;
	}
}
