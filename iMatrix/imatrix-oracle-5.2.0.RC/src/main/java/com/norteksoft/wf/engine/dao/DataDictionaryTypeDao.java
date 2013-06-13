package com.norteksoft.wf.engine.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.entity.DataDictionaryType;

@Repository
public class DataDictionaryTypeDao extends HibernateDao<DataDictionaryType, Long>{
	
	public void getDataDictTypesPage(Page<DataDictionaryType> dictPage, Long companyId){
		if(StringUtils.isEmpty(dictPage.getOrderBy())){
			dictPage.setOrderBy("no");
			dictPage.setOrder(Page.ASC);
		}
		searchPageByHql(dictPage, "from DataDictionaryType dict where dict.companyId=?",companyId);
	}
	
	public List<DataDictionaryType> getAllDictTypes(Long companyId){
		return find("from DataDictionaryType dict where dict.companyId=?order by dict.no",companyId);
	}
	
	public List<DataDictionaryType> getAllDictTypes(Long companyId,Long typeId){
		return find("from DataDictionaryType dict where dict.companyId=? and dict.id<>? order by dict.no",companyId,typeId);
	}
	
	public DataDictionaryType getDictTypeById(Long id){
		return findUnique("from DataDictionaryType dict where dict.id=?",id);
	}
	
	public DataDictionaryType getDictTypeByNo(String no,Long companyId){
		return findUnique("from DataDictionaryType dict where dict.no=? and dict.companyId=?",no,companyId);
	}
	/**
	 * 根据类型id集合获得类型编码的集合
	 * @param typeIds
	 * @param companyId
	 * @return
	 */
	public List<String> getDictTypeCodesByIds(String typeIds,Long companyId){
		StringBuilder hql=new StringBuilder("select t.no from DataDictionaryType t where t.companyId=? ");
		Object[] values=new Object[1];
		values[0]=companyId;
		if(StringUtils.isNotEmpty(typeIds)){
			String[] ids=typeIds.split(",");
			values=new Object[1+ids.length];
			if(ids.length>0){
				hql.append("and (");
			}
			for(int i=0;i<ids.length;i++){
				hql.append("t.id=? ");
				if(i<ids.length-1){
					hql.append(" or ");
				}
				if(i==ids.length-1)hql.append(")");
				values[1+i]=Long.parseLong(StringUtils.trim(ids[i]));
			}
		}
		return find(hql.toString(), values);
	}
	/**
	 * 根据类型编码集合获得类型id的集合
	 * @param typeIds
	 * @param companyId
	 * @return
	 */
	public List<String> getDictTypeIdsByCodes(String typeNos){
		StringBuilder hql=new StringBuilder("select t.id from DataDictionaryType t where t.companyId=? ");
		Object[] values=new Object[1];
		values[0]=ContextUtils.getCompanyId();
		if(StringUtils.isNotEmpty(typeNos)){
			String[] ids=typeNos.split(",");
			values=new Object[1+ids.length];
			if(ids.length>0){
				hql.append("and (");
			}
			for(int i=0;i<ids.length;i++){
				hql.append("t.id=? ");
				if(i<ids.length-1){
					hql.append(" or ");
				}
				if(i==ids.length-1)hql.append(")");
				values[1+i]=Long.parseLong(ids[i]);
			}
		}
		return find(hql.toString(), values);
	}
	
}
