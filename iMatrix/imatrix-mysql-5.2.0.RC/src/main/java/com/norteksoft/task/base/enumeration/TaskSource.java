package com.norteksoft.task.base.enumeration;
/**
 * 任务状态
 * @author Administrator
 *
 */
public enum TaskSource {
	NORMAL("task.source.normal"),//正常，有上一环节正常生成的任务
	ADD_SIGN("task.source.addSign"),//加签生成的任务
	ADD_TRANSACTOR("task.source.addTransactor"),//增加办理人生成的任务
	CHANGE_TRANSACTOR("task.source.changeTransactor"),//更改办理人生成的任务
	ASSIGN("task.source.assign"),//指派生成的任务
	TASK_RETURN("task.source.taskReturn");//退回生成的任务
	
	public String code;
	TaskSource(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	public Integer getIndex(){
		return this.ordinal();
	}
}
