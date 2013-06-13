package com.norteksoft.product.web.struts2.query;


public class QueryBean {
	private String query;
	private Object[] parameters;
	
	public QueryBean(){
		
	}
	
	public QueryBean(String query,Object[] parameters){
		this.query=query;
		this.parameters=parameters;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	
	
}
