package com.norteksoft.product.orm.hibernate;

import java.util.Date;

import org.hibernate.event.SaveOrUpdateEvent;
import org.hibernate.event.def.DefaultSaveOrUpdateEventListener;
import org.springframework.stereotype.Service;

import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.product.util.ContextUtils;

import flex.messaging.util.StringUtils;

@Service
public class SaveOrUpdateListener extends DefaultSaveOrUpdateEventListener {

	private static final long serialVersionUID = 3175703536757344524L;

	@Override
	public void onSaveOrUpdate(SaveOrUpdateEvent event) {
		Object obj = event.getObject();
		if(obj instanceof IdEntity){
			IdEntity entity = (IdEntity) obj;
			if(entity.getId() == null){
				setEntityCreatorInfo(entity);
			}else{
				setEntityModifierInfo(entity);
			}
		}
		super.onSaveOrUpdate(event);
	}

	private void setEntityCreatorInfo(IdEntity entity){
		if(entity.getCompanyId()==null){
			entity.setCompanyId(ContextUtils.getCompanyId());
		}
		if(StringUtils.isEmpty(entity.getCreator())){
			entity.setCreator(ContextUtils.getLoginName());
		}
		if(StringUtils.isEmpty(entity.getCreatorName())){
			entity.setCreatorName(ContextUtils.getUserName());
		}
		entity.setCreatedTime(new Date());
	}
	
	private void setEntityModifierInfo(IdEntity entity){
		entity.setModifier(ContextUtils.getLoginName());
		entity.setModifierName(ContextUtils.getUserName());
		entity.setModifiedTime(new Date());
	}
}
