<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>流程监控</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${wfCtx }/js/workflow-definition.js" type="text/javascript"></script>
	
	<script language="javascript" type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<script type="text/javascript">
	/**下拉按钮效果 ****/
	$(function() {//默认按钮效果 
		initBtnGroup();
	});
	</script>
</head>
	<body>
	<div class="ui-layout-center">
		<form id="defaultForm" action="" name="defaultForm" method="post">
			<input id="wf_type" name="type" type="hidden" value="${type }"/>
			<input id="wf_name" name="definitionCode" type="hidden" value="${definitionCode }"/>
			<input id="workflowId" name="workflowId" type="hidden"/>
		</form>
		<div class="opt-body">
		<aa:zone name="wf_definition">	
			<div class="opt-btn">
				<security:authorize ifAnyGranted="wf_engine_wf_definition_search">
				<button class='btn' onclick="iMatrix.showSearchDIV(this);" hidefocus="true"><span><span>查询</span></span></button>
				</security:authorize>
				<div class="btndiv" id="_flowbtn" style="*top:-2px;">
					<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentFlowBtn">
						<span class="ui-button-text">操作流程</span>
					</button>
					<button  title="更多"  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
						<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
						<span class="ui-button-text">更多</span>
					</button>
				</div>
				<div id="flowbtn" class="flag">
					<ul >
						<security:authorize ifAnyGranted="wf_engine_workflow_endWorkflow">
							<li><a href="#" onclick="end_workflow_def();">取消流程</a></li>
						</security:authorize>
						<security:authorize ifAnyGranted="wf_engine_workflow_pauseWorkflow">
							<li><a href="#" onclick="pause_workflow_def('${type}','${definitionCode }');">暂停流程</a></li>
						</security:authorize>
						<security:authorize ifAnyGranted="wf_engine_workflow_continueWorkflow">
							<li><a href="#"  onclick="continue_workflow_def('${type}','${definitionCode }');">继续流程</a></li>
						</security:authorize>
						<security:authorize ifAnyGranted="wf_engine_workflow_compelEndWorkflow">
							<li><a href="#"  onclick="compel_end_workflow_def();">强制结束</a></li>
						</security:authorize>
					</ul>
				</div>
				
				<div class="btndiv" id="_transactorBtn" style="*top:-2px;">
					<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentTransactorBtn">
						<span class="ui-button-text">办理人处理</span>
					</button>
					<button  title="更多"  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
						<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
						<span class="ui-button-text">更多</span>
					</button>
				</div>
				<div id="transactorbtn" class="flag" >
					<ul >
						<security:authorize ifAnyGranted="wf_engine_task_changeTransactor">	
							<li><a href="#" onclick="changeTransactor('changeTransactor');">更改办理人</a></li>
						</security:authorize>
						<security:authorize ifAnyGranted="wf_task_addTransactor">	
							<li><a href="#" onclick="addTransactor();">增加办理人</a></li>
						</security:authorize>
						<security:authorize ifAnyGranted="wf_task_delTransactor">	
							<li><a href="#"  onclick="delTransactor();">减少办理人</a></li>
						</security:authorize>
					</ul>
				</div>
				
				<div class="btndiv" id="_taskBtn" style="*top:-2px;">
					<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentTaskBtn">
						<span class="ui-button-text">任务处理</span>
					</button>
					<button  title="更多"  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
						<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
						<span class="ui-button-text">更多</span>
					</button>
				</div>
				<div id="taskbtn" class="flag" >
					<ul >
						<security:authorize ifAnyGranted="wf_task_go_back_view">	
							<li><a href="#" onclick="backView('backView','monitorManager');">环节跳转</a></li>
						</security:authorize>
						<security:authorize ifAnyGranted="wf_task_delTasks_batch">	
							<li><a href="#" onclick="delTasks_batch('monitorManager');">移除任务</a></li>
						</security:authorize>
					</ul>
				</div>
				<security:authorize ifAnyGranted="wf_urgen_done">
				<button class='btn' onclick="urgen_done();" hidefocus="true"><span><span>应急处理</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="wf_engine_wf_definition_deleteWorkflow">	
				<button class='btn' onclick="delete_monitor_workflow('standardManager');" hidefocus="true"><span><span>删除</span></span></button>
				</security:authorize>
			</div>
									
								
			<aa:zone name="monitorList">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
											
			<form id="wf_form" name="wf_form" method="post" action="">
				<input id="wf_type1" name="type" type="hidden" value="${type }"/>
				<input id="wf_name1" name="definitionCode" type="hidden" value="${definitionCode }"/>
				<input name="position" id="position" type="hidden"/>
			</form>
			
			<div id="opt-content" >
				<form id="searchSubmit" name="searchSubmit" action="" method="post">
					<view:jqGrid url="${wfCtx}/engine/workflow-definition!monitorDefintion.htm?type=${type}&definitionCode=${definitionCode}" pageName="wiPage" code="WFD_MONITOR" gridId="main_table"></view:jqGrid>
				</form>	
			</div>
			</aa:zone>
	</aa:zone>
	</div>
	</div>							
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
