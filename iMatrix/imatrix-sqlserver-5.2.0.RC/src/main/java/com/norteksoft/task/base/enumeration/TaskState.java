package com.norteksoft.task.base.enumeration;
/**
 * 任务状态
 * @author Administrator
 *
 */
public enum TaskState {
	/**
	 * 待办理
	 */
	WAIT_TRANSACT("task.state.waitTransact"),//待办理
	WAIT_DESIGNATE_TRANSACTOR("task.state.waitDesignateTransactor"),//等待设置办理人
	COMPLETED("task.state.completed"),//已完成
	CANCELLED("task.state.cancelled"),//已取消
	DRAW_WAIT("task.state.drawWait"),//待领取
	ASSIGNED("task.state.assigned"),//已指派
	WAIT_CHOICE_TACHE("task.state.waitChoiceTache"),//等待选择环节
	HAS_DRAW_OTHER("task.state.hasDrawOther");//他人已领取
	
	public String code;
	TaskState(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	public Integer getIndex(){
		return this.ordinal();
	}
}
