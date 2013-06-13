<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>选择办理人</title>
	<%@ include file="/common/wf-colorbox-meta.jsp"%>

	<script src="${resourcesCtx}/js/public.js" type="text/javascript"></script>
	<script src="${wfCtx }/js/wf.js" type="text/javascript"></script>
	
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	<script>
	//用户树多选 ；
	function OK(){
		if("${forkTask}"=="true" || "${hasActivitySubProcess}"=="true" ){//如果是并发任务
			window.parent.$.colorbox.close();
		}else{//不是并发任务
			var info=getInfo();
			var arr=eval(info);
			if(info!=""){
				$("#transactorForm").append('<input type="hidden" name="workflowId" value="${workflowId}"/>');
				for(var i=0;i<arr.length;i++){
					if(arr[i].type=="user"){
						$("#transactorForm").append('<input type="hidden" name="transactors" value="'+arr[i].loginName+'"/>');
					}else if(arr[i].type=="allDepartment" || arr[i].type=="company"){
						$("#transactorForm").append('<input type="hidden" name="transactors" value="all_user"/>');
						break;
					}
				}
				ajaxSubmit('transactorForm','${wfCtx}/engine/task!addTransactorSave.htm','wf_task',postSuccess);
			}else{
				window.parent.$.colorbox.close();
			}
		}
	}

	function postSuccess(){
		window.parent.$.colorbox.close();
	}

	function closeBtn(){
		window.parent.$.colorbox.close();
	}
	</script>
								
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="wf_task">
			<div style="width:auto; padding: 0; margin: 0;text-align: left;" id="leafTree">
				<div class="opt-btn">
					<button id="wf_ok" class='btn' onclick="OK();" hidefocus="true"><span><span>确定</span></span></button>
				</div>
			   	<div id="opt-content">
			   		<s:if test="forkTask">
				   		<div style="margin-top: 5px ;margin-left: 5px;">
							<font color="red">当前任务为并发环节，不能进行增加办理人操作</font>
						</div>
			   		</s:if><s:elseif test="hasActivitySubProcess">
			   			<font color="red">当前任务为子流程，不能进行增加办理人操作</font>
			   		</s:elseif><s:else>
			   			<acsTags:tree defaultable="true" treeId="companyTree" treeType="MAN_DEPARTMENT_TREE" multiple="true" inputId="searchValue"></acsTags:tree>
			   		</s:else>
			   	</div>
		   	</div>
			<form id="transactorForm" action="post" name="transactorForm"/>
		</aa:zone>
	</div>
</div>
</body>
</html>
