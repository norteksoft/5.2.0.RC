package com.norteksoft.mms.authority.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.mms.authority.dao.ConditionDao;
import com.norteksoft.mms.authority.dao.DataRuleDao;
import com.norteksoft.mms.authority.dao.PermissionDao;
import com.norteksoft.mms.authority.dao.PermissionItemDao;
import com.norteksoft.mms.authority.dao.RuleTypeDao;
import com.norteksoft.mms.authority.entity.Condition;
import com.norteksoft.mms.authority.entity.DataRule;
import com.norteksoft.mms.authority.entity.Permission;
import com.norteksoft.mms.authority.entity.PermissionItem;
import com.norteksoft.mms.authority.entity.RuleType;
import com.norteksoft.mms.authority.enumeration.FieldOperator;
import com.norteksoft.mms.authority.enumeration.ItemType;
import com.norteksoft.mms.authority.enumeration.UserOperator;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.base.data.DataSheetConfig;
import com.norteksoft.mms.base.data.DataTransfer;
import com.norteksoft.mms.base.data.FileConfigModel;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.wf.base.enumeration.LogicOperator;

@Service
@Transactional
public class importAuthorityManager implements DataTransfer {
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private DataHandle dataHandle;
	@Autowired
	private RuleTypeDao ruleTypeDao;
	@Autowired
	private DataRuleDao dataRuleDao;
	@Autowired
	private DataTableDao dataTableDao;
	@Autowired
	private ConditionDao conditionDao;
	@Autowired
	private PermissionDao permissionDao;
	@Autowired
	private PermissionItemDao permissionItemDao;
	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private RoleManager roleManager;
	
	public void backup(String systemIds, Long companyId,FileConfigModel fileConfig) {
		try {
			ThreadParameters parameters=new ThreadParameters(companyId, null);
			ParameterUtils.setParameters(parameters);
			String path=fileConfig.getExportRootPath()+"/"+fileConfig.getExportPath()+"/";
			File file = new File(path+fileConfig.getFilename()+".xls");
			OutputStream out=null;
			out=new FileOutputStream(file);
			if("MMS_RULE_TYPE".equals(fileConfig.getData())){
				exportRuleType(out);
			}else if("MMS_DATA_RULE".equals(fileConfig.getData())){
				exportDataRule(out);
			}else if("MMS_PERMISSION".equals(fileConfig.getData())){
				exportPermission(out);
			}
			
		}catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}
	}

	private void exportPermission(OutputStream fileOut) {
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_PERMISSION']");
		List<DataSheetConfig> conditionConfs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_PERMISSION_ITEM']");
		wb = new HSSFWorkbook();
		
		HSSFSheet sheet=wb.createSheet("MMS_PERMISSION");
        HSSFRow row = sheet.createRow(0);
        dataHandle.getFileHead(wb,row,confs);
        
        HSSFSheet conditionSheet=wb.createSheet("MMS_PERMISSION_ITEM");
        HSSFRow conditionRow = conditionSheet.createRow(0);
        dataHandle.getFileHead(wb,conditionRow,conditionConfs);
        List<Permission> permissions=permissionDao.getAllPermissions();
        for(Permission permission:permissions){
        	permissionInfo(permission,sheet,conditionSheet,confs,conditionConfs);
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}

	private void permissionInfo(Permission permission, HSSFSheet sheet,HSSFSheet conditionSheet, List<DataSheetConfig> confs,List<DataSheetConfig> conditionConfs) {
		if(permission != null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					if("dataRuleCode".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(permission.getDataRule().getCode());
					}else{
						dataHandle.setFieldValue(conf,i,rowi,permission);
					}
				}
			}
			permissionItemInfo(permission,conditionSheet,conditionConfs);
		}
	}

	private void permissionItemInfo(Permission permission,HSSFSheet conditionSheet, List<DataSheetConfig> conditionConfs) {
		List<PermissionItem> permissionItems=permissionItemDao.getAllPermissionItems(permission.getId());
		for(PermissionItem permissionItem:permissionItems){
			HSSFRow rowi = conditionSheet.createRow(conditionSheet.getLastRowNum()+1);
			for(int i=0;i<conditionConfs.size();i++){
				DataSheetConfig conf=conditionConfs.get(i);
				if(!conf.isIgnore()){
					if("conditionValue".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						if(ItemType.USER.equals(permissionItem.getItemType())){
							cell.setCellValue(permissionItem.getConditionValue());
						}else if(ItemType.DEPARTMENT.equals(permissionItem.getItemType())){
							Department department=ApiFactory.getAcsService().getDepartmentById(Long.valueOf(permissionItem.getConditionValue()));
							cell.setCellValue(department.getCode());
						}else if(ItemType.ROLE.equals(permissionItem.getItemType())){
							cell.setCellValue(permissionItem.getConditionValue());
						}else if(ItemType.WORKGROUP.equals(permissionItem.getItemType())){
							Workgroup workgroup=ApiFactory.getAcsService().getWorkgroupById(Long.valueOf(permissionItem.getConditionValue()));
							cell.setCellValue(workgroup.getCode());
						}
					}else if("priority".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(permissionItem.getPermission().getPriority().toString());
					}else if("authority".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(permissionItem.getPermission().getAuthority().toString());
					}else if("dataRuleCode".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(permissionItem.getPermission().getDataRule().getCode());
					}else{
						dataHandle.setFieldValue(conf,i,rowi,permissionItem);
					}
				}
			}
		}
		
	}

	private void exportDataRule(OutputStream fileOut) {
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_DATA_RULE']");
		List<DataSheetConfig> conditionConfs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_CONDITION']");
		wb = new HSSFWorkbook();
		
		HSSFSheet sheet=wb.createSheet("MMS_DATA_RULE");
        HSSFRow row = sheet.createRow(0);
        dataHandle.getFileHead(wb,row,confs);
        
        HSSFSheet conditionSheet=wb.createSheet("MMS_CONDITION");
        HSSFRow conditionRow = conditionSheet.createRow(0);
        dataHandle.getFileHead(wb,conditionRow,conditionConfs);
        List<DataRule> dataRules=dataRuleDao.getAllDataRule();
        for(DataRule dataRule:dataRules){
        	dataRuleInfo(dataRule,sheet,conditionSheet,confs,conditionConfs);
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}

	private void dataRuleInfo(DataRule dataRule, HSSFSheet sheet,HSSFSheet conditionSheet, List<DataSheetConfig> confs,List<DataSheetConfig> conditionConfs) {
		if(dataRule != null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					if("dataTableName".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						DataTable dataTable=dataTableDao.get(dataRule.getDataTableId());
						if(dataTable!=null){
							cell.setCellValue(dataTable.getName());
						}else{
							cell.setCellValue("");
						}
					}else if("ruleTypeCode".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						RuleType ruleType=ruleTypeDao.get(dataRule.getRuleTypeId());
						if(ruleType!=null){
							cell.setCellValue(ruleType.getCode());
						}else{
							cell.setCellValue("");
						}
					}else if("systemCode".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						BusinessSystem system=ApiFactory.getAcsService().getSystemById(dataRule.getSystemId());
						if(system!=null){
							cell.setCellValue(system.getCode());
						}else{
							cell.setCellValue("");
						}
					}else{
						dataHandle.setFieldValue(conf,i,rowi,dataRule);
					}
				}
			}
			conditionInfo(dataRule,conditionSheet,conditionConfs);
		}
	}

	private void conditionInfo(DataRule dataRule, HSSFSheet conditionSheet,List<DataSheetConfig> conditionConfs) {
		List<Condition> conditions=conditionDao.getConditionsByDataRuleId(dataRule.getId());
		for(Condition condition:conditions){
			HSSFRow rowi = conditionSheet.createRow(conditionSheet.getLastRowNum()+1);
			for(int i=0;i<conditionConfs.size();i++){
				DataSheetConfig conf=conditionConfs.get(i);
				if(!conf.isIgnore()){
					if("dataRuleCode".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(dataRule.getCode());
					}else{
						dataHandle.setFieldValue(conf,i,rowi,condition);
					}
				}
			}
		}
	}

	private void exportRuleType(OutputStream fileOut) {
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_RULE_TYPE']");
		wb = new HSSFWorkbook();
    	HSSFSheet sheet=wb.createSheet("MMS_RULE_TYPE");
        HSSFRow row = sheet.createRow(0);
        
        dataHandle.getFileHead(wb,row,confs);
        List<RuleType> ruleTypes=ruleTypeDao.getAllRuleType();
		for(RuleType ruleType:ruleTypes){
			ruleTypeInfo(ruleType,sheet,confs);
		}
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}

	private void ruleTypeInfo(RuleType ruleType, HSSFSheet sheet,List<DataSheetConfig> confs) {
		HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
		for(int i=0;i<confs.size();i++){
			DataSheetConfig conf=confs.get(i);
			if(!conf.isIgnore()){
				if("parentCode".equals(conf.getFieldName())){
					HSSFCell cell = rowi.createCell(i);
					RuleType parent=ruleType.getParent();
					if(parent!=null){
						cell.setCellValue(parent.getCode());
					}else{
						cell.setCellValue("");
					}
				}else{
					dataHandle.setFieldValue(conf,i,rowi,ruleType);
				}
			}
		}
	}

	public void restore(Long companyId, FileConfigModel fileConfig,String... imatrixInfo) {
		File file=new File(fileConfig.getImportRootPath()+"/"+fileConfig.getImportPath()+"/"+fileConfig.getFilename()+".xls");
		if(file.exists()){
			if("MMS_RULE_TYPE".equals(fileConfig.getData())){
				importRuleType(file, companyId);
			}else if("MMS_DATA_RULE".equals(fileConfig.getData())){
				importDataRule(file, companyId);
			}else if("MMS_PERMISSION".equals(fileConfig.getData())){
				importPermission(file, companyId);
			}
		}
	}

	private void importPermission(File file, Long companyId) {
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_PERMISSION']");
		List<DataSheetConfig> conditionConfs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_PERMISSION_ITEM']");
		Map<String,Integer> map=dataHandle.getIdentifier(confs);
		Map<String,Integer> conditionMap=dataHandle.getIdentifier(conditionConfs);
		FileInputStream fis=null;
		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("MMS_PERMISSION");
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 						ParameterUtils.setParameters(parameters);
 						importPermission(sheet,confs,map);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId,null);
						ParameterUtils.setParameters(parameters);
						importPermission(sheet,confs,map);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importPermission(sheet,confs,map);
 			}
 			HSSFSheet conditionSheet=wb.getSheet("MMS_PERMISSION_ITEM");
 			if(ContextUtils.getCompanyId()==null){
 				List<Company> companys=companyManager.getCompanys();
 				for(Company company:companys){
 					ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 					ParameterUtils.setParameters(parameters);
 					importPermissionItem(conditionSheet,conditionConfs,conditionMap);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importPermissionItem(conditionSheet,conditionConfs,conditionMap);
 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}

	private void importPermissionItem(HSSFSheet conditionSheet,
			List<DataSheetConfig> conditionConfs,
			Map<String, Integer> conditionMap) {
		int firstRowNum = conditionSheet.getFirstRowNum();
		int rowNum=conditionSheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =conditionSheet.getRow(i);
			if(conditionSheet.getRow(i)!=null){
				addPermissionItem(conditionConfs,row,conditionMap);
			}
		}
	}

	private void addPermissionItem(List<DataSheetConfig> conditionConfs,
			HSSFRow row, Map<String, Integer> conditionMap) {
		Integer index=conditionMap.get("itemType");
		String itemType=row.getCell(index).getStringCellValue();
		index=conditionMap.get("operator");
		String operator=row.getCell(index).getStringCellValue();
		index=conditionMap.get("joinType");
		String joinType=row.getCell(index).getStringCellValue();
		index=conditionMap.get("conditionValue");
		String conditionValue=row.getCell(index).getStringCellValue();
		index=conditionMap.get("conditionName");
		String conditionName=row.getCell(index).getStringCellValue();
		index=conditionMap.get("displayOrder");
		String displayOrder=row.getCell(index).getStringCellValue();
		index=conditionMap.get("priority");
		String priority=row.getCell(index).getStringCellValue();
		index=conditionMap.get("authority");
		String authority=row.getCell(index).getStringCellValue();
		index=conditionMap.get("dataRuleCode");
		String dataRuleCode=row.getCell(index).getStringCellValue();
		DataRule dataRule=dataRuleDao.getDataRuleByCode(dataRuleCode);
		if(dataRule != null){
			conditionValue=getConditionValue(itemType,conditionValue,dataRule.getSystemId());
			if(StringUtils.isNotEmpty(conditionValue)){
				Permission permission=permissionDao.getPermissions(Integer.valueOf(priority), Integer.valueOf(authority),dataRule.getId());
				if(permission!=null){
					PermissionItem permissionItem=permissionItemDao.getPermissionItem(ItemType.valueOf(itemType),UserOperator.valueOf(operator),LogicOperator.valueOf(joinType),conditionValue,permission.getId());
					if(permissionItem==null){
						permissionItem=new PermissionItem();
					}
					permissionItem.setItemType(ItemType.valueOf(itemType));
					permissionItem.setOperator(UserOperator.valueOf(operator));
					permissionItem.setJoinType(LogicOperator.valueOf(joinType));
					permissionItem.setConditionValue(conditionValue);
					permissionItem.setConditionName(conditionName);
					permissionItem.setDisplayOrder(Integer.valueOf(displayOrder));
					permissionItem.setPermission(permission);
					permissionItem.setCreatedTime(new Date());
					permissionItem.setCreator(ContextUtils.getLoginName());
					permissionItem.setCreatorName(ContextUtils.getUserName());
					permissionItem.setCompanyId(ContextUtils.getCompanyId());
					permissionItemDao.save(permissionItem);
				}
			}
		}
	}
	
	private String getConditionValue(String itemType,String conditionValue,Long systemId){
		if(ItemType.USER.equals(ItemType.valueOf(itemType))){
			User user=ApiFactory.getAcsService().getUserByLoginName(conditionValue);
			if(user!=null){
				return conditionValue;
			}
		}else if(ItemType.DEPARTMENT.equals(ItemType.valueOf(itemType))){
			Department department=ApiFactory.getAcsService().getDepartmentByCode(conditionValue);
			if(department!=null){
				return department.getId().toString();
			}
		}else if(ItemType.WORKGROUP.equals(ItemType.valueOf(itemType))){
			Workgroup workgroup=ApiFactory.getAcsService().getWorkgroupByCode(conditionValue);
			if(workgroup!=null){
				return workgroup.getId().toString();
			}
		}else if(ItemType.ROLE.equals(ItemType.valueOf(itemType))){
			Role role=roleManager.getRole(systemId, conditionValue);
			if(role != null){
				return conditionValue;
			}
		}
		return null;
	}

	private void importPermission(HSSFSheet sheet, List<DataSheetConfig> confs,
			Map<String, Integer> map) {
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
				addPermission(confs,row,map);
			}
		}
	}

	private void addPermission(List<DataSheetConfig> confs, HSSFRow row,
			Map<String, Integer> map) {
		Integer index=map.get("priority");
		String priority=row.getCell(index).getStringCellValue();
		index=map.get("authority");
		String authority=row.getCell(index).getStringCellValue();
		index=map.get("dataRuleCode");
		String dataRuleCode=row.getCell(index).getStringCellValue();
		DataRule dataRule=dataRuleDao.getDataRuleByCode(dataRuleCode);
		Permission permission=permissionDao.getPermissions(Integer.valueOf(priority), Integer.valueOf(authority),dataRule.getId());
		if(permission==null){
			permission=new Permission();
		}
		permission.setPriority(Integer.valueOf(priority));
		permission.setAuthority(Integer.valueOf(authority));
		permission.setDataRule(dataRule);
		permission.setCreatedTime(new Date());
		permission.setCreator(ContextUtils.getLoginName());
		permission.setCreatorName(ContextUtils.getUserName());
		permission.setCompanyId(ContextUtils.getCompanyId());
		permissionDao.save(permission);
	}

	private void importDataRule(File file, Long companyId) {
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_DATA_RULE']");
		List<DataSheetConfig> conditionConfs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_CONDITION']");
		Map<String,Integer> map=dataHandle.getIdentifier(confs);
		Map<String,Integer> conditionMap=dataHandle.getIdentifier(conditionConfs);
		FileInputStream fis=null;
		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("MMS_DATA_RULE");
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 						ParameterUtils.setParameters(parameters);
 						importDataRule(sheet,confs,map);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId,null);
						ParameterUtils.setParameters(parameters);
						importDataRule(sheet,confs,map);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importDataRule(sheet,confs,map);
 			}
 			HSSFSheet conditionSheet=wb.getSheet("MMS_CONDITION");
 			if(ContextUtils.getCompanyId()==null){
 				List<Company> companys=companyManager.getCompanys();
 				for(Company company:companys){
 					ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 					ParameterUtils.setParameters(parameters);
 					importCondition(conditionSheet,conditionConfs,conditionMap);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importCondition(conditionSheet,conditionConfs,conditionMap);
 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}

	private void importCondition(HSSFSheet conditionSheet,
			List<DataSheetConfig> conditionConfs,
			Map<String, Integer> conditionMap) {
		int firstRowNum = conditionSheet.getFirstRowNum();
		int rowNum=conditionSheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =conditionSheet.getRow(i);
			if(conditionSheet.getRow(i)!=null){
				addCondition(conditionConfs,row,conditionMap);
			}
		}
	}

	private void addCondition(List<DataSheetConfig> conditionConfs,
			HSSFRow row, Map<String, Integer> conditionMap) {
		//数据表字段
		Integer index=conditionMap.get("field");
		String field=row.getCell(index).getStringCellValue();
		//比较符号
		index=conditionMap.get("operator");
		String operator=row.getCell(index).getStringCellValue();
		//条件连接类型
		index=conditionMap.get("lgicOperator");
		String lgicOperator=row.getCell(index).getStringCellValue();
		//字段数据类型
		index=conditionMap.get("dataType");
		String dataType=row.getCell(index).getStringCellValue();
		//条件值
		index=conditionMap.get("conditionValue");
		String conditionValue=row.getCell(index).getStringCellValue();
		//数据规则编号
		index=conditionMap.get("dataRuleCode");
		String dataRuleCode=row.getCell(index).getStringCellValue();
		DataRule dataRule=dataRuleDao.getDataRuleByCode(dataRuleCode);
		Condition condition=conditionDao.getCondition(field,FieldOperator.valueOf(operator),
				LogicOperator.valueOf(lgicOperator),DataType.valueOf(dataType),conditionValue,dataRule.getId());
		if(condition==null){
			condition=new Condition();
		}
		for(int j=0;j<conditionConfs.size();j++){
			DataSheetConfig conf=conditionConfs.get(j);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(j)!=null){
					value=row.getCell(j).getStringCellValue();
				}
				if("dataRuleCode".equals(fieldName)){
					condition.setDataRule(dataRule);
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						dataHandle.setValue(condition,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						dataHandle.setValue(condition,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		condition.setCreatedTime(new Date());
		condition.setCreator(ContextUtils.getLoginName());
		condition.setCreatorName(ContextUtils.getUserName());
		condition.setCompanyId(ContextUtils.getCompanyId());
		conditionDao.save(condition);
		
	}

	private void importDataRule(HSSFSheet sheet, List<DataSheetConfig> confs,
			Map<String, Integer> map) {
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
				addDataRule(confs,row,map);
			}
		}
	}

	private void addDataRule(List<DataSheetConfig> confs, HSSFRow row,
			Map<String, Integer> map) {
		Integer index=map.get("code");
		String code=row.getCell(index).getStringCellValue();
		DataRule dataRule=dataRuleDao.getDataRuleByCode(code);
		if(dataRule==null){
			dataRule=new DataRule();
		}
		for(int j=0;j<confs.size();j++){
			DataSheetConfig conf=confs.get(j);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(j)!=null){
					value=row.getCell(j).getStringCellValue();
				}
				if("dataTableName".equals(fieldName)){
					if(StringUtils.isNotEmpty(value)){
						DataTable dataTable=dataTableDao.findDataTableByName(value);
						dataRule.setDataTableId(dataTable.getId());
						dataRule.setDataTableName(dataTable.getAlias());
					}
				}else if("ruleTypeCode".equals(fieldName)){
					if(StringUtils.isNotEmpty(value)){
						RuleType ruleType=ruleTypeDao.getRuleTypeByCode(value);
						dataRule.setRuleTypeId(ruleType.getId());
						dataRule.setRuleTypeName(ruleType.getName());
					}
				}else if("systemCode".equals(fieldName)){
					if(StringUtils.isNotEmpty(value)){
						BusinessSystem system=ApiFactory.getAcsService().getSystemByCode(value);
						dataRule.setSystemId(system.getId());
					}
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						dataHandle.setValue(dataRule,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						dataHandle.setValue(dataRule,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		dataRule.setCreatedTime(new Date());
		dataRule.setCreator(ContextUtils.getLoginName());
		dataRule.setCreatorName(ContextUtils.getUserName());
		dataRule.setCompanyId(ContextUtils.getCompanyId());
		dataRuleDao.save(dataRule);
		
	}

	private void importRuleType(File file, Long companyId) {
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='MMS_RULE_TYPE']");
		Map<String,Integer> map=dataHandle.getIdentifier(confs);
		FileInputStream fis=null;
		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("MMS_RULE_TYPE");
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 						ParameterUtils.setParameters(parameters);
 						importRuleType(sheet,confs,map);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId,null);
						ParameterUtils.setParameters(parameters);
						importRuleType(sheet,confs,map);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importRuleType(sheet,confs,map);
 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}

	private void importRuleType(HSSFSheet sheet, List<DataSheetConfig> confs,
			Map<String, Integer> map) {
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
				addRuleType(confs,row,map);
			}
		}
	}

	private void addRuleType(List<DataSheetConfig> confs, HSSFRow row,Map<String, Integer> map) {
		Integer index=map.get("code");
		String code=row.getCell(index).getStringCellValue();
		RuleType ruleType=ruleTypeDao.getRuleTypeByCode(code);
		if(ruleType==null){
			ruleType=new RuleType();
		}
		for(int j=0;j<confs.size();j++){
			DataSheetConfig conf=confs.get(j);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(j)!=null){
					value=row.getCell(j).getStringCellValue();
				}
				if("parentCode".equals(fieldName)){
					if(StringUtils.isNotEmpty(value)){
						RuleType parent=ruleTypeDao.getRuleTypeByCode(value);
						ruleType.setParent(parent);
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){
						RuleType parent=ruleTypeDao.getRuleTypeByCode(conf.getDefaultValue());
						ruleType.setParent(parent);
					}
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						dataHandle.setValue(ruleType,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						dataHandle.setValue(ruleType,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		ruleType.setCreatedTime(new Date());
		ruleType.setCreator(ContextUtils.getLoginName());
		ruleType.setCreatorName(ContextUtils.getUserName());
		ruleType.setCompanyId(ContextUtils.getCompanyId());
		ruleTypeDao.save(ruleType);
	}

}
