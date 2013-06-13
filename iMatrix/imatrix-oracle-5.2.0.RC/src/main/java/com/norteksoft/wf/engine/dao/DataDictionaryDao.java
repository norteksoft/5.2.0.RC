package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.wf.engine.entity.DataDictionary;
import com.norteksoft.wf.engine.entity.DataDictionaryUser;

@Repository
public class DataDictionaryDao extends HibernateDao<DataDictionary, Long>{
	public void getDataDicts(Page<DataDictionary> dictPage,Long companyId){// order by dict.typeName
		if(StringUtils.isEmpty(dictPage.getOrderBy())){
			dictPage.setOrderBy("typeNo");
			dictPage.setOrder(Page.ASC);
		}
		searchPageByHql(dictPage, "from DataDictionary dict where dict.companyId = ? order by dict.typeNo,dict.displayIndex desc", companyId);
	}
	
	public void getDataDictsByTypeName(Page<DataDictionary> dictPage,Long companyId,String typeName){// order by dict.typeName
		searchPageByHql(dictPage, "from DataDictionary dict where dict.companyId = ? and dict.typeName like '%"+typeName+"%' order by dict.typeNo,dict.id desc", companyId);
	}
	
	public void getDataDictsByInfo(Page<DataDictionary> dictPage,Long companyId,String queryName){// order by dict.typeName
		searchPageByHql(dictPage, "from DataDictionary dict where dict.companyId = ? and dict.info like '%"+queryName+"%' order by dict.id desc", companyId);
	}
	
	public void getDataDictsByTypeNo(Page<DataDictionary> dictPage,Long companyId,String typeNo){// order by dict.typeName
		searchPageByHql(dictPage, "from DataDictionary dict where dict.companyId = ? and dict.typeNo like '%"+typeNo+"%' order by dict.id desc", companyId);
	}
	
	public void getDataDictsByTypeNoAndName(Page<DataDictionary> dictPage,Long companyId,String typeNo,String typeName){// order by dict.typeName
		searchPageByHql(dictPage, "from DataDictionary dict where dict.companyId = ? and dict.typeNo like '%"+typeNo+"%' and dict.typeName like '%"+typeName+"%' order by dict.id desc", companyId);
	}
	
	public void getDataDictsByInfoAndTypeName(Page<DataDictionary> dictPage,Long companyId,String typeName,String queryName){// order by dict.typeName
		searchPageByHql(dictPage, "from DataDictionary dict where dict.companyId = ? and dict.info like '%"+queryName+"%' and dict.typeName like '%"+typeName+"%' order by dict.id desc", companyId);
	}
	
	public void getDataDictsByInfoAndTypeNo(Page<DataDictionary> dictPage,Long companyId,String typeNo,String queryName){// order by dict.typeName
		searchPageByHql(dictPage, "from DataDictionary dict where dict.companyId = ? and dict.info like '%"+queryName+"%' and dict.typeNo like '%"+typeNo+"%' order by dict.id desc", companyId);
	}
	
	public void getDataDictsByInfoAndTypeNoAndName(Page<DataDictionary> dictPage,Long companyId,String typeNo,String typeName,String queryName){// order by dict.typeName
		searchPageByHql(dictPage, "from DataDictionary dict where dict.companyId = ? and dict.typeNo like '%"+typeNo+"%' and dict.typeName like '%"+typeName+"%' and dict.info like '%"+queryName+"%' order by dict.id desc", companyId);
	}
	
	public List<DataDictionary> getDataDictsByTypeId(Long typeId,Long companyId){
		return find("from DataDictionary dict where dict.typeId=? and dict.companyId = ?",typeId,companyId);
	}
	public List<DataDictionaryUser> getCandidate(String title){
		return find("select ddu from DataDictionary dict,DataDictionaryUser ddu where dict.id=ddu.dictId and dict.info=?",title);
	}
	public List<DataDictionary> getDataDicts(String loginName,Long companyId){
		return find("select dict from DataDictionary dict,DataDictionaryUser ddu where dict.id=ddu.dictId and ddu.loginName=? and ddu.companyId=? order by dict.displayIndex desc",loginName,companyId);
	}
	public DataDictionary getDataDictByTitle(String title){
		List<DataDictionary> dicts=find("from DataDictionary dict where dict.info=?",title);
		if(dicts.size()>0)return dicts.get(0);
		return null;
	}
	
}
