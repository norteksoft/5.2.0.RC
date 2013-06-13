package com.norteksoft.bs.rank.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.bs.rank.entity.Subordinate;
import com.norteksoft.bs.rank.enumeration.SubordinateType;

@Repository
public class RankUserDao extends HibernateDao<Subordinate, Long> {
	public List<Subordinate> getDataDictRankUsersByRankId(Long dictRankId){
		return findNoCompanyCondition("from Subordinate ddr where ddr.dataDictionaryRank.id=?",dictRankId);
	}
	public Subordinate getRankUserByInfo(Long rankId,SubordinateType type,String loginName,Long infoId){
		String hql="";
		Object[] values=new Object[3];
		values[0]=rankId;
		values[1]=type;
		if(StringUtils.isNotEmpty(loginName)){
			hql="from Subordinate ddr where ddr.dataDictionaryRank.id=? and ddr.subordinateType=? and ddr.loginName=?";
			values[2]=loginName;
		}else if(infoId!=null){
			hql="from Subordinate ddr where ddr.dataDictionaryRank.id=? and ddr.subordinateType=? and ddr.targetId=?";
			values[2]=infoId;
			
		}
		if(StringUtils.isNotEmpty(hql)){
			List<Subordinate> rus=find(hql, values);
			if(rus.size()>0)return rus.get(0);
		}
		return null;
	}
}
