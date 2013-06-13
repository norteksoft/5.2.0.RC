package com.norteksoft.task.base.enumeration;

/**
 * 任务处理结果
 * 办理任务时执行的操作
 * @author wurong
 *
 */
public enum TaskProcessingResult {
	/**
	 * 同意
	 */
	APPROVE("approve", "transition.approval.result.agree"),
	/**
	 * 不同意
	 */
	REFUSE("refuse", "transition.approval.result.disagree"),
	/**
	 * 赞成
	 */
	AGREEMENT("agreement", "赞成"),
	/**
	 * 反对
	 */
	OPPOSE("oppose", "反对"),
	/**
	 * 弃权
	 */
	KIKEN("kiken", "弃权"),
	/**
	 * 签收
	 */
	SIGNOFF("signoff", "签收"),
	/**
	 * 提交
	 */
	SUBMIT("submit", "提交"),
	/**
	 * 交办
	 */
	ASSIGN("assign", "交办"),
	/**
	 * 分发
	 */
	DISTRIBUTE("distribute", "分发"),
	
	/**
	 * 已阅
	 */
	READED("readed","已阅"),
	/**
	 * 指派
	 */
	ASSIGN_TASK("assign_task", "指派");
    
    String key;
    String name;
    TaskProcessingResult(String key, String name){
        this.key = key;
        this.name = name;
    }

    /**
     * 该操作的key
     */
	@Override
    public String toString() {
        return this.key;
    }
	/**
	 * 该操作的名称
	 * @return 名称
	 */
	public String getName(){
		return name;
	}
	/**
	 * 该操作的名称
	 * @return 名称
	 */
	public String getKey(){
		return key;
	}
}
