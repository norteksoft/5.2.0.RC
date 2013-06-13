package com.norteksoft.portal.base.enumeration;
/**
 * 消息类型
 * @author qiao
 *
 */
public enum MessageType {
	
	SYSTEM_MESSAGE("system.message"),//系统消息
	ONLINE_MESSAGE("online.message");//在线消息
	
	String code;
	MessageType(String code){
        this.code = code;
    }
	public String getCode(){
		return this.code;
	}

    @Override
    public String toString() {
        return this.code;
    }
}
