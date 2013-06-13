package com.norteksoft.bs.options.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.norteksoft.bs.options.entity.OptionGroup;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class OptionGroupDao extends HibernateDao<OptionGroup, Long> {
	
	/**
	 * 分页显示选项组
	 * @param groups
	 */
	public void getAllOptionGroupForPage(Page<OptionGroup> groups,Long systemId){
		String hql = "";
		if(systemId!=null){
			hql = "from OptionGroup og where og.companyId = ? and og.systemId=? order by og.createdTime desc";
			this.searchPageByHql(groups, hql.toString(),ContextUtils.getCompanyId(),systemId);
		} else{
			hql = "from OptionGroup og where og.companyId = ? order by og.createdTime desc";
			this.searchPageByHql(groups, hql.toString(),ContextUtils.getCompanyId());
		}
	}
	
	/**
	 * 检查用户组的唯一性
	 * @param name
	 * @return
	 */
	public OptionGroup CheckOptionGroupName(String name){
		String hql = "from OptionGroup og where og.companyId = ? and og.name = ?";
		return (OptionGroup)getSession().createQuery(hql).setLong(0, ContextUtils.getCompanyId()).setString(1, name).uniqueResult();
	}

	/**
	 * 用户组编码的唯一性
	 * @param groupNo
	 * @return
	 */
	public OptionGroup CheckOptionGroupNo(String groupNo){
		String hql = "from OptionGroup og where og.companyId = ? and og.code = ?";
		return (OptionGroup)getSession().createQuery(hql).setLong(0, ContextUtils.getCompanyId()).setString(1, groupNo).uniqueResult();
	}
	
	/**
	 * 查询所有的选项组
	 * @return
	 */
	public List<OptionGroup> getOptionGroups(){
		Assert.notNull(ContextUtils.getCompanyId(), "companyId不能为null");
		return find("from OptionGroup og where og.companyId=? ", 
				ContextUtils.getCompanyId());
	}
	
    /**
     * 根据选项组编号查询选项组
     * @param code
     * @return
     */
    public OptionGroup getOptionGroupByCode(String code){
    	Assert.notNull(code, "选项组编号code不能为null");
    	Assert.notNull(ContextUtils.getCompanyId(), "companyId不能为null");
    	List<OptionGroup> list = find("from OptionGroup og where og.companyId=? and og.code=?", 
				ContextUtils.getCompanyId(), code);
    	if(list.size() == 1){
    		return list.get(0);
    	}
    	return null;
    }

    /**
     * 根据选项组名称查询选项组
     * @param code
     * @return
     */
    public OptionGroup getOptionGroupByName(String name){
    	Assert.notNull(name, "选项组名称name不能为null");
    	Assert.notNull(ContextUtils.getCompanyId(), "companyId不能为null");
    	List<OptionGroup> list = find("from OptionGroup og where og.companyId=? and og.name=?", 
				ContextUtils.getCompanyId(),  name);
    	if(list.size() == 1){
    		return list.get(0);
    	}
    	return null;
    }
    
    /**
	 * 查询所有的选项组
	 * @return
	 */
	public List<OptionGroup> getOptionGroups(Long companyId,String systemIds){
		StringBuilder hql=new StringBuilder("from OptionGroup og where og.companyId=? ");
		if(StringUtils.isNotEmpty(systemIds)&&systemIds.charAt(systemIds.length()-1)==',')systemIds=systemIds.substring(0,systemIds.length()-1);
		Object[] values=new Object[1];
		if(StringUtils.isNotEmpty(systemIds)){
			hql.append(" and ");
			values=new Object[1+systemIds.split(",").length];
		}
		values[0]=companyId;
		if(StringUtils.isNotEmpty(systemIds)){
			String[] sysIds=systemIds.split(",");
			for(int i=0;i<sysIds.length;i++){
				if(StringUtils.isNotEmpty(sysIds[i])){
					if(i==0)hql.append("(");
					hql.append(" og.systemId=? ");
					if(i<sysIds.length-1){
						hql.append(" or ");
					}
					if(i==sysIds.length-1)hql.append(")");
					values[1+i]=Long.parseLong(sysIds[i]);
				}
			}
		}
		return this.find(hql.toString(), values);
	}
}
