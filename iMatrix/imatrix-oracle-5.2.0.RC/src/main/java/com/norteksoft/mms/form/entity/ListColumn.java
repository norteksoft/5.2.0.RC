package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.mms.form.enumeration.DefaultValue;
import com.norteksoft.product.orm.IdEntity;

/**
 * 列表视图的列设置
 * @author wurong
 */
@Entity
@Table(name="MMS_LIST_COLUMN")
public class ListColumn extends IdEntity  implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	@ManyToOne
	@JoinColumn(name="FK_LIST_VIEW_ID")
	private ListView listView;//列表视图
	@ManyToOne
	@JoinColumn(name="FK_TABLE_COLUMN_ID")
	private TableColumn tableColumn;//数据表列配置
	private String headerName;//列头名
	private Boolean visible;//是否显示
	private Integer displayOrder;//显示顺序
	private Boolean exportable;//是否导出
	private Boolean sortable=true;//是否排序
	private String href = "#this";//链接
	private String headStyle;//表头样式
	
	private Boolean total;//是否需要合计
	private Boolean editable;//该列是否可以编辑
	private String querySettingName;//查询设置
	private String querySettingValue;//查询设置值
	private String valueSet;//值设置，以逗号隔开:数据库值:显示的值,数据库值:显示的值...
	private String editRules;//编辑规则,email:true, required:true....
	private String format;//格式设置
	private String controlName;//编辑时控件类型
	private String controlValue;//控件值
	
	@ManyToOne
	@JoinColumn(name="FK_MAIN_KEY_ID")
	private TableColumn mainKey;//合并单元格主键
	private Boolean mergerCell=false;//是否合并单元格
	@Enumerated(EnumType.STRING)
	private DefaultValue defaultValue;//编辑时默认值设置
	private String eventType;//事件类型,多个事件以逗号隔开
	@Transient
	private String optionSet;
	@Transient
	private String optionKey;
	@Transient
	private String columnName;
	@Transient
	private String internationName;//国际化列名
	
	public TableColumn getTableColumn() {
		return tableColumn;
	}
	public void setTableColumn(TableColumn tableColumn) {
		this.tableColumn = tableColumn;
	}
	
	public String getHeaderName() {
		return headerName;
	}
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public Boolean getExportable() {
		return exportable;
	}
	public void setExportable(Boolean exportable) {
		this.exportable = exportable;
	}
	public ListView getListView() {
		return listView;
	}
	public void setListView(ListView listView) {
		this.listView = listView;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getHeadStyle() {
		return headStyle;
	}
	public void setHeadStyle(String headStyle) {
		this.headStyle = headStyle;
	}
	public Boolean getTotal() {
		return total;
	}
	public void setTotal(Boolean total) {
		this.total = total;
	}
	public Boolean getEditable() {
		return editable;
	}
	public void setEditable(Boolean editable) {
		this.editable = editable;
	}
	public String getValueSet() {
		return valueSet;
	}
	public void setValueSet(String valueSet) {
		this.valueSet = valueSet;
	}
	public String getEditRules() {
		return editRules;
	}
	public void setEditRules(String editRules) {
		this.editRules = editRules;
	}


	public String getOptionSet() {
		return optionSet;
	}
	public void setOptionSet(String optionSet) {
		this.optionSet = optionSet;
	}
	public String getOptionKey() {
		return optionKey;
	}
	public void setOptionKey(String optionKey) {
		this.optionKey = optionKey;
	}
	@Override
	public ListColumn clone() {
		try {
			return (ListColumn) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("ListColumn clone failure");
		}
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public DefaultValue getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(DefaultValue defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public Boolean getSortable() {
		return sortable;
	}
	public void setSortable(Boolean sortable) {
		this.sortable = sortable;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getInternationName() {
		return internationName;
	}
	public void setInternationName(String internationName) {
		this.internationName = internationName;
	}
	public TableColumn getMainKey() {
		return mainKey;
	}
	public void setMainKey(TableColumn mainKey) {
		this.mainKey = mainKey;
	}
	public Boolean getMergerCell() {
		return mergerCell;
	}
	public void setMergerCell(Boolean mergerCell) {
		this.mergerCell = mergerCell;
	}
	public String getControlValue() {
		return controlValue;
	}
	public void setControlValue(String controlValue) {
		this.controlValue = controlValue;
	}
	public String getControlName() {
		return controlName;
	}
	public void setControlName(String controlName) {
		this.controlName = controlName;
	}
	public String getQuerySettingName() {
		return querySettingName;
	}
	public void setQuerySettingName(String querySettingName) {
		this.querySettingName = querySettingName;
	}
	public String getQuerySettingValue() {
		return querySettingValue;
	}
	public void setQuerySettingValue(String querySettingValue) {
		this.querySettingValue = querySettingValue;
	}
}
