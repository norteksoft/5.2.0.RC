package com.norteksoft.product.api.entity;

import java.io.Serializable;
import java.util.List;
import com.norteksoft.mms.base.OpenWay;
import com.norteksoft.mms.form.enumeration.MenuType;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.enumeration.DataState;

public class Menu implements Serializable{
		private static final long serialVersionUID = 1L;
		
		//entity
		private Long id;
		private boolean deleted;
		private Long companyId;
		//Menu
		private String code; //编号
		private String name; //名称
		private Integer displayOrder; //序号
		private String imageUrl;//图片路径
		private String iconName; // 显示的图片的名称
		private DataState enableState;//启用状态
		private com.norteksoft.mms.module.entity.Menu parent; //父菜单
		private String url; //按钮对应的url，一级系统菜单才会使用
		private String event;
		private Long systemId; //系统id
		private Integer layer; //菜单的层次 根节点的层次为1，子菜单为父菜单层次加1
		private MenuType type;
		private Long lastMenuId;
		private OpenWay openWay;
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public boolean isDeleted() {
			return deleted;
		}
		public void setDeleted(boolean deleted) {
			this.deleted = deleted;
		}
		public Long getCompanyId() {
			return companyId;
		}
		public void setCompanyId(Long companyId) {
			this.companyId = companyId;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getDisplayOrder() {
			return displayOrder;
		}
		public void setDisplayOrder(Integer displayOrder) {
			this.displayOrder = displayOrder;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public String getIconName() {
			return iconName;
		}
		public void setIconName(String iconName) {
			this.iconName = iconName;
		}
		public DataState getEnableState() {
			return enableState;
		}
		public void setEnableState(DataState enableState) {
			this.enableState = enableState;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getEvent() {
			return event;
		}
		public void setEvent(String event) {
			this.event = event;
		}
		public Long getSystemId() {
			return systemId;
		}
		public void setSystemId(Long systemId) {
			this.systemId = systemId;
		}
		public Integer getLayer() {
			return layer;
		}
		public void setLayer(Integer layer) {
			this.layer = layer;
		}
		public MenuType getType() {
			return type;
		}
		public void setType(MenuType type) {
			this.type = type;
		}
		public Long getLastMenuId() {
			return lastMenuId;
		}
		public void setLastMenuId(Long lastMenuId) {
			this.lastMenuId = lastMenuId;
		}
		public OpenWay getOpenWay() {
			return openWay;
		}
		public void setOpenWay(OpenWay openWay) {
			this.openWay = openWay;
		}
}
