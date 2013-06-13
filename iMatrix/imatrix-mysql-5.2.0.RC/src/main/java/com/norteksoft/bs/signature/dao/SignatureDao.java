package com.norteksoft.bs.signature.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.norteksoft.bs.signature.entity.Signature;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;


@Repository
public class SignatureDao extends HibernateDao<Signature,Long>{
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	
	/**
	 * 获得签章实体
	 * @param page
	 */
	public Signature getSignatureById(Long id){
		List<Signature> list = this.find("from Signature s where s.id=? and s.companyId=?", id,getCompanyId());
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 获得所有签章
	 * @param page
	 */
	public void getAllSignaturePicture(Page<Signature> page){
		this.searchPageByHql(page, "from Signature s where s.companyId=?", getCompanyId());
	}
	
	/**
	 * 获得所有签章除当前signatureId
	 * @param page
	 */
	public List<Signature> getAllSignatureNoCurrentId(Long signatureId){
		if(signatureId==null){
			return this.find("from Signature s where s.companyId=? ", getCompanyId());
		}else{
			return this.find("from Signature s where s.companyId=? and s.id<>? ", getCompanyId(),signatureId);
		}
	}
	
	public Signature getSignByUserName(String userName) {
		List<Signature> list = this.find("from Signature s where s.userName=? and s.companyId=?", userName,getCompanyId());
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	public Signature getSignByUserId(Long userId) {
		List<Signature> list = this.find("from Signature s where s.userId=? and s.companyId=?", userId,getCompanyId());
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
}
