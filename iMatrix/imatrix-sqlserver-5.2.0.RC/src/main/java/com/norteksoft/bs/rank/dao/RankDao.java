package com.norteksoft.bs.rank.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.bs.rank.enumeration.SubordinateType;
import com.norteksoft.bs.rank.entity.Superior;

@Repository
public class RankDao extends HibernateDao<Superior, Long> {
	
	public void getDataDictRanksPage(Page<Superior> dictRankPage, Long companyId){
		searchPageByHql(dictRankPage, "from Superior dict where dict.companyId=? order by dict.name asc",companyId);
	}
	
	public List<Superior> getDirectLeader(Long userId, Long companyId){
		return find("select dd from Superior dd join dd.dataDictionaryRankUser du where du.subordinateType=? and du.targetId=? and du.companyId=? ", 
				SubordinateType.USER, userId, companyId);
	}
	
	public List<Superior> getDirectLeader(String loginName, Long companyId){
		return find("select dd from Superior dd join dd.dataDictionaryRankUser du where du.subordinateType=? and du.loginName=? and du.companyId=? ", 
				SubordinateType.USER, loginName, companyId);
	}
	
	public List<Superior> getDirectLeader(List<Department> depts, Long companyId){
		StringBuilder hql = new StringBuilder("select dd from Superior dd join dd.dataDictionaryRankUser du ");
		hql.append("where du.subordinateType=? and du.companyId=? and (");
		Object[] paras = new Object[depts.size()+2];
		paras[0] = SubordinateType.DEPARTMENT;
		paras[1] = companyId;
		for(int i = 0; i < depts.size(); i++){
			if(i != 0) hql.append(" or ");
			hql.append("du.targetId=?");
			paras[i + 2] = depts.get(i).getId();
		}
		hql.append(")");
		return find(hql.toString(), paras);
	}
	public List<Superior> getDataDictRanks(Long companyId,String value){
		StringBuilder hql = new StringBuilder("select dd from Superior dd where dd.companyId=?");
		if(!StringUtils.isEmpty(value)){
			hql.append(" and dd.name like '%");
			hql.append(value);
			hql.append("%'");
		}
		return find(hql.toString(), companyId);
	}
	
	public List<Superior> getDataDictRanksByDictId(Long dictId){
		return find("select dd from Superior dd where dd.dataDictionary.id=?", 
				dictId);
	}
	
	public List<Superior> getRanks(Long companyId){
		return find("from Superior dd where dd.companyId=?", 
				companyId);
	}
	
	public Superior getRankByTitle(String rankTitle){
		List<Superior> ranks=find("from Superior dd where dd.title=?", 
				rankTitle);
		if(ranks.size()>0)return ranks.get(0);
		return null;
	}
	
}
