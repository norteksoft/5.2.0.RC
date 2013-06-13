package com.norteksoft.portal.web.index;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.portal.entity.StickyNote;
import com.norteksoft.portal.service.IndexManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;

@Namespace("/student")
@ParentPackage("default")
@Results({@Result(name=CrudActionSupport.RELOAD,location="small-note",type="redirectAction")})
public class SmallNoteAction extends CrudActionSupport<StickyNote> {

	private static final long serialVersionUID = 1L;
	@Autowired
	private IndexManager indexManager;
	
	public String getMessageCount()throws Exception{
		this.renderText(indexManager.getCurrentTotalNoteNum(ContextUtils.getUserId()));
		return null;
	}
	
	@Override
	public String delete() throws Exception {
		return null;
	}

	@Override
	public String input() throws Exception {
		return null;
	}

	@Override
	public String list() throws Exception {
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		
	}

	@Override
	public String save() throws Exception {
		return null;
	}

	public StickyNote getModel() {
		return null;
	}

}
