package com.norteksoft.mms.form.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.entity.ImportColumn;
import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.mms.form.dao.JqGridPropertyDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.GroupHeader;
import com.norteksoft.mms.form.entity.JqGridProperty;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.impl.DefaultDataImporterCallBack;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;

@Service
@Transactional
public class ImportListViewManager extends DefaultDataImporterCallBack{
	@Autowired
	private ListViewManager listViewManager;
	@Autowired
	private ListColumnManager listColumnManager;
	@Autowired
	private DataTableManager dataTableManager;
	@Autowired
	private MenuManager menuManager;
	@Autowired
	private JqGridPropertyDao jqGridPropertyDao;
	@Autowired
	private TableColumnManager tableColumnManager;
	@Autowired
	private GroupHeaderManager groupHeaderManager;
	
	public String saveSingleRow(String[] rowValue,ImportDefinition importDefinition) {
		if("MMS_LIST_VIEW".equals(importDefinition.getCode())){
			saveListView(rowValue,importDefinition);
		}else if("MMS_LIST_COLUMN".equals(importDefinition.getCode())){
			saveListColumn(rowValue,importDefinition);
		}else if("MMS_GROUP_HEADER".equals(importDefinition.getCode())){
			saveGroupHeader(rowValue,importDefinition);
		}
		return "";
	}

	private void saveGroupHeader(String[] rowValue,ImportDefinition importDefinition) {
		ListView listview=listViewManager.getListViewByCode(rowValue[0]);
		if(listview!=null){
			GroupHeader header=groupHeaderManager.getGroupHeaderByInfo(listview.getId(), rowValue[1], Integer.parseInt(rowValue[2]), rowValue[3]);
			if(header==null){
				header=new GroupHeader();
			}
			header.setListViewId(listview.getId());
			header.setStartColumnName(rowValue[1]);
			header.setNumberOfColumns( Integer.parseInt(rowValue[2]));
			header.setTitleText(rowValue[3]);
			groupHeaderManager.save(header);
		}
	}

	private void saveListColumn(String[] rowValue,ImportDefinition importDefinition) {
		ListView listview=listViewManager.getListViewByCode(rowValue[0]);
		ListColumn col=new ListColumn();
		col.setCompanyId(ContextUtils.getCompanyId());
		int i=0;
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			if(i==0){
				col.setListView(listview);
			}else if(i==1){
				TableColumn tbCol=null;
				if(listview!=null&&listview.getDataTable()!=null)tbCol=tableColumnManager.getTableColumnByColName(listview.getDataTable().getId(), rowValue[i]);
				col.setTableColumn(tbCol);
			}else{
				if(i<rowValue.length && StringUtils.isNotEmpty(rowValue[i])){
					String enumname="";
					if(i==10){
						enumname="com.norteksoft.mms.form.enumeration.EditControlType";
					}else if(i==11){
						enumname="com.norteksoft.mms.form.enumeration.QueryType";
					}else if(i==15){
						enumname="com.norteksoft.mms.form.enumeration.DefaultValue";
					}
					setValue(col,importColumn,rowValue[i],enumname);	
				}
					
			}
			i++;
		}
		listColumnManager.saveColumn(col);
	}

	private void saveListView(String[] rowValue,ImportDefinition importDefinition) {
		ListView listview=listViewManager.getListViewByCode(rowValue[0]);
		if(listview!=null){
			//彻底删除列表对应的所有的字段信息
			listColumnManager.deleteAllColumns(listview.getId());
		}
		if(listview==null){
			listview=new ListView();
		}
		//创建者姓名,创建者id,创建时间,公司id
		listview.setCreator(ContextUtils.getLoginName());
		listview.setCreatorName(ContextUtils.getUserName());
		listview.setCreatedTime(new Date());
		listview.setCompanyId(ContextUtils.getCompanyId());
		listViewManager.saveListView(listview);
		int i=0;
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			if(i==2){
				DataTable datatable=dataTableManager.getDataTableByTableName(rowValue[i]);
				if(datatable!=null)listview.setDataTable(datatable);
			}else if(i==4){
				Menu menu=menuManager.getMenuByCode(rowValue[i]);
				if(menu!=null)listview.setMenuId(menu.getId());
			}else if(i==15 && StringUtils.isNotEmpty(rowValue[i])){
				packagingJqGridProperty(rowValue[i],listview);
			}else{
				if(i<rowValue.length && StringUtils.isNotEmpty(rowValue[i])){
					String enumname="";
					if(i==10){
						enumname="com.norteksoft.mms.form.enumeration.OrderType";
					}else if(i==18){
						enumname="com.norteksoft.mms.form.enumeration.StartQuery";
					}
					setValue(listview,importColumn,rowValue[i],enumname);
				}
			}
			i++;
		}
		listViewManager.saveListView(listview);
	}
	
	public void setValue(Object entity,ImportColumn importColumn,String value,String enumname){
		try{
			if(DataType.DATE.equals(importColumn.getDataType())||DataType.TIME.toString().equals(importColumn.getDataType())){
				BeanUtils.copyProperty(entity, importColumn.getName(),getDate(importColumn.getDataType(),value));
			}else if(DataType.ENUM.equals(importColumn.getDataType())){
				BeanUtils.copyProperty(entity, importColumn.getName(), JsonParser.getEnum(value, enumname));
			}else if(DataType.BOOLEAN.equals(importColumn.getDataType())){
				String val=value;
				if(StringUtils.isEmpty(val)){
					val="false";
				}
				BeanUtils.copyProperty(entity, importColumn.getName(), val);
			}else{
				BeanUtils.copyProperty(entity, importColumn.getName(), value);
			}
	    }catch (Exception e) {
		}
		
	}
	
	public Date getDate(DataType dataType,String value){
		try {
			if(DataType.DATE.equals(dataType)){
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				return simpleDateFormat.parse(value);
			 }else if(DataType.TIME.equals(dataType)){
				 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 return simpleDateFormat.parse(value);
			 }
		}catch (Exception e) {
		}
		return null;
	}
	
	private void packagingJqGridProperty(String value,ListView listview){
		String[] jgpStrs=value.split(",");
		List<JqGridProperty> jgps=new ArrayList<JqGridProperty>();
		for(String str:jgpStrs){
			if(StringUtils.isNotEmpty(str)){
				JqGridProperty jgp=new JqGridProperty();
				String[] prs=str.split(":");
				jgp.setName(prs.length>=1?prs[0]:"");
				jgp.setValue(prs.length>=2?prs[1]:"");
				jgp.setCompanyId(ContextUtils.getCompanyId());
				jgp.setListView(listview);
				jqGridPropertyDao.save(jgp);
				jgps.add(jgp);
			}
		}
		listview.setJqGridPropertys(jgps);
	}
}
