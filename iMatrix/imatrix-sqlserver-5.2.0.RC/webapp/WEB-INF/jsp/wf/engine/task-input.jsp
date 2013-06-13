<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	
	<title>流程管理</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	
	
	<link href="${imatrixCtx}/widgets/workflow-swfupload/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/workflow-attachment-handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/swfupload.js"></script>
	
	<script type="text/javascript" src="${wfCtx}/js/swfobject.js"></script>
	
	<script src="${wfCtx}/js/util.js" type="text/javascript"></script>
	<script src="${wfCtx}/js/task.js" type="text/javascript"></script>
	<script src="${wfCtx}/js/workflow-instance.js" type="text/javascript"></script>
	
	<script src="${wfCtx}/js/workflow.js"></script>
	<script src="${wfCtx}/js/opinion.js"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		$("#tabs").tabs();
	});
	function loadUserTree(){
	    $("#user_tree").tree({
	        data:{
		        type:"json",
		        url:"${wfCtx}/engine/tree!load.htm",
		        async:true,
		        async_data:function (NODE){ return {currentId:$(NODE).attr("id") || "INITIALIZED"}}
	      	},
	      	selected    : "array",        // FALSE or STRING or ARRAY
			rules   : {        
				multiple    : "on"
			},
			ui : {
				theme_name : "checkbox",
				context : []
			},
			callback : {
				onchange : function (NODE, TREE_OBJ) {
					treeSetting(NODE, TREE_OBJ);
					}
				}
	    });
	}
	function treeSetting(NODE, TREE_OBJ){
		if(TREE_OBJ.settings.ui.theme_name == "checkbox") {
			var $this = $(NODE).is("li") ? $(NODE) : $(NODE).parent();
			if($this.children("a.unchecked").size() == 0) {
				TREE_OBJ.container.find("a").addClass("unchecked");
			}
			$this.children("a").removeClass("clicked");
			if($this.children("a").hasClass("checked")) {
				$this.find("li").andSelf().children("a").removeClass("checked").removeClass("undetermined").addClass("unchecked");
				var state = 0;
			}
			else {
				$this.find("li").andSelf().children("a").removeClass("unchecked").removeClass("undetermined").addClass("checked");
				var state = 1;
			}
			$this.parents("li").each(function () { 
				if(state == 1) {
					if($(this).find("a.unchecked, a.undetermined").size() - 1 > 0) {
						$(this).parents("li").andSelf().children("a").removeClass("unchecked").removeClass("checked").addClass("undetermined");
						return false;
					}
					else $(this).children("a").removeClass("unchecked").removeClass("undetermined").addClass("checked");
				}
				else {
					if($(this).find("a.checked, a.undetermined").size() - 1 > 0) {
						$(this).parents("li").andSelf().children("a").removeClass("unchecked").removeClass("checked").addClass("undetermined");
						return false;
					}
					else $(this).children("a").removeClass("checked").removeClass("undetermined").addClass("unchecked");
				}
			});
		}
	}
	//分发
	function distribute(){
		ajax("inputForm", webRoot + "/engine/task!save.htm", "wf_task");
	}

	//领取
	function drawTask(){
		ajax("inputForm", webRoot + "/engine/task!receive.htm", "wf_task");
	}
	function readed(){
		submitForm("READED","default");
	}	
	
	//完成分发
	function completeDistribute(value){
		if(value!=""){
			var arr=eval(value);
			for(var i=0;i<arr.length;i++){
				if(arr[i].type=="user" || arr[i].type=="allDepartment" || arr[i].type=="company"){
					if(arr[i].type=="user"){
						$('#wf_form').append('<input name="transactors" type="hidden" value="'+arr[i].loginName+'" />');
					}else if(arr[i].type=="allDepartment" || arr[i].type=="company"){
						$('#wf_form').append('<input name="transactors" type="hidden" value="all_user" />');
						break;
					}
				}
			}
		}
		var tors=$("input[name=transactors]");
		if(tors.length>0){
			ajax("wf_form", webRoot + "/engine/task!distribute.htm", "wf_task");
		}else{
			alert('请选择用户');
		}
	}

	//同意
	function approve(){
		if($("#mustoption").attr("value")=='true'&&$("#editedoption").attr("value")=='false'){
			alert('必须填写意见');
			return ;
		}
		$("#wf_task_transact").attr("value", "APPROVE");
		ajax("inputForm", webRoot + "/engine/task!save.htm", "wf_task");
	}

	//拒绝
	function refuse(){
		if($("#mustoption").attr("value")=='true'&&$("#editedoption").attr("value")=='false'){
			alert('必须填写意见');
			return ;
		}
		$("#wf_task_transact").attr("value", "REFUSE");
		ajax("inputForm", webRoot + "/engine/task!save.htm", "wf_task");
	}
	function ajax(fromId, url, zone, callback){
		$("#"+fromId).attr("action", url);
		ajaxAnywhere.formName = fromId;
		ajaxAnywhere.getZonesToReload = function() {
			return zone;
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			if(typeof callback == "function"){
				callback();
			}
			showMsg("successMessage");
		};
		ajaxAnywhere.submitAJAX();
	}

	function back(){
		parent.window.close();
	}

	</script>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<div style="text-align: left;" >
		<form id="defaultForm" action="" name="defaultForm" method="post"></form>
		<form id="wf_form" name="wf_form" action="${wfCtx}/engine/task!save.htm" method="post">
			<input type="hidden" id="task_id" name="taskId" value="${taskId }" />
			<input type="hidden" id="task_transact" name="transact" value="" >
		</form>
		<aa:zone name="wf_task">
			<p  class="buttonP">
				<span id="taskbutton">
					<s:if test='task.active == 0 && task.processingMode.condition!="阅"'>
						<a href="#" id="saveFormButton"   onclick="saveInputForm('${wfCtx}/engine/task!saveForm.htm')" class="btnStyle">保存</a>
					</s:if>
					<s:if test="task.active == 1">
						<a href="#" class="btnStyle" onclick="assignTransactor()">提交</a>
					</s:if>
						<s:if test='task.active == 2 && task.processingMode.condition != "阅" && task.processingMode.condition !=  "会签式" && task.processingMode.condition !=  "投票式" &&task.processingMode.condition !=  "分发" '>
							<a href="#" onclick="getBack()" class="btnStyle ">取回</a>&nbsp;&nbsp;
						</s:if>
					<s:if test='task.active == 0&&task.processingMode.condition == "编辑式"'>
							<a href="#" id="submitFormButton"  onclick="submitEdit()" class="btnStyle">提交</a>
					</s:if>
					<s:if test='task.active == 0 && (task.processingMode.condition == "审批式" || task.processingMode.condition == "会签式")'>
							<a href="#" class="btnStyle" onclick="approve()">同意</a>
							<a href="#" class="btnStyle" onclick="refuse()">不同意</a>
						<s:if test="task.processingMode.condition == '会签式'">
							<a href="#" onclick='init_tb("${wfCtx}/engine/task!addCountersign.htm?taskId=${taskId }TB_iframe=true&width=300&height=400","加签")' class="btnStyle ">加签</a>
							<a href="#" onclick='init_tb("${wfCtx}/engine/task!deleteCountersign.htm?taskId=${taskId }TB_iframe=true&width=300&height=400","减签")' class="btnStyle ">减签</a>
						</s:if>
					</s:if>
					<s:if test="task.active == 0&&task.processingMode.condition == '签收式'">
							<a href="#" class="btnStyle" onclick="signoff()">签收</a>
					</s:if>
					<s:if test="task.active == 0&&task.processingMode.condition == '投票式'">
							<a href="#" class="btnStyle" onclick="agreement()">赞成</a>
							<a href="#" class="btnStyle" onclick="oppose()">反对</a>
							<a href="#" class="btnStyle" onclick="kiken()">弃权</a>
					</s:if>
					<s:if test='task.active == 0&&task.processingMode.condition == "交办式"'>
							<a href="#"  onclick="assign();" class="btnStyle ">交办</a>
					</s:if>
					<s:if test='task.active == 0&&task.processingMode.condition == "分发"'>
							<a href="#"  onclick="distribute();" class="btnStyle ">分发</a>
					</s:if>
					<s:if test='task.active == 0 && "阅" == task.processingMode.condition  '>
						<a href="#"  onclick="readed();" class="btnStyle ">已阅</a>
					</s:if>	
					<s:if test="task.active==4">
						<a href="#" class="btnStyle " onclick="drawTask();">领取</a>
					</s:if>
					<a href="#" onclick="parent.window.close();" class="btnStyle">关闭</a>
				</span>
				<span id="closeBtn" style="display: none;"><a href="#" onclick="parent.window.close();" class="btnStyle">关闭</a></span>
			</p>
			<div id="successMessage"><s:actionmessage theme="mytheme" /></div>
			<div id="tabs">
				<ul>
					<li><a href="#tabs-1"  onclick="ajaxAnyWhere_workflow('${wfCtx}/engine/task!input.htm?workflowId=${workflowId }&taskId=${taskId }','viewProcess',this);">表单信息</a></li>
					<li ><a  href="#tabs-1"  onclick="ajaxAnyWhere_workflow('${wfCtx}/engine/task!flowHistory.htm?workflowId=${workflowId}&taskId=${taskId }','viewProcess',this);">流转历史</a></li>
					<li><a  href="#tabs-1" onclick="ajaxAnyWhere_workflow('${wfCtx}/engine/task!countersign.htm?workflowId=${workflowId }&taskId=${taskId }','viewProcess',this);">会签结果</a></li>
					<li><a  href="#tabs-1"  onclick="ajaxAnyWhere_workflow('${wfCtx}/engine/task!vote.htm?workflowId=${workflowId }&taskId=${taskId }','viewProcess',this);">投票结果</a></li>
				</ul>
			</div>
			<aa:zone name="viewProcess">
				<div id="tabs-1">
					<input id="taskName"  type="hidden" name="taskName" value="${task.name}"/>
					<%@ include file="task-fragment.jsp" %>
				</div>
			</aa:zone>
		</aa:zone>
		<div style="display: none;" id="custom"></div>
	</div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>

