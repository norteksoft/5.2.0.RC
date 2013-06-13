package com.norteksoft.bs.options.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

/**
 * 导入定义
 * @author Administrator
 *
 */
@Repository
public class ImportDefinitionDao extends HibernateDao<ImportDefinition, Long> {

	/**
	 * 获得所有的导入定义
	 * @param page
	 */
	public void getImportDefinitionPage(Page<ImportDefinition> page) {
		this.searchPageByHql(page, "from ImportDefinition i where i.companyId=? ", ContextUtils.getCompanyId());
	}

	/**
	 * 根据编号获得导入定义
	 * @param code
	 * @return
	 */
	public ImportDefinition getImportDefinitionByCode(String code) {
		return this.findUnique("from ImportDefinition i where i.companyId=? and i.code=? ",ContextUtils.getCompanyId(),code);
	}

	/**
	 * 根据编码和ID获得编码相同且ID不同的导入定义
	 * @param code
	 * @param id
	 * @return
	 */
	public ImportDefinition getImportDefinitionByCode(String code, Long id) {
		return this.findUnique("from ImportDefinition i where i.companyId=? and i.code=? and i.id <> ?",ContextUtils.getCompanyId(),code,id);
	}

	/**
	 * 获得所有的导入定义
	 * @return
	 */
	public List<ImportDefinition> getAllImportDefinition() {
		return this.find("from ImportDefinition i where i.companyId=? ", ContextUtils.getCompanyId());
	}

}
