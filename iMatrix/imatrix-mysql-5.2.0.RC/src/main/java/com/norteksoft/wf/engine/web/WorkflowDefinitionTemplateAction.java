package com.norteksoft.wf.engine.web;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.ZipUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionTemplate;
import com.norteksoft.wf.engine.entity.WorkflowDefinitionTemplateFile;
import com.norteksoft.wf.engine.entity.WorkflowType;
import com.norteksoft.wf.engine.service.WorkflowDefinitionTemplateManager;
import com.norteksoft.wf.engine.service.WorkflowTypeManager;

@Namespace("/engine")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "forkflow-definition-template", type = "redirectAction") })
public class WorkflowDefinitionTemplateAction extends CrudActionSupport<WorkflowDefinitionTemplate>{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private WorkflowDefinitionTemplate workflowDefinitionTemplate;
	private Page<WorkflowDefinitionTemplate> page=new Page<WorkflowDefinitionTemplate>(0,true);
	private Long typeId = 0l;
	private List<WorkflowType> typeList ;
	private String ids ;
	
	@Autowired
	private WorkflowDefinitionTemplateManager workflowDefinitionTemplateManager;
	@Autowired
	private WorkflowTypeManager workflowTypeManager;

	@Override
	@Action("workflow-definition-template-delete")
	public String delete() throws Exception {
		workflowDefinitionTemplateManager.delete(ids);
		ApiFactory.getBussinessLogService().log("流程定义模板", 
				"删除流程定义模板", 
				ContextUtils.getSystemId("wf"));
		this.renderText("ok");
		return null;
	}

	@Override
	@Action("workflow-definition-template-input")
	public String input() throws Exception {
		typeList = workflowTypeManager.getAllWorkflowType();
		typeId = workflowDefinitionTemplate.getTypeId();
		ApiFactory.getBussinessLogService().log("流程定义模板", 
				"流程定义模板表单页面", 
				ContextUtils.getSystemId("wf"));
		return "workflow-definition-template-input";
	}

	@Override
	@Action("workflow-definition-template-list")
	public String list() throws Exception {
		if(page.getPageSize()>1){
			if(typeId==null||typeId==0){
				workflowDefinitionTemplateManager.getTemplate(page);
			}else{
				workflowDefinitionTemplateManager.getTemplate(page,typeId);
			}
			ApiFactory.getBussinessLogService().log("流程定义模板", 
					"流程定义模板列表", 
					ContextUtils.getSystemId("wf"));
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "workflow-definition-template-list";
	}

	@Override
	public void prepareModel() throws Exception {
		if(id==null){
			workflowDefinitionTemplate=new WorkflowDefinitionTemplate();
		}else{
			workflowDefinitionTemplate=workflowDefinitionTemplateManager.getWorkflowDefinitionTemplate(id);
		}
	}

	@Override
	@Action("workflow-definition-template-save")
	public String save() throws Exception {
		workflowDefinitionTemplateManager.save(workflowDefinitionTemplate);
		ApiFactory.getBussinessLogService().log("流程定义模板", 
				"保存流程定义模板", 
				ContextUtils.getSystemId("wf"));
		this.renderText(workflowDefinitionTemplate.getId().toString());
		return null;
	}
	
	/**
	 * 上传模板xml
	 * @return
	 * @throws Exception
	 */
	@Action("upload-xml")
	public String uploadXml() throws Exception {
		//从request中获取参数并封装实体
		HttpServletRequest request = ServletActionContext.getRequest();
		MultiPartRequestWrapper wrapper = (MultiPartRequestWrapper) request;
		File file = wrapper.getFiles("Filedata")[0];
		String fileName = request.getParameter("Filename");
		workflowDefinitionTemplate = workflowDefinitionTemplateManager.getWorkflowDefinitionTemplate(id);
		workflowDefinitionTemplate.setName(fileName);
		workflowDefinitionTemplate.setTemplateType(StringUtils.substring(fileName, fileName.lastIndexOf('.')));
		InputStreamReader inr=new InputStreamReader(new FileInputStream(file),ZipUtils.prexEncoding(file.getPath()));
		BufferedReader br=new BufferedReader(inr);
		StringBuilder content=new StringBuilder();
		while(true){
			String rl=br.readLine();
			if(rl==null)break;
			content.append(rl);
		}
		WorkflowDefinitionTemplateFile workflowDefinitionTemplateFile=workflowDefinitionTemplateManager.getWorkflowDefinitionTemplateFileByTemplateId(id);
		if(workflowDefinitionTemplateFile==null){
			workflowDefinitionTemplateFile=new WorkflowDefinitionTemplateFile();
		}
		workflowDefinitionTemplateFile.setXml(content.toString());
		workflowDefinitionTemplateFile.setTemplateId(id);
		workflowDefinitionTemplateManager.save(workflowDefinitionTemplate,workflowDefinitionTemplateFile);
		ApiFactory.getBussinessLogService().log("流程定义模板", 
				"上传流程定义模板", 
				ContextUtils.getSystemId("wf"));
		return null;
	}
	
	/**
	 * 上传模板图片
	 * @return
	 * @throws Exception
	 */
	@Action("upload-picture")
	public String uploadPicture() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		MultiPartRequestWrapper wrapper = (MultiPartRequestWrapper) request;
		String fileName = wrapper.getParameter("Filename");
		workflowDefinitionTemplate = workflowDefinitionTemplateManager.getWorkflowDefinitionTemplate(id);
		workflowDefinitionTemplate.setPreviewImageName(fileName);
		Long time=System.currentTimeMillis();
		workflowDefinitionTemplate.setPreviewImage("images/"+time+fileName);
		File file = wrapper.getFiles("Filedata")[0];
		
		//进行文件的输出
		//1.先创建指定文件
		File imgFile = new File(workflowDefinitionTemplateManager.getLocalPath()+time+fileName);
		if(!imgFile.exists()){
			try{
				imgFile.getParentFile().mkdir();
				imgFile.createNewFile();
			}catch(Exception e) {
				throw new Exception("创建指定文件时失败...");
			}
		}
		//2.输出
		byte[] copyImg = getContent(file);
		FileOutputStream fot = new FileOutputStream(imgFile);
		fot.write(copyImg);
		fot.flush();
		fot.close();
		workflowDefinitionTemplateManager.save(workflowDefinitionTemplate);
		ApiFactory.getBussinessLogService().log("流程定义模板", 
				"上传流程定义模板图片", 
				ContextUtils.getSystemId("wf"));
		return null;
	}
	
	/**
	 * 把文件转成byte[]
	 * @param file
	 * @return
	 */
	private byte[] getContent(File file) {
		BufferedInputStream in = null;
		byte[] img = null;
		try {
			FileInputStream fin = new FileInputStream(file);
			in = new BufferedInputStream(fin);
			img = new byte[fin.available()];
			
			int readLength = 0; // 每次读取长度
			int allLength = 0; // 已经读取的长度
			byte[] bs = new byte[2048];
			while( ( readLength = in.read(bs) ) != -1 ){
				System.arraycopy(bs, 0, img, allLength, readLength);
				allLength += readLength;
	        }
		} catch (FileNotFoundException e) {
			throw new RuntimeException(" [" + file.getName() + "] not found ");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return img;
	}

	public WorkflowDefinitionTemplate getModel() {
		return workflowDefinitionTemplate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Page<WorkflowDefinitionTemplate> getPage() {
		return page;
	}

	public void setPage(Page<WorkflowDefinitionTemplate> page) {
		this.page = page;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public List<WorkflowType> getTypeList() {
		return typeList;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}
