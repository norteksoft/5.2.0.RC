package com.norteksoft.wf.engine.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.product.api.FileService;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.wf.engine.dao.DocumentFileDao;
import com.norteksoft.wf.engine.dao.DocumentTemplateDao;
import com.norteksoft.wf.engine.dao.DocumentTemplateFileDao;
import com.norteksoft.wf.engine.dao.OfficeDao;
import com.norteksoft.wf.engine.entity.Document;
import com.norteksoft.wf.engine.entity.DocumentFile;
import com.norteksoft.wf.engine.entity.DocumentTemplate;
import com.norteksoft.wf.engine.entity.DocumentTemplateFile;

@Service
@Transactional
public class OfficeManager {
	@Autowired
	private OfficeDao officeDao;
	
	@Autowired
	private DocumentFileDao documentFileDao;
    
	@Autowired
	private DocumentTemplateFileDao documentTemplateFileDao;
	
	@Autowired
	private DocumentTemplateDao documentTemplateDao;

	
	/**
	 * 根据流程实例ID删除所有正文内容
	 * @param WorkflowInstanceId
	 */
	@Transactional(readOnly=false)
	public void deleteAllOfficesByWorkflowInstanceId(String workFlowId,Long companyId) {
		List<Document> list = getAllDocumentsByWorkflowInstanceId(workFlowId,companyId);
		for(Document doc:list){
			deleteText(doc);
		}
	}
	
	/**
	 * 根据流程实例ID查询所有正文(返回List)
	 * @param WorkflowInstanceId
	 * @return
	 */
	public List<Document> getAllDocumentsByWorkflowInstanceId(String workFlowId,Long companyId){
		return officeDao.find( "from Document u where u.companyId=? and u.workflowId=? order by u.createdTime", companyId,workFlowId);
	}
	
	public List<DocumentFile> getAllDocumentFilesByWorkflowId(String workFlowId,Long companyId){
		return officeDao.find( "select df from DocumentFile df ,Document d where df.recordId=d.recordId and d.workflowId=? and d.companyId=?  order by d.createdTime", workFlowId,companyId);
	}
	
	/**
	 * 删除正文
	 * @param documentId
	 */
	@Transactional(readOnly=false)
	public void deleteText(Long documentId) {
		Assert.notNull(documentId, "documentId不能为null");
		Document doc = officeDao.get(documentId);
		deleteText(doc);
	}
	
	/**
	 * 删除正文
	 * @param doc
	 */
	@Transactional(readOnly=false)
	public void deleteText(Document doc){
		DocumentFile file = documentFileDao.getDocumentFileByDocumentId(doc.getId());
		if(file==null){
			FileService fileService =(FileService)ContextUtils.getBean("fileService");
			if(doc!=null)
			  fileService.deleteFile(doc.getFilePath());
		}else{
			documentFileDao.delete(file);
		}
		officeDao.delete(doc);
	}
	
	/**
     * 根据实例查询正文
     * @param page
     * @param workflowInstanceId
     * @param taskId
     */
	public Page<Document> displayTexts(Page<Document> page,String workFlowId){
		return officeDao.findPage(page, "from Document u where u.workFlowId=? ", workFlowId);
	}
	/**
     * 查询某个正文
     * @param recordId
     */
	public DocumentFile getDocumentFile(Long documentId){
		Document doc=getDocument(documentId);
		return documentFileDao.findUnique("from DocumentFile u where u.companyId=? and u.documentId=? ", doc.getCompanyId(),documentId);
	}
	public Document getDocument(Long id){
		return officeDao.get(id);
	}
	/**
     * 保存正文
     * @param recordId
     */
	@Transactional(readOnly=false)
	public void saveDocumentFile(DocumentFile entity){
		documentFileDao.save(entity);
	}
	@Transactional(readOnly=false)
	public void saveDocument(Document entity){
		officeDao.save(entity);
	}
	@Transactional(readOnly=false)
	public void saveDocumentAndFile(Document entity,DocumentFile file){
		officeDao.save(entity);
		documentFileDao.save(file);
	}
	/**
	 * office接收端代码
	 * @param request
	 * @param response
	 */
	@Transactional(readOnly=false)
	public DBstep.iMsgServer2000 operateOffice(DBstep.iMsgServer2000 msgServer){
		String optionMessage;
		if (msgServer.GetMsgByName("DBSTEP").equalsIgnoreCase("DBSTEP")) { // 如果是合法的信息包
			optionMessage = msgServer.GetMsgByName("OPTION"); // 取得操作信息
			if (optionMessage.equalsIgnoreCase("LOADFILE")) { // 下面的代码为打开服务器数据库里的文件
				doLoadFile(msgServer);
			}else if (optionMessage.equalsIgnoreCase("SAVEFILE")) { // 下面的代码为保存文件在服务器的数据库里
				doSaveFile(msgServer);
			}

		}else{
			msgServer.MsgError("客户端发送数据包错误!");
			msgServer.MsgTextClear();
			msgServer.MsgFileClear();
		}
		return msgServer;
	}
	
	/**
	 * 调出文档，将文档内容保存在mFileBody里，以便进行打包
	 * @return
	 */
	private boolean loadFile(DBstep.iMsgServer2000 msgServer) {
		boolean result = false;
		String recordId = msgServer.GetMsgByName("RECORDID"); // 取得文档编号
		String templateId = msgServer.GetMsgByName("TEMPLATE");//模板
		String fileType = msgServer.GetMsgByName("FILETYPE");
		msgServer.MsgTextClear(); // 清除文本信息
		if(StringUtils.isNotEmpty(recordId)){
			DocumentFile file = getDocumentFile(Long.parseLong(recordId));
			Document document = this.getDocument(Long.parseLong(recordId));
			if(document!=null){
				if (file != null) {//数据库取，兼容历史文
					byte[] temp = file.getFileBody();
					msgServer.MsgFileBody(temp); // 将文件信息打包
					msgServer.MsgFileSize(document.getFileSize());
					result = true;
				}else{//数据库中取不到，去配置的方式中取
					String filePath=document.getFilePath();
					if (StringUtils.isNotEmpty(filePath)) {
						FileService fileService =(FileService)ContextUtils.getBean("fileService");
						byte[] temp = fileService.getFile(filePath);
						msgServer.MsgFileBody(temp); // 将文件信息打包
						msgServer.MsgFileSize(document.getFileSize());
						result = true;
					}
				}
			}
		}else if(StringUtils.isNotEmpty(templateId)){
			DocumentTemplate templateFile = documentTemplateDao.get(Long.parseLong(templateId));
			if (isUseTemplate(templateFile,fileType)) {
				DocumentTemplateFile documentTemplateFile = documentTemplateFileDao.get(Long.parseLong(templateId));
				byte[] temp = documentTemplateFile.getFileBody();
				msgServer.MsgFileBody(temp); // 将文件信息打包
				msgServer.MsgFileSize(templateFile.getFileSize());
				result = true;
			}
		}
		return result;
	}
	
	/*
	 * 判断是不是要用模板
	 */
	private boolean isUseTemplate(DocumentTemplate templateFile,String fileType){
		if(templateFile != null){
			if(fileType.endsWith(".doc")&&(templateFile.getFileType().equals(".doc")||templateFile.getFileType().equals(".dot"))){
				return true;
			}else if(fileType.endsWith(".xls")&&(templateFile.getFileType().equals(".xls")||templateFile.getFileType().equals(".xlt"))){
				return true;
			}
		}
		return false;
	}
	@Transactional(readOnly=false)
	private void doLoadFile(DBstep.iMsgServer2000 msgServer){
		if (loadFile(msgServer)) { // 从数据库调入文档
			msgServer.SetMsgByName("STATUS", "打开成功!"); // 设置状态信息
			msgServer.MsgError(""); // 清除错误信息
		} else {
			msgServer.MsgError("打开失败!"); // 设置错误信息
		}
	}
	@Transactional(readOnly=false)
    private void doSaveFile(DBstep.iMsgServer2000 msgServer){
    	
		if (saveFile(msgServer)) // 保存文档内容到数据库中
		{
			msgServer.SetMsgByName("STATUS", "保存成功!"); // 设置状态信息
			msgServer.MsgError(""); // 清除错误信息
		} else {
			msgServer.MsgError("保存失败!"); // 设置错误信息
		}
		msgServer.MsgFileClear(); // 清除文档内容
	}

    private Map<String,String> parseExtParam(String extParam ){
    	Map<String,String> paramMap = new HashMap<String,String>();
    	String[] params =extParam.split(";");
    	for(String param:params){
    		String[] key_value = param.split(":");
    		if(key_value.length>=2){
    			paramMap.put(key_value[0],key_value[1] );
    		}
    	}
    	return paramMap;
    }
    private final static String COMPANYID ="COMPANYID";
    private final static String WORKFLOWID = "WORKFLOWID";
    private final static String TASKNAME = "TASKNAME";
    private final static String TASKMODE = "TASKMODE";
    private final static String TRUENAME = "TRUENAME";
	/**
	 * 保存文档，如果文档存在，则覆盖，不存在，则添加
	 * @return
	 */
    @Transactional(readOnly=false)
	private boolean saveFile(DBstep.iMsgServer2000 msgServer) {
    	boolean result = false;
		String recordId = msgServer.GetMsgByName("RECORDID"); // 取得文档编号
		String fileName = msgServer.GetMsgByName("FILENAME"); // 取得文档名称
		String userName = msgServer.GetMsgByName("USERNAME");
		String extParam = msgServer.GetMsgByName("EXTPARAM");//获得自定义参数COMPANYID:1050;WORKFLOWID:workflow_33253.11490013;TASKNAME:;TASKMODE:
		String fileType = msgServer.GetMsgByName("FILETYPE");
		int fileSize = msgServer.MsgFileSize(); // 取得文档大小
		byte[] fileBody = msgServer.MsgFileBody(); // 取得文档内容
		msgServer.MsgTextClear(); // 清除文本信息
		Map<String,String> paramMap = parseExtParam(extParam);
		FileService fileService =(FileService)ContextUtils.getBean("fileService");
		if(StringUtils.isEmpty(recordId)){
			Document document = new Document();
			document.setFileName(fileName);
			document.setFileSize(fileSize);
			document.setCreator(userName);
			document.setCreatorName(paramMap.get(TRUENAME));
			document.setCompanyId(Long.valueOf(paramMap.get(COMPANYID)));
			document.setWorkflowId(paramMap.get(WORKFLOWID));
			document.setTaskName(paramMap.get(TASKNAME));
			if(StringUtils.isNotEmpty(paramMap.get(TASKMODE))){
				document.setTaskMode(TaskProcessingMode.valueOf(paramMap.get(TASKMODE)));
			}
			document.setFileType(fileType);
			document.setCreatedTime(new Date());
			document.setFilePath(fileService.saveFile(fileBody));
			this.saveDocument(document);
			msgServer.SetMsgByName("DOCUMENTID", document.getId().toString());
			result = true;
		}else{
			Document document = this.getDocument(Long.valueOf(recordId));
			document.setFileName(fileName);
			document.setFileSize(fileSize);
			DocumentFile file = getDocumentFile(Long.parseLong(recordId));
			if(file==null){
				document.setFilePath(fileService.saveFile(fileBody));
			}else{
				file.setDocumentId(document.getId());
				file.setFileBody(fileBody);
				this.saveDocumentFile(file);
			}
			this.saveDocument(document);
			msgServer.SetMsgByName("DOCUMENTID", document.getId().toString());
			result = true;
		}
		return result;
	}
	public List<Document> getDocumentsExceptTaskName(String workflowId2,
			String taskName2) {
		
		return officeDao.getDocumentsExceptTaskName(workflowId2,taskName2);
	}
	public List<Document> getDocumentsExceptTaskMode(String workflowId2,
			TaskProcessingMode taskMode2) {
		return officeDao.getDocumentsExceptTaskMode(workflowId2, taskMode2);
	}
	public List<Document> getDocumentsExceptCustomField(String workflowId2,
			String customField) {
		return officeDao.getDocumentsExceptCustomField(workflowId2,customField);
	}
	public List<Document> getDocumentsByCustomField(String workflowId2,
			String customField) {
		return officeDao.getDocumentsByCustomField(workflowId2,customField);
	}
	public List<Document> getDocuments(String workflowId2,
			TaskProcessingMode taskMode2) {
		return officeDao.getDocuments(workflowId2, taskMode2);
	}
	public List<Document> getDocuments(String workflowId2, String taskName2) {
		return officeDao.getDocuments(workflowId2, taskName2);
	}
	public List<Document> getDocuments(Long taskId, Long companyId2) {
		Assert.notNull(taskId, "taskId不能为null");
		Assert.notNull(companyId2, "companyId不能为null");
		return officeDao.getDocuments(taskId, companyId2);
	}

}
