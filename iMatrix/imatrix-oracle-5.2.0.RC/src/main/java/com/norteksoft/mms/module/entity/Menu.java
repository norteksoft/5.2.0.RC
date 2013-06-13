package com.norteksoft.mms.module.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.mms.base.OpenWay;
import com.norteksoft.mms.form.enumeration.MenuType;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.task.entity.WorkflowTask;

/**
 * 菜单实体
 * @author wurong
 *
 */
@Entity
@Table(name="MMS_MENU")
public class Menu extends IdEntity  implements Serializable,Comparable<Menu>,Cloneable {
		private static final long serialVersionUID = 1L;
		
		@Column(length=64)
		private String code; //编号
		@Column(length=64)
		private String name; //名称
		
		private Integer displayOrder = 0; //序号
		
		private String imageUrl;//图片路径
		private String iconName; // 显示的图片的名称
		
		@Enumerated(EnumType.STRING)
		@Column(length=32)
		private DataState enableState = DataState.DRAFT; //启用状态
		
		@OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
		private List<Menu> children = new ArrayList<Menu>(); //菜单下的菜单
		
		@ManyToOne
		@JoinColumn(name="FK_PARENT_MENU")
		private Menu parent; //父菜单
		
		private String url="#this"; //按钮对应的url，一级系统菜单才会使用
		@Lob
	    @Column(columnDefinition="CLOB", nullable=true)
		private String event;
		
		private Long systemId; //系统id
		
		private Integer layer = 1; //菜单的层次 根节点的层次为1，子菜单为父菜单层次加1
		
		
		@Enumerated(EnumType.STRING)
		@Column(length=32)
		private MenuType type=MenuType.CUSTOM;
		@Transient
		private Long lastMenuId;
		
		private OpenWay openWay=OpenWay.CURRENT_PAGE_OPEN;

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

		public DataState getEnableState() {
			return enableState;
		}

		public void setEnableState(DataState enableState) {
			this.enableState = enableState;
		}

		public List<Menu> getChildren() {
			Collections.sort(children, new Comparator<Menu>() {
				public int compare(Menu m1, Menu m2) {
					return m1.getDisplayOrder()-m2.getDisplayOrder();
				}
			});
			return children;
		}
		/**
		 * 获得第一个子菜单
		 */
		public Menu getFirstChildren(){
			return getChildren().size()==0?null:getChildren().get(0);
		}

		public void setChildren(List<Menu> children) {
			this.children = children;
		}

		public Menu getParent() {
			return parent;
		}

		public void setParent(Menu parent) {
			this.parent = parent;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
		
		public String getIconName() {
			return iconName;
		}

		public void setIconName(String iconName) {
			this.iconName = iconName;
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

		public String getEvent() {
			return event;
		}

		public void setEvent(String event) {
			this.event = event;
		}

		public int compareTo(Menu menu) {
			return this.getDisplayOrder()-menu.getDisplayOrder();
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

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public OpenWay getOpenWay() {
			return openWay;
		}

		public void setOpenWay(OpenWay openWay) {
			this.openWay = openWay;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((code == null) ? 0 : code.hashCode());
			result = prime * result
					+ ((displayOrder == null) ? 0 : displayOrder.hashCode());
			result = prime * result
					+ ((enableState == null) ? 0 : enableState.hashCode());
			result = prime * result + ((event == null) ? 0 : event.hashCode());
			result = prime * result
					+ ((iconName == null) ? 0 : iconName.hashCode());
			result = prime * result
					+ ((imageUrl == null) ? 0 : imageUrl.hashCode());
			result = prime * result
					+ ((lastMenuId == null) ? 0 : lastMenuId.hashCode());
			result = prime * result + ((layer == null) ? 0 : layer.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result
					+ ((openWay == null) ? 0 : openWay.hashCode());
			result = prime * result
					+ ((systemId == null) ? 0 : systemId.hashCode());
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			if (getId() == null) {
				if (((Menu)obj).getId() != null)
					return false;
			} else{
				return getId().equals(((Menu)obj).getId());
			}
			return true;
		}
		
		@Override
		public Menu clone(){
			try {
				return (Menu) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				throw new RuntimeException("Menu clone failure");
			}
		}
		
}
