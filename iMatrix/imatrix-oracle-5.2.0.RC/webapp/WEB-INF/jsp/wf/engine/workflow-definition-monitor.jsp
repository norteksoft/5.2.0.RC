<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>流程监控</title>
<%@ include file="/common/wf-iframe-meta.jsp"%>
<script src="${wfCtx }/js/util.js" type="text/javascript"></script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="wfd_main">
			<form id="backForm" action="" name="backForm" method="post">
				<input id="type" type="hidden" name="type" value="${type }" />
				<input id="system_id" name="sysId" type="hidden" value="${sysId }"/>
				<input id="vertion_type" name="vertionType" type="hidden" value="${vertionType }"/>
			</form>
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
				<div id="flowbtn" class="flag" >
					<ul >
						<security:authorize ifAnyGranted="wf_engine_workflow_endWorkflow">
							<li><a href="#" onclick="end_workflow_def();">取消流程</a></li>
						</security:authorize>
						<security:authorize ifAnyGranted="wf_engine_workflow_pauseWorkflow">
							<li><a href="#" onclick="pause_workflow(${wfdId});">暂停流程</a></li>
						</security:authorize>
						<security:authorize ifAnyGranted="wf_engine_workflow_continueWorkflow">
							<li><a href="#"  onclick="continue_workflow(${wfdId});">继续流程</a></li>
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
							<li><a href="#" onclick="backView('backView','monitor');">环节跳转</a></li>
						</security:authorize>
						<security:authorize ifAnyGranted="wf_task_delTasks_batch">	
							<li><a href="#" onclick="delTasks_batch('monitor','${wfdId}');">移除任务</a></li>
						</security:authorize>
						<security:authorize ifAnyGranted="wf_task_volume_back_view">	
							<li><a href="#" onclick="volumeBackView('volumeBackView');">批量跳转</a></li>
						</security:authorize>
					</ul>
				</div>
				<security:authorize ifAnyGranted="wf_urgen_done">
					<button class='btn' onclick="urgen_done();" hidefocus="true"><span><span>应急处理</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="wf_engine_wf_definition_deleteWorkflow">	
					<button  class='btn' onclick="delete_monitor_workflow();" hidefocus="true"><span><span>删除</span></span></button>
				</security:authorize>	
				<button  class='btn' onclick="goBack('backForm','${wfCtx }/engine/workflow-definition-data.htm','wfd_main','wfdPage');" hidefocus="true"><span><span>返回</span></span></button>
			</div>
			<aa:zone name="monitorList">
			<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
			<div id="opt-content" >
				<form id="wf_form" name="wf_form" method="post">
					<input id="type" type="hidden" name="type" value="${type}" />
					<input id="system_id" name="sysId" type="hidden" value="${sysId}"/>
					<input id="vertion_type" name="vertionType" type="hidden" value="${vertionType}"/>
					<input name="position" id="position" type="hidden"/>
					
					<view:jqGrid url="${wfCtx}/engine/workflow-definition!monitor.htm?wfdId=${wfdId}" pageName="wiPage" code="WF_MONITOR_DEF" gridId="main_table"></view:jqGrid>
					
				</form>
			</div>		
			</aa:zone>	
			</aa:zone>	
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
