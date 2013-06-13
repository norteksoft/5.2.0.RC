/************************************************************
 模块名称: 工作流标签使用的js 
 编写时间: 2010年12月6日
 编    程: wurong
 说    明: 使用工作流标签时，需要引用该js文件
 ************************************************************/

WorkflowButtonGroup.buttonInstanceName = "workflowButtonGroup";
WorkflowButtonGroup.btnSaveFormName="保存";
WorkflowButtonGroup.btnStartWorkflowName="保存";
WorkflowButtonGroup.btnSubmitWorkflowName="提交";
WorkflowButtonGroup.btnAssignTransactorName="提交";
WorkflowButtonGroup.btnChoiceTacheName="提交";
WorkflowButtonGroup.btnDrawTaskName="领取";
WorkflowButtonGroup.btnReadTaskName="已阅";
WorkflowButtonGroup.btnDistributeTaskName="提交";
WorkflowButtonGroup.btnAssignTaskName="交办";
WorkflowButtonGroup.btnVoteKikenName="弃权";
WorkflowButtonGroup.btnVoteOpposeName="反对";
WorkflowButtonGroup.btnVoteAgreementName="赞成";
WorkflowButtonGroup.btnSignoffTaskName="签收";
WorkflowButtonGroup.btnAddCountersignName="加签";
WorkflowButtonGroup.btnDeleteCountersignName="减签";
WorkflowButtonGroup.btnApproveTaskName="同意";
WorkflowButtonGroup.btnRefuseTaskName="不同意";
WorkflowButtonGroup.btnSubmitTaskName="提交";
WorkflowButtonGroup.btnGetBackTaskName="取回";
WorkflowButtonGroup.btnCopyTacheName="抄送";
WorkflowButtonGroup.btnAbandonTaskName="放弃";
WorkflowButtonGroup.btnAssignName="指派";
/*
 * 按钮对象的构造方法
 * 参数：按钮的id
 */
function Button(id,groupName,name){
	this.id=groupName+"_"+id;
	this.name= name;
	this.click=function(taskId){};
}
/**
 * 标签按钮事件对象
 */
function WorkflowButtonGroup(id){
	this.id = id || WorkflowButtonGroup.buttonInstanceName;
	this.btnSaveForm=new Button('btnSaveForm',this.id,WorkflowButtonGroup.btnSaveFormName);//保存按钮
	this.btnStartWorkflow=new Button('btnStartWorkflow',this.id,WorkflowButtonGroup.btnStartWorkflowName);//启动流程按钮
	this.btnSubmitWorkflow=new Button('btnSubmitWorkflow',this.id,WorkflowButtonGroup.btnSubmitWorkflowName);//提交流程按钮
	this.btnAssignTransactor=new Button('btnAssignTransactor',this.id,WorkflowButtonGroup.btnAssignTransactorName);//指定办理人按钮
	this.btnChoiceTache=new Button('btnChoiceTache',this.id,WorkflowButtonGroup.btnChoiceTacheName);//待选择环节
	this.btnDrawTask=new Button('btnDrawTask',this.id,WorkflowButtonGroup.btnDrawTaskName);//领取按钮
	this.btnReadTask=new Button('btnReadTask',this.id,WorkflowButtonGroup.btnReadTaskName);//阅读按钮
	this.btnDistributeTask=new Button('btnDistributeTask',this.id,WorkflowButtonGroup.btnDistributeTaskName);//分发按钮
	this.btnAssignTask=new Button('btnAssignTask',this.id,WorkflowButtonGroup.btnAssignTaskName);//交办指派按钮
	this.btnVoteKiken=new Button('btnVoteKiken',this.id,WorkflowButtonGroup.btnVoteKikenName);//弃权票按钮
	this.btnVoteOppose=new Button('btnVoteOppose',this.id,WorkflowButtonGroup.btnVoteOpposeName);//反对票按钮
	this.btnVoteAgreement=new Button('btnVoteAgreement',this.id,WorkflowButtonGroup.btnVoteAgreementName);//赞成票按钮
	this.btnSignoffTask=new Button('btnSignoffTask',this.id,WorkflowButtonGroup.btnSignoffTaskName);//签收任务按钮
	this.btnAddCountersign=new Button('btnAddCountersign',this.id,WorkflowButtonGroup.btnAddCountersignName);//加签按钮
	this.btnDeleteCountersign=new Button('btnDeleteCountersign',this.id,WorkflowButtonGroup.btnDeleteCountersignName);//减签按钮
	this.btnApproveTask=new Button('btnApproveTask',this.id,WorkflowButtonGroup.btnApproveTaskName);//审批同意按钮
	this.btnRefuseTask=new Button('btnRefuseTask',this.id,WorkflowButtonGroup.btnRefuseTaskName);//审批拒绝按钮
	this.btnSubmitTask=new Button('btnSubmitTask',this.id,WorkflowButtonGroup.btnSubmitTaskName);//提交任务按钮
	this.btnGetBackTask=new Button('btnGetBackTask',this.id,WorkflowButtonGroup.btnGetBackTaskName);//取回按钮
	this.btnCopyTache=new Button('btnCopyTache',this.id,WorkflowButtonGroup.btnCopyTacheName);//抄送按钮
	this.btnAbandonTask=new Button('btnAbandonTask',this.id,WorkflowButtonGroup.btnAbandonTaskName);//放弃按钮,放弃领取的任务
	this.btnAssign=new Button('btnAssign',this.id,WorkflowButtonGroup.btnAssignName);//放弃按钮,放弃领取的任务
}

/*
 * 默认实例
 */
var workflowButtonGroup = new WorkflowButtonGroup("workflowButtonGroup");

/*
 * 取回任务（如果有特殊操作，请重写该方法）
 */
workflowButtonGroup.btnGetBackTask.click = function(taskId){
	$.get(webRoot + "/engine/task!getBack.htm", { taskId: taskId },
	  function(data){
		if(data=="任务已取回"){
			window.location.reload(false);
		}else{
			alert(data);
		}
	  });
};

/*
 * 领取任务（如果有特殊操作，请重写该方法）
 */
workflowButtonGroup.btnDrawTask.click = function(taskId){
	$.get(webRoot + "/engine/task!receive.htm", { taskId: taskId },
		function(data){
			if(data=="task.receive.success"){
				window.location.reload(false);
			}else if(data="task.not.need.receive"){
				alert("不需要领取");
			}else{
				alert("领取出错");
			}
		});
};

/*
 * 放弃领取的任务（如果有特殊操作，请重写该方法）
 */
workflowButtonGroup.btnAbandonTask.click = function(taskId){
	$.get(webRoot + "/engine/task!abandonReceive.htm", { taskId: taskId },
		function(data){
			if(data=="task.abandon.receive.success"){
				window.location.reload(false);
			}else{
				alert("放弃领取出错");
			}
		});
};
