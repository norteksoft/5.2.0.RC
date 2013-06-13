package com.norteksoft.wf.engine.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.engine.dao.DocumentTemplateDao;
import com.norteksoft.wf.engine.dao.DocumentTemplateFileDao;
import com.norteksoft.wf.engine.entity.DocumentTemplate;
import com.norteksoft.wf.engine.entity.DocumentTemplateFile;
/**
 * 正文模板servic
 * @author wurong
 */
@Service
@Transactional
public class DocumentTemplateFileManager {
	private DocumentTemplateFileDao documentTemplateFileDao;
	@Autowired
	private DocumentTemplateDao documentTemplateDao;

	@Autowired
	public void setDocumentTemplateFileDao(
			DocumentTemplateFileDao documentTemplateFileDao) {
		this.documentTemplateFileDao = documentTemplateFileDao;
	}
	
	private Long getCompanyId(){
    	return ContextUtils.getCompanyId();
	}

	public DocumentTemplateFile getTemplate(Long id) {
		return documentTemplateFileDao.get(id);
	}
	public DocumentTemplate getDocumentTemplate(Long id) {
		return documentTemplateDao.get(id);
	}

	public void getTemplate(Page<DocumentTemplate> page) {
		documentTemplateDao.getTemplate(page,getCompanyId());
	}

	public void getTemplate(Page<DocumentTemplate> page, Long typeId) {
		documentTemplateDao.getTemplate(page,typeId,getCompanyId());
		
	}
	
	/**
	 * 删除模板
	 * @param id 模板id
	 */
	@Transactional(readOnly=false)
	public void deleteTemplate(Long id) {
		documentTemplateFileDao.delete(id);
	}

	/**
	 * 删除多个模板
	 * @param ids 模板ids
	 */
	@Transactional(readOnly=false)
	public void deleteTemplateByIds(List<Long> ids) {
		for(int i=0;i<ids.size();i++){
			documentTemplateFileDao.deleteTemplateFile(ids.get(i),ContextUtils.getCompanyId());
			documentTemplateDao.delete(ids.get(i));	
		}
	}
	
	
	
	/**
	 * 保存模板
	 * @param documentTemplateFile 模板文件
	 */
	@Transactional(readOnly=false)
	public void saveTemplate(DocumentTemplate documentTemplate) {
		documentTemplateDao.save(documentTemplate);
		
	}
	
	/**
	 * 查询正文模板列表(flex用)
	 * @param typeId 类型id
	 * @param companyId 公司id
	 * @param systemId 系统id
	 * @return 模板列表
	 */
	public List<DocumentTemplate> getTemplate(Long typeId,Long companyId){
		return documentTemplateDao.getTemplate(typeId, companyId);
	}
	
	/**
	 * 保存模板文件
	 * @param documentTemplateFile
	 */
	@Transactional(readOnly=false)
	public void saveTemplateFile(DocumentTemplateFile documentTemplateFile,DocumentTemplate documentTemplate) {
		documentTemplateDao.save(documentTemplate);
		documentTemplateFileDao.save(documentTemplateFile);
		
	}
	/**
	 * 根据模板ID获得模板文件
	 * @param templateId
	 * @return
	 */
	public DocumentTemplateFile getDocumentTemplateFile(Long templateId) {
		return documentTemplateFileDao.getDocumentTemplateFile(templateId,ContextUtils.getCompanyId());
	}
}
