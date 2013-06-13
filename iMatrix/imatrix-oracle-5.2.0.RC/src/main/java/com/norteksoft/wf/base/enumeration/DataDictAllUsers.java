package com.norteksoft.wf.base.enumeration;

public enum DataDictAllUsers {
	/**
	 * 所有人员
	 */
	ALL_USERS("all_users", "所有人员");
	
	String key;
    String name;
    DataDictAllUsers(String key, String name){
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
	 * @return
	 */
	public String getName(){
		return name;
	}

}
