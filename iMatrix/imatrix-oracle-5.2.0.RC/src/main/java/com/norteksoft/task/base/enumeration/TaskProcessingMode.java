package com.norteksoft.task.base.enumeration;

public enum TaskProcessingMode {
	/**
	 * 编辑式
	 */
	TYPE_EDIT("编辑式"),
	/**
	 * 审批式
	 */
	TYPE_APPROVAL("审批式"),
	/**
	 * 会签式
	 */
	TYPE_COUNTERSIGNATURE("会签式"),
	/**
	 * 签收式
	 */
	TYPE_SIGNOFF("签收式"),
	/**
	 * 投票式
	 */
	TYPE_VOTE("投票式"),
	/**
	 * 交办式
	 */
	TYPE_ASSIGN("交办式"),
	/**
	 * 分发式
	 */
	TYPE_DISTRIBUTE("分发"),
	/**
	 * 阅
	 */
	TYPE_READ("阅");
    
    String condition;
    TaskProcessingMode(String condition){
        this.condition = condition;
    }
    
    public static TaskProcessingMode getTaskModeFromStringToEnum(String processingMode){
		for(TaskProcessingMode mode:TaskProcessingMode.values()){
			if(mode.toString().equals(processingMode)) return mode;
		}
		return TYPE_EDIT;
	}
    
	public String getCondition() {
		return condition;
	}

	@Override
    public String toString() {
        return this.condition;
    }
}
