package com.norteksoft.wf.engine.entity;

import java.util.List;

import com.norteksoft.task.entity.WorkflowTask;


public class Temp {
	
	private String name;   //名称
	
	private int yesNum;    //同意数
	
	private int noNum;    //不同意数
	
	private int invaNum;
	
	private List<WorkflowTask> task;  //详细信息
	
	

	public Temp(String name, int yesNum, int noNum, List<WorkflowTask> task) {
		super();
		this.name = name;
		this.yesNum = yesNum;
		this.noNum = noNum;
		this.task = task;
	}

	public Temp(String name, int yesNum, int noNum, int invaNum,
			List<WorkflowTask> task) {
		super();
		this.name = name;
		this.yesNum = yesNum;
		this.noNum = noNum;
		this.invaNum = invaNum;
		this.task = task;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getYesNum() {
		return yesNum;
	}

	public void setYesNum(int yesNum) {
		this.yesNum = yesNum;
	}

	public int getNoNum() {
		return noNum;
	}

	public void setNoNum(int noNum) {
		this.noNum = noNum;
	}

	public int getInvaNum() {
		return invaNum;
	}

	public void setInvaNum(int invaNum) {
		this.invaNum = invaNum;
	}

	public List<WorkflowTask> getTask() {
		return task;
	}

	public void setTask(List<WorkflowTask> task) {
		this.task = task;
	}
	
	

}
