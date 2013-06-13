package com.norteksoft.acs.service.sale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.sale.PricePolicy;
import com.norteksoft.acs.entity.sale.Product;
import com.norteksoft.acs.entity.sale.SubsciberPricePolicy;
import com.norteksoft.product.orm.Page;

/**
 * 价格策略管理
 */
@Service
@Transactional
public class PricePolicyManager {

	private SimpleHibernateTemplate<PricePolicy, Long>  pricePolicyDao;
	private SimpleHibernateTemplate<SubsciberPricePolicy, Long> subsciberPricePolicyDao;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		pricePolicyDao = new SimpleHibernateTemplate<PricePolicy, Long>(sessionFactory, PricePolicy.class);
		subsciberPricePolicyDao = new SimpleHibernateTemplate<SubsciberPricePolicy, Long>(sessionFactory, SubsciberPricePolicy.class);
	}
	
	public void savePricePolicy(PricePolicy pricePolicy){
		pricePolicyDao.save(pricePolicy);
	}
	
	public void deletePricePolicy(Long id){
		pricePolicyDao.delete(id);
	}
	
	/**
	 * 根据系统ID查询价格策略
	 * @param page
	 * @param productId
	 */
	public void getPricePolicyBySystem(Page<PricePolicy> page, Long productId){
		String hql = "select pp from PricePolicy pp where pp.product.id = ?";
		pricePolicyDao.find(page, hql, productId);
	}

	public PricePolicy getPricePolicy(Long id) {
		return pricePolicyDao.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public Map<Product, List<PricePolicy>> getAllPricePolicy(){
		Map<Product, List<PricePolicy>> result = new HashMap<Product, List<PricePolicy>>();
		List<Object[]> pps = pricePolicyDao.find("select pp.product, pp from PricePolicy pp where pp.deleted = false order by pp.id");
		for(Object obj : pps){
			Object[] o = (Object[])obj;
			Product p = ((Product)o[0]);
			if(result.get(p) == null){
				List<PricePolicy> list = new ArrayList<PricePolicy>();
				list.add((PricePolicy)o[1]);
				result.put(p, list);
			}else{
				result.get(p).add((PricePolicy)o[1]);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getPricePolicysBySubsciber(Long subId){
		return subsciberPricePolicyDao.find("select spp.pricePolicyId from SubsciberPricePolicy spp where spp.subsciberId=?", subId);
	}
	
	/**
	 * 根据系统ID查询价格策略
	 * @param page
	 * @param productId
	 */
	public List<PricePolicy> getPricePolicyByProduct(Long productId){
		String hql = "select pp from PricePolicy pp where pp.product.id = ?";
		return pricePolicyDao.find( hql, productId);
	}
}
