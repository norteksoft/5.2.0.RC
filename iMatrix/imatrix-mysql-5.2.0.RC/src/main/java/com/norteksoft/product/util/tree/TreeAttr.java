package com.norteksoft.product.util.tree;

public class TreeAttr {
private String id;
private String rel;

public TreeAttr() {
	super();
}
public TreeAttr(String id, String rel) {
	super();
	this.id = id;
	this.rel = rel;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getRel() {
	return rel;
}
public void setRel(String rel) {
	this.rel = rel;
}

}
