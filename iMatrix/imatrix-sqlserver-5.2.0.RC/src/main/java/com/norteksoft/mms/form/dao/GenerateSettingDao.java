package com.norteksoft.mms.form.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.entity.GenerateSetting;
import com.norteksoft.product.orm.hibernate.HibernateDao;

@Repository
public class GenerateSettingDao extends HibernateDao<GenerateSetting, Long> {
	
	public GenerateSetting getGenerateSettingByTable(Long tableId){
		String hql = "from GenerateSetting dt where dt.tableId=?";
		List<GenerateSetting> settings = find(hql, tableId);
		if(settings.size()>0){
			return settings.get(0);
		}else{
			return null;
		}
	}
	
	public GenerateSetting getGenerateSetting(Long id){
		String hql = "from GenerateSetting dt where dt.id=?";
		List<GenerateSetting> settings = find(hql, id);
		if(settings.size()>0){
			return settings.get(0);
		}else{
			return null;
		}
	}
	
	
}
