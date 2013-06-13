package com.norteksoft.mms.form.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import com.norteksoft.product.enumeration.DataState;


/**
 * 表单视图
 * @author wurong
 */
@Entity
@DiscriminatorValue("FormView")
public class FormView extends View implements Cloneable {
	private static final long serialVersionUID = 1L;

	@Lob
    @Column(columnDefinition="CLOB", nullable=true)
	private String html;
	private Integer version = 0;//如果版本号为0，则保存的时候需要重新生成版本号；否则，保持原来的版本
	@Enumerated(EnumType.STRING)
	private DataState formState;//表单的状态
	/**
	 * 用于页面显示， 得到的是标准的html代码
	 */
	public String getHtml() {
		String formIdStr = this.getId()==null?"${formId}":this.getId().toString();
		return html==null?"":html.replace("&lt;", "<").replace("&gt;", ">").replace("${formId}", formIdStr);
	}

	/**
	 * 用于编辑器编辑，得到的是转义后的代码
	 */
	public String getHtmlCode(){
		return this.html;
	}
	/**
	 * 保证存在数据库里的html代码都是转义后的代码
	 * @param html
	 */
	public void setHtml(String html) {
		this.html = html==null?null:html.replace("<", "&lt;").replace(">", "&gt;");
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	public DataState getFormState() {
		return formState;
	}

	public void setFormState(DataState formState) {
		this.formState = formState;
	}

	/**
	 * 返回该表单是否为标准表单
	 */
	public Boolean isStandardForm(){
		return this.getStandard();
	}
	
	@Override
	public String toString() {
		if(this.getDataTable()==null){
			return "View["+this.getCode()+";"+this.getName()+"]";
		}else{
			return "View["+this.getCode()+";"+this.getName()+";"+this.getDataTable().getAlias()+";"+this.getDataTable().getName()+"]";
		}
	}
	
	@Override
	public FormView clone(){
		try {
			return (FormView) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("FormView clone failure");
		}
	}
}
