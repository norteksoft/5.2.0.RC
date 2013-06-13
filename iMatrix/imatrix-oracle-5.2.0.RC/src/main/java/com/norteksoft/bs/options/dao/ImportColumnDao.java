package com.norteksoft.bs.options.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.bs.options.entity.ImportColumn;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

/**
 * 导入列
 * @author Administrator
 *
 */
@Repository
public class ImportColumnDao extends HibernateDao<ImportColumn, Long> {

	/**
	 * 根据导入定义的id获得导入列
	 * @param importId
	 * @return
	 */
	public List<ImportColumn> getImportColumnByImportId(Long importId) {
		return this.find("from ImportColumn ic where ic.companyId=? and ic.importDefinition.id=? ",ContextUtils.getCompanyId(),importId);
	}

	/**
	 *  根据导入定义的id和字段名获得导入列
	 * @param id
	 * @param columnName
	 * @return
	 */
	public ImportColumn getImportColumn(Long importId, String columnName) {
		return this.findUnique("from ImportColumn ic where ic.companyId=? and ic.importDefinition.id=? and ic.name=? ",ContextUtils.getCompanyId(),importId,columnName);
	}

	/**
	 * 根据导入定义的id获得导入列中固定长度的总和
	 * @param importDefinitionId
	 * @return
	 */
	public List<Integer> getColumnWidth(Long importDefinitionId) {
		return this.find("select ic.width from ImportColumn ic where ic.companyId=? and ic.importDefinition.id=?", ContextUtils.getCompanyId(),importDefinitionId);
	}

}
