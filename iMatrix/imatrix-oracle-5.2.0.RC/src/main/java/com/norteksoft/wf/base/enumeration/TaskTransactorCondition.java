package com.norteksoft.wf.base.enumeration;

public enum TaskTransactorCondition {
	
	USER_CONDITION("user-condition"),
	ONLY_IN_CREATOR_DEPARTMENT("only-in-creator-department"),
    WITH_CREATOR_DEPARTMENT("with-creator-department"),
    SELECT_ONE_FROM_MULTIPLE("select-one-from-multiple"),
    SELECT_TYPE("select-type"),
    SELECT_BEAN("select-bean")
    ;
	/**
	 * 程序自动选择
	 */
	public static final String SELECT_TYPE_AUTO = "autoType";
	/**
	 * 由办理用户选择
	 */
	public static final String SELECT_TYPE_CUSTOM = "customType";
    String condition;
    TaskTransactorCondition(String condition){
        this.condition = condition;
    }

	@Override
    public String toString() {
        return this.condition;
    }
}
