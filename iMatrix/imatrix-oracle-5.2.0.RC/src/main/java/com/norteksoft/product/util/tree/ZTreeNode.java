package com.norteksoft.product.util.tree;


public class ZTreeNode {
	
	private String id;
	private String pId;
	private String name;
	private String open;
	private String isParent;
	private String type;
	private String data;
	private String iconSkin;
	
	public ZTreeNode(String id, String pId, String name,String open,String isParent,String type,String data) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.open = open;
		this.isParent = isParent;
		this.type = type;
		this.data = data;
	}
	
	public ZTreeNode(String id, String pId, String name,String open,String isParent,String type,String data,String iconSkin) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.open = open;
		this.isParent = isParent;
		this.type = type;
		this.data = data;
		this.iconSkin = iconSkin;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getIsParent() {
		return isParent;
	}

	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	public String getIconSkin() {
		return iconSkin;
	}
	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}
	
}
