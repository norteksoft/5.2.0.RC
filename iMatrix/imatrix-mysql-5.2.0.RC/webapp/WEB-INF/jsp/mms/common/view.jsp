<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>${modulePage.name }</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<!--上传js-->
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/otherHandlers.js"></script>
	
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/util.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/text.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/opinion.js"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js" ></script>
	
	<script type="text/javascript" src="${imatrixCtx}/wf/js/workflowTag.js"></script>
	<script src="${imatrixCtx}/mms/js/mmsapi.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/mms/js/mms-workflow.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/form.js" type="text/javascript"></script>
	
	<script type="text/javascript">
	  isUsingFormLayout=false;
		$().ready(function() {
			$( "#tabs" ).tabs();
			format_Validate();
		});

		function format_Validate(){
			addFormValidate($("#_validate_string").attr("value"),'default_submit_form');
			_formValidate();
		}
		
	</script>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center" >
	<div class="opt-body">
		<aa:zone name="default_refresh_zone">
			<aa:zone name="button_zone">
			</aa:zone>
			<div id="opt-content">
				<div id="opt_message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<form id="defaultForm1" name="defaultForm1"action="">
					<input type="hidden" name="id" id="id" value="${data.id }"  />
					<input type="hidden" name="taskId" id="taskId" value="${taskId }" />
					<input type="hidden" name="instanceId" id="instanceId" value="${instanceId }" />
					<input type='hidden' value="" id="_is_validate_ok"/>
				</form>
				<div id="tabs">
					<ul >
						<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/input.htm','button_zone,content_zone',format_Validate);">表单信息</a></li>
						<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/history.htm','button_zone,content_zone');">流转历史</a></li>
						<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/countersign.htm','button_zone,content_zone');">会签结果</a></li>
						<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/vote.htm','button_zone,content_zone');">投票结果</a></li>
					</ul>
					<div id="tabs-1">
						<aa:zone name="content_zone">
							<form action="" id="default_submit_form" name="default_submit_form" method="post">
								<input type='hidden' id="_choose_url" value="${chooseUrl}">
								<input type='hidden' id="__transact" name="transact" value="">
								<input type="hidden"  id="taskId" name="taskId" value="${taskId}">
								<input type='hidden' value='${validateString}' id="_validate_string" />
								<input type='hidden' id="id"  value="${data.id}"/>
								<input type="hidden"  name="assignee" id="assignee" ></input>
								<input type="hidden"  name="monitorFlag" id="monitorFlag" value="true"></input>
								<aa:zone name="history_refresh_zone">
									<view:formView code="${formCode}" entity="${data}" version="${formVersion}"></view:formView>
								</aa:zone>
								</form>
									<p>办理意见：
										<a href="#" onclick="_view_opinion(this,'${taskId}');" state="1"><img id="viewImg" src="${imatrixCtx}/mms/images/x1.png"></a> 
									</p>
									<aa:zone name="default_opinion_zone"></aa:zone>
									
									<p>正&emsp;&emsp;文：
									   <a href="#" onclick="_view_text(this,'${taskId }');" state="1"><img id="textImg" src="${mmsCtx}/images/x1.png"></a> 
									</p>
									<aa:zone name="default_text_zone"></aa:zone>
									
									<p>附&emsp;&emsp;件：
										<a href="#" onclick="_view_accessory(this,'${taskId }');" state="1"><img id="accImg" src="${mmsCtx}/images/x1.png"></a> 
									</p>
									<aa:zone name="default_accessory_zone"></aa:zone>
								<form action="" name="workflow_attachments_form" id="workflow_attachments_form" method="post">
									<input type="hidden" name="taskId" value="${taskId }"/>
									<input type="hidden" id="companyId" value="${data.company_id }"/>
								</form>
								
								<form id="officeForm1" name="officeForm1" action="" method="post">
									<input type="hidden" id="workflowId" name="workflowId" value="${workflowId}">
									<input type="hidden" id="taskId" name="taskId" value="${taskId}">
						        </form>
						        <form action="" id="opinion_form" name="opinion_form" method="post">
						        	<input type="hidden" name="taskId" value="${taskId }"/>
						        </form>
								<form action="" name="officeForm" id="officeForm" method="post">
									<input type="hidden" name="workflowId" value="${workflowId}"/>
									<input type="hidden" name="taskId" value="${taskId}"/>
									<input type="hidden" name="opinion" id="opinion"/>
								</form>
						</aa:zone>
					</div>
				</div>
			</div>
			<div id="select_transactor" style="display: none;">
					<s:if test="choiceTransactor.size()>0">
					<div class="opt-btn">
						<button class="btn" type="button" onclick="selectTransactorOk();"><span><span>确定</span></span></button> 
					</div>
					<form action="#" id="select_transactor_form" name="select_transactor_form" method="post">
						<s:iterator value="choiceTransactor.keySet()" id="tran">
							<s:set id="tranName" value="choiceTransactor.get(#tran)"></s:set>
							<div style="padding: 4px 8px;"> <input type="radio" value="${tran}" name="transactor"/> ${tranName} </div>
						</s:iterator>
						<input name="taskId" id="transact_task_id" type="hidden"/>
					</form>
					<script type="text/javascript">
						function selectTransactorOk(){
							if($("input[name='transactor']:checked").length==1){
								$('#transact_task_id').attr('value', $('#taskId').val());
								ajaxAnyWhereSubmit("select_transactor_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
							}else{
								alert('请选择办理人');
							}
						}
					</script>
					</s:if>
				</div>
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>
