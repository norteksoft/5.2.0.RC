package com.norteksoft.wf.engine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;

import rtx.RtxMsgSender;

import com.norteksoft.mms.form.entity.AutomaticallyFilledField;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.AsyncMailUtils;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.MailUtils;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.task.base.enumeration.TaskProcessingMode;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.ProcessProperties;
import com.norteksoft.wf.base.enumeration.TaskTransactorCondition;
import com.norteksoft.wf.base.utils.DocumentParameterUtils;
import com.norteksoft.wf.base.utils.Dom4jUtils;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.base.utils.WorkflowMemcachedUtil;

/**
 * 根据JBPM流程定义扩展文件获取在流程执行过程中所需要的条件
 * @author 
 */
public class DefinitionXmlParse {
	
	public static String FIELD_CONSTRAINT_RULE_REQUIRED = "field.fill.way.must.fill";//必填
	public static String FIELD_CONSTRAINT_RULE_NON_EDITABLE = "field.fill.way.prohibit";//禁止填写
	
    public static String MORE_TRANSACTOR="more-transactor";
    public static String PASS_RATE = "pass-rate";
    
    public static final String REMIND_STYLE = "remind-type";
	public static final String REMIND_DUEDATE = "duedate";
	public static final String REMIND_REPEAT = "repeat";
	public static final String REMIND_TIME = "remind-time";
	public static final String REMIND_NOTICE_TYPE = "notice-type";
	public static final String REMIND_NOTICE_USER_CONDITION = "notice-user-condition";
    
	public static final String TO = "to";
	public static final String TASK_NAME = "task-name";
	public static String SUBMIT_NAME = "submit-name";
	 public static String APPROVE_NAME = "approve-name";
	 public static String REFUSE_NAME = "refuse-name";
	 public static String ADD_COUNTER_NAME = "add-counter-name";
	 public static String DEL_COUNTER_NAME = "del-counter-name";
	 public static String SIGNOFF_NAME = "signoff-name";
	 public static String AGREEMENT_NAME = "agreement-name";
	 public static String OPPOSE_NAME = "oppose-name";
	 public static String KIKEN_NAME = "kiken-name";
	 public static String ASSIGN_NAME = "assign-name";
	 private static String DEFAULT_VALUE="_A_A";//当在监听中将该值已放入缓存，但其值是null时，设置默认值为该值。
	 
	
    /**
     * 通过扩展的JBPM文件获取标准的JBMP文件
     * @param file
     * @return
     */
    public static String getStandardXml4Jbpm(String xmlFile,Long definitionId){
    	Document doc = Dom4jUtils.getDocument(xmlFile);
        Element root = doc.getRootElement();
        Dom4jUtils.removeAllElementsByName(root, "extend");
        root.addAttribute("name", "workflow_" + definitionId);
        return doc.asXML();
    }
    
//    /**
//     * 根据Task的Name属性获取Task的环节基本属性
//     * @param xmlFile
//     * @param taskName
//     * @return Map
//     * 		keys：
//     * 			processing-mode  
//     * 			remark
//     */
//    public static String getTaskBasicProp(String xmlFile, String taskName){
//    	return Dom4jUtils.getSingleElementValueByPath(xmlFile, 
//    			"/process/task[@name='" + taskName+ 
//    			"']/extend/basic-properties/processing-mode");
//    }
    
    /**
     * 根据Task的Name属性获取任务办理人的条件
     * @param xmlFile
     * @param taskName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<TaskTransactorCondition, String> getTaskTransactor(String processId, String taskName){
    	Map<TaskTransactorCondition, String> result =null;
    	if(StringUtils.isNotEmpty(taskName)){
    		result = (Map<TaskTransactorCondition, String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=taskTransactor="+taskName));
    		if(result==null){
    			result = getTaskTransactors(processId,taskName);
    		}
    	}else{
    		result = new HashMap<TaskTransactorCondition, String>();
    	}
    	return result;
    }
    
    public static Map<TaskTransactorCondition, String> getTaskTransactors(String processId, String taskName){
    	Document document = DocumentParameterUtils.getDocument(processId);
		 String namespaceUrl = document.getRootElement().getNamespace().getURI();
		 HashMap<String,String> xmlMap = new HashMap<String,String>();   
		 xmlMap.put("wf",namespaceUrl);   
		 XPath x = document.createXPath("/wf:process/wf:*[@name='" + taskName+ 
		 "']/wf:extend/wf:transactor-settings/wf:user-condition");   
		 x.setNamespaceURIs(xmlMap);  
		 Node node = x.selectSingleNode(document);
		 String userCondition = node==null?null:node.getText();
		 x = document.createXPath("/wf:process/wf:*[@name='" + taskName+ 
		 "']/wf:extend/wf:transactor-settings/wf:additional-condition/wf:only-in-creator-department"); 
		 x.setNamespaceURIs(xmlMap);  
		 node = x.selectSingleNode(document);
		 String onlyCreatorDept = node==null?"false":node.getText();
		 x = document.createXPath("/wf:process/wf:*[@name='" + taskName+ 
		 "']/wf:extend/wf:transactor-settings/wf:additional-condition/wf:with-creator-department"); 
		 x.setNamespaceURIs(xmlMap);  
		 node = x.selectSingleNode(document);
		 String withCreatorDept = node==null?"false":node.getText();
		 x = document.createXPath("/wf:process/wf:*[@name='" + taskName+ 
		 "']/wf:extend/wf:transactor-settings/wf:additional-condition/wf:select-one-from-multiple"); 
		 x.setNamespaceURIs(xmlMap);  
		 node = x.selectSingleNode(document);
		 String selecteOne = node==null?"false":node.getText();
		 x = document.createXPath("/wf:process/wf:*[@name='" + taskName+ 
		 "']/wf:extend/wf:transactor-settings/wf:additional-condition/wf:select-type"); 
		 x.setNamespaceURIs(xmlMap);  
		 node = x.selectSingleNode(document);
		 String selectType = node==null?TaskTransactorCondition.SELECT_TYPE_CUSTOM:node.getText();
		 x = document.createXPath("/wf:process/wf:*[@name='" + taskName+ 
		 "']/wf:extend/wf:transactor-settings/wf:additional-condition/wf:select-bean"); 
		 x.setNamespaceURIs(xmlMap);  
		 node = x.selectSingleNode(document);
		 String selectBean = node==null?"":node.getText();
		 Map<TaskTransactorCondition, String> result = new HashMap<TaskTransactorCondition, String>();
		 result.put(TaskTransactorCondition.USER_CONDITION, userCondition);
		 result.put(TaskTransactorCondition.ONLY_IN_CREATOR_DEPARTMENT, StringUtils.isNotEmpty(onlyCreatorDept)?onlyCreatorDept:"false");
		 result.put(TaskTransactorCondition.WITH_CREATOR_DEPARTMENT, StringUtils.isNotEmpty(withCreatorDept)?withCreatorDept:"false");
		 result.put(TaskTransactorCondition.SELECT_ONE_FROM_MULTIPLE, StringUtils.isNotEmpty(selecteOne)?selecteOne:"false");
		 result.put(TaskTransactorCondition.SELECT_TYPE, StringUtils.isNotEmpty(selectType)?selectType:TaskTransactorCondition.SELECT_TYPE_CUSTOM);
		 result.put(TaskTransactorCondition.SELECT_BEAN, StringUtils.isNotEmpty(selectBean)?selectBean:"");
		 return result;
    }
    
//    /**
//     * 根据Task的Name属性获取任务办理人的条件
//     * @param xmlFile
//     * @param taskName
//     * @return
//     */
//    public static Map<TaskTransactorCondition, String> getTaskTransactorCondition(String file, String taskName){
//    	
//    	Document document = Dom4jUtils.getDocument(file);
//		String namespaceUrl = document.getRootElement().getNamespace().getURI();
// 		HashMap<String,String> xmlMap = new HashMap<String,String>();   
// 		xmlMap.put("wf",namespaceUrl);   
// 		XPath x = document.createXPath("/wf:process/*[@name='"+taskName+"']/wf:extend/wf:transactor-settings");   
// 		x.setNamespaceURIs(xmlMap);
//    	Node node = x.selectSingleNode(document);
//    	Map<TaskTransactorCondition, String> result = new HashMap<TaskTransactorCondition, String>();
//    	if(node!=null){
//    		Element element = (Element)node;
//    		result.put(TaskTransactorCondition.USER_CONDITION, element.elementText(TaskTransactorCondition.USER_CONDITION.toString()));
//    		Element additionCondition = element.element("additional-condition");
//    		result.put(TaskTransactorCondition.ONLY_IN_CREATOR_DEPARTMENT, additionCondition==null?"false":additionCondition.elementText(TaskTransactorCondition.ONLY_IN_CREATOR_DEPARTMENT.toString()));
//        	result.put(TaskTransactorCondition.WITH_CREATOR_DEPARTMENT, additionCondition==null?"false":additionCondition.elementText(TaskTransactorCondition.WITH_CREATOR_DEPARTMENT.toString()));
//        	result.put(TaskTransactorCondition.SELECT_ONE_FROM_MULTIPLE, additionCondition==null?"false":additionCondition.elementText(TaskTransactorCondition.SELECT_ONE_FROM_MULTIPLE.toString()));
//    	}
//    	return result;
//    }
//    
    /**
     * 返回上一环节指定办理人的url
     * @param xmlFile
     * @param taskName
     * @return url 和url条件的String数组集合
     */
	public static String getPreviousTransactorAssignmentUrl(String processId,String taskName){
		String result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=previousTransactorAssignmentUrl="+taskName));
			if(result == null){//如果监听中没有将流程相关值放入缓存中
				result = getMyPreviousTransactorAssignmentUrl(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){//如果监听中将流程相关值放入了缓存中，但其值是null时
					result = null;
				}
			}
		}else{
			result = "";
		}
		return result;
    }
	private static String getMyPreviousTransactorAssignmentUrl(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:*[@name='" + taskName+ "']/wf:extend/wf:transactor-settings/wf:url");   
		x.setNamespaceURIs(xmlMap);  
		Node node = x.selectSingleNode(document);
		if(node==null){
			return "";
		}else{
			return node.getText();
		}
	}
    
//    public static String getNextTaskName(String xmlFile, String taskName){
//    	Document document = Dom4jUtils.getDocument(xmlFile);
//        String namespaceUrl = document.getRootElement().getNamespace().getURI();
//		HashMap<String,String> xmlMap = new HashMap<String,String>();   
//		xmlMap.put("wf",namespaceUrl);   
//		XPath x = document.createXPath("/wf:process/wf:start/wf:transition");   
//		x.setNamespaceURIs(xmlMap);  
//		Node node = x.selectSingleNode(document);
//    	if(node==null){
//    		return null;
//    	}else{
//    		return ((Element)node).attribute("to").getText();
//    	}
//    }
    
    /**
     * 获得办理模式
     * @param xmlFile
     * @param taskName
     * @return
     */
    public static String getTaskProcessingMode(String processId, String taskName){
    	String result = null;
    	if(StringUtils.isNotEmpty(taskName)){
    		result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=taskProcessingMode="+taskName));
    		if(result == null){
				result = getMyTaskProcessingMode(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
    	}else{
    		result = null;
    	}
		return result;
    }
    
    private static String getMyTaskProcessingMode(String processId, String taskName){
    	return Dom4jUtils.getSingleElementValueByPath(processId, 
    			"/process/task[@name='" + taskName+ 
		"']/extend/basic-properties/processing-mode");
    }
//    /**
//     * 获得是否多人办理
//     * @param xmlFile
//     * @param taskName
//     * @return
//     */
//    public static boolean isMoreTransactor(String xmlFile, String taskName){
//    	return  new Boolean(Dom4jUtils.getSingleElementPropByPath(xmlFile, 
//    			"/process/task[@name='" + taskName+ 
//				"']/extend/basic-properties/processing-mode",MORE_TRANSACTOR));
//    }
    /**
     * 获得是否多人办理
     * @param xmlFile
     * @param taskName
     * @return
     */
    public static boolean hasMoreTransactor(String processId, String taskName){
    	Boolean result = null;
    	if(StringUtils.isNotEmpty(taskName)){
    		result = (Boolean)WorkflowMemcachedUtil.get(getHashCode(processId+"=hasMoreTransactor="+taskName));
    		if(result==null)result = myHasMoreTransactor(processId,taskName);;
    	}else{
    		result = false;
    	}
    	return result;
    }
    private static Boolean myHasMoreTransactor(String processId, String taskName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:transactor-settings/wf:more-transactor");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null){
    		return false;
    	}else{
    		return new Boolean(node.getText());
    	}
    }
    
    /**
     * 获得是否多人办理
     * @param xmlFile
     * @param taskName
     * @return
     */
    public static Integer getTransactPassRate(String processId, String taskName){
    	Integer result = null;
    	if(StringUtils.isNotEmpty(taskName)){
    		Object value = WorkflowMemcachedUtil.get(getHashCode(processId+"=transactPassRate="+taskName));
    		if(value == null){
    			result=getMyTransactPassRate(processId,taskName);
    		}else{
    			if(DEFAULT_VALUE.equals(value.toString())){
        			result = null;
        		}else{
        			result = (Integer)value;
        		}
    		}
    	}
    	return result;
    }
    
    private static Integer getMyTransactPassRate(String processId, String taskName){
    	String passRate=Dom4jUtils.getSingleElementPropByPath(processId, 
    			"/process/task[@name='" + taskName+ 
				"']/extend/basic-properties/processing-mode",PASS_RATE);
		if(StringUtils.isNotEmpty(passRate)){
			return Integer.valueOf(passRate);
		}else{
			return null;
		}
    }
    
    public static String getAfterTaskCompletedBean(String processId, String taskName){
    	String result = null;
    	if(StringUtils.isNotEmpty(taskName)){
    		result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=afterTaskCompletedBean="+taskName));
    		if(result == null){
				result = getMyAfterTaskCompletedBean(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
    	}
    	return result;
    }
    
    private static String getMyAfterTaskCompletedBean(String processId, String taskName){
    	return  Dom4jUtils.getSingleElementValueByPath(processId, 
				"/process/task[@name='" + taskName+ 
		"']/extend/after-complete");
    }
    
    /**
     * 获的流向流过时执行的beanName
     * @param xmlFile
     * @param transitionName
     * @return
     */
    public static String getFlowingExecuteBean(String processId, String transitionName){
    	String result = null;
    	if(StringUtils.isNotEmpty(transitionName)){
    		result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=flowingExecuteBean="+transitionName));
    		if(result == null){
    			result=getMyFlowingExecuteBean(processId,transitionName);
    		}else{
    			if(DEFAULT_VALUE.equals(result)){
    				result = null;
    			}
    		}
    	}
    	return result;
    }
    private static String getMyFlowingExecuteBean(String processId, String transitionName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("//wf:transition[@name='" + transitionName+ "']/wf:extend/wf:flow-execution");   
    	x.setNamespaceURIs(xmlMap);  
    	if(x.selectSingleNode(document)==null){
    		return null;
    	}
    	return x.selectSingleNode(document).getText();
    }
    
    /**
     * 解析环节删除权限aaa
     * @param xmlFile
     * @param taskName
     * @return
     */
    public static String getDeleteInstancePermissionsInTask(String processId, String taskName){
    	String result = null;
    	if(StringUtils.isNotEmpty(taskName)){
    		result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=deleteInstancePermissionsInTask="+taskName));
    		if(result == null){
				result = getMyDeleteInstancePermissionsInTask(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
    	}
    	return result;
    }
    
    private static String getMyDeleteInstancePermissionsInTask(String processId, String taskName){
    	return Dom4jUtils.getSingleElementValueByPath(processId, 
				"/process/task[@name='" + taskName+ 
		"']/extend/right-settings/delete-right/user-condition");
    }
    
    /**
     * 解析任务预设的显示标题
     * @param xmlFile
     * @param taskName
     * @return
     */
    public static String getTaskTitle(String processId, String taskName){
    	String result = null;
    	if(StringUtils.isNotEmpty(taskName)){
    		result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=taskTitle="+taskName));
    		if(result == null){
				result = getMyTaskTitle(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
    	}
    	return result;
    }
    private static String getMyTaskTitle(String processId, String taskName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("/wf:process/wf:*[@name='" + taskName+ 
    	"']/wf:extend/wf:basic-properties/wf:task-name");   
    	x.setNamespaceURIs(xmlMap);  
    	Node node = x.selectSingleNode(document);
    	if(node==null){
    		return null;
    	}
    	String text=node.getText();
    	return text;
    }
    
//    /**
//     * 解析任务预设的环节编号
//     * @param xmlFile
//     * @param taskName
//     * @return
//     */
//    public static String getTaskCode(String xmlFile, String taskName){
//    	Document document = Dom4jUtils.getDocument(xmlFile);
//        String namespaceUrl = document.getRootElement().getNamespace().getURI();
//		HashMap<String,String> xmlMap = new HashMap<String,String>();   
//		xmlMap.put("wf",namespaceUrl);   
//		XPath x = document.createXPath("/wf:process/wf:*[@name='" + taskName+ 
//    			"']/wf:extend/wf:basic-properties/wf:task-code");   
//		x.setNamespaceURIs(xmlMap);  
//    	if(x.selectSingleNode(document)==null){
//    		return null;
//    	}
//    	return x.selectSingleNode(document).getText();
//    }
    
//    /**
//     * 解析任务预设的显示标题属性
//     * @param xmlFile
//     * @param taskName
//     * @return
//     */
//    public static String getTaskTitleProp(String xmlFile, String taskName, String propName){
//    	return Dom4jUtils.getSingleElementPropByPath(xmlFile, 
//    			"/process/task[@name='" + taskName+ 
//				"']/extend/basic-properties/task-name", propName);
//    }
    
    /**
     * 获取流程的定义基本信息
     * @param file
     * @return Map
     * keys:
     *         wf_name
     *         wf_admin
     *         wf_form
     *         wf_version
     *         wf_code
     *         wf_creator
     *         wf_created_time
     *         wf_state
     *         wf_type
     *         wf_type_code
     *         process-code
     */
    //走流程时使用
    @SuppressWarnings("unchecked")
    public static Map<String, String> getWorkFlowBaseInfo(String processId){
    	Map<String, String> result = (Map<String, String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=workFlowBaseInfo"));
    	if(result == null) result = getMyWorkFlowBaseInfo(processId);
    	return result;
    }
    
    private static Map<String, String> getMyWorkFlowBaseInfo(String processId){
    	Document doc = DocumentParameterUtils.getDocument(processId);
    	return getMyProcessBaseInfo(doc);
    }
    //更新流程定义时使用（因为草稿状态的流程还没有processId）
    public static Map<String, String> getProcessBaseInfo(String xmlFile){
    	Document doc = Dom4jUtils.getDocument(xmlFile);
    	return getMyProcessBaseInfo(doc);
    }
    
    private static Map<String, String> getMyProcessBaseInfo(Document doc){
    	Map<String, String> properties = new HashMap<String, String>();
    	Element root = doc.getRootElement();
    	String wf_name = root.attributeValue("name");
    	properties.put("wf_name", wf_name);
    	
    	Element basePropElement = Dom4jUtils.getElementByPath(root, "extend:basic-properties");
    	Element propElement = null;
    	
    	for(ProcessProperties bp : ProcessProperties.values()){
    		propElement = Dom4jUtils.getSubElementByName(basePropElement, bp.toString());
    		properties.put(bp.toString(), propElement == null ? null : propElement.getText());
    	}
    	propElement = Dom4jUtils.getSubElementByName(basePropElement, "system-id");
    	if(propElement!=null)properties.put("system-id", propElement.getText());
        propElement = Dom4jUtils.getSubElementByName(basePropElement, "system-code");
        if(propElement!=null)properties.put("system-code", propElement.getText());
        propElement = Dom4jUtils.getSubElementByName(basePropElement, "custom-type");
        if(propElement!=null)properties.put("custom-type", propElement.getText());
    	return properties;
    }
    
//    /**
//     * 返回流程定义绑定的表单名
//     * @return
//     */
//    public static String getFormName(String file){
//        Document document = Dom4jUtils.getDocument(file);
//        String namespaceUrl = document.getRootElement().getNamespace().getURI();
//		HashMap<String,String> xmlMap = new HashMap<String,String>();   
//		xmlMap.put("wf",namespaceUrl);   
//		XPath x = document.createXPath("/wf:process/wf:extend/wf:basic-properties/wf:form-name");   
//		x.setNamespaceURIs(xmlMap);  
//    	if(x.selectSingleNode(document)==null){
//    		throw new RuntimeException("invalid Document.");
//    	}
//    	return x.selectSingleNode(document).getText();
//    }
    
    /**
     * 返回名字为decisionName的decision中XPATH为/transition/extend/basic-properties/condition的所有condition元素
     * @param document
     * @param decisionName
     * @return
     */
    @SuppressWarnings("unchecked")
	public static List<String> getDecisionConditions(String processId ,String decisionName){
    	List<String> result = null;
    	if(StringUtils.isNotEmpty(decisionName)){
    		result = (List<String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=decisionConditions="+decisionName));
    		if(result==null)result = getMyDecisionConditions(processId,decisionName);
    	}else{
    		result = new ArrayList<String>();
    	}
    	return result;
    }
    @SuppressWarnings("unchecked")
    private static List<String> getMyDecisionConditions(String processId ,String decisionName){
    	List<String> list = new ArrayList<String>();
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("//wf:decision[@name='"+ decisionName +"']/wf:transition/wf:extend/wf:basic-properties/wf:condition");   
    	x.setNamespaceURIs(xmlMap);
    	List<Node> childs = x.selectNodes(document);
    	for (int i = 0; i < childs.size(); i++) {
    		Node condition = childs.get(i);
    		list.add(condition.getText());
    	}
    	return list;
    }
    
    /**
     * 返回名字为decisionName的decision中第index个transition子元素
     * @param document
     * @param decisionName
     * @param index
     * @return string[]{trasitionName,下一环节名称}
     */
    public static String[] getDecisionTransition(String processId ,String decisionName,int index){
    	String[] result = null;
    	if(StringUtils.isNotEmpty(decisionName)){
    		Object value = WorkflowMemcachedUtil.get(getHashCode(processId+"=decisionTransition="+decisionName+"="+index));
    		if(value == null){
    			result = getMyDecisionTransition(processId,decisionName,index);
    		}else{
    			if(DEFAULT_VALUE.equals(value.toString())){
    				result = null;
    			}else{
    				result = (String[])value;
    			}
    		}
    	}
    	return result;
    }
    private static String[] getMyDecisionTransition(String processId ,String decisionName,int index){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("//wf:decision[@name='"+decisionName+"']/wf:transition["+ index + "]");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null)return null;
    	return new String[]{((Element) node).attributeValue("name"),((Element) node).attributeValue("to")};
    }
    
    /**
     * 返回该流向要改变的状态
     * @param file
     * @param transitionName
     * @return
     */
    public static String getChangeStatus(String processId,String transitionName){
    	String result = null;
    	if(StringUtils.isNotEmpty(transitionName)){
    		result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=changeStatus="+transitionName));
    		if(result == null){
    			result=getMyChangeStatus(processId,transitionName);
    		}else{
    			if(DEFAULT_VALUE.equals(result)){
    				result = null;
    			}
    		}
    	}
    	return result;
    }
    private static String getMyChangeStatus(String processId,String transitionName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("//wf:transition[@name='"+transitionName+"']/wf:extend/wf:additional-properties/wf:change-status");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null){
    		return null;
    	}
    	return node.getText();
    }
    
    /**
     * 返回该流向是否需要通知
     * @param file
     * @param transitionName
     * @return
     */
    public static boolean transitionInform(String processId,String transitionName){
    	Boolean result = false;
    	if(StringUtils.isNotEmpty(transitionName)){
    		result = (Boolean)WorkflowMemcachedUtil.get(getHashCode(processId+"=transitionInform="+transitionName));
    		if(result == null) result = myTransitionInform(processId,transitionName);
    	}
    	return result;
    }
    private static boolean myTransitionInform(String processId,String transitionName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("//wf:transition[@name='"+transitionName+"']/wf:extend/wf:additional-properties/wf:inform");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    /**
     * 获得流向通知方式
     * @param file
     * @param transitionName
     * @return
     */
    public static String getTransitionInformType(String processId,String transitionName){
    	String result = null;
    	if(StringUtils.isNotEmpty(transitionName)){
    		result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=transitionInformType="+transitionName));
    		if(result == null){
    			result=getMyTransitionInformType(processId,transitionName);
    		}else{
    			if(DEFAULT_VALUE.equals(result)){
    				result = null;
    			}
    		}
    	}
    	return result;
    }
    private static String getMyTransitionInformType(String processId,String transitionName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("//wf:transition[@name='"+transitionName+"']/wf:extend/wf:additional-properties/wf:inform/wf:inform-type");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null){
    		return null;
    	}
    	//如果没有设置通知方式,默认为"邮件方式"
    	if("".equals(node.getText())){
    		return CommonStrings.EMAIL_STYLE;
    	}
    	return node.getText();
    }
    
    /**
     * 流程是否需要通知
     * @param file
     * @param transitionName
     * @return
     */
    public static boolean processInform(String processId){
    	Boolean result = (Boolean)WorkflowMemcachedUtil.get(getHashCode(processId+"=processInform"));
    	if(result==null)result = myProcessInform(processId);
    	return result;
    }
    private static boolean myProcessInform(String processId){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("/wf:process/wf:extend/wf:inform");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    /**
     * 获得流程通知方式
     * @param file
     * @param transitionName
     * @return
     */
    public static String getProcessInformType(String processId){
    	String result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=processInformType"));
    	if(DEFAULT_VALUE.equals(result)){
    		result = null;
    	}else{
    		result = getMyProcessInformType(processId);
    	}
    	return result;
    }
    private static String getMyProcessInformType(String processId){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("/wf:process/wf:extend/wf:inform/wf:inform-type");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null){
    		return null;
    	}
    	//如果没有设置通知方式,默认为"邮件方式"
    	if("".equals(node.getText())){
    		return CommonStrings.EMAIL_STYLE;
    	}
    	return node.getText();
    }
    
    /**
     * 流向通知
     * @param file
     * @return
     */
    public static void transitionInformMail(String processId,String transitionName,Set<String> emails){
		AsyncMailUtils.sendMail(emails, getNeedInformSubject(processId,transitionName), getNeedInformContent(processId,transitionName));
	}
    
    /**
     * 流向通知
     * @param file
     * @return
     */
    public static void transitionInformRTX(String processId,String transitionName,String receivers){
    	 RtxMsgSender.sendNotify(receivers, getNeedInformSubject(processId,transitionName), "1", getNeedInformContent(processId,transitionName), "", ContextUtils.getCompanyId());
	}
    /**
     * 流向通知
     * @param file
     * @return
     */
    public static void transitionInformSwing(String processId,String transitionName,String receivers,WorkflowInstance workflow){
    	if(StringUtils.isNotEmpty(receivers)){
    		String[] loginNames=receivers.split(",");
    		for(String loginName:loginNames){
    			try {
					ApiFactory.getPortalService().addMessage("task", ContextUtils.getUserName(), ContextUtils.getLoginName(), loginName,"待办任务流向通知",getNeedInformSubject(processId,transitionName), "/task/workflow-notification.htm?notificationType=transition&processId="+processId+"&transitionName="+transitionName);
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    
    /**
     * 流程通知:邮件方式
     * @param file
     * @return
     */
    public static void processInformMail(String processId,Set<String> emails){
    	MailUtils.sendMail(emails, getProcessInformSubject(processId), getProcessInformContent(processId));
	}
    
    /**
     * 流程通知:RTX方式
     * @param file
     * @return
     */
    public static void processInformRTX(String processId,String receivers){
	    RtxMsgSender.sendNotify(receivers, getProcessInformSubject(processId), "1", getProcessInformContent(processId), "", ContextUtils.getCompanyId());
	}
    /**
     * 流程通知:Swing方式
     * @param file
     * @return
     */
    public static void processInformSwing(String processId,String receivers,WorkflowInstance workflow){
    	if(StringUtils.isNotEmpty(receivers)){
    		String[] loginNames=receivers.split(",");
    		for(String loginName:loginNames){
    			try {
					ApiFactory.getPortalService().addMessage("task", ContextUtils.getUserName(), ContextUtils.getLoginName(), loginName,"流程结束时通知",getProcessInformSubject(processId), "/task/workflow-notification.htm?notificationType=process&processId="+processId);
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    
    
    /**
     * 得到流程需要通知的用户条件
     * @return
     */
    public static String getProcessInformUserCondition(String processId){
    	String result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=processInformUserCondition"));
    	if(result == null){
			result = getMyProcessInformUserCondition(processId);
		}else{
			if(DEFAULT_VALUE.equals(result)){
				result = null;
			}
		}
    	return result;
    }
    private static String getMyProcessInformUserCondition(String processId){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("/wf:process/wf:extend/wf:inform/wf:user-condition");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null)return null;
    	return node.getText();
    }
    
   /**
    * 得到流程通知主题
    * @param file
    * @return
    */
    public static String getProcessInformSubject(String processId){
    	String result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=processInformSubject"));
    	if(result == null){
			result = getMyProcessInformSubject(processId);
		}else{
			if(DEFAULT_VALUE.equals(result)){
				result = null;
			}
		}
    	return result;
    }
    private static String getMyProcessInformSubject(String processId){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("/wf:process/wf:extend/wf:inform/wf:subject");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null)return null;
    	return node.getText();
    }
    
    /**
     * 得到流程的通知内容
     * @param file
     * @return
     */
    public static String getProcessInformContent(String processId){
    	String result =  (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=processInformContent"));
    	if(result == null){
			result = getMyProcessInformContent(processId);
		}else{
			if(DEFAULT_VALUE.equals(result)){
				result = null;
			}
		}
    	return result;
    }
    private static String getMyProcessInformContent(String processId){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("/wf:process/wf:extend/wf:inform/wf:content");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null)return null;
    	return node.getText();
    }
    
    /**
     * 返回需要通知的用户条件
     * @return
     */
    public static String getNeedInformUserCondition(String processId,String transitionName){
    	String result = null;
    	if(StringUtils.isNotEmpty(transitionName)){
    		result =  (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=needInformUserCondition="+transitionName));
    		if(result == null){
    			result=getMyNeedInformUserCondition(processId,transitionName);
    		}else{
    			if(DEFAULT_VALUE.equals(result)){
    				result = null;
    			}
    		}
    	}
    	return result;
    }
    private static String getMyNeedInformUserCondition(String processId,String transitionName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("//wf:transition[@name='"+transitionName+"']/wf:extend/wf:additional-properties/wf:inform/wf:user-condition");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null)return null;
    	return node.getText();
    }
    
    /**
     * 返回通知的主题
     * @return
     */
    public static String getNeedInformSubject(String processId,String transitionName){
    	String result = null;
    	if(StringUtils.isNotEmpty(transitionName)){
    		result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=needInformSubject="+transitionName));
    		if(result == null){
    			result=getMyNeedInformSubject(processId,transitionName);
    		}else{
    			if(DEFAULT_VALUE.equals(result)){
    				result = null;
    			}
    		}
    	}
    	return result;
    }
    private static String getMyNeedInformSubject(String processId,String transitionName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("//wf:transition[@name='"+transitionName+"']/wf:extend/wf:additional-properties/wf:inform/wf:subject");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null)return null;
    	return node.getText();
    }
    
    /**
     * 返回通知的内容
     * @return
     */
    public static String getNeedInformContent(String processId,String transitionName){
    	String result = null;
    	if(StringUtils.isNotEmpty(transitionName)){
    		result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=needInformContent="+transitionName));
    		if(result == null){
    			result=getMyNeedInformContent(processId,transitionName);
    		}else{
    			if(DEFAULT_VALUE.equals(result)){
    				result = null;
    			}
    		}
    	}
    	return result;
    }
    private static String getMyNeedInformContent(String processId,String transitionName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("//wf:transition[@name='"+transitionName+"']/wf:extend/wf:additional-properties/wf:inform/wf:content");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null)return null;
    	return node.getText();
    }



	public final static String RIGHT_ALLOW = "app.allow";
	public final static String RIGHT_UNALLOW = "app.unallow";
	private final static String RIGHT_TYPE = "type";
	private final static String RIGHT_CONDITION = "condition";
	

	/**
	 * 判断任务名为taskName的任务有没有edit-right节点
	 */
	public static boolean haveEditRight(String processId, String taskName) {
		Boolean result = false;
		if(StringUtils.isNotEmpty(taskName)){
			result = (Boolean)WorkflowMemcachedUtil.get(getHashCode(processId+"=haveEditRight="+taskName));
			if(result==null)result=myHaveEditRight(processId,taskName);
		}
		return result;
	}
	public static boolean myHaveEditRight(String processId, String taskName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:edit-right");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null) return false;
		return true;
	}
	
	/**
	 * 获得第一个任务的名字
	 * @param file
	 * @return
	 */
	public static String getFirstTaskName(String processId){
		String result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=firstTaskName"));
		if(result == null){
			result = getMyFirstTaskName(processId);
		}else{
			if(DEFAULT_VALUE.equals(result)){
				result = null;
			}
		}
		return result;
	}
	private static String getMyFirstTaskName(String processId){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:start/wf:transition");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null) return null;
		return ((Element) node).attributeValue("to");
	}

	/**
	 * 返回所有必填字段，key为字段名，值为条件
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getRequiredFields(String processId,
			String taskName) {
		Map<String, String> result = (Map<String, String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=requiredFields="+taskName));
		if(result == null) result = getMyRequiredFields(processId,taskName);
		return result;
	}
	@SuppressWarnings("unchecked")
	private static Map<String, String> getMyRequiredFields(String processId,
			String taskName) {
		Map<String,String> map = new HashMap<String,String>();
		if(haveEditRight(processId,taskName)){ 
			Document document = DocumentParameterUtils.getDocument(processId);
			String namespaceUrl = document.getRootElement().getNamespace().getURI();
			HashMap<String,String> xmlMap = new HashMap<String,String>();   
			xmlMap.put("wf",namespaceUrl);   
			XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:edit-right/wf:field");   
			x.setNamespaceURIs(xmlMap);
			List<Element> childs = x.selectNodes(document);
			if(childs==null ) throw new RuntimeException("is no 'field' node in edit-right node. " );
			for(Element node: childs){
				if(DefinitionXmlParse.FIELD_CONSTRAINT_RULE_REQUIRED.equalsIgnoreCase(node.element("constraint-rule").getText())){
					map.put(StringUtils.substringBetween(node.element("name").getText(), "[", "]"), node.element("condition").getText());
				}
			}
		}
		return map;
	}
	
	
	/**
	 * 返回所有不可编辑字段，key为字段名，值为条件
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getNonEditableFields(String processId,
			String taskName) {
		Map<String,String> result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (Map<String, String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=nonEditableFields="+taskName));
			if(result == null)result=getMyNonEditableFields(processId,taskName);
		}else{
			result=new HashMap<String,String>();
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, String> getMyNonEditableFields(String processId,
			String taskName) {
		
		Map<String,String> map = new HashMap<String,String>();
		if(haveEditRight(processId,taskName)){
			Document document = DocumentParameterUtils.getDocument(processId);
			String namespaceUrl = document.getRootElement().getNamespace().getURI();
			HashMap<String,String> xmlMap = new HashMap<String,String>();   
			xmlMap.put("wf",namespaceUrl);   
			XPath x = document.createXPath("//wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:edit-right/wf:field");   
			x.setNamespaceURIs(xmlMap);       
			List<Element> childs = x.selectNodes(document);
			if(childs==null ) throw new RuntimeException("is no 'field' node in edit-right node. " );
			for(Element node: childs){
				Element constraint_rule = node.element("constraint-rule");
				if(DefinitionXmlParse.FIELD_CONSTRAINT_RULE_NON_EDITABLE.equalsIgnoreCase(constraint_rule.getText())){
					map.put(StringUtils.substringBetween(node.element("name").getText(), "[", "]"), node.element("condition").getText());
				}
			}
		}
		return map;
	}
	
	/**
	 * 返回所有该环节自动填写域及追加方式
	 * @param file
	 * @param taskName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<AutomaticallyFilledField> getAfterFilledFields(String processId,
			String taskName,String fillTimeMark){
		List<AutomaticallyFilledField> autoFF = new ArrayList<AutomaticallyFilledField>();
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("//wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:automatically-filled-fields/wf:field");   
		x.setNamespaceURIs(xmlMap);       
		List<Element> childs = x.selectNodes(document);
		if(childs==null)return autoFF;
		for(Element node: childs){
			String name = node.element("name").getText();
			String value = node.element("value").getText();
			String separate = "";
			Element separateElement = node.element("separate");
			if(separateElement!=null&&StringUtils.isNotEmpty(separateElement.getText()))  separate = separateElement.getText().substring(1, separateElement.getText().length()-1);
			String fillType = node.element("fill-type").getText();
			String fillTime = node.element("fill-time").getText();
			if(fillTime.equals(fillTimeMark)){
				autoFF.add(new AutomaticallyFilledField(StringUtils.substringBetween(name, "[", "]"),value,fillType,separate)); 
			}
		}
		return autoFF;
	}
	
	/**
	 * 获得任务完成时需要自动填写字段
	 * @param file
	 * @param taskName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<AutomaticallyFilledField> getAfterFilledFields(String processId,
			String taskName){
		List<AutomaticallyFilledField> result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (List<AutomaticallyFilledField>)WorkflowMemcachedUtil.get(getHashCode(processId+"=afterFilledFields="+taskName));
			if(result==null) result = getMyAfterFilledFields(processId,taskName);
		}else{
			result = new ArrayList<AutomaticallyFilledField>();
		}
		return result;
	}
	
	private static List<AutomaticallyFilledField> getMyAfterFilledFields(String processId,
			String taskName){
		return getAfterFilledFields(processId,taskName,"after");
	}
	
	/**
	 * 获得任务执行前需要自动填写的字段
	 * @param file
	 * @param taskName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<AutomaticallyFilledField> getBeforeFilledFields(String processId,
			String taskName){
		List<AutomaticallyFilledField> result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (List<AutomaticallyFilledField>)WorkflowMemcachedUtil.get(getHashCode(processId+"=beforeFilledFields="+taskName));
			if(result==null) result = getMyBeforeFilledFields(processId,taskName);
		}else{
			result = new ArrayList<AutomaticallyFilledField>();
		}
		return result;
	}
	
	private static List<AutomaticallyFilledField> getMyBeforeFilledFields(String processId,
			String taskName){
		return getAfterFilledFields(processId,taskName,"before");
	}
	
	@SuppressWarnings("unchecked")
	public static List<AutomaticallyFilledField> getFlowingFilledFields(String processId,
			String transitionName){
		List<AutomaticallyFilledField> result = null;
		if(StringUtils.isNotEmpty(transitionName)){
			 result = (List<AutomaticallyFilledField>)WorkflowMemcachedUtil.get(getHashCode(processId+"=flowingFilledFields="+transitionName));
			if(result==null) result = getMyFlowingFilledFields(processId,transitionName);
		}else{
			result = new ArrayList<AutomaticallyFilledField>();
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	private static List<AutomaticallyFilledField> getMyFlowingFilledFields(String processId,
			String transitionName){
		List<AutomaticallyFilledField> autoFF = new ArrayList<AutomaticallyFilledField>();
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("//wf:transition[@name='"+transitionName+"']/wf:extend/wf:automatically-filled-fields/wf:field");   
		x.setNamespaceURIs(xmlMap);       
		List<Element> childs = x.selectNodes(document);
		if(childs==null)return autoFF;
		for(Element node: childs){
			String name = node.element("name").getText();
			String value = node.element("value").getText();
			String fillType = node.element("fill-type").getText();
			String separate = "";
			Element separateElement = node.element("separate");
			if(separateElement!=null&&StringUtils.isNotEmpty(separateElement.getText())) separate = separateElement.getText().substring(1, separateElement.getText().length()-1);
			autoFF.add(new AutomaticallyFilledField(StringUtils.substringBetween(name, "[", "]"),value,fillType,separate)); 
		}
		return autoFF;
	}
 
	/**
	 * 返回环节查看会签结果的条件
	 * @param file
	 * @param taskName
	 * @return
	 */
	public static String[] getViewMeetingResultRight(String processId,
			String taskName) {
		String[] result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=viewMeetingResultRight="+taskName));
			if(result == null)result =  getMyViewMeetingResultRight(processId,taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	
	private static String[] getMyViewMeetingResultRight(String processId,
			String taskName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:view-meeting-result-right/wf:condition-set");
		
	}
	
	/**
	 * 返回环节查看投票结果的条件
	 * @param file
	 * @param taskName
	 * @return
	 */
	public static String[] getViewVoteResultRight(String processId,
			String taskName) {
		String[] result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=viewVoteResultRight="+taskName));
			if(result == null)result = getMyViewVoteResultRight(processId,taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	
	private static String[] getMyViewVoteResultRight(String processId,
			String taskName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:view-vote-result-right/wf:condition-set");
		
	}
	
	/**
     * 解析流程定义中流程的流转历史权限
     * ${documentCreator} 
 ${processAdmin} 
 ${allHandleTransactors} 
 ${user} operator.text.et 'zhaoyu[zhaoyu]' 
 ${role} operator.text.et '系统管理员'
  ${workGroup} operator.text.et 'SBU工作组'
  ${documentCreator}
 ${processAdmin} 
 ${allHandleTransactors}
${user} operator.text.et 'zhaoyu[zhaoyu]' 
 ${role} operator.text.et '系统管理员' condition.operator.or ${workGroup} operator.text.et 'SBU工作组'
     * @param xmlFile
     * @return
     */
    public static String getProcessHistoryPermissions(String processId){
    	String result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=processHistoryPermissions"));
    	if(result == null){
			result = getMyProcessHistoryPermissions(processId);
		}else{
			if(DEFAULT_VALUE.equals(result)){
				result = null;
			}
		}
    	return result;
    }
    
    private static String getMyProcessHistoryPermissions(String processId){
    	return  Dom4jUtils.getSingleElementValueByPath(processId, 
		"/process/extend/access-right/view-flow-history/user-condition");
    }
	
	/**
	 * 返回环节办理人查看流转历史的权限
	 * @param file
	 * @param taskName
	 * @return
	 */
	public static String[] getViewFlowHistoryRight(String processId,
			String taskName) {
		String[] result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=viewFlowHistoryRight="+taskName));
			if(result == null)result =  getMyViewFlowHistoryRight(processId, taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	private static String[] getMyViewFlowHistoryRight(String processId,
			String taskName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:view-flow-history-right/wf:condition-set");
	}
	
	
	 /**
     * 返回xml文件中设置的意见查看条件
     */
	public static String[] getViewOpinion(String processId, String taskName) {
		String[] result =  null;
		if(StringUtils.isNotEmpty(taskName)){
			result =   (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=viewOpinion="+taskName));
			if(result == null)result =  getMyViewOpinion(processId,taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	private static String[] getMyViewOpinion(String processId, String taskName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:view-opinion-right/wf:condition-set");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return new String[]{RIGHT_ALLOW,"false"};
		}else{
			Element element = (Element)node;
			String type = element.element(RIGHT_TYPE).getText();
			String condition = element.element(RIGHT_CONDITION).getText();
			return new String[]{type,condition};
		}
	}
	
	/**
     * 返回xml文件中设置的意见编辑条件
     */
	public static String[] getEditOpinion(String processId, String taskName) {
		String[] result =  null;
		if(StringUtils.isNotEmpty(taskName)){
			result =  (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=editOpinion="+taskName));
			if(result == null)result =  getMyEditOpinion(processId, taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	
	private static String[] getMyEditOpinion(String processId, String taskName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:edit-opinion-right/wf:condition-set");
		
	}
	
	/**
     * 返回xml文件中设置的意见必填条件
     */
	public static String[] getMustOpinion(String processId, String taskName) {
		String[] result =  null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=mustOpinion="+taskName));
			if(result == null)result = getMyMustOpinion(processId,taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	
	private static String[] getMyMustOpinion(String processId, String taskName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:must-fill-opinion-right/wf:condition-set");
		
	}
	/**
	 * 表单打印权限
	 * @param file xml文件
	 * @param taskName 环节名
	 * @return String 二维数组  第一个为权限类型，即允许、不允许；第二个为权限条件，级该类型下需要满足的条件
	 */
	public static String[] getPrintFormRight(String processId,
			String taskName) {
		String[] result =  null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=printFormRight="+taskName));
			if(result == null)result = getMyPrintFormRight(processId,taskName) ;
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	private static String[] getMyPrintFormRight(String processId,
			String taskName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:print-form-right/wf:condition-set");
	}
	/**
	 * 返回环节办理人创建正文的条件
	 * @param file
	 * @param taskName
	 * @return   第一个为权限类型，即允许、不允许；第二个为权限条件，级该类型下需要满足的条件
	 */
	public static String[] getOfficialTextCreateCondition(String processId,String taskName){
		String[] result =  null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=officialTextCreateCondition="+taskName));
			if(result == null)result =  getMyOfficialTextCreateCondition(processId,taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	private static String[] getMyOfficialTextCreateCondition(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:create-right");
		
	}
	
	/**
	 * 获得正文模板
	 */
	public static String getOfficialTextTemplate(String processId,String taskName){
		String  result =  null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=officialTextTemplate="+taskName));
			if(result == null){
				result = getMyOfficialTextTemplate(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
	}
	private static String getMyOfficialTextTemplate(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:create-right/wf:offical-template");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}
		return node.getText();
	}
	
	
	/**
	 * 获得正文查看权限的设置
	 * @param file
	 * @param taskName
	 * @return 是否有查看痕迹权限
	 */
	public static boolean getOfficialTextViewSetting(String processId,String taskName){
		Boolean result = false;
		if(StringUtils.isNotEmpty(taskName)){
			result = (Boolean)WorkflowMemcachedUtil.get(getHashCode(processId+"=officialTextViewSetting="+taskName));
			if(result == null)result=getMyOfficialTextViewSetting(processId,taskName);
		}
		return result;
	}
	private static boolean getMyOfficialTextViewSetting(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:view-right/wf:view-trace");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return false;
		}else{
			return new Boolean(node.getText());
		}
	}
	/**
	 * 正文下载权限
	 */
	public static String[] getOfficialTextDownloadSetting(String processId,String taskName){
		String[] result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=officialTextDownloadSetting="+taskName));
			if(result == null)result =  getMyOfficialTextDownloadSetting(processId,taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	
	private static String[] getMyOfficialTextDownloadSetting(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:download-right");
		
	}
	
	/**
	 * 正文打印权限
	 */
	public static String[] getOfficialTextPrintSetting(String processId,String taskName){
		String[] result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=officialTextPrintSetting="+taskName));
			if(result == null)result =  getMyOfficialTextPrintSetting(processId,taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	private static String[] getMyOfficialTextPrintSetting(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:print-right");
		
	}
	
	/**
	 * 获得编辑正文时对痕迹的设置
	 * @param file
	 * @param taskName
	 * @return 长度为4的String数组，第一个为正文编辑类型（即允许或不允许）,第二个为附加条件，第三个为保留痕迹的设置，第四个为显示痕迹的设置
	 */
	
	public static String[] getOfficialTextEditSetting(String processId,String taskName){
		String[] result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=officialTextEditSetting="+taskName));
			if(result == null)result = getMyOfficialTextEditSetting(processId,taskName);
		}else{
			result = new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	private static String[] getMyOfficialTextEditSetting(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		return documentEditableCondition(document, "/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:edit-right");
		
	}
	
	/**
	 * 返回环节办理人删除正文的条件
	 * @param file
	 * @param taskName
	 * @return
	 */
	public static String[] getOfficialTextDeleteCondition(String processId,String taskName){
		String[] result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=officialTextDeleteCondition="+taskName));
			if(result == null)result = getMyOfficialTextDeleteCondition(processId,taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	private static String[] getMyOfficialTextDeleteCondition(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:delete-right");
		
	}
	/**
	 * 返回上传附件的条件
	 */
	public static String[] getAttachmentAddCondition(String processId,String taskName){
		String[] result =  null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=attachmentAddCondition="+taskName));
			if(result == null)result = getMyAttachmentAddCondition(processId,taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	}
	
	private static String[] getMyAttachmentAddCondition(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:attachment-right/wf:add-right");
		
	}
	
	/**
	 * 返回删除附件的条件
	 */
	public static String[] getAttachmentDeleteCondition(String processId,String taskName){
		String[] result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=attachmentDeleteCondition="+taskName));
			if(result == null)result = getMyAttachmentDeleteCondition(processId,taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	} 
	
	private static String[] getMyAttachmentDeleteCondition(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:attachment-right/wf:delete-right");
		
	}
	
	/**
	 * 返回下载附件的条件
	 */
	public static String[] getAttachmentDownloadCondition(String processId,String taskName){
		String[] result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String[])WorkflowMemcachedUtil.get(getHashCode(processId+"=attachmentDownloadCondition="+taskName));
			if(result == null)result = getMyAttachmentDownloadCondition(processId,taskName);
		}else{
			result =  new String[]{RIGHT_ALLOW,"false"};
		}
		return result;
	} 
	
	private static String[] getMyAttachmentDownloadCondition(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		return activityCondition(document, "/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:attachment-right/wf:download-right");
		
	}
	
	/**
	 * 返回删除流程的条件
	 */
	public static String getProcessDeleteCondition(String processId,String taskName){
		String result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=processDeleteCondition="+taskName));
			if(result == null){
				result = getMyProcessDeleteCondition(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
		
	} 
	private static String getMyProcessDeleteCondition(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:delete-right/wf:user-condition");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}
		return node.getText();
	} 
	
	/**
	 * 返回删除流程定义中的所有task的name
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getTaskNames(String processId){
		List<String> result  = (List<String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=taskNames"));
		if(result==null)result = getMyTaskNames(processId);
		return result;
	}
	@SuppressWarnings("unchecked")
	private static List<String> getMyTaskNames(String processId){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("//wf:process/wf:task");   
		x.setNamespaceURIs(xmlMap);
		List<String> taskNames = new ArrayList<String>();
		List<Element> childs = x.selectNodes(document);
		if(childs==null)return taskNames;
		for(Element node: childs){
			String name = node.attributeValue("name");
			taskNames.add(name);
		}
		x = document.createXPath("//wf:process/wf:custom");   
		x.setNamespaceURIs(xmlMap);
		childs = x.selectNodes(document);
		if(childs==null)return taskNames;
		for(Element node: childs){
			//子流程环节
			String name = node.attributeValue("name");
			if(isSubProcessTask(processId,name)){
				taskNames.add(name);
			}
		}
		return taskNames;
	}
	
	/**
	 * 返回删除流程定义中的所有办理人不为“字段中指定人员”的task的name（环节跳转时修改的）
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getUnFieldTaskNames(String processId){
		List<String> result = (List<String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=unFieldTaskNames"));
		if(result==null)result = getMyUnFieldTaskNames(processId);
		return result;
	}
	@SuppressWarnings("unchecked")
	private static List<String> getMyUnFieldTaskNames(String processId){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("//wf:process/wf:task");   
		x.setNamespaceURIs(xmlMap);
		List<String> taskNames = new ArrayList<String>();
		List<Element> childs = x.selectNodes(document);
		if(childs==null)return taskNames;
		for(Element node: childs){
			String name = node.attributeValue("name");
			String userCondition = Dom4jUtils.getSingleElementValueByPath(processId, 
					"/process/task[@name='" + name+ 
			"']/extend/transactor-settings/user-condition");
			if(StringUtils.isNotEmpty(userCondition)&&!userCondition.startsWith("${field[")){
				taskNames.add(name);
			}
		}
		x = document.createXPath("//wf:process/wf:custom");   
		x.setNamespaceURIs(xmlMap);
		childs = x.selectNodes(document);
		if(childs==null)return taskNames;
		for(Element node: childs){
			//子流程环节
			String name = node.attributeValue("name");
			String userCondition = Dom4jUtils.getSingleElementValueByPath(processId, 
					"/process/custom[@name='" + name+ 
			"']/extend/transactor-settings/user-condition");
			if(isSubProcessTask(processId,name)){
				if(StringUtils.isEmpty(userCondition)){
					taskNames.add(name);
				}else{
					if(!userCondition.startsWith("${field[")){
						taskNames.add(name);
					}
				}
			}
		}
		return taskNames;
	}
	
	/**
	 * 返回删除流程定义中的所有办理人不为“字段中指定人员”的task的name（环节跳转时修改的）
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getFieldTaskNames(String processId){
		List<String> result = (List<String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=fieldTaskNames"));
		if(result==null)result = getMyFieldTaskNames(processId);
		return result;
	}
	@SuppressWarnings("unchecked")
	private static List<String> getMyFieldTaskNames(String processId){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("//wf:process/wf:task");   
		x.setNamespaceURIs(xmlMap);
		List<String> taskNames = new ArrayList<String>();
		List<Element> childs = x.selectNodes(document);
		if(childs==null)return taskNames;
		for(Element node: childs){
			String name = node.attributeValue("name");
			String userCondition = Dom4jUtils.getSingleElementValueByPath(processId, 
					"/process/task[@name='" + name+ 
			"']/extend/transactor-settings/user-condition");
			if(StringUtils.isNotEmpty(userCondition)&&userCondition.startsWith("${field[")){
				taskNames.add(name);
			}
		}
		x = document.createXPath("//wf:process/wf:custom");   
		x.setNamespaceURIs(xmlMap);
		childs = x.selectNodes(document);
		if(childs==null)return taskNames;
		for(Element node: childs){
			//子流程环节
			String name = node.attributeValue("name");
			String userCondition = Dom4jUtils.getSingleElementValueByPath(processId, 
					"/process/custom[@name='" + name+ 
			"']/extend/transactor-settings/user-condition");
			if(isSubProcessTask(processId,name)&&StringUtils.isNotEmpty(userCondition)&&userCondition.startsWith("${field[")){
				taskNames.add(name);
			}
		}
		return taskNames;
	}
	
	
	/**
	 * 判断两个定义文件是否共用一个表单
	 * @param parentFile
	 * @param file
	 * @return
	 */
	public static boolean isSharedForm(String parentProcessId, String processId) {
		String formNameKey = "form-name";
		String formVersionKey = "form-version";
		Map<String, String> parentMap = getWorkFlowBaseInfo(parentProcessId);
		Map<String, String> map = getWorkFlowBaseInfo(processId);
		return parentMap.get(formNameKey).equals(map.get(formNameKey))&& parentMap.get(formVersionKey).equals(map.get(formVersionKey));
	} 
	
	/**
	 * 获得主流程定义中main-to-sub
	 * @param file
	 * @param subProcessName
	 * @return key为mainfield ,value为subfield
	 */
	@SuppressWarnings("unchecked")
	public static Map<String ,String> getMainToSub(String processId,String subProcessName){
		Map<String ,String> result=  null;
		if(StringUtils.isNotEmpty(subProcessName)){
			result= (Map<String ,String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=mainToSub="+subProcessName));
			if(result!=null){
				return result;
			}else{
				return getMyMainToSub(processId,subProcessName);
			}
		}else{
			result = new HashMap<String,String>();
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	private static Map<String ,String> getMyMainToSub(String processId,String subProcessName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:custom[@name='"+subProcessName+"']/wf:extend/wf:main-to-sub");   
		x.setNamespaceURIs(xmlMap);
		List<Node> nodes = x.selectNodes(document);
		Map<String ,String> map = new HashMap<String,String>();
		if(nodes==null)return map;
		for(Node node:nodes){
			Element e = (Element)node;
			String mainfield = e.attributeValue("mainfield");
			String subfield = e.attributeValue("subfield");
			map.put(StringUtils.substringBetween(mainfield, "[", "]"), StringUtils.substringBetween(subfield, "[", "]"));
		}
		return map;
	}
	
	/**
	 * 获得主流程定义中main-to-sub
	 * @param file
	 * @param subProcessName
	 * @return key为subfield ,value为mainfield
	 */
	@SuppressWarnings("unchecked")
	public static Map<String ,String> getSubToMain(String processId,String subProcessName){
		Map<String ,String> result=  null;
		if(StringUtils.isNotEmpty(subProcessName)){
			result= (Map<String ,String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=subToMain="+subProcessName));
			if(result!=null){
				return result;
			}else{
				return getMySubToMain(processId,subProcessName);
			}
		}else{
			result = new HashMap<String,String>();
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	private static Map<String ,String> getMySubToMain(String processId,String subProcessName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:custom[@name='"+subProcessName+"']/wf:extend/wf:sub-to-main");   
		x.setNamespaceURIs(xmlMap);
		List<Node> nodes = x.selectNodes(document);
		Map<String ,String> map = new HashMap<String,String>();
		if(nodes==null)return map;
		for(Node node:nodes){
			Element e = (Element)node;
			String mainfield = e.attributeValue("mainfield");
			String subfield = e.attributeValue("subfield");
			map.put(StringUtils.substringBetween(subfield, "[", "]"), StringUtils.substringBetween(mainfield, "[", "]"));
		}
		return map;
	}
	
	/**
	 * <before-submit>
				<class-name></class-name>
				<result-message></result-message>
			</before-submit>
	 * @param file
	 * @param taskName
	 * @return
	 */
	public static String getBeforeTaskSubmitImpClassName(String processId,String taskName){
		String result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=beforeTaskSubmitImpClassName="+taskName));
			if(result == null){
				result = getMyBeforeTaskSubmitImpClassName(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
	}
	private static String getMyBeforeTaskSubmitImpClassName(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:before-submit/wf:class-name");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}
		return node.getText();
	}
	
	
	/**
	 * <before-submit>
			<excute-url>
            	<url>url</url>
            	<condition>条件</condition>
        	</excute-url>
		</before-submit>
	 * @param file
	 * @param taskName
	 * @return url
	 */
	@SuppressWarnings("unchecked")
	public static List<String[]> getBeforeTaskSubmitUrl(String processId,String taskName){
		List<String[]> result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (List<String[]>)WorkflowMemcachedUtil.get(getHashCode(processId+"=beforeTaskSubmitUrl="+taskName));
			if(result==null)result = getMyBeforeTaskSubmitUrl(processId,taskName);
		}else{
			result = new ArrayList<String[]>();
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	private static List<String[]> getMyBeforeTaskSubmitUrl(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:before-submit/wf:urls/wf:excute-url");   
		x.setNamespaceURIs(xmlMap);
		List<Element> nodes = x.selectNodes(document);
		List<String[]> resultList = new ArrayList<String[]>();
		if(nodes==null){
			return null;
		}else{
			for(Element node:nodes){
				resultList.add(new String[]{node.element("condition").getText(),node.element("url").getText()});
			}
		}
		return resultList;
	}
	
	
	/**
	 * 返回子流程开始时，执行的实体beanName
	 * @param file
	 * @param name
	 * @return
	 */
	public static String getSubProcessBeginning(String processId,String name){
		String result = null;
		if(StringUtils.isNotEmpty(name)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=subProcessBeginning="+name));
			if(result == null){
				result = getMySubProcessBeginning(processId,name);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
	} 
	private static String getMySubProcessBeginning(String processId,String name){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:custom[@name='"+name+"']/wf:extend/wf:sub-process-beginning");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}
		return node.getText();
	} 
	
	/**
	 * 返回子流程开始前，执行的实体beanName
	 * @param file
	 * @param name
	 * @return
	 */
	public static String getBeforeStartSubProcess(String processId,String name){
		String result = null;
		if(StringUtils.isNotEmpty(name)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=beforeStartSubProcess="+name));
			if(result == null){
				result = getMyBeforeStartSubProcess(processId,name);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
	} 
	private static String getMyBeforeStartSubProcess(String processId,String name){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:custom[@name='"+name+"']/wf:extend/wf:before-start-sub-process");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}
		return node.getText();
	} 
	
	/**
	 * 返回子流程结束后，执行的实体beanName
	 * @param file
	 * @param name
	 * @return
	 */
	public static String getSubProcessEnd(String processId,String name){
		String result = null;
		if(StringUtils.isNotEmpty(name)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=subProcessEnd="+name));
			if(result == null){
				result = getMySubProcessEnd(processId,name);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
	}
	private static String getMySubProcessEnd(String processId,String name){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:custom[@name='"+name+"']/wf:extend/wf:sub-process-end");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}
		return node.getText();
	}
	public static String getBeforeTaskSubmitResultMessage(String processId,String taskName){
		String result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=beforeTaskSubmitResultMessage="+taskName));
			if(result == null){
				result = getMyBeforeTaskSubmitResultMessage(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
	}
	private static String getMyBeforeTaskSubmitResultMessage(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:before-submit/wf:result-message");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}
		return node.getText();
	}
	
	public static boolean haveBeforeSubmit(String processId,String taskName){
		Boolean result = false;
		if(StringUtils.isNotEmpty(taskName)){
			result = (Boolean)WorkflowMemcachedUtil.get(getHashCode(processId+"=haveBeforeSubmit="+taskName));
			if(result==null)result=myHaveBeforeSubmit(processId, taskName);
		}
		return result;
	}
	private static boolean myHaveBeforeSubmit(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:before-submit");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 查询子流程的定义ID
	 * @param definitionFile
	 * @param activityName
	 * @return
	 */
	public static String getSubDefinitionId(String processId,
			String activityName) {
		String result = null;
		if(StringUtils.isNotEmpty(activityName)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=subDefinitionId="+activityName));
			if(result == null){
				result = getMySubDefinitionId(processId,activityName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
	}
	private static String getMySubDefinitionId(String processId,
			String activityName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:custom[@name='"+activityName+"']/wf:extend/wf:sub-process-id");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}else{
		}
		return node.getText();
	}

	/**
	 * 获得子流程的流向
	 * @param definitionFile
	 * @param activityName
	 * @return
	 */
	public static String getSubProcessTransition(String processId,
			String activityName) {
		String result = null;
		if(StringUtils.isNotEmpty(activityName)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=subProcessTransition="+activityName));
			if(result == null){
				result = getMySubProcessTransition(processId,activityName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
	}
	private static String getMySubProcessTransition(String processId,
			String activityName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:custom[@name='"+activityName+"']/wf:transition");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}else{
			return ((Element)node).attributeValue("name");
		}
	}
	
	/**
	 * 返回是否拥有特事特办
	 * @param file
	 * @param taskName
	 * @return
	 */
	public static boolean isHaveSpecialTask(String processId,String taskName){
		Boolean result = false;
		if(StringUtils.isNotEmpty(taskName)){
			result = (Boolean)WorkflowMemcachedUtil.get(getHashCode(processId+"=isHaveSpecialTask="+taskName));
			if(result == null)result=isMyHaveSpecialTask(processId,taskName);
		}
		return result;
	}
	private static boolean isMyHaveSpecialTask(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:special-transition");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 获得标准实体删除功能的bean设置
	 * @param file
	 * @return
	 */
	public static String getFormFlowableDeleteBeanName(String processId) {
		String result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=formFlowableDeleteBeanName"));
		if(result == null){
			result = getMyFormFlowableDeleteBeanName(processId);
		}else{
			if(DEFAULT_VALUE.equals(result)){
				result = null;
			}
		}
		return result;
	}
	private static String getMyFormFlowableDeleteBeanName(String processId) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:extend/wf:parameter-setting/wf:delete-instance-bean");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}else{
			return node.getText();
		}
	}
	
	/**
	 * 获得特事特办流向的to属性
	 * @param file
	 * @param taskName
	 * @return
	 */
	public static String getSpecialTaskProperties(String processId,String taskName){
		String result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=specialTaskProperties="+taskName));
			if(result == null){
				result = getMySpecialTaskProperties(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
	}
	private static String getMySpecialTaskProperties(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:special-transition");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}else{
			Element specialTask = ((Element)node);
			return specialTask.attributeValue(TO);
		}
	}
	
	/**
	 * 获得特事特办流向的标题
	 * @param file
	 * @param taskName
	 * @return
	 */
	public static String getSpecialTaskTitle(String processId,String taskName){
		String result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result =  (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=specialTaskTitle="+taskName));
			if(result == null){
				result = getMySpecialTaskTitle(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
	}
	private static String getMySpecialTaskTitle(String processId,String taskName){
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:special-transition/wf:task-name");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node==null){
			return null;
		}else{
			return node.getText();
		}
	}
	
	/**
	 * 获得环节催办设置
	 * @param file
	 * @param taskName
	 * @return 封装了催办相关的设置map
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String>  getReminderSetting(String processId,String taskName){
		Map<String,String> result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (Map<String,String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=reminderSetting="+taskName));
			if(result==null)result = getMyReminderSetting(processId,taskName);
		}else{
			result = new HashMap<String,String>();
		}
		return result;
	}
	private static Map<String,String>  getMyReminderSetting(String processId,String taskName){
		Map<String,String> reminderSetting = new HashMap<String,String>();
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:reminder");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node!=null){
			Element element = (Element)node;
			reminderSetting.put(REMIND_STYLE, element.elementText(REMIND_STYLE));
			reminderSetting.put(REMIND_DUEDATE, element.attributeValue(REMIND_DUEDATE));
			reminderSetting.put(REMIND_REPEAT, element.attributeValue(REMIND_REPEAT));
			reminderSetting.put(REMIND_TIME, element.elementText(REMIND_TIME));
			reminderSetting.put(REMIND_NOTICE_TYPE,element.elementText(REMIND_NOTICE_TYPE));
			reminderSetting.put(REMIND_NOTICE_USER_CONDITION,element.elementText(REMIND_NOTICE_USER_CONDITION));
		}
		return reminderSetting;
	}
	
	/**
	 * 获得流程催办设置
	 * @param file
	 * @param taskName
	 * @return 封装了催办相关的设置map
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String>  getReminderSetting(String processId){
		Map<String,String> result = (Map<String,String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=reminderSetting"));
		if(result==null)result = getMyReminderSetting(processId);
		return result;
	}
	private static Map<String,String>  getMyReminderSetting(String processId){
		Map<String,String> reminderSetting = new HashMap<String,String>();
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:extend/wf:reminder");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node!=null){
			Element element = (Element)node;
			reminderSetting.put(REMIND_STYLE, element.elementText(REMIND_STYLE));
			reminderSetting.put(REMIND_DUEDATE, element.attributeValue(REMIND_DUEDATE));
			reminderSetting.put(REMIND_REPEAT, element.attributeValue(REMIND_REPEAT));
			reminderSetting.put(REMIND_TIME, element.elementText(REMIND_TIME));
			reminderSetting.put(REMIND_NOTICE_TYPE,element.elementText(REMIND_NOTICE_TYPE));
			reminderSetting.put(REMIND_NOTICE_USER_CONDITION,element.elementText(REMIND_NOTICE_USER_CONDITION));
		}
		return reminderSetting;
	}
	
	/**
	 * 获得流程参数设置
	 * @param file 流动定义文件
	 * @return 封装了参数设置的map
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String>  getParameterSetting(String processId){
		Map<String, String> result =  (Map<String,String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=parameterSetting"));
		if(result==null)result = getMyParameterSetting(processId);
		return result;
	}
	private static Map<String,String>  getMyParameterSetting(String processId){
		String doTaskUrl = Dom4jUtils.getSingleElementValueByPath(processId, 
		"/process/extend/parameter-setting/do-task-url/url");
		String doTaskUrlParameterName = Dom4jUtils.getSingleElementValueByPath(processId, 
		"/process/extend/parameter-setting/do-task-url/parameter-name");
		String formViewUrl = Dom4jUtils.getSingleElementValueByPath(processId, 
		"/process/extend/parameter-setting/form-view-url/url");
		String formViewUrlParameterName = Dom4jUtils.getSingleElementValueByPath(processId, 
		"/process/extend/parameter-setting/form-view-url/parameter-name");
		String processStartUrl = Dom4jUtils.getSingleElementValueByPath(processId, 
		"/process/extend/parameter-setting/process-start-url/url");
		String processStartUrlParameterName = Dom4jUtils.getSingleElementValueByPath(processId, 
		"/process/extend/parameter-setting/process-start-url/parameter-name");
		String processStartUrlParameterValue = Dom4jUtils.getSingleElementValueByPath(processId, 
		"/process/extend/parameter-setting/process-start-url/parameter-value");
		String urgenUrl = Dom4jUtils.getSingleElementValueByPath(processId, 
		"/process/extend/parameter-setting/process-urgen-url/url");
		String urgenUrlParameterName = Dom4jUtils.getSingleElementValueByPath(processId, 
		"/process/extend/parameter-setting/process-urgen-url/parameter-name");
		
		Map<String, String> result = new HashMap<String, String>();
		result.put(DO_TASK_URL, doTaskUrl);
		result.put(DO_TASK_URL_PARAMETER_NAME, doTaskUrlParameterName);
		result.put(FORM_VIEW_URL, formViewUrl);
		result.put(FORM_VIEW_URL_PARAMETER_NAME, formViewUrlParameterName);
		result.put(URGEN_URL, urgenUrl);
		result.put(URGEN_URL_PARAMETER_NAME, urgenUrlParameterName);
		result.put(PROCESS_START_URL, processStartUrl);
		result.put(PROCESS_START_URL_PARAMETER_NAME, processStartUrlParameterName);
		result.put(PROCESS_START_URL_PARAMETER_VALUE, processStartUrlParameterValue);
		return result;
	}
	
	public static String getCurrentTacheType(String processId, String taskName) {
		String result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=currentTacheType="+taskName));
			if(result == null){
				result = getMyCurrentTacheType(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
		}
		return result;
	}
	private static String getMyCurrentTacheType(String processId, String taskName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/*[@name='"+taskName+"']/wf:extend/wf:tache-type");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node!=null){
			return node.getText();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getChoiceTaches(String processId, String taskName) {
		Map<String,String> result = null;
		if(StringUtils.isNotEmpty(taskName)){
			result = (Map<String, String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=choiceTaches="+taskName));
			if(result==null)result = getMyChoiceTaches(processId, taskName);
		}else{
			result = new HashMap<String,String>();
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	private static Map<String, String> getMyChoiceTaches(String processId, String taskName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:state[@name='"+taskName+"']/wf:transition");   
		x.setNamespaceURIs(xmlMap);
		List<Element> nodes = x.selectNodes(document) ;
		Map<String,String> result = new HashMap<String,String>();
		if(nodes==null)return result;
		for(Element element : nodes){
			result.put(element.attributeValue("name"), element.attributeValue("to"));
		}
		return result;
	}
//	public static void main(String[] args){
//		String file = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//				"<process name=\"选择环节测试\" xmlns=\"http://jbpm.org/4.0/jpdl\">" +
//					"<task></task>" +
//					"<state name=\"测试\">" +
//						"<extend>" +
//							"<tache-type>dddd" +
//							"</tache-type>" +
//						"</extend>" +
//					"</state>" +
//				"</process>";
//		getCurrentTacheType(file,"测试");
//	}
	
	 /**
     * 获得流向"是否使用目标任务的原办理人"
     * @param file
     * @param transitionName
     * @return
     */
    public static String getTransitionOriginalUser(String processId,String transitionName){
    	String result = null;
    	if(StringUtils.isNotEmpty(transitionName)){
    		result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=transitionOriginalUser="+transitionName));
    		if(result == null){
    			result=getMyTransitionOriginalUser(processId,transitionName);
    		}else{
    			if(DEFAULT_VALUE.equals(result)){
    				result = null;
    			}
    		}
    	}
    	return result;
    }
    private static String getMyTransitionOriginalUser(String processId,String transitionName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("//wf:transition[@name='"+transitionName+"']/wf:extend/wf:basic-properties/wf:is-original-user");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null){
    		return null;
    	}
    	//如果没有设置通知方式,默认为"邮件方式"
    	if("".equals(node.getText())){
    		return "false";
    	}
    	return node.getText();
    }
    
    public static boolean isSubProcessTask(String processId,String taskName){
    	Boolean result = false;
    	if(StringUtils.isNotEmpty(taskName)){
    		result = (Boolean)WorkflowMemcachedUtil.get(getHashCode(processId+"=isSubProcessTask="+taskName));
    		if(result == null)result = isMySubProcessTask(processId,taskName);
    	}
    	return result;
    }
    private static boolean isMySubProcessTask(String processId,String taskName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("/wf:process/wf:custom[@name='"+taskName+"']/wf:extend/wf:sub-process-id");   
    	x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    
    /**
	 * 获得流程参数设置
	 * @param file 流动定义文件
	 * @return 封装了参数设置的map
	 */
    @SuppressWarnings("unchecked")
	public static Map<String,String>  getButtonNameByProcessMode(String processId,String taskName,TaskProcessingMode processModel){
    	Map<String, String> result = null;
    	if(StringUtils.isNotEmpty(taskName)){
    		result = (Map<String, String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=buttonNameByProcessMode="+taskName+"="+processModel));
    		if(result==null) result = getMyButtonNameByProcessMode(processId,taskName,processModel);
    	}else{
    		result = new HashMap<String,String>();
    	}
    	return result;
	}
	private static Map<String,String>  getMyButtonNameByProcessMode(String processId,String taskName,TaskProcessingMode processModel){
		Map<String,String> buttonNameSetting = new HashMap<String,String>();
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='"+taskName+"']/wf:extend/wf:basic-properties/wf:processing-mode");   
		x.setNamespaceURIs(xmlMap);
		Node node = x.selectSingleNode(document);
		if(node!=null){
			Element element = (Element)node;
			if(TaskProcessingMode.TYPE_EDIT.equals(processModel)){
				buttonNameSetting.put(SUBMIT_NAME, element.attributeValue(SUBMIT_NAME));
			}else if(TaskProcessingMode.TYPE_APPROVAL.equals(processModel)){
				buttonNameSetting.put(APPROVE_NAME, element.attributeValue(APPROVE_NAME));
				buttonNameSetting.put(REFUSE_NAME, element.attributeValue(REFUSE_NAME));
			}else if(TaskProcessingMode.TYPE_COUNTERSIGNATURE.equals(processModel)){
				buttonNameSetting.put(APPROVE_NAME, element.attributeValue(APPROVE_NAME));
				buttonNameSetting.put(REFUSE_NAME, element.attributeValue(REFUSE_NAME));
				buttonNameSetting.put(ADD_COUNTER_NAME, element.attributeValue(ADD_COUNTER_NAME));
				buttonNameSetting.put(DEL_COUNTER_NAME, element.attributeValue(DEL_COUNTER_NAME));
			}else if(TaskProcessingMode.TYPE_SIGNOFF.equals(processModel)){
				buttonNameSetting.put(SIGNOFF_NAME, element.attributeValue(SIGNOFF_NAME));
			}else if(TaskProcessingMode.TYPE_VOTE.equals(processModel)){
				buttonNameSetting.put(AGREEMENT_NAME, element.attributeValue(AGREEMENT_NAME));
				buttonNameSetting.put(OPPOSE_NAME, element.attributeValue(OPPOSE_NAME));
				buttonNameSetting.put(KIKEN_NAME, element.attributeValue(KIKEN_NAME));
			}else if(TaskProcessingMode.TYPE_ASSIGN.equals(processModel)){
				buttonNameSetting.put(ASSIGN_NAME, element.attributeValue(ASSIGN_NAME));
			}else if(TaskProcessingMode.TYPE_DISTRIBUTE.equals(processModel)){
				buttonNameSetting.put(SUBMIT_NAME, element.attributeValue(SUBMIT_NAME));
			}
		}
		return buttonNameSetting;
	}
	
	/**
     * 解析任务预设的显示标题
     * @param xmlFile
     * @param taskName
     * @return
     */
    public static String getTacheCode(String processId, String taskName){
    	String result = null; 
    	if(StringUtils.isNotEmpty(taskName)){
    		result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=tacheCode="+taskName));
    		if(result == null){
				result = getMyTacheCode(processId,taskName);
			}else{
				if(DEFAULT_VALUE.equals(result)){
					result = null;
				}
			}
    	}
    	return result;
    }
    private static String getMyTacheCode(String processId, String taskName){
    	Document document = DocumentParameterUtils.getDocument(processId);
    	String namespaceUrl = document.getRootElement().getNamespace().getURI();
    	HashMap<String,String> xmlMap = new HashMap<String,String>();   
    	xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("/wf:process/wf:task[@name='" + taskName+ 
    	"']/wf:extend/wf:basic-properties/wf:task-code");   
    	x.setNamespaceURIs(xmlMap);  
    	Node node = x.selectSingleNode(document); 
    	if(node==null)return null;
    	return node.getText();
    }
    
    /**
	 * 返回环节办理人创建正文的条件
	 * @param file
	 * @param taskName
	 * @return   第一个为权限类型，即允许、不允许；第二个为权限条件，级该类型下需要满足的条件
	 */
    @SuppressWarnings("unchecked")
	public static Map<String,String[]> getActivityPermissionCondition(String processId,String taskName){
		Map<String,String[]> result= null;
		if(StringUtils.isNotEmpty(taskName)){
			result =(Map<String,String[]>)WorkflowMemcachedUtil.get(getHashCode(processId+"=activityPermissionCondition="+taskName));
			if(result==null)result=getMyActivityPermissionCondition(processId,taskName);
		}else{
			result=new HashMap<String, String[]>();
		}
		return result;
	}
	private static Map<String,String[]> getMyActivityPermissionCondition(String processId,String taskName){
		Map<String,String[]> permissionConditions=new HashMap<String, String[]>();
		Document document = DocumentParameterUtils.getDocument(processId);
 		//查看会签结果
    	permissionConditions.put("countersignResultVisible", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:view-meeting-result-right/wf:condition-set"));
    	//查看投票结果
    	permissionConditions.put("voteResultVisible", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:view-vote-result-right/wf:condition-set"));
    	//编辑意见
    	permissionConditions.put("opinionEditable", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:edit-opinion-right/wf:condition-set"));
    	//意见必填
    	permissionConditions.put("opinionRequired", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:must-fill-opinion-right/wf:condition-set"));
    	//查看流转历史
    	permissionConditions.put("historyVisible", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:view-flow-history-right/wf:condition-set"));
    	//表单打印
    	permissionConditions.put("formPrintable", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:print-form-right/wf:condition-set"));
    	//创建正文
    	permissionConditions.put("documentCreateable", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:create-right"));
    	//删除正文
    	permissionConditions.put("documentDeletable", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:delete-right"));
    	//编辑正文
    	permissionConditions.put("documentEditable", documentEditableCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:edit-right"));
    	//打印正文
    	permissionConditions.put("documentPrintable", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:print-right"));
    	//下载正文
    	permissionConditions.put("documentDownloadable", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:official-text-right/wf:download-right"));
    	//创建附件
    	permissionConditions.put("attachmentCreateable", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:attachment-right/wf:add-right"));
    	//删除附件
    	permissionConditions.put("attachmentDeletable", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:attachment-right/wf:delete-right"));
    	//下载附件
    	permissionConditions.put("attachmentDownloadable", activityCondition(document,"/wf:process/wf:*[@name='"+taskName+"']/wf:extend/wf:right-settings/wf:attachment-right/wf:download-right"));
    	return permissionConditions;
	}
	
	private static String[] activityCondition(Document document,String xpath){
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
 		HashMap<String,String> xmlMap = new HashMap<String,String>();   
 		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath(xpath); 
 		x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null){
    		return  new String[]{RIGHT_ALLOW,"false"};
    	}else{
    		Element element = (Element)node;
    		String type = element.element(RIGHT_TYPE).getText();
    		String condition = element.element(RIGHT_CONDITION).getText();
    		return new String[]{type,condition};
    	}
	}
	
	private static String[] documentEditableCondition(Document document,String xpath){
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
 		HashMap<String,String> xmlMap = new HashMap<String,String>();   
 		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath(xpath); 
 		x.setNamespaceURIs(xmlMap);
    	Node node = x.selectSingleNode(document);
    	if(node==null){
    		return new String[]{RIGHT_ALLOW,"false","false","false"};
    	}else{
    		Element element = (Element)node;
    		String type = element.element(RIGHT_TYPE).getText();
    		String condition = element.element(RIGHT_CONDITION).getText();
    		String retainTrace = element.element("retain-trace").getText();
    		String viewTrace = element.element("view-trace").getText();
    		return new String[]{type,condition,retainTrace,viewTrace};
    	}
	}
	
	/**
	 * 获得业务补偿/流程监控删除实例的设置
	 * @param file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String> getMonitorDeleteInstanceSet(String processId) {
		Map<String,String> result = (Map<String,String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=monitorDeleteInstanceSet"));
		if(result==null)result=getMyMonitorDeleteInstanceSet(processId);
		return result;
	}
	private static Map<String,String> getMyMonitorDeleteInstanceSet(String processId) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:extend/wf:expiation-setting/wf:set-type");
		x.setNamespaceURIs(xmlMap);  
		Node node = x.selectSingleNode(document);
		String setType = node==null?"":node.getText();
		x = document.createXPath("/wf:process/wf:extend/wf:expiation-setting/wf:delete-instance");   
		x.setNamespaceURIs(xmlMap);  
		node = x.selectSingleNode(document);
		String deleteInstanceMonitor = node==null?"":node.getText();
		Map<String,String> map=new HashMap<String, String>();
		map.put(SET_TYPE, setType);
		map.put(DELETE_INSTANCE_MONITOR, deleteInstanceMonitor);
		return map;
	}
	/**
	 * 获得业务补偿/流程监控取消实例的设置
	 * @param file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String> getMonitorCancelInstancSet(String processId) {
		Map<String,String> result = (Map<String,String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=monitorCancelInstancSet"));
		if(result==null)result=getMyMonitorCancelInstancSet(processId);
		return result;
	}
	private static Map<String,String> getMyMonitorCancelInstancSet(String processId) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:extend/wf:expiation-setting/wf:set-type");
		x.setNamespaceURIs(xmlMap);  
		Node node = x.selectSingleNode(document);
		String setType = node==null?"":node.getText();
		x = document.createXPath("/wf:process/wf:extend/wf:expiation-setting/wf:cancel-instance");   
		x.setNamespaceURIs(xmlMap);  
		node = x.selectSingleNode(document);
		String cancelInstanceMonitor = node==null?"":node.getText();
		Map<String,String> map=new HashMap<String, String>();
		map.put(SET_TYPE, setType);
		map.put(CANCEL_INSTANCE_MONITOR, cancelInstanceMonitor);
		return map;
	}
	/**
	 * 获得业务补偿/流程监控环节跳转的设置
	 * @param file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String> getMonitorTaskJumpSet(String processId) {
		Map<String,String> result = (Map<String,String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=monitorTaskJumpSet"));
		if(result==null)result=getMyMonitorTaskJumpSet(processId);
		return result;
	}
	private static Map<String,String> getMyMonitorTaskJumpSet(String processId) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:extend/wf:expiation-setting/wf:set-type");
		x.setNamespaceURIs(xmlMap);  
		Node node = x.selectSingleNode(document);
		String setType = node==null?"":node.getText();
		x = document.createXPath("/wf:process/wf:extend/wf:expiation-setting/wf:task-jump");   
		x.setNamespaceURIs(xmlMap);  
		node = x.selectSingleNode(document);
		String taskJumpMonitor = node==null?"":node.getText();
		Map<String,String> map=new HashMap<String, String>();
		map.put(SET_TYPE, setType);
		map.put(TASK_JUMP_MONITOR, taskJumpMonitor);
		return map;
	}
	
	/**
	 * 获得业务补偿/流程监控暂停实例的设置
	 * @param file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String> getPauseInstancSet(String processId) {
		Map<String,String> result = (Map<String,String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=pauseInstancSet"));
		if(result==null)result=getMyPauseInstancSet(processId);
		return result;
	}
	private static Map<String,String> getMyPauseInstancSet(String processId) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:extend/wf:expiation-setting/wf:set-type");
		x.setNamespaceURIs(xmlMap);  
		Node node = x.selectSingleNode(document);
		String setType = node==null?"":node.getText();
		x = document.createXPath("/wf:process/wf:extend/wf:expiation-setting/wf:pause-instance");   
		x.setNamespaceURIs(xmlMap);  
		node = x.selectSingleNode(document);
		String pauseInstanceMonitor = node==null?"":node.getText();
		Map<String,String> map=new HashMap<String, String>();
		map.put(SET_TYPE, setType);
		map.put(PAUSE_INSTANCE_MONITOR, pauseInstanceMonitor);
		return map;
	}
	
	/**
	 * 获得业务补偿/流程监控继续实例的设置
	 * @param file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String> getContinueInstancSet(String processId) {
		Map<String,String> result = (Map<String,String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=continueInstancSet"));
		if(result==null)result=getMyContinueInstancSet(processId);
		return result;
	}
	private static Map<String,String> getMyContinueInstancSet(String processId) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:extend/wf:expiation-setting/wf:set-type");
		x.setNamespaceURIs(xmlMap);  
		Node node = x.selectSingleNode(document);
		String setType = node==null?"":node.getText();
		x = document.createXPath("/wf:process/wf:extend/wf:expiation-setting/wf:continue-instance");   
		x.setNamespaceURIs(xmlMap);  
		node = x.selectSingleNode(document);
		String continueInstanceMonitor = node==null?"":node.getText();
		Map<String,String> map=new HashMap<String, String>();
		map.put(SET_TYPE, setType);
		map.put(CONTINUE_INSTANCE_MONITOR, continueInstanceMonitor);
		return map;
	}
	
	/**
	 * 获得业务补偿/取回任务的设置
	 * @param file
	 * @return
	 */
	public static String getRetrieveTaskSet(String processId) {
		String result = (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=retrieveTaskSet"));
		if(result == null){
			result = getMyRetrieveTaskSet(processId);
		}else{
			if(DEFAULT_VALUE.equals(result)){
				result = null;
			}
		}
		return result;
	}
	private static String getMyRetrieveTaskSet(String processId) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:extend/wf:parameter-setting/wf:retrieve-task");   
		x.setNamespaceURIs(xmlMap);  
		Node node = x.selectSingleNode(document);
		String retrieveTask = node==null?"":node.getText();
		return retrieveTask;
	}
	
	/**
	 * 获得业务补偿/流程正常结束的设置
	 * @param file
	 * @return
	 */
	public static String getEndInstanceBean(String processId) {
		String result =  (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=endInstanceBean"));
		if(result == null){
			result = getMyEndInstanceBean(processId);
		}else{
			if(DEFAULT_VALUE.equals(result)){
				result = null;
			}
		}
		return result;
	}
	private static String getMyEndInstanceBean(String processId) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:extend/wf:parameter-setting/wf:end-instance-bean");   
		x.setNamespaceURIs(xmlMap);  
		Node node = x.selectSingleNode(document);
		String retrieveTask = node==null?"":node.getText();
		return retrieveTask;
	}
	@SuppressWarnings("unchecked")
	public static Map<String,String> getExtendFields(String processId){
		Map<String,String>  result = (Map<String,String>)WorkflowMemcachedUtil.get(getHashCode(processId+"=extendFields"));
		if(result == null)result = getMyExtendFields(processId);
		return result;
	}
	private static Map<String,String> getMyExtendFields(String processId){
		Map<String,String> extendFieldMap=new HashMap<String, String>();
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
 		HashMap<String,String> xmlMap = new HashMap<String,String>();   
 		xmlMap.put("wf",namespaceUrl);   
    	XPath x = document.createXPath("/wf:process/wf:extend/wf:extend-field");   
 		x.setNamespaceURIs(xmlMap);  
 		Node node = x.selectSingleNode(document);
    	String extendFields = node==null?"":node.getText();
    	if(StringUtils.isNotEmpty(extendFields)){
    		String[] fields=extendFields.split(",");
    		for(String field:fields){
    			extendFieldMap.put(field.split(":")[0], field.split(":")[1]);
    		}
    	}
    	return extendFieldMap;
	}
	
	/**
	 * 获得是否启用邮件通知
	 * @param file
	 * @return
	 */
	public static boolean isMailNotice(String processId,String taskName) {
		Boolean result = false;
		if(StringUtils.isNotEmpty(taskName)){
			result = (Boolean)WorkflowMemcachedUtil.get(getHashCode(processId+"=isMailNotice="+taskName));
			if(result==null)result=isMyMailNotice(processId,taskName);
		}
		return result;
	}
	private static boolean isMyMailNotice(String processId,String taskName) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:task[@name='" + taskName+ 
		"']/wf:extend/wf:basic-properties/wf:mail-notice");   
		x.setNamespaceURIs(xmlMap);  
		Node node = x.selectSingleNode(document);
		String isNotice = node==null?"false":node.getText();
		return "true".equals(isNotice);
	}
	
	/**
	 * 获得业务补偿/任务退回的设置
	 * @param file
	 * @return
	 */
	public static String getGobackTaskBean(String processId) {
		String result =  (String)WorkflowMemcachedUtil.get(getHashCode(processId+"=gobackTaskBean"));
		if(result == null){
			result = getMyGobackTaskBean(processId);
		}else{
			if(DEFAULT_VALUE.equals(result)){
				result = null;
			}
		}
		return result;
	}
	private static String getMyGobackTaskBean(String processId) {
		Document document = DocumentParameterUtils.getDocument(processId);
		String namespaceUrl = document.getRootElement().getNamespace().getURI();
		HashMap<String,String> xmlMap = new HashMap<String,String>();   
		xmlMap.put("wf",namespaceUrl);   
		XPath x = document.createXPath("/wf:process/wf:extend/wf:expiation-setting/wf:goback-task");   
		x.setNamespaceURIs(xmlMap);  
		Node node = x.selectSingleNode(document);
		String retrieveTask = node==null?"":node.getText();
		return retrieveTask;
	}
	
	/**
	 * 将信息放入缓存中
	 * @param processId jbpm部署定义的id
	 */
	@SuppressWarnings("unchecked")
	public static void getDefinitionParseInfo(String processId){
		if(PropUtils.getProp("project.model")==null||PropUtils.getProp("project.model").equals("product.model")){//默认是产品模式，或配置的是产品模式时
			Document document = DocumentParameterUtils.getDocument(processId);
			String namespaceUrl = document.getRootElement().getNamespace().getURI();
			HashMap<String,String> xmlMap = new HashMap<String,String>();   
			xmlMap.put("wf",namespaceUrl);   
			
			//获得所有task节点
			XPath x = document.createXPath("/wf:process/wf:task");   
			x.setNamespaceURIs(xmlMap);
			List<Element> nodes = x.selectNodes(document) ;
			getDefinitionParseTacheInfo(nodes,processId,"task");
			//获得所有state节点
			x = document.createXPath("/wf:process/wf:state");   
			x.setNamespaceURIs(xmlMap);
			nodes = x.selectNodes(document) ;
			getDefinitionParseTacheInfo(nodes,processId,"state");
			//获得所有custom节点
			x = document.createXPath("/wf:process/wf:custom");  
			x.setNamespaceURIs(xmlMap);
			nodes = x.selectNodes(document) ;
			getDefinitionParseTacheInfo(nodes,processId,"custom");
			
			//获得所有流向
			x = document.createXPath("//wf:transition");  
			x.setNamespaceURIs(xmlMap);
			nodes = x.selectNodes(document) ;
			getDefinitionParseTransitionInfo(nodes,processId);
			
			//获得所有判断环节
			x = document.createXPath("//wf:decision");  
			x.setNamespaceURIs(xmlMap);
			nodes = x.selectNodes(document) ;
			getDefinitionParseDecision(nodes,processId);
			
			//流程配置
			getDefinitionParseProcess(processId);
		}
		
	}
	
	public static String getHashCode(String key){
		return (key).hashCode()+"";
	}
	/**
	 * 
	 * @param nodes
	 * @param processId
	 * @param tacheType
	 */
	private static void getDefinitionParseTacheInfo(List<Element> nodes,String processId,String tacheType ){
		for(Element element : nodes){
				String name=element.attributeValue("name");
				MemCachedUtils.add(getHashCode(processId+"=taskTransactor="+name), getTaskTransactors( processId, name));
				
				String value = getMyPreviousTransactorAssignmentUrl( processId, name);
				MemCachedUtils.add(getHashCode(processId+"=previousTransactorAssignmentUrl="+name), value==null?DEFAULT_VALUE:value);
				MemCachedUtils.add(getHashCode(processId+"=hasMoreTransactor="+name), myHasMoreTransactor( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=haveEditRight="+name), myHaveEditRight( processId, name));
				
				value = getMyTaskTitle( processId, name);
				MemCachedUtils.add(getHashCode(processId+"=taskTitle="+name), value==null?DEFAULT_VALUE:value);
				MemCachedUtils.add(getHashCode(processId+"=viewMeetingResultRight="+name), getMyViewMeetingResultRight( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=viewVoteResultRight="+name), getMyViewVoteResultRight( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=viewFlowHistoryRight="+name), getMyViewFlowHistoryRight( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=viewOpinion="+name), getMyViewOpinion( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=printFormRight="+name), getMyPrintFormRight( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=officialTextViewSetting="+name), getMyOfficialTextViewSetting( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=officialTextDownloadSetting="+name), getMyOfficialTextDownloadSetting( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=editOpinion="+name), getMyEditOpinion( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=mustOpinion="+name), getMyMustOpinion( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=officialTextCreateCondition="+name), getMyOfficialTextCreateCondition( processId, name));
				
				value = getMyOfficialTextTemplate( processId, name);
				MemCachedUtils.add(getHashCode(processId+"=officialTextTemplate="+name), value==null?DEFAULT_VALUE:value);
				MemCachedUtils.add(getHashCode(processId+"=officialTextPrintSetting="+name), getMyOfficialTextPrintSetting( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=officialTextEditSetting="+name), getMyOfficialTextEditSetting( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=officialTextDeleteCondition="+name), getMyOfficialTextDeleteCondition( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=attachmentAddCondition="+name), getMyAttachmentAddCondition( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=attachmentDeleteCondition="+name), getMyAttachmentDeleteCondition( processId, name));
				MemCachedUtils.add(getHashCode(processId+"=attachmentDownloadCondition="+name), getMyAttachmentDownloadCondition( processId, name));
				
				value = getMyCurrentTacheType( processId, name);
				MemCachedUtils.add(getHashCode(processId+"=currentTacheType="+name), value==null?DEFAULT_VALUE:value);
				MemCachedUtils.add(getHashCode(processId+"=activityPermissionCondition="+name), getMyActivityPermissionCondition( processId, name));
				if("task".equals(tacheType)){
					
					value = getMyTaskProcessingMode( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=taskProcessingMode="+name),  value==null?DEFAULT_VALUE:value);
					
					Integer intvalue = getMyTransactPassRate( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=transactPassRate="+name),  intvalue==null?DEFAULT_VALUE:intvalue);
					
					value = getMyAfterTaskCompletedBean( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=afterTaskCompletedBean="+name), value==null?DEFAULT_VALUE:value);
					
					value = getMyDeleteInstancePermissionsInTask( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=deleteInstancePermissionsInTask="+name), value==null?DEFAULT_VALUE:value);
					MemCachedUtils.add(getHashCode(processId+"=requiredFields="+name), getMyRequiredFields( processId, name));
					MemCachedUtils.add(getHashCode(processId+"=nonEditableFields="+name), getMyNonEditableFields( processId, name));
					MemCachedUtils.add(getHashCode(processId+"=afterFilledFields="+name), getMyAfterFilledFields( processId, name));
					MemCachedUtils.add(getHashCode(processId+"=beforeFilledFields="+name), getMyBeforeFilledFields( processId, name));
					
					value = getMyProcessDeleteCondition( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=processDeleteCondition="+name),  value==null?DEFAULT_VALUE:value);
					
					value = getMyBeforeTaskSubmitImpClassName( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=beforeTaskSubmitImpClassName="+name), value==null?DEFAULT_VALUE:value);
					MemCachedUtils.add(getHashCode(processId+"=beforeTaskSubmitUrl="+name), getMyBeforeTaskSubmitUrl( processId, name));
					
					value = getMyBeforeTaskSubmitResultMessage( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=beforeTaskSubmitResultMessage="+name), value==null?DEFAULT_VALUE:value);
					MemCachedUtils.add(getHashCode(processId+"=haveBeforeSubmit="+name), myHaveBeforeSubmit( processId, name));
					MemCachedUtils.add(getHashCode(processId+"=isHaveSpecialTask="+name), isMyHaveSpecialTask( processId, name));
					
					value = getMySpecialTaskProperties( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=specialTaskProperties="+name),  value==null?DEFAULT_VALUE:value);
					
					value = getMySpecialTaskTitle( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=specialTaskTitle="+name),  value==null?DEFAULT_VALUE:value);
					MemCachedUtils.add(getHashCode(processId+"=reminderSetting="+name), getMyReminderSetting( processId, name));
					
					value = getMyTacheCode( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=tacheCode="+name), value==null?DEFAULT_VALUE:value);
					MemCachedUtils.add(getHashCode(processId+"=isMailNotice="+name), isMyMailNotice( processId, name));
					
					
				}else if("state".equals(tacheType)){
					MemCachedUtils.add(getHashCode(processId+"=choiceTaches="+name), getMyChoiceTaches( processId, name));
					
				}else if("custom".equals(tacheType)){
					
					value = getMySubProcessBeginning( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=subProcessBeginning="+name), value==null?DEFAULT_VALUE:value);
					
					value = getMyBeforeStartSubProcess( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=beforeStartSubProcess="+name), value==null?DEFAULT_VALUE:value);
					
					value = getMySubProcessEnd( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=subProcessEnd="+name), value==null?DEFAULT_VALUE:value);
					
					value = getMySubDefinitionId( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=subDefinitionId="+name), value==null?DEFAULT_VALUE:value);
					
					value = getMySubProcessTransition( processId, name);
					MemCachedUtils.add(getHashCode(processId+"=subProcessTransition="+name), value==null?DEFAULT_VALUE:value);
					MemCachedUtils.add(getHashCode(processId+"=isSubProcessTask="+name), isMySubProcessTask( processId, name));
					
				}
			}
	}
	
	
	private static void getDefinitionParseTransitionInfo(List<Element> nodes,String processId){
		for(Element element : nodes){
			String name=element.attributeValue("name");
			String value = getMyFlowingExecuteBean( processId, name);
			MemCachedUtils.add(getHashCode(processId+"=flowingExecuteBean="+name), value==null?DEFAULT_VALUE:value);
			
			value =  getMyChangeStatus( processId, name);
			MemCachedUtils.add(getHashCode(processId+"=changeStatus="+name), value==null?DEFAULT_VALUE:value);
			MemCachedUtils.add(getHashCode(processId+"=transitionInform="+name), myTransitionInform( processId, name));
			
			value = getMyTransitionInformType( processId, name);
			MemCachedUtils.add(getHashCode(processId+"=transitionInformType="+name), value==null?DEFAULT_VALUE:value);
			
			value = getMyNeedInformUserCondition( processId, name);
			MemCachedUtils.add(getHashCode(processId+"=needInformUserCondition="+name),  value==null?DEFAULT_VALUE:value);
			
			value = getMyNeedInformSubject( processId, name);
			MemCachedUtils.add(getHashCode(processId+"=needInformSubject="+name), value==null?DEFAULT_VALUE:value);
			
			value = getMyNeedInformContent( processId, name);
			MemCachedUtils.add(getHashCode(processId+"=needInformContent="+name), value==null?DEFAULT_VALUE:value);
			MemCachedUtils.add(getHashCode(processId+"=flowingFilledFields="+name), getMyFlowingFilledFields( processId, name));
			
			value = getMyTransitionOriginalUser( processId, name);
			MemCachedUtils.add(getHashCode(processId+"=transitionOriginalUser="+name),  value==null?DEFAULT_VALUE:value);
		}
	}
	
	private static void getDefinitionParseProcess(String processId){
		MemCachedUtils.add(getHashCode(processId+"=workFlowBaseInfo"), getMyWorkFlowBaseInfo( processId));
		MemCachedUtils.add(getHashCode(processId+"=processInform"), myProcessInform( processId));
		String value = getMyProcessInformType(processId);
		MemCachedUtils.add(getHashCode(processId+"=processInformType"), value==null?DEFAULT_VALUE:value);
		
		value = getMyProcessInformUserCondition( processId);
		MemCachedUtils.add(getHashCode(processId+"=processInformUserCondition"), value==null?DEFAULT_VALUE:value);
		
		value = getMyProcessInformSubject( processId);
		MemCachedUtils.add(getHashCode(processId+"=processInformSubject"),  value==null?DEFAULT_VALUE:value);
		
		value = getMyProcessInformContent( processId);
		MemCachedUtils.add(getHashCode(processId+"=processInformContent"),  value==null?DEFAULT_VALUE:value);
		
		value = getMyFirstTaskName( processId);
		MemCachedUtils.add(getHashCode(processId+"=firstTaskName"), value==null?DEFAULT_VALUE:value);
		
		value = getMyProcessHistoryPermissions( processId);
		MemCachedUtils.add(getHashCode(processId+"=processHistoryPermissions"), value==null?DEFAULT_VALUE:value);
		MemCachedUtils.add(getHashCode(processId+"=taskNames"), getMyTaskNames( processId));
		MemCachedUtils.add(getHashCode(processId+"=unFieldTaskNames"), getMyUnFieldTaskNames( processId));
		MemCachedUtils.add(getHashCode(processId+"=fieldTaskNames"), getMyFieldTaskNames( processId));
		
		value = getMyFormFlowableDeleteBeanName( processId);
		MemCachedUtils.add(getHashCode(processId+"=formFlowableDeleteBeanName"), value==null?DEFAULT_VALUE:value);
		MemCachedUtils.add(getHashCode(processId+"=reminderSetting"), getMyReminderSetting( processId));
		MemCachedUtils.add(getHashCode(processId+"=parameterSetting"), getMyParameterSetting( processId));
		MemCachedUtils.add(getHashCode(processId+"=monitorDeleteInstanceSet"), getMyMonitorDeleteInstanceSet( processId));
		MemCachedUtils.add(getHashCode(processId+"=monitorCancelInstancSet"), getMyMonitorCancelInstancSet( processId));
		MemCachedUtils.add(getHashCode(processId+"=monitorTaskJumpSet"), getMyMonitorTaskJumpSet( processId));
		MemCachedUtils.add(getHashCode(processId+"=pauseInstancSet"), getMyPauseInstancSet( processId));
		MemCachedUtils.add(getHashCode(processId+"=continueInstancSet"), getMyContinueInstancSet( processId));
		
		value = getMyRetrieveTaskSet( processId);
		MemCachedUtils.add(getHashCode(processId+"=retrieveTaskSet"), value==null?DEFAULT_VALUE:value);
		
		value = getMyEndInstanceBean( processId);
		MemCachedUtils.add(getHashCode(processId+"=endInstanceBean"),  value==null?DEFAULT_VALUE:value);
		
		MemCachedUtils.add(getHashCode(processId+"=extendFields"), getMyExtendFields(processId));
		
		value = getMyGobackTaskBean( processId);
		MemCachedUtils.add(getHashCode(processId+"=gobackTaskBean"),  value==null?DEFAULT_VALUE:value);
	}
	
	private static void getDefinitionParseDecision(List<Element> nodes,String processId){
		for(Element element : nodes){
			String name=element.attributeValue("name");
			List<String> decisionConditions = getMyDecisionConditions( processId,name);
			MemCachedUtils.add(getHashCode(processId+"=decisionConditions="+name), decisionConditions);
			getDefinitionParseDecisionTransition(decisionConditions,processId,name);
		}
	}
	
	
	private static void getDefinitionParseDecisionTransition(List<String> conditions,String processId,String decisionName){
		for (int i=0;i<conditions.size();i++) {
			String[] value = getMyDecisionTransition( processId,decisionName,i+1);
			MemCachedUtils.add(getHashCode(processId+"=decisionTransition="+decisionName+"="+(i+1)), value==null?DEFAULT_VALUE:value);
		}
	}
	
	public static final String DO_TASK_URL = "doTaskUrl";
	public static final String DO_TASK_URL_PARAMETER_NAME = "doTaskUrlParameterName";
	public static final String FORM_VIEW_URL = "formViewUrl";
	public static final String FORM_VIEW_URL_PARAMETER_NAME = "formViewUrlParameterName";
	public static final String PROCESS_START_URL = "processStartUrl";
	public static final String PROCESS_START_URL_PARAMETER_NAME = "processStartUrlParameterName";
	public static final String PROCESS_START_URL_PARAMETER_VALUE = "processStartUrlParameterValue";
	public static final String URGEN_URL = "urgenUrl";
	public static final String URGEN_URL_PARAMETER_NAME = "urgenUrlParameterName";
	public static final String SYSTEM_ID = "system-id";
	public static final String SYSTEM_CODE = "system-code";
	public static final String CUSTOME_TYPE = "custom-type";
	public static final String SET_TYPE="setType";
	public static final String DELETE_INSTANCE_MONITOR="deleteInstanceMonitor";
	public static final String CANCEL_INSTANCE_MONITOR="cancelInstanceMonitor";
	public static final String PAUSE_INSTANCE_MONITOR="pauseInstanceMonitor";
	public static final String CONTINUE_INSTANCE_MONITOR="continueInstanceMonitor";
	public static final String TASK_JUMP_MONITOR="taskJumpMonitor";
	public static final String RETRIEVE_TASK="retrieveTask";
}
