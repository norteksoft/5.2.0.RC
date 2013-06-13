package com.norteksoft.acs.service.sale;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.sale.Product;
import com.norteksoft.product.orm.Page;

/**
 * 产品管理
 */
@Service
@Transactional
public class ProductManager{
	private SimpleHibernateTemplate<Product, Long> productDao;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		productDao = new SimpleHibernateTemplate<Product, Long>(sessionFactory, Product.class);
	}
 	
	public void saveProduct(Product product){
		//保存产品、保存销售包及他们之间的关系，还有销售包和功能之间的关系
		productDao.save(product);
	}
	
	public void deleteProduct(Long id){
		Product product = productDao.get(id);
		productDao.delete(product);
	}
	
	public List<Product> getAllProduct(){
		return productDao.find("from Product p where p.deleted = false");
	}
	
	public Page<Product> getAllProduct(Page<Product> page){
		return productDao.findAll(page);
	}
	
	public Product getProduct(Long id){
		return productDao.get(id);
	}
	
	public Product getProduct(String productName,String version,Long systemId){
		List<Product> products=productDao.find("from Product p where p.deleted = false and p.productName=? and p.version=? and p.systemId=?",productName,version,systemId);
		if(products.size()>0)return products.get(0);
		return null;
	}
	public List<Product> getProductBySystem(Long systemId){
		return productDao.find("from Product p where p.deleted = false and p.systemId=?",systemId);
	}
}
