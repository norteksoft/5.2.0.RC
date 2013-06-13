package ${packageName}.service;

import ${entityPath};
import ${packageName}.dao.${entityName}Dao;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;

<#if processFlag?if_exists=="true">
import java.util.Map;
import com.norteksoft.wf.engine.client.FormFlowable;
import com.norteksoft.product.web.wf.WorkflowManagerSupport;
import com.norteksoft.wf.engine.client.EndInstanceInterface;
import com.norteksoft.wf.engine.client.FormFlowableDeleteInterface;
import com.norteksoft.wf.engine.client.OnStartingSubProcess;
import com.norteksoft.wf.engine.client.RetrieveTaskInterface;
</#if>
@Service
@Transactional
public class ${entityName}Manager <#if processFlag?if_exists=="true">extends WorkflowManagerSupport<${entityName}> implements FormFlowableDeleteInterface,RetrieveTaskInterface,EndInstanceInterface,OnStartingSubProcess </#if>{
	@Autowired
	private ${entityName}Dao ${lowCaseEntityName}Dao;
	

	public ${entityName} get${entityName}(Long id){
		return ${lowCaseEntityName}Dao.get(id);
	}

	public void save${entityName}(${entityName} ${lowCaseEntityName}){
		${lowCaseEntityName}Dao.save(${lowCaseEntityName});
	}

	
	
	public void delete${entityName}(${entityName} ${lowCaseEntityName}){
		${lowCaseEntityName}Dao.delete(${lowCaseEntityName});
	}

	public Page<${entityName}> search(Page<${entityName}>page){
		return ${lowCaseEntityName}Dao.search(page);
	}

	public List<${entityName}> listAll(){
		return ${lowCaseEntityName}Dao.getAll${entityName}();
	}
		
<#if processFlag?if_exists=="true">
 	/*
	 * 删除流程实例时的回调方法（在流程参数中配置了beanName）
	 * 
	 * @see com.norteksoft.wf.engine.client.FormFlowableDeleteInterface#
	 * deleteFormFlowable(java.lang.Long)
	 */
	@Override
	public void deleteFormFlowable(Long id) {}
	
	/**
	 * 取回任务业务补偿
	 */
	@Override
	public void retrieveTaskExecute(Long entityId,Long taskId) {}

	/**
	 * 流程正常结束时的业务补偿
	 */
	@Override
	public void endInstanceExecute(Long entityId) {}

	@Override
	protected ${entityName} getEntity(Long id) {
		return ${lowCaseEntityName}Dao.get(id);
	}

	@Override
	protected void saveEntity(${entityName} ${lowCaseEntityName}) {
		${lowCaseEntityName}Dao.save(${lowCaseEntityName});
	}
	
	@Override
	public FormFlowable getRequiredSubEntity(Map<String, Object> param) {
		return null;
	}
	
		public String goback(Long taskId){
		return ApiFactory.getTaskService().returnTask(taskId);
	}
	
		/**
	 * 得到当前环节办理人
	 * @param ${lowCaseEntityName}
	 * @return
	 */
	public List<String[]> getTaskHander(${entityName} ${lowCaseEntityName}) {
	  return ApiFactory.getTaskService().getActivityTaskTransactors(${lowCaseEntityName});
	}
	
	public ${entityName} get${entityName}ByTaskId(Long taskId) {
		if(taskId==null)return null;
		return get${entityName}(ApiFactory.getFormService().getFormFlowableIdByTask(taskId));
	}

	public void delete${entityName}(Long id){
		ApiFactory.getInstanceService()
		.deleteInstance(get${entityName}(id));
	}
	/**
	 * 删除实体，流程相关文件都删除
	 * @param ids
	 */
	public String delete${entityName}(String ids) {
		String[] deleteIds = ids.split(",");
		int deleteNum=0;
		int failNum=0;
		for (String id : deleteIds) {
			${entityName}  ${lowCaseEntityName} = ${lowCaseEntityName}Dao.get(Long.valueOf(id));
			if(deleteRight(${lowCaseEntityName})){
				if(${lowCaseEntityName}.getWorkflowInfo()!=null){
					ApiFactory.getInstanceService().deleteInstance(${lowCaseEntityName}Dao.get(Long.valueOf(id)));
				}else{
					${lowCaseEntityName}Dao.delete(${lowCaseEntityName});
				}
				deleteNum++;
			}else{
				failNum++;
			}
		}
		return deleteNum+" 条数据成功删除，"+failNum+" 条数据没有权限删除！";
	}
	
	private boolean deleteRight(${entityName} ${lowCaseEntityName}){
		return ApiFactory.getInstanceService().isInstanceComplete(${lowCaseEntityName})||ApiFactory.getInstanceService().canDeleteInstanceInTask(${lowCaseEntityName}, ${lowCaseEntityName}.getWorkflowInfo().getCurrentActivityName());
	}
<#else>	
	public void delete${entityName}(Long id){
		${lowCaseEntityName}Dao.delete(id);
	}
	/**
	 * 删除实体，流程相关文件都删除
	 * @param ids
	 */
	public String delete${entityName}(String ids) {
		String[] deleteIds = ids.split(",");
		int deleteNum=0;
		int failNum=0;
		for (String id : deleteIds) {
			${entityName}  ${lowCaseEntityName} = ${lowCaseEntityName}Dao.get(Long.valueOf(id));
				${lowCaseEntityName}Dao.delete(${lowCaseEntityName});
				deleteNum++;
		}
		return deleteNum+" 条数据成功删除，"+failNum+" 条数据没有权限删除！";
	}

</#if>
	
}
