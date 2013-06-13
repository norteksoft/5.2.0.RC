package com.norteksoft.wf.base.enumeration;


/**
 * 委托整个生命周期中的状态
 * @author wurong
 *
 */
public enum TrustRecordState {
	/**
	 * 创建中(可以修改和删除)
	 */
	NEW_CREATING("delegate.main.states.new.creating"),
	/**
	 * 已启用(不可以修改和删除)
	 */
	STARTED("delegate.main.states.started"),
	/**
	 * 已生效(对应的委托已经到生效时间，所以已经生效)
	 */
	EFFICIENT("delegate.main.states.efficient"),
	/**
	 * 已取消(委托生效后还没有到截至时间，就提前取消了)
	 */
	CANCEL("delegate.main.states.cancel"),
	/**
	 * 已结束(委托已经到截至时间，正常结束了)
	 */
	END("delegate.main.states.end");
    
	private String code;
	
	TrustRecordState(String code){
		this.code = code;
	}

	public short getIndex(){
		return (short)(this.ordinal());
	}
	
	/**
	 * 返回该枚举值的名称的国际化资源key
	 * @return 国际化资源key
	 */
	public String getCode() {
		return code;
	}
	
	public static TrustRecordState valueOf(short ordinal){
		for(TrustRecordState ps:TrustRecordState.values()){
			if(ps.getIndex()==ordinal)return ps;
		}
		return NEW_CREATING;
	}
}
