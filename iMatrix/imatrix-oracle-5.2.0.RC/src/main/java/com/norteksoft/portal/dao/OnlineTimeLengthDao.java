package com.norteksoft.portal.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.portal.entity.OnlineTime;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
/**
 * 在线时长排行榜
 * @author Administrator
 *
 */
@Repository
public class OnlineTimeLengthDao extends HibernateDao<OnlineTime, Long>{
	
	public List<OnlineTime> getOnlineTimeLengthList(){
		return this.find("from OnlineTimeLength o where o.companyId=?",ContextUtils.getCompanyId());
	}
	public List<OnlineTime> getAllOnlineTimeLength(){
		return this.find("from OnlineTimeLength o");
	}
}
