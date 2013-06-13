package com.norteksoft.product.util.tree;

import java.util.List;

public class TreeNode {
	
	private TreeAttr attr;
	private String state;
	private String data;
	private List<TreeNode> children;
	
	public TreeNode() {
		super();
	}
	public TreeNode(TreeAttr attr, String state, String data) {
		super();
		this.attr = attr;
		this.state = state;
		this.data = data;
	}
	public TreeNode(TreeAttr attr, String state, String data,
			List<TreeNode> children) {
		super();
		this.attr = attr;
		this.state = state;
		this.data = data;
		this.children = children;
	}
	public TreeAttr getAttr() {
		return attr;
	}
	public void setAttr(TreeAttr attr) {
		this.attr = attr;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public List<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}
	
}
