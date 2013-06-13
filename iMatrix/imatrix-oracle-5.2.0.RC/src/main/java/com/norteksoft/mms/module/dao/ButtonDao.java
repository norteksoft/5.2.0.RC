package com.norteksoft.mms.module.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.mms.module.entity.Button;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class ButtonDao extends HibernateDao<Button, Long> {
	
	public void getButtonPages(Page<Button> page){
		this.findPage(page, "from Button btn where btn.companyId=? order by btn.type,btn.code", ContextUtils.getCompanyId());
	}
	public List<Button> getButtonsByCode(String code,Long pageId){
		return this.find("from Button btn where btn.companyId=? and btn.modulePage.id=? and btn.code=?",  ContextUtils.getCompanyId(),pageId,code);
	}
	
	public List<Button> getButtonsByToPageId(Long pageId){
		return this.find("from Button btn where btn.companyId=? and btn.toPage.id=? order by btn.displayOrder",  ContextUtils.getCompanyId(),pageId);
	}
	
	public List<Button> getButtonsByPageId(Long pageId){
		return this.find("from Button btn where btn.companyId=? and btn.modulePage.id=? order by btn.displayOrder",  ContextUtils.getCompanyId(),pageId);
	}
	public void getButtonsPageByPageId(Page<Button> buttonsPage ,Long pageId){
		this.findPage(buttonsPage ,"from Button btn where btn.companyId=? and btn.modulePage.id=? order by btn.displayOrder",  ContextUtils.getCompanyId(),pageId);
	}
	
	
	public List<Long> getButtonIdsByPageId(Long pageId){
		return this.find("select btn.id from Button btn where btn.companyId=? and btn.modulePage.id=?", ContextUtils.getCompanyId(),pageId);
	}
	
	public void deleteButtonByModulePage(Long pageId) {
		this.batchExecute("delete Button btn where btn.companyId=? and btn.modulePage.id=? or btn.toPage.id=?", ContextUtils.getCompanyId(),pageId,pageId);
	}
}
