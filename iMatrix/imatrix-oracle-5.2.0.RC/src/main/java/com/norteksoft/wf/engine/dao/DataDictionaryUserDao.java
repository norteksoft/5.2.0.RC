package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.base.enumeration.DataDictUserType;
import com.norteksoft.wf.engine.entity.DataDictionaryUser;

@Repository
public class DataDictionaryUserDao extends HibernateDao<DataDictionaryUser, Long>{

	public void deleteDdu(Long ddId, String loginName, Long companyId){
		createQuery("delete DataDictionaryUser ddu where ddu.dictId = ? and ddu.loginName = ? and ddu.companyId = ?", 
				ddId, loginName, companyId).executeUpdate();
	}
	
	public List<DataDictionaryUser> getDDUs(Long ddId, Long companyId){
		return find("from DataDictionaryUser ddu where ddu.dictId = ? and ddu.companyId = ?", ddId, companyId);
	}
	
	public DataDictionaryUser getDictUserByType(Long dictId,DataDictUserType type,String loginName,Long infoId){
		String hql=null;
		Object[] values=new Object[3];
		values[0]=dictId;
		values[1]=type;
		if(StringUtils.isNotEmpty(loginName)){
			hql="from DataDictionaryUser ddu where ddu.dictId = ? and (ddu.type=? and ddu.loginName=?) ";
			values[2]=loginName;
		}else if(infoId!=null){
			hql="from DataDictionaryUser ddu where ddu.dictId = ? and (ddu.type=? and ddu.infoId=?)";
			values[2]=infoId;
		}
		if(StringUtils.isNotEmpty(hql)){
			List<DataDictionaryUser> dictUsers=find(hql, values);
			if(dictUsers.size()>0)return dictUsers.get(0);
		}
		return null;
	}
	
}
