package com.norteksoft.bs.rank.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.rank.dao.RankUserDao;
import com.norteksoft.bs.rank.entity.Subordinate;
import com.norteksoft.bs.rank.enumeration.SubordinateType;
import com.norteksoft.product.util.ContextUtils;

@Service
@Transactional
public class RankUserManager {
	private RankUserDao dataDictionaryRankuserDao;

	@Autowired
	public void setDataDictionaryRankuserDao(
			RankUserDao dataDictionaryRankuserDao) {
		this.dataDictionaryRankuserDao = dataDictionaryRankuserDao;
	}

	public Long getSystemId(){
    	return ContextUtils.getSystemId();
    }
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	public List<Subordinate> getDataDictRankUsersByRank(Long dictRankId){
		return dataDictionaryRankuserDao.getDataDictRankUsersByRankId(dictRankId);
	}
	public Subordinate getRankUserByInfo(Long rankId,SubordinateType type,String loginName,Long infoId){
		return dataDictionaryRankuserDao.getRankUserByInfo(rankId, type, loginName, infoId);
	}
	@Transactional(readOnly=false)
	public void saveRankUser(Subordinate rankUser){
		dataDictionaryRankuserDao.save(rankUser);
	}
	
}
