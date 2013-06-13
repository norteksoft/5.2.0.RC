<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	<title>工作流平台</title>
	
	<script src="${wfCtx }/js/opinion.js" type="text/javascript"></script>
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	
	<link href="${imatrixCtx}/widgets/workflow-swfupload/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/workflow-attachment-handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/swfupload.js"></script>
	
	<script src="${wfCtx }/js/workflow-instance.js" type="text/javascript"></script>
	<script src="${wfCtx }/js/util.js" type="text/javascript"></script>
	<script src="${wfCtx }/js/task.js" type="text/javascript"></script>
	
	<script src="${wfCtx }/js/workflow.js" type="text/javascript"></script>
	
	<script src="${resourcesCtx}/js/form.js" type="text/javascript"></script>
	
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>
	
	
	<script type="text/javascript">

	//供子页面返回到本主页用
	function back(){
		$("#wf_back_type").attr("value",$("#wf_type").attr("value"));
		$("#wf_back_definition").attr("value",$("#wf_definition").attr("value"));
		$("#wf_back_instanceState").attr("value",$("#wf_instanceState").attr("value"));
		goBack("backForm", webRoot + "/engine/workflow.htm", "myprocess","workflowInstances");
	}
	function viewDelegate(defId){
		ajaxSubmit('defaultForm', '${ctx}/engine/workflow!input.htm?workflowId='+defId, 'myprocess');
	}

	//查看
	function viewWorkflow(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"_click_fun_view("+opts.id+");\">" + ts1 + "</a>";
		return v;
	}
	
	function _click_fun_view(id){
		$("#wf_definition").attr("value",id);
		ajaxSubmit('defaultForm',webRoot+'/engine/workflow!input.htm','myprocess');
	}

	
	function selectProcess(val){
		$.colorbox.close();
		//tb_remove();
		ajaxAnywhere_1 = new AjaxAnywhere();
		var url =  webRoot + '/engine/workflow!inputForm.htm?processId=' + val ;
		var form = '<form id="tempForm" name="rcqForm" action="' + url + '" method="post">' + '</form>';
		$('#custom').html(form);
		ajaxAnywhere_1.formName = "rcqForm";
		ajaxAnywhere_1.getZonesToReload = function(){
			return "myprocess";
		};
		ajaxAnywhere_1.onAfterResponseProcessing = function (){
		};
		ajaxAnywhere_1.submitAJAX();
	}
	
	
	</script>
</head>
<body>
<div class="ui-layout-center">
	<form id="backForm" action="" name="backForm" method="post">
		<input id="wf_back_type" type="hidden" name="type" />
		<input id="wf_back_definition" type="hidden" name="definitionId" />
		<input id="wf_back_instanceState" type="hidden" name="end" />
	</form>
	<form id="defaultForm" action="" name="defaultForm" method="post">
		<input id="wf_workflowId" type="hidden" name="workflowId" />
		<input id="wf_type" type="hidden" name="type" />
		<input id="wf_definition" type="hidden" name="definitionId" />
		<input id="wf_instanceState" type="hidden" name="end" />
	</form>
	<div class="opt-body">
		<aa:zone name="myprocess">
			<s:if test="end==false">
				<div class="opt-btn">
					<security:authorize ifAnyGranted="wf_engine_workflow_delete">
						<button class="btn" onclick="deleteWorkflow();"><span><span >删除</span></span></button>
					</security:authorize>
				</div>
			</s:if>	
			<div id="opt-content" >
				<s:if test="definitionId==null">
					<view:jqGrid url="${wfCtx}/engine/workflow.htm?end=${end }&type=${type }" pageName="workflowInstances" code="WF_INSTANCE" gridId="main_table"></view:jqGrid>
				</s:if><s:else>
					<view:jqGrid url="${wfCtx}/engine/workflow.htm?definitionId=${definitionId }&end=${end }&type=${type }" pageName="workflowInstances" code="WF_INSRANCE_FORM" gridId="main_table"></view:jqGrid>
				</s:else>
			</div>
		</aa:zone>
	</div>
</div>   	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
