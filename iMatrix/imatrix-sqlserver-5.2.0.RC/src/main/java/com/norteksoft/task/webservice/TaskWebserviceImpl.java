package com.norteksoft.task.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.task.entity.Task;
import com.norteksoft.task.service.TaskManager;

//@WebService(endpointInterface = "com.norteksoft.task.webservice.TaskWebservice")
@Service
@Transactional
public class TaskWebserviceImpl implements TaskWebservice{
	
	private TaskManager taskManager;
	private UserManager userManager;
	public final static String TASK_SYSTEM_CODE = "task";
	public final static String TASK_INPUT_URL = "/task/task!input.htm?id=";
	
	@Autowired
	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	public String personalTasks(List<String> prmtNames, List<String> prmtValues) {
		Map<String, String> prmts = processParameter(prmtNames, prmtValues);
		String loginName = prmts.get("loginName");
		Long companyId = Long.valueOf(prmts.get("companyId"));
		return personalTasks(loginName, companyId, 5,"createdTime");
	}
	
	public String personalTasks(String loginName, Long companyId, Integer size, String order) {	
		return getTaskTable(loginName,companyId,size,order,null);
	}
	
	public String detailTasks(String loginName, Long companyId, Integer size, String order, String typeName) {	
		return getTaskTable(loginName,companyId,size,order,typeName);
	}
	
	private String getTaskTable(String loginName, Long companyId, Integer size, String order, String typeName){
		List<Task> tasks = null;
		if(StringUtils.isNotEmpty(typeName)){
			tasks = taskManager.getDetailTasksByUserType(companyId,loginName,typeName,size,order);
		}else{
			tasks = taskManager.getPersonalTasks(loginName, companyId, size,order);
		}
		processTaskCreator(tasks);
		List<String> headNames = new ArrayList<String>();
		headNames.add("任务名称");
		headNames.add("创建时间");
		headNames.add("发起人");
		
		List<String> propNames = new ArrayList<String>();
		propNames.add("title");
		propNames.add("createdTime");
		propNames.add("creator");
		
		return generatTable(headNames, tasks, propNames);
	}
	
	private void processTaskCreator(List<Task> tasks) {
		for(Task task : tasks){
			Object o = userManager.getUserByLoginName(task.getCreator());
			if(o != null){
				task.setCreator(getBeanProp(o, "name"));
			}
		}
	}

	private Map<String, String> processParameter(List<String> prmtNames, List<String> prmtValues){
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < prmtNames.size(); i++){
			map.put(prmtNames.get(i), prmtValues.get(i));
		}
		return map;
	}
	
	private String generatTable(List<String> headNames, List<? extends Object> objs, List<String> propNames){
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("TABLE");
		root.addAttribute("class", "leadTable");
		generatTHead(root, headNames);
		generatTBody(root, objs, propNames);
		return root.asXML();
	}
	
	/*
	 * 生成表头 
	 */
	private void generatTHead(Element root, List<String> headNames){
		Element thead = root.addElement("THEAD");
		Element tr = thead.addElement("TR");
		Element td = null;
		for(String headName : headNames){
			td = tr.addElement("TH");
			td.setText(headName);
			if(!"任务名称".equals(headName)){
				td.addAttribute("style", "width: 15%;");
			}
		}
	}

	/*
	 * 生成表体
	 */
	private void generatTBody(Element root, List<? extends Object> values, List<String> props){
		Element tbody = root.addElement("TBODY");
		if(CollectionUtils.isEmpty(values) || CollectionUtils.isEmpty(props)) 
			return;
		Element tr = null;
		Element tagA = null;
		for(Object bean : values){
			tr = tbody.addElement("TR");
			for(String prop : props){
				if("createdTime".equals(prop)){
					tr.addElement("TD").setText(getDataProp(bean, prop));
				}else if("creator".equals(prop)){
					tr.addElement("TD").setText(getBeanProp(bean, prop));
				}else if("title".equals(prop)){
					String taskActionInputUrl;
					try {
						taskActionInputUrl = getSystemUrl(TASK_SYSTEM_CODE);
						Task task = taskManager.getTaskById(Long.valueOf(getBeanProp(bean, "id")));
						if(!task.getRead()){
							tagA = tr.addElement("TD").addElement("A")
							.addAttribute("href", "#")
							.addAttribute("onclick", "popWindow(this,'"+taskActionInputUrl
									+getBeanProp(bean, "id")
									+"', 'task');")
									.addAttribute("style", "font-weight:bold;");
						}else{
							tagA = tr.addElement("TD").addElement("A")
							.addAttribute("href", "#")
							.addAttribute("onclick", "popWindow(this,'"+taskActionInputUrl
									+getBeanProp(bean, "id")
									+"', 'task');");
						}
						tagA.setText(getBeanProp(bean, prop));
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private String getSystemUrl(String key) throws Exception{
		String url = SystemUrls.getSystemUrl(key);
		url += TASK_INPUT_URL;
		return url;
	}
	
	private String getDataProp(Object bean, String propName){
		String value = null;
		try {
			value = BeanUtils.getProperty(bean, propName);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		if(value != null && value.length() >= 19){
			value = value.substring(0, 10);
		}
		return value == null?  "" : value;
	}
	
	/*
	 * 根据属性名从对象中取属性值
	 */
	private String getBeanProp(Object bean, String propName){
		String value = null;
		try {
			value = BeanUtils.getProperty(bean, propName);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return value == null?  "" : value;
	}
}
