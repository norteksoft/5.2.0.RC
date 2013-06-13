package com.norteksoft.bs.options.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class OptionDao extends HibernateDao<Option, Long> {
	
	/**
     * 根据选项组ID查询选项
     * @param optionGroupId
     * @return
     */
	public List<Option> getOptionsByGroup(Long optionGroupId){
		  Assert.notNull(optionGroupId, "选项组Id不能为null");
		  Assert.notNull(ContextUtils.getCompanyId(), "companyId不能为null");
		return find("from Option o where o.companyId=? and  o.optionGroup.id=? order by o.optionIndex", 
				ContextUtils.getCompanyId(), optionGroupId);
	}
    
    /**
     * 根据选项组编号查询选项
     * @param code
     * @return
     */
    public List<Option> getOptionsByGroupCode(String code){
    	 Assert.notNull(code, "选项组code不能为null");
		 Assert.notNull(ContextUtils.getCompanyId(), "companyId不能为null");
    	return find("from Option o where o.companyId=? and o.optionGroup.code=? order by o.optionIndex", 
    			ContextUtils.getCompanyId(), code);
    }
    
    /**
     * 根据选项组名称查询选项
     * @param code
     * @return
     */
    public List<Option> getOptionsByGroupName(String name){
    	Assert.notNull(name, "选项组名name不能为null");
		Assert.notNull(ContextUtils.getCompanyId(), "companyId不能为null");
    	return find("from Option o where o.companyId=? and o.optionGroup.name=? order by o.optionIndex", 
    			ContextUtils.getCompanyId(), name);
    }
    /**
     * 根据选项组ID查询选项
     * @param optionGroupId
     * @return
     */
	public List<Option> getOptionsByGroup(Long optionGroupId,Long companyId){
		return find("from Option o where o.companyId=? and o.optionGroup.id=? order by o.optionIndex", 
				companyId,  optionGroupId);
	}
	/**
	 * 根据选项值和标题获得选项
	 * @param value
	 * @param name
	 * @param groupCode
	 * @return
	 */
	 public Option getOptionByInfo(String value,String name,String groupCode){
		 String hql="from Option o where o.optionGroup.code=? and o.name=? and o.value=? order by o.optionIndex";
		 List<Option> options=find(hql,groupCode,name,value);
		 if(options.size()>0)return options.get(0);
		 return null;
	 }
	 
	 public Option getOptionById(Long optionId){
		 String hql="from Option o where o.id=? order by o.optionIndex";
		 List<Option> options=find(hql,optionId);
		 if(options.size()>0)return options.get(0);
		 return null;
	 }
}
