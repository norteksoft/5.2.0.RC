package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.ibm.icu.math.BigDecimal;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.IdEntity;
/**
 * 数据表类
 * @author wurong
 */
@Entity
@Table(name="MMS_DATA_TABLE")
public class DataTable  extends IdEntity  implements Serializable,Comparable<DataTable>{
	private static final long serialVersionUID = 1L;
	private String name;//表名
	private String alias;//别名
	private String entityName;//实体名
	@Enumerated(EnumType.STRING)
	private DataState tableState;//数据表的状态
	@Column(length=500)
	private String remark;//备注
	@Column(name="FK_MENU_ID")
	private Long menuId;//菜单列表
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public DataState getTableState() {
		return tableState;
	}
	public void setTableState(DataState tableState) {
		this.tableState = tableState;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
//	public List<TableColumn> getTableColumns() {
//		List<TableColumn> columns=new ArrayList<TableColumn>();
//		for(TableColumn tc:tableColumns){
//			if(!tc.getDeleted()){
//				columns.add(tc);
//			}
//		}
//		Collections.sort(columns, new Comparator<TableColumn>() {
//			public int compare(TableColumn tc1, TableColumn tc2) {
//				return tc1.getDisplayOrder()-tc2.getDisplayOrder();
//			}
//		});
//		return columns;
//	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public int compareTo(DataTable dataTable) {
		BigDecimal dataTable1 = new BigDecimal(this.getId());
		BigDecimal dataTable2 = new BigDecimal(dataTable.getId());
		return  dataTable1.compareTo(dataTable2);
	}
	
}
