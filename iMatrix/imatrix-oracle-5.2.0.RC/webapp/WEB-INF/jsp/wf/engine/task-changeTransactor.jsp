<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>更改办理人</title>
	<%@ include file="/common/wf-colorbox-meta.jsp"%>
	
	<script src="${resourcesCtx}/js/public.js" type="text/javascript"></script>
	<script src="${wfCtx }/js/wf.js" type="text/javascript"></script>
	
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	
	<style type="text/css">
		a{text-decoration:none;}
	</style>
	<script type="text/javascript">
	

	function selectMan(id){
		var man = id.split(",");
		if(man[0] == "USER"){
			$("#wf_transactor").attr("value", man[2]);
		}
	}

	var taskid ;
	
	//指派
	function assignTo(){
		var info=getInfo("user");
		if(info!=""){
			var user=eval(info);
			var type=user[0].type;
			if(type == "user"){
				var taskid = $('#current_task option:selected').attr('value');
				if(taskid==null||taskid==''){
					showmmm("请选择任务");
					return;
				}
				$("#changeform_taskId").attr("value",taskid);
				$("#changeforn_transactor").attr("value",user[0].loginName);
				if(validateChangeUser(user[0].loginName)){
				    ajaxSubmit("changeForm", webRoot + "/engine/task!changeTransactorSave.htm", "changeZone", closeWin);
				}else{
					showmmm("不能选择当前环节的其他办理人");
				}
				
			}else{
				showmmm("请选择用户");
			}
		}else{
			showmmm("请选择用户");
		}
	}
    function validateChangeUser(validateLoginName){
        var option = $("#current_task").find('option');
        for(var i=0;i<option.length;i++){
          var loginName = $(option[i]).attr("loginName");
          if(loginName!='undefined'){
            if(loginName==validateLoginName)return false;
          }
        }
        return true;
    }
	
	function closeWin(){
		window.parent.$.colorbox.close();
	}

	function showmmm(msg){
		$("#message").html("<span class='onError' >"+msg+"</span>");
		$("#message").show();
		setTimeout('$("#message").hide("show");',3000);
	}

	</script>
</head>
<body style="padding: 5px; text-align: left" onload="getContentHeight();" >
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="changeZone">
			<form action="" id="changeForm" name="changeForm" method="post">
				<input type="hidden" name="taskId" value="" id="changeform_taskId">
				<input type="hidden" name="transactor" value="" id="changeforn_transactor">
			</form>
			<div class="opt-btn">
				<s:if test="hasActivitySubProcess">
					<button class='btn' onclick="window.parent.$.colorbox.close();" hidefocus="true"><span><span>关闭</span></span></button>
				</s:if><s:else>
					<button id="wf_ok" class='btn' onclick="assignTo();" hidefocus="true"><span><span>确定</span></span></button>
				</s:else>
			</div>
			<div id="opt-content">
				<s:if test="hasActivitySubProcess">
					<font color="red">当前任务为子流程，不能改变办理人</font>
				</s:if><s:else>
					<div id="message"><s:actionmessage theme="mytheme" /></div>
					<div style="margin: 5px">
					选择具体环节：<select id="current_task" >
										<option transactor="" value="">---请选择任务---</option>
									<s:iterator value="taskList">
										<option id="${id }"  value="${id }" transactor="${transactorName }" loginName="${transactor}">${name }(${transactorName })</option>
									</s:iterator>
								</select>
					<br/>			
					</div>
					<input id="wf_transactor" type="hidden" name="transactor">
					<div style="margin-bottom: 10px">
						人员列表：
					<acsTags:tree  defaultable="true" treeId="user_tree" treeType="MAN_DEPARTMENT_TREE" multiple="false"></acsTags:tree>
					</div>
				</s:else>
			</div>
		</aa:zone>
	</div>
</div>
</body>
</html>