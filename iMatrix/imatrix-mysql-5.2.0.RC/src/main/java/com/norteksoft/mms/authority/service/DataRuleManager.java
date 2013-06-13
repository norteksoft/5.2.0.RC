package com.norteksoft.mms.authority.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.authority.dao.ConditionDao;
import com.norteksoft.mms.authority.dao.DataRuleDao;
import com.norteksoft.mms.authority.dao.PermissionDao;
import com.norteksoft.mms.authority.entity.Condition;
import com.norteksoft.mms.authority.entity.DataRule;
import com.norteksoft.mms.authority.entity.Permission;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.dao.TableColumnDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsonParser;

@Service
@Transactional
public class DataRuleManager {
	@Autowired
	private DataRuleDao dataRuleDao;
	@Autowired
	private ConditionDao conditionDao;
	@Autowired
	private DataTableDao dataTableDao;
	@Autowired
	private TableColumnDao tableColumnDao;
	@Autowired
	private PermissionDao permissionDao;

	/**
	 * 根据id获得数据规则
	 * @param id
	 * @return
	 */
	@Transactional(readOnly=true)
	public DataRule getDataRule(Long id) {
		return dataRuleDao.get(id);
	}

	/**
	 * 获得所有数据规则
	 * @param page
	 */
	@Transactional(readOnly=true)
	public void getDataRulePage(Page<DataRule> page) {
		dataRuleDao.getDataRulePage(page);
	}

	/**
	 * 保存数据规则
	 * @param dataRule
	 */
	public void saveDataRule(DataRule dataRule) {
		dataRuleDao.save(dataRule);
		List<Condition> conditions=new ArrayList<Condition>();
		List<Object> objects=JsonParser.getFormTableDatas(Condition.class);
		for(Object obj:objects){
			Condition condition=(Condition)obj;
			condition.setDataRule(dataRule);
			conditionDao.save(condition);
			conditions.add(condition);
		}
		if(conditions.size()>0){
			dataRule.setConditions(conditions);
		}
	}
	
	public List<DataRule> getDataRuleByDataTable(Long tableId){
		return dataRuleDao.getDataRuleByDataTable(tableId);
	}

	/**
	 * 删除数据规则且该规则下的所有条件
	 * @param ids
	 */
	public void deleteDataRule(String ids) {
		for(String id:ids.split(",")){
			List<Permission> list = permissionDao.getPermissionsByDataRule(Long.valueOf(id));
			for(Permission p:list){
				p.setDataRule(null);
				permissionDao.delete(p);
			}
			//permissionDao.deletePermissionByDataRuleId(Long.valueOf(id));
			conditionDao.deleteConditionByRuleId(Long.valueOf(id));
			dataRuleDao.delete(Long.valueOf(id));
		}
	}

	/**
	 * 根据编号获得规则
	 * @param code
	 * @return
	 */
	@Transactional(readOnly=true)
	public DataRule getDataRuleByCode(String code) {
		return dataRuleDao.getDataRuleByCode(code);
	}

	/**
	 * 根据编号和ID获得编号相同且ID不同的规则
	 * @param code
	 * @param id
	 * @return
	 */
	@Transactional(readOnly=true)
	public DataRule getDataRuleByCode(String code, Long id) {
		return dataRuleDao.getDataRuleByCode(code,id);
	}

	/**
	 * 获得所有启用的数据表
	 * @return
	 */
	public void findAllEnabledDataTable(Page<DataTable> page) {
		dataTableDao.findAllEnabledDataTable(page);
	}

	/**
	 * 根据数据表id获得字段
	 * @param tableColumnPage
	 * @param tableId
	 */
	public void getTableColumnByDataTableId(Page<TableColumn> tableColumnPage,Long dataTableId) {
		tableColumnDao.getTableColumnByDataTableId(tableColumnPage, dataTableId);
	}
	
	/**
	 * 根据规则类型查询数据规则
	 * @param ruleTypeId
	 * @return
	 */
	public List<DataRule> getDataRulesByRuleType(Long ruleTypeId){
		return dataRuleDao.getDataRulesByRuleType(ruleTypeId);
	}

	/**
	 * 根据规则类型查询数据规则
	 * @param page
	 * @param ruleTypeId
	 */
	public void getDataRulesByRuleType(Page<DataRule> page, Long ruleTypeId) {
		dataRuleDao.getDataRulesByRuleType(page,ruleTypeId);
	}

	/**
	 * 验证删除
	 * @param ids
	 * @return
	 */
	public String validateDelete(String ids) {
		String result="";
		for(String id:ids.split(",")){
			List<Permission> permissions=permissionDao.getPermissionsByDataRule(Long.valueOf(id));
			if(permissions != null && permissions.size()>0){
				DataRule dataRule=dataRuleDao.get(Long.valueOf(id));
				if(StringUtils.isNotEmpty(result))
					result+="、";
				result+=dataRule.getName();
			}
		}
		if(StringUtils.isNotEmpty(result))
			result="名称为："+result+" 的数据规则中有数据授权，确定删除吗？";
		return result;
	}
}
