package com.norteksoft.wf.engine.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jbpm.internal.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.product.api.FileService;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.wf.engine.dao.DocumentFileDao;
import com.norteksoft.wf.engine.dao.OfficeDao;
import com.norteksoft.wf.engine.entity.Document;
import com.norteksoft.wf.engine.entity.DocumentFile;
import com.norteksoft.wf.engine.web.servlet.WebPdf;

@Service
@Transactional
public class PdfManager {
	private static final Log log = Log.getLog(WebPdf.class.getName());
	private OfficeDao officeDao;
	private DocumentFileDao documentFileDao;
	@Autowired
    public void setOfficeDao(OfficeDao officeDao) {
		this.officeDao = officeDao;
	}
	@Autowired
	public void setDocumentFileDao(DocumentFileDao documentFileDao) {
		this.documentFileDao = documentFileDao;
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
		return documentFileDao.findUniqueNoCompanyCondition("from DocumentFile u where u.documentId=? ", documentId);
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
	/**
     * office接收端代码
     * @param recordId
     */
	@Transactional(readOnly=false)
	public void operatePdf(DBstep.iMsgServer2000 msgObj){
			String optionMessage;
	        if (msgObj.GetMsgByName("DBSTEP").equalsIgnoreCase("DBSTEP")) {		//如果是合法的信息包
	        	optionMessage = msgObj.GetMsgByName("OPTION");				//取得操作信息
	          log.debug(optionMessage);		//打印出调试信息

	          if (optionMessage.equalsIgnoreCase("LOADFILE")) {				//下面的代码为打开服务器数据库里的文件
	        	  doLoadFile(msgObj);
	          }

	          else if (optionMessage.equalsIgnoreCase("SAVEFILE")) {			//下面的代码为保存文件在服务器的数据库里
	        	  doSaveFile(msgObj);
	           
	          }
	        }
	        else {
	        	msgObj.MsgError("客户端发送数据包错误!");
	        	msgObj.MsgTextClear();
	        	msgObj.MsgFileClear();
	        }
		}
	@Transactional(readOnly=false)
	private void doLoadFile(DBstep.iMsgServer2000 msgObj){
         if (loadFile(msgObj)) {							//从数据库调入文档
         	msgObj.SetMsgByName("STATUS", "打开成功!");			//设置状态信息
         	msgObj.MsgError("");						//清除错误信息
         }else {
         	msgObj.MsgError("打开失败!");					//设置错误信息
         }
	}
	  //调出文档，将文档内容保存在mFileBody里，以便进行打包
	@Transactional(readOnly=false)
	  private boolean loadFile(DBstep.iMsgServer2000 msgObj) {
		  boolean result = false;
			String recordId = msgObj.GetMsgByName("RECORDID"); // 取得文档编号
			msgObj.MsgTextClear(); // 清除文本信息
			if(StringUtils.isNotEmpty(recordId)&&(!recordId.equals(NEWFILE))){
				DocumentFile file = getDocumentFile(Long.parseLong(recordId));
				Document document = this.getDocument(Long.parseLong(recordId));
				if(document!=null){
					if (file != null) {//数据库中取，兼容历史文
						msgObj.MsgFileBody(file.getFileBody()); // 将文件信息打包
						msgObj.MsgFileSize(document.getFileSize());
						result = true;
					}else{//数据库中不存在，取配置的方式
						String filePath=document.getFilePath();
						if (StringUtils.isNotEmpty(filePath)) {
							FileService fileService =(FileService)ContextUtils.getBean("fileService");
							byte[] temp = fileService.getFile(filePath);
							msgObj.MsgFileBody(temp); // 将文件信息打包
							msgObj.MsgFileSize(document.getFileSize());
							result = true;
						}
					}
				}
			}
			return result;
	  }

	@Transactional(readOnly=false)
	  private void doSaveFile(DBstep.iMsgServer2000 msgObj){
          if (saveFile(msgObj)) {							//保存文档内容到数据库中
          	msgObj.SetMsgByName("STATUS", "保存成功!");			//设置状态信息
          	msgObj.MsgError("");						//清除错误信息
          }else {
          	msgObj.MsgError("保存失败!");					//设置错误信息
          }
          msgObj.MsgFileClear(); // 清除文档内容
	  }
	  //保存文档，如果文档存在，则覆盖，不存在，则添加
	@Transactional(readOnly=false)
	  private boolean saveFile(DBstep.iMsgServer2000 msgObj) {
		  boolean result = false;
			
			String recordId = msgObj.GetMsgByName("RECORDID"); // 取得文档编号
			String fileName = msgObj.GetMsgByName("FILENAME"); // 取得文档名称
			String userName = msgObj.GetMsgByName("USERNAME");
			String extParam = msgObj.GetMsgByName("EXTPARAM");//获得自定义参数COMPANYID:1050;WORKFLOWID:workflow_33253.11490013;TASKNAME:;TASKMODE:
			String fileType = msgObj.GetMsgByName("FILETYPE");
			int fileSize = msgObj.MsgFileSize(); // 取得文档大小
			byte[] fileBody = msgObj.MsgFileBody(); // 取得文档内容
			msgObj.MsgTextClear(); // 清除文本信息
			Map<String,String> paramMap = parseExtParam(extParam);
			FileService fileService =(FileService)ContextUtils.getBean("fileService");
			if(StringUtils.isEmpty(recordId)||recordId.equals(NEWFILE)){
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
				msgObj.SetMsgByName("DOCUMENTID", document.getId().toString());
				
				result = true;
			}else{
				Document document = this.getDocument(Long.valueOf(recordId));
				document.setFileName(fileName);
				document.setFileSize(fileSize);
				document.setCreatedTime(new Date());
				DocumentFile file = getDocumentFile(Long.parseLong(recordId));
				if(file==null){
					document.setFilePath(fileService.saveFile(fileBody));
				}else{
					file.setDocumentId(document.getId());
					file.setFileBody(fileBody);
					this.saveDocumentFile(file);
				}
				this.saveDocument(document);
				msgObj.SetMsgByName("DOCUMENTID", document.getId().toString());
				result = true;
			}
			return result;
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
	  
	private final static String NEWFILE = "newFile";
	private final static String COMPANYID ="COMPANYID";
	private final static String WORKFLOWID = "WORKFLOWID";
	private final static String TASKNAME = "TASKNAME";
	private final static String TASKMODE = "TASKMODE";
	private final static String TRUENAME = "TRUENAME";
}
