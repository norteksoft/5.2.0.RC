<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>报销单</title>
<%@include file="/common/meta.jsp"%>
<!--上传js-->
	<script type="text/javascript" src="${ctx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${ctx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"> </script>
	<script type="text/javascript" src="${ctx}/widgets/workflowEditor/swfobject.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/workflowTag.js"></script>

<script type="text/javascript">
var buttonSign="";
isUsingComonLayout=false;
	$().ready(function() {
		$( "#tabs" ).tabs();
		validateForm();
	});

	function validateForm(){
		addFormValidate('${fieldPermission}', '${lowCaseEntityName}Form');
		${lowCaseEntityName}FormValidate();
	}

	//提交form
	function completeTask(taskTransact) {
		$('#taskTransact').val(taskTransact);
		$("#${lowCaseEntityName}Form").attr("action","${ctx }/${nameSpace}/${lowCaseEntityName}-completeTask.htm");
		$('#${lowCaseEntityName}Form').submit();
	}
	
	//保存form
	function saveTask() {
	buttonSign="save";
		$("#${lowCaseEntityName}Form").attr("action","${ctx }/${nameSpace}/${lowCaseEntityName}-save.htm");
		$("#saveTaskFlag").attr("value","true");
		$('#${lowCaseEntityName}Form').submit();
	}

	//表单验证
	function ${lowCaseEntityName}FormValidate() { 
		$("#${lowCaseEntityName}Form").validate({
			submitHandler : function() {
				$(".opt_btn").find("button.btn").attr("disabled", "disabled");
				var cansave = iMatrix.getSubTableDatas("${lowCaseEntityName}Form");
				if(cansave){
					$("#${lowCaseEntityName}Form").ajaxSubmit(function (id){
						dealResult(id);
						if(""==buttonSign){
							window.parent.close();
						}else{
							buttonSign="";
						}
					});
				}
			}
		});
	}
	
	//提交后回调事件
	function dealResult(id){
	if(id!=""&&typeof id!='undefined'&&id!=null){
		var ids=id.split(";");
		$("#id").attr("value",ids[0]);
		if(ids[1]!=undefined&&ids[1]!=null&&ids[1]!=''){
		//根据后台返回id判断执行操作，这里代码根据需求写
		}else{
			$("#message").show("show");
			setTimeout('$("#message").hide("show");',3000);
		}
	}
}
	//流转历史和表单信息切换
	function changeViewSet(opt){
		if(opt=="basic"){
			aa.submit("defaultForm1", "${ctx}/${nameSpace}/${lowCaseEntityName}-task.htm", 'btnZone,viewZone', validateForm);
		}else if(opt=="history"){
			aa.submit("defaultForm1", "${ctx}/${nameSpace}/${lowCaseEntityName}-showHistory.htm", 'btnZone,viewZone');
		}
	}

	//办理完任务关闭窗口前执行
	function beforeCloseWindow(opt){
		aa.submit("defaultForm1", "${ctx}/${nameSpace}/${lowCaseEntityName}-task.htm", 'btnZone,viewZone');
	}

	//选择加签人员
	function addTask(){
		popTree({ title :'选择加签人员',
			innerWidth:'400',
			treeType:'MAN_DEPARTMENT_TREE',
			defaultTreeValue:'id',
			leafPage:'false',
			multiple:'true',
			hiddenInputId:"addSignPerson",
			acsSystemUrl:"${ctx}",
			callBack:function(){addSignCallBack();}});
	}
	function addSignCallBack(){
		if(jstree.getNames().indexOf("全公司")>=0){
			$('#addSignPerson').attr("value","all_user");
		}else{
			$('#addSignPerson').attr("value",jstree.getLoginNames());
		}
		$("#${lowCaseEntityName}Form").attr("action","${ctx}/${nameSpace}/${lowCaseEntityName}-addSigner.htm");
		$("#${lowCaseEntityName}Form").ajaxSubmit(function (id){
			alert(id);
		});
		validateForm();
	}
	//选择减签人员
	function cutTask(){
		custom_tree({url:webRoot+'/${nameSpace}/${lowCaseEntityName}-cutsignTree.htm',
			onsuccess:function(){removeSignerCallBack();},
			width:500,
			height:400,
			title:'选择环节',
			postData:{taskId:$("#taskId").attr("value")},
			nodeInfo:['id'],
			multiple:true,
			webRoot:webRoot
		});
	}
	function removeSignerCallBack(){
	$("#removeSignPerson").attr("value",getSelectValue('id'));
		$("#${lowCaseEntityName}Form").attr("action","${ctx}/${nameSpace}/${lowCaseEntityName}-removeSigner.htm");
		$("#${lowCaseEntityName}Form").ajaxSubmit(function (id){
			alert(id);
		});
		validateForm();
	}
	//领取回调
	function receiveback(){
		$("#message").show("show");
		setTimeout('$("#message").hide("show");',3000);
		$( "#tabs" ).tabs();
	}

	//指派
	function assignPerson(){
		popTree({ title :'指派人员',
				innerWidth:'400',
				treeType:'MAN_DEPARTMENT_TREE',
				defaultTreeValue:'loginName',
				leafPage:'false',
				multiple:'true',
				hiddenInputId:"assignee",
				showInputId:"assignee",
				acsSystemUrl:"${ctx}",
				callBack:function(){assignPersonCallBack();}});
	}
	//指派回调
	function assignPersonCallBack(){
		$('#assignee').attr("value",jstree.getLoginName());
		$("#${lowCaseEntityName}Form").attr("action","${ctx}/${nameSpace}/${lowCaseEntityName}-assign.htm");
			$("#${lowCaseEntityName}Form").ajaxSubmit(function (id){
				alert(id);
				window.parent.close();
			});
	}

	//提交
	workflowButtonGroup.btnSubmitTask.click = function(taskId){
		completeTask('SUBMIT');
	};
	//同意
	workflowButtonGroup.btnApproveTask.click = function(taskId){
		completeTask('APPROVE');
	};
	//不同意
	workflowButtonGroup.btnRefuseTask.click = function(taskId){
		completeTask('REFUSE');
	};
	//加签
	workflowButtonGroup.btnAddCountersign.click = function(taskId){
		addTask();
	};
	//减签
	workflowButtonGroup.btnDeleteCountersign.click = function(taskId){
		cutTask();
	};

	//减签
	workflowButtonGroup.btnSaveForm.click = function(taskId){
		saveTask();
	};

	//取回
	workflowButtonGroup.btnGetBackTask.click = function(taskId){
		$("#${lowCaseEntityName}Form").attr("action","${ctx}/${nameSpace}/${lowCaseEntityName}-retrieveTask.htm");
		$("#${lowCaseEntityName}Form").ajaxSubmit(function (id){
			if(id=="任务已取回"){
				window.location.reload(false);
			}else{
				alert(id);
			}
		});
	};

	//领取
	workflowButtonGroup.btnDrawTask.click = function(taskId){
		aa.submit("defaultForm1", "${ctx}/${nameSpace}/${lowCaseEntityName}-drawTask.htm", 'btnZone');
	};
	//放弃领取
	workflowButtonGroup.btnAbandonTask.click = function(taskId){
		aa.submit("defaultForm1", "${ctx}/${nameSpace}/${lowCaseEntityName}-abandonReceive.htm", 'btnZone');
	};

	//指派
	workflowButtonGroup.btnAssign.click = function(taskId){
		assignPerson();
	};

	//已阅
	workflowButtonGroup.btnReadTask.click = function(taskId){
		$('#taskTransact').val('READED');
		aa.submit("${lowCaseEntityName}Form", "${ctx}/${nameSpace}/${lowCaseEntityName}-completeTask.htm", 'main', readTaskCallback);
	};
	//选择环节
	workflowButtonGroup.btnChoiceTache.click = function(){
		completeTask('READED');
	};
	
	function readTaskCallback(){
		$("#message").show("show");
		setTimeout('$("#message").hide("show");',3000);
		window.parent.close();
	}

	//抄送
	workflowButtonGroup.btnCopyTache.click = function(taskId){
		popTree({ title :'抄送人员',
			innerWidth:'400',
			treeType:'MAN_DEPARTMENT_TREE',
			defaultTreeValue:'id',
			leafPage:'false',
			multiple:'true',
			hiddenInputId:"copyPerson",
			showInputId:"copyPerson",
			acsSystemUrl:"${ctx}",
			callBack:function(){copyPersonCallBack();}});
		};

	function copyPersonCallBack(){
		if(jstree.getNames().indexOf("全公司")>=0){
			$('#copyPerson').attr("value","all_user");
		}else{
			$('#copyPerson').attr("value",jstree.getLoginNames());
		}
		$("#${lowCaseEntityName}Form").attr("action","${ctx}/${nameSpace}/${lowCaseEntityName}-copyTask.htm");
		$("#${lowCaseEntityName}Form").ajaxSubmit(function (id){
			alert(id);
		});
	}
	function gobackTask(){
		$("#${lowCaseEntityName}Form").attr("action","${ctx}/${nameSpace}/${lowCaseEntityName}-goback.htm");
		$("#${lowCaseEntityName}Form").ajaxSubmit(function (id){
			alert(id);
			changeViewSet('basic');
			window.parent.close();
		});
	}
</script>
</head>

<body>
<div class="opt-body">
<aa:zone name="main">
		<aa:zone name="btnZone">
				<div class="opt-btn">
					<wf:workflowButtonGroup taskId="${taskId }"></wf:workflowButtonGroup>
					<button class='btn' onclick="gobackTask();">
						<span><span>退回</span>
						</span>
					</button>
				</div>
				<span id="message" style="display: none;"><font class="onSuccess"><nobr>操作成功！</nobr></font></span>
			</aa:zone>
			<div style="display: none;" id="message">
				<s:actionmessage theme="mytheme" />
			</div>
			<div id="opt-content" class="form-bg">
				<form id="defaultForm1" name="defaultForm1"action="">
					<input type="hidden" name="id" id="id" value="${id }"  />
					<input name="taskId" id="taskId" value="${taskId }" type="hidden"/>
					<input id="selecttacheFlag" type="hidden" value="true"/>
				</form>
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1" onclick="changeViewSet('basic');">表单信息</a></li>
						<li><a href="#tabs-1" onclick="changeViewSet('history');">流转历史</a></li>
					</ul>
					<div id="tabs-1">
						<aa:zone name="viewZone">
							<form id="${lowCaseEntityName}Form" name="${lowCaseEntityName}Form" method="post"
								action="${ctx }/expense-report/complete-task.htm">
								<input type="hidden" name="id" id="id" value="${id }" />
								<input type="hidden" name="taskId" id="taskId" value="${taskId }" />
								<input type="hidden" name="taskTransact" id="taskTransact" />
								<input type="hidden" name="addSignPerson" id="addSignPerson" />
								<input type="hidden" name="copyPerson" id="copyPerson" />
								<input type="hidden" name="saveTaskFlag" id="saveTaskFlag"  />
								<input type="hidden" name="removeSignPerson" id="removeSignPerson" />
								<input type="hidden" name="assignee" id="assignee" />
								
								<grid:formView code="${formCode}" entity="${entity}"></grid:formView>
						</aa:zone>
					</div>
				</div>
			</div>
		</aa:zone>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>