package com.norteksoft.product.orm.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.product.util.ContextUtils;

public class AuditInterceptor extends EmptyInterceptor {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		if(entity instanceof IdEntity){
			for(int i=0; i<propertyNames.length; i++){
				if("creator".equals(propertyNames[i])){
					state[i] = ContextUtils.getLoginName();
				}else if("creatorName".equals(propertyNames[i])){
					state[i] = ContextUtils.getUserName();
				}else if("createdTime".equals(propertyNames[i])){
					state[i] = new Date();
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		if(entity instanceof IdEntity){
			for(int i=0; i<propertyNames.length; i++){
				if("modifier".equals(propertyNames[i])){
					currentState[i] = ContextUtils.getLoginName();
				}else if("modifierName".equals(propertyNames[i])){
					currentState[i] = ContextUtils.getUserName();
				}else if("modifiedTime".equals(propertyNames[i])){
					currentState[i] = new Date();
				}
			}
			return true;
		}
		return false;
	}
	
}
