package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.engine.entity.DataDictionaryProcess;

@Repository
public class DataDictionaryProcessDao extends HibernateDao<DataDictionaryProcess, Long>{
	public List<DataDictionaryProcess> getAllDictProcessesByDictId(Long dictId){
		return find("from DataDictionaryProcess ddp where ddp.dataDictionary.id=?",dictId);
	}
	
	public DataDictionaryProcess getDataDictProcessById(Long id){
		return findUnique("from DataDictionaryProcess ddp where ddp.id = ? ", id);
	}
	
	public List<DataDictionaryProcess> getAllDictProcessesByDictId(Long dictId,Long companyId){
		return find("from DataDictionaryProcess ddp where ddp.dataDictionary.id=? and ddp.companyId=?",dictId,companyId);
	}
	
	public DataDictionaryProcess getDictProcessByDef(Long defId,Long dictId,String tachName){
		String hql="from DataDictionaryProcess ddp where ddp.dataDictionary.id=? and ddp.processDefinitionId=? ";
		Object[] values=new Object[2];
		values[0]=dictId;
		values[1]=defId;
		if(StringUtils.isNotEmpty(tachName)){
			hql="from DataDictionaryProcess ddp where ddp.dataDictionary.id=? and ddp.processDefinitionId=? and ddp.tacheName=? ";
			values=new Object[3];
			values[0]=dictId;
			values[1]=defId;
			values[2]=tachName;
		}
		List<DataDictionaryProcess> processes=find(hql,values);
		if(processes.size()>0)return processes.get(0);
		return null;
	}
}
