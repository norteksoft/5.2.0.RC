<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>${modulePage.name }</title>
	<%@ include file="/common/mms-meta.jsp"%>
	
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/colorbox/colorbox.css" />
	<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
	
	<!--上传js-->
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/otherHandlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/util.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/text.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/opinion.js"></script>
	
	
	<script src="${imatrixCtx}/wf/js/workflowTag.js"></script>
	<script src="${imatrixCtx}/mms/js/form-view.js"></script>
	<script src="${mmsCtx}/js/mmsapi.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/mms/js/mms-workflow.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/form.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js" ></script>
</head>
<body>
<script type="text/javascript">
		var thirdMenu = "mmm_t_th";
	</script>
  	<%@ include file="/menus/header.jsp"%>
	<div id="secNav">
		<menu:secondMenu></menu:secondMenu>
		<div class="hid-header" onclick="headerChange(this);" title="隐藏"></div>
	</div>
	<div class="ui-layout-west">
		<menu:leftMenu></menu:leftMenu>
	</div>
	<div class="ui-layout-center">
		<div class="opt-body">
			<aa:zone name="default_refresh_zone">
			<s:if test="modulePage==null">
				<div class="opt-btn">
					<button href="#" class="btn" type="button" onclick="window.location.reload(true);"><span><span>返回</span></span></button> 
				</div>
				<div id="opt-content">
					<p>没有定义此列表对应的表单页面</p>
					<p>&nbsp;</p>
					<p>请在 【页面管理】中，为其设置页面</p>
				</div>
			</s:if><s:else>
				<input type="hidden" id="selectWorkflowUrl" value="${workflowUrl}" />
				<form id="default_refresh_form" name="default_refresh_form" method="post">
					<input type='hidden' name="pageId" value="${pageId}" id="_pageId">
				</form>
				<s:if test="(workflowUrl!=null&&workflowUrl!='') || (processId != null&&processId!='')">
						<div class="opt-btn">
							<wf:workflowButtonGroup taskId="${taskId}"></wf:workflowButtonGroup>
							<button href="#" class="btn" type="button" onclick="buttonExecute(this, {execute: toListPage});" pageid="${toPageId }"><span><span>返回</span></span></button> 
						</div>
				</s:if><s:else>
					<button:button code="${modulePage.code}"></button:button>
				</s:else> 
				<div id="opt-content">
					<div id="opt_message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
					<input type='hidden' value="" id="_is_validate_ok"/>
					<form id="default_submit_form" name="default_submit_form" method="post">
						<input type='hidden' value='${validateString}' id="_validate_string" name="validateString" />
						<input type='hidden' id="_wfDefId" name="processId" value="${processId }" />
						<input type='hidden' id="taskId" name="taskId" value="${taskId}">
						<input type='hidden' name="pageId" value="${pageId}" id="pageId">
						<input type='hidden' id="_choose_url" value="${chooseUrl}">
						<input type='hidden' id="assignee" name="assignee" />
						<input type='hidden' id="__transact" name="transact" value="">
						<input type='hidden' id="dataId" name="dataId" value="${data.id}">
						<view:formView code="${formCode}" entity="${data}" version="${formVersion}"></view:formView>
					</form>
					<s:if test="(workflowUrl!=null&&workflowUrl!='') || (processId != null&&processId!='')">
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
				</s:if>
				</div>
				<script type="text/javascript">
				//启用流程
				workflowButtonGroup.btnStartWorkflow.click= function(){
					var url = $('#selectWorkflowUrl').val();
					if("${onlyTable}"=="onlyTable"&&url == '/common/select-workflow.htm'){
						alert("一个表单只能有一个启用的版本");
						return;
					}
					$("#default_submit_form").submit();
					if($('#_is_validate_ok').attr('value')=="TRUE"){
						ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/start.htm", "default_refresh_zone",__startCallback);
					}
				};
				//提交流程
				workflowButtonGroup.btnSubmitWorkflow.click = function(taskId){
					var url = $('#selectWorkflowUrl').val();
					if("${onlyTable}"=="onlyTable"&&url == '/common/select-workflow.htm'){
						alert("一个表单只能有一个启用的版本");
						return;
					}
					$("#default_submit_form").submit();
					if($('#_is_validate_ok').attr('value')=="TRUE"){
						ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit.htm", "default_refresh_zone", __submitOK);
					}
				};
				//取回后保存按钮
				workflowButtonGroup.btnSaveForm.click= function(){
					$("#default_submit_form").submit();
					if($('#_is_validate_ok').attr('value')=="TRUE"){
						ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/save-task.htm", "default_refresh_zone", __startCallback);
					}
				};
				//取回后提交按钮
				workflowButtonGroup.btnSubmitTask.click= function(){
					$("#default_submit_form").submit();
					if($('#_is_validate_ok').attr('value')=="TRUE"){
						$('#__transact').attr('value', 'SUBMIT');
						ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", __submitOK);
					}
				};
				//取回后抄送
				workflowButtonGroup.btnCopyTache.click = function(taskId){
					var acsSystemUrl = "${mmsCtx}";
					popTree({ title :'抄送人员',
						innerWidth:'400',
						treeType:'MAN_DEPARTMENT_TREE',
						defaultTreeValue:'id',
						leafPage:'false',
						multiple:'true',
						hiddenInputId:"assignee",
						showInputId:"assignee",
						acsSystemUrl:imatrixRoot,
						callBack:function(){copyPersonCallBack();}});
					};

					function copyPersonCallBack(){
						$('#assignee').attr("value",jstree.getLoginNames());
						$("#default_submit_form").attr("action","${mmsCtx}/common/copy-tache.htm");
						$("#default_submit_form").ajaxSubmit(function (id){
							alert(id);
						});
					}
					
				//取回后指派
				workflowButtonGroup.btnAssign.click = function(taskId){
					var acsSystemUrl = "${mmsCtx}";
					popTree({ title :'指派任务',
						innerWidth:'400',
						treeType:'MAN_DEPARTMENT_TREE',
						defaultTreeValue:'id',
						leafPage:'false',
						multiple:'false',
						hiddenInputId:"assignee",
						showInputId:"assignee",
						acsSystemUrl:imatrixRoot,
						callBack:function(){btnAssignCallBack();}});
					};

				function btnAssignCallBack(){
					$('#assignee').attr("value",jstree.getLoginName());
					$("#default_submit_form").attr("action","${mmsCtx}/common/assign-tree.htm");
					$("#default_submit_form").ajaxSubmit(function (id){
						alert(id);
						window.location.reload(true);
					});
				}
				//取回
				workflowButtonGroup.btnGetBackTask.click = function(taskId){
					$.ajax({
						data:{taskId: taskId},
						type:"post",
						url:webRoot + "/common/get-back.htm",
						beforeSend:function(XMLHttpRequest){},
						success:function(data, textStatus){
							if(data=="任务已取回"){
								intoInput();
							}else{
								alert(data);
							}
						},
						complete:function(XMLHttpRequest, textStatus){},
				        error:function(){
			
						}
					});
				};

				//选择办理人
				workflowButtonGroup.btnAssignTransactor.click = function(taskId){
					workflowButtonGroup.btnSubmitWorkflow.click(taskId);
				};
				
				//选择环节
				workflowButtonGroup.btnChoiceTache.click = function(taskId){
					workflowButtonGroup.btnSubmitWorkflow.click(taskId);
				};
				function __submitOK(){
					var url = $('#_choose_url').val();
					if('choose_user'==url){
						popbox("select_transactor", 300, 400, "请选择办理人");
						return;
					}
					if(url != ''){
						url = url+'&closeFlag=false';
						$.colorbox({href:webRoot+url,iframe:true, innerWidth:700, innerHeight:500,overlayClose:false,title:"选择办理人",onClosed:function(){intoInput();}});
					}else{
						window.location.reload(true);
					}
				}
				function __startCallback(){
					__show_message('opt_message','保存成功','onSuccess');
					_formValidate();
					getContentHeight();
				}

				$(document).ready(function(){
						_formValidate();
						addFormValidate('${validateString}','default_submit_form');
						$("#workflowButtonGroup_btnStartWorkflow").html('<span><span>'+workflowButtonGroup.btnStartWorkflow.name+'</span></span>');
						$("#workflowButtonGroup_btnSubmitWorkflow").html('<span><span>'+workflowButtonGroup.btnSubmitWorkflow.name+'</span></span>');
				});

				function intoInput(){
					ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/input.htm", "default_refresh_zone",getContentHeight);
				}
				function __choose_transctor_call_back(){
					window.location.reload(true);
				}
				</script>
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
						<input name="taskId" id="transact_task_id" type="hidden" value="${taskId}"/>
					</form>
					<script type="text/javascript">
						function selectTransactorOk(){
							if($("input[name='transactor']:checked").length==1){
								ajaxAnyWhereSubmit("select_transactor_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", __choose_transctor_call_back);
							}else{
								alert('请选择办理人');
							}
						}
					</script>
					</s:if>
				</div>
			</s:else>
			</aa:zone>
		</div>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>
