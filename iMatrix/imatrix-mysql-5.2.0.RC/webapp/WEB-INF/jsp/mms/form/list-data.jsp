<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
	
	
	<link href="${imatrixCtx}/widgets/formeditor/themes/default/default.css" rel="stylesheet" type="text/css" />
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	
	<script src="${mmsCtx}/js/dataTable.js" type="text/javascript"></script>
	<script src="${mmsCtx}/js/form-view.js" type="text/javascript" charset="UTF-8"></script>
	
	<script type="text/javascript">
		//返回调用,保持页数。
		function goBackForm(form,url,zone,jemesaId){
		//	getPageStateAttr(jemesaId, form);
			ajaxAnyWhereSubmit("backForm", "", "form_main",goBackCallback);
		}
		function goBackCallback(){
			parent.showWestAndNorth();
			validateNext();
		}
		function next(){
			$.colorbox.close();
			ajaxAnyWhereSubmit("inputForm", "", "form_main",inputCallBack);
			
		}
		function update(id){
			//隐藏查询框
			HideSearchBox();
			if(id){
				$("#updateForm > input[name='formId']").attr("value",id);
			}else{
				var ids = jQuery("#page").getGridParam('selarrrow');
				if(ids==""){
					showMessage("message", "<font color=\"red\">请选择一条数据</font>");
					return;
				}else if(ids.length > 1){
					showMessage("message", "<font color=\"red\">只能选择一条数据</font>");
					return;
				}else if(ids.length == 1){
					$("#updateForm > input[name='formId']").attr("value",ids[0]);
				}
			}
			ajaxAnyWhereSubmit("updateForm", "", "form_main",inputCallBack);
		}
		function copy(){
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">请选择一条数据</font>");
			}else if(ids.length > 1){
				showMessage("message", "<font color=\"red\">只能选择一条数据</font>");
			}else if(ids.length == 1){
				var meId = $("#menuId").val();
				$.colorbox({href:"${mmsCtx}/form/form-view!copy.htm?menuId="+meId+"&formId="+ids[0],iframe:true, innerWidth:400, innerHeight:300,overlayClose:false,title:"复制表单"});
			}
		}
		$(document).ready(function(){
			validateNext();
		});
		function validateNext(){
			$("#inputForm").validate({
				submitHandler: function() {
				   var code = $("#code").val();
					if(validateFormCode(code)){
						next();
					}else{
						alert("表单编号只能包含下划线(_),不能包含其他任何特殊字符");
					}
				},
				rules: {
					code:"required",
					name: "required",
					remark:{
						maxlength:500
					}
				},
				messages: {
					code:"必填",
					name: "必填",
					remark:{
						maxlength:"最多输入500字"
					}
				}
			});
			validateFormViewCode();
		}

		//验证表单编号是否符合规则，表单编号只能包含"_",否则返回false;
		function validateFormCode(code){
			return code.indexOf("-")<0&&code.indexOf("~")<0
			&&code.indexOf("!")<0&&code.indexOf("@")<0
			&&code.indexOf("#")<0&&code.indexOf("$")<0
			&&code.indexOf("%")<0&&code.indexOf("%")<0
			&&code.indexOf("^")<0&&code.indexOf("&")<0
			&&code.indexOf("*")<0&&code.indexOf("(")<0
			&&code.indexOf(")")<0&&code.indexOf("+")<0
			&&code.indexOf("=")<0&&code.indexOf("{")<0
			&&code.indexOf("}")<0&&code.indexOf("|")<0
			&&code.indexOf("\\")<0&&code.indexOf("[")<0
			&&code.indexOf("]")<0&&code.indexOf(";")<0
			&&code.indexOf("'")<0&&code.indexOf(":")<0
			&&code.indexOf("\"")<0&&code.indexOf("<")<0
			&&code.indexOf(">")<0&&code.indexOf("?")<0
			&&code.indexOf(",")<0&&code.indexOf(".")<0
			&&code.indexOf("/")<0&&code.indexOf("￥")<0
			&&code.indexOf("（")<0&&code.indexOf("）")<0
			&&code.indexOf("——")<0&&code.indexOf("【")<0
			&&code.indexOf("】")<0&&code.indexOf("、")<0
			&&code.indexOf("；")<0&&code.indexOf("‘")<0
			&&code.indexOf("：")<0&&code.indexOf("“")<0
			&&code.indexOf("《")<0&&code.indexOf("》")<0
			&&code.indexOf("，")<0&&code.indexOf("。")<0
			&&code.indexOf("？")<0&&code.indexOf(" ")<0
			&&code.indexOf("`")<0&&code.indexOf("·")<0;
		}
		
		function ok(){
			$("#inputForm").submit();
		}
		var editor;
		function inputCallBack(){
			var hh = $(window).height();
			var ww = $(window).width();
			parent.hideWestAndNorth();
               editor = KindEditor.create('#content', {
               	width:ww+170,
   				height:hh+50,
   				themeType : 'default',
   				filterMode:false,
   				resizeType: 0 ,
   				items : ['source', '|','save','preview','back','undo','redo', 'print', 'cut', 'copy', 'paste','plainpaste', 'wordpaste','|',
   							'justifyleft','justifycenter','justifyright','justifyfull','insertorderedlist', 'insertunorderedlist',
   							'indent', 'outdent', 'subscript','superscript', '|','selectall', '-',
   							'fontname', 'fontsize', '|','forecolor', 'hilitecolor','bold', 'italic', 'underline', 'strikethrough', 'removeformat','|',
   							 'table','hr', '|', 'text','textarea','time','-',
   			     			'URGENCY','CREATE_SPECIAL_TASK','SELECT_MAN_DEPT','CALCULATE_COMPONENT','PULLDOWNMENU','DATA_SELECTION','DATA_ACQUISITION','LIST_CONTROL','STANDARD_LIST_CONTROL','BUTTON','LABEL']
   			
               });
		}
				
		function saveNewVersion(){
			$("#operation").attr("value","addVersion");
			ajaxSave();
		}
		function editorSave(editor){
				$("#html").attr("value",editor.html());
			if($("#inputForm > input[name='formId']").attr("value")==""||$("#formStates").val()=='DRAFT'||$("#formStates").val()==''){
				//直接保存
				ajaxSave();
			}else{
				//弹出保存选项 
				$.colorbox({href:"#saveChoice",inline:true, innerWidth:380, innerHeight:100,overlayClose:false,title:"保存项"});
			}
		}

		function ajaxSave(){
			$("html").attr("value",editor.html());
			$("#inputForm").ajaxSubmit(function (data){
				var result = data.split(":");
				if(result[0]=="id"){
					$("#formId").attr("value",result[1]);
					successTip("保存成功");
				}else if(result[0]=="ms"){
					errorTip("错误:"+result[1]);
				}
			});
		}
		function deleteForm(){
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==''){
				showMessage("message", "<font color=\"red\">请选择一条数据</font>");
			}else{
				var canPost = true;
				$.each(ids, function(i){
					var id = ids[i];
					var state=jQuery("#page").jqGrid("getCell",id,"formState");
					if(state!= "DRAFT"){
						showMessage("message", "<font color=\"red\">不能删已启用和禁用的表单</font>");
						canPost = false;
					}
				});
				if(canPost){
					if(confirm("确定删除吗？")){
						$.each(ids, function(i){
							$("#pageForm").append(createHiddenInput("formViewDeleteIds", ids[i]));
						});
						setPageState();
						ajaxSubmit("pageForm", webRoot+"/form/form-view-delete.htm?menuId=${menuId}", "viewTable", deleteCallBack);
					}
				}
			}
		}
		function deleteEnableFormView(){
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==''){
				showMessage("message", "<font color=\"red\">请选择一条数据</font>");
			}else{
				
				if(confirm("将删除相关联的页面，确定删除吗？")){
					$.each(ids, function(i){
						$("#pageForm").append(createHiddenInput("formViewDeleteIds", ids[i]));
					});
					$("#pageForm").append(createHiddenInput("deleteEnable", true));
					ajaxSubmit("pageForm", webRoot+"/form/form-view-delete.htm?menuId=${menuId}", "viewTable", deleteCallBack);
				}
			}
		}
		//验证编号唯一
		function validateFormViewCode(){
			$("#code").blur(function(){
				$.ajax({
					   type: "POST",
					   url: "${ctx }/form/form-view!validateFormCode.htm",
					   data: "soleCode="+$("#code").attr("value")+"&formId="+$("#formId").attr("value"),
					   success: function(data){
					   		if(data=="true"){
					   			alert('编号 '+$("#code").attr("value")+' 已存在');
				   				$("#code").attr("value","");
				   				$("#code").focus();
					   		}
					   }
					}); 
			});
		}
		//改变表单状态
		function changeFormStates(){
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">请选择一条数据</font>");
			}else if(ids.length >= 1){
				$.each(ids, function(i){
					$("#updateForm").append(createHiddenFormInput("formViewIds", ids[i]));
				});
				ajaxSubmit("updateForm", webRoot+"/form/form-view-state.htm", "form_main", changeCallBack);
			}
		}
		function createHiddenFormInput(name, value){
			return "<input type=\"hidden\" name=\""+name+"\" value=\""+value+"\"/>";
		}
		function changeCallBack(){
			showMsg("message",5000);
			validateNext();
		}
		function deleteCallBack(){
			showMsg("message",2000);
		}
		function back(){
			ajaxSubmit("defaultForm",webRoot+"/form/list-data.htm", "form_main",validateNext);
		}
		//创建一个隐藏域
		function createHiddenInput(name, value){
			return "<input type=\"hidden\" name=\""+name+"\" value=\""+value+"\"/>";
		}
		function createForm(){
			$.colorbox({href:"#subPage",inline:true, innerWidth:600, innerHeight:300,overlayClose:false,title:"创建表单"});
		}

		function exportFormView(){
			$("#exportForm").find("input[name='formViewIds']").remove();
			var ids = jQuery("#page").getGridParam('selarrrow');
			if(ids==''){
				if(confirm("是否导出当前系统下的所有表单?")){
					$("#exportForm").attr("action",webRoot+"/form/export-form-view.htm");
					$("#exportForm").submit();
				}
			}else{
				$.each(ids, function(i){
					$("#exportForm").append(createHiddenInput("formViewIds", ids[i]));
				});
				$("#exportForm").attr("action",webRoot+"/form/export-form-view.htm");
				$("#exportForm").submit();
			}
		}
		function importFormView(){
			$.colorbox({href:'${mmsCtx}/form/show-import-form-view.htm',
				iframe:true, innerWidth:350, innerHeight:100,overlayClose:false,title:"导入表单"});
		}

		function listViewList(){
			ajaxSubmit("exportForm",  webRoot+"/form/list-data.htm","viewTable");
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body >
<div class="ui-layout-center">
<form id="exportForm" name="exportForm"action="" method="post" >
	<input type="hidden" id="menu_Id"  name="menuId" value="${menuId }"/>
</form>
<form id="defaultForm" name="defaultForm"action="" method="post" >
	<input type="hidden" id="menuId"  name="menuId" value="${menuId }"/>
	<input id="formTypeId"  name="formTypeId" type="hidden"></input>
</form>
<div class="opt-body">
	<aa:zone name="form_main">
		<s:if test="menuId!=null">
			<div class="opt-btn">
				<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
				<button class="btn" onclick='createForm();' id="create"><span><span >新建</span></span></button>
				<button class="btn" onclick="copy();"><span><span >复制</span></span></button>
				<button class="btn" onclick="update();"><span><span >修改</span></span></button>
				<button class="btn" onclick="changeFormStates();"><span><span >启用/禁用</span></span></button>
				<button class="btn" onclick="deleteForm();"><span><span >删除</span></span></button>
				<button class="btn" onclick="exportFormView();"><span><span >导出</span></span></button>
				<button class="btn" onclick="importFormView();"><span><span >导入</span></span></button>
				<button class="btn" onclick="deleteEnableFormView();"><span><span >删除已启用</span></span></button>
			</div>
			<aa:zone name="viewTable">
				<div id="opt-content">
					<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
					<form name="updateForm" id="updateForm" action="${mmsCtx }/form/form-view!next.htm">
						<s:hidden name="formId"></s:hidden>
						<s:hidden name="dataTableId"></s:hidden>
						<input type="hidden" id="menuId"  name="menuId" value="${menuId }"/>
						<input type="hidden" name="states" value="all"/>
					</form>
					<form action="${mmsCtx}/form/list-data.htm" name="pageForm" id="pageForm" method="post">
						<view:jqGrid url="${mmsCtx}/form/list-data.htm?menuId=${menuId }" code="MMS_FORM_VIEW" gridId="page" pageName="page"></view:jqGrid>
					</form>
					<div style="display: none;">
						<div id="subPage" align="left" style="margin: 10px;">
							<div class="opt-btn">
								<button class="btn" onclick="ok();"><span><span>确定</span></span></button>
								<button class="btn" onclick="$.colorbox.close();"><span><span >返回</span></span></button>
							</div>
							<form id="inputForm" name="inputForm" action="${mmsCtx }/form/form-view!next.htm" method="post">
									<table class="form-table-without-border">
										<s:hidden id="menuId" name="menuId" theme="simple"></s:hidden>
										<tr>
											<td class="content-title">编号：</td>
											<td> <s:textfield id="code" name="code" theme="simple" maxlength="26"></s:textfield><span class="required">*</span> </td>
											<td> <span id="codeTip"></span> </td>
										</tr>	
										<tr>
											<td class="content-title">名称：</td>
											<td> <s:textfield id="name" name="name" theme="simple"></s:textfield><span class="required">*</span>  </td>
											<td> <span id="nameTip"></span> </td>
										</tr>
										
										<tr>
											<td class="content-title">备注：</td>
											<td> 
											</td>
											<td> <span id="remarkTip"></span> </td>
										</tr>
										<tr>
											<td class="content-title"></td>
											<td colspan="2"> 
												<s:textarea theme="simple"  name="remark" id="remark"  cols="55" rows="5" ></s:textarea>
											</td>
										</tr>
									</table>
								</form>		
						</div>
					</div>
					
				</div>
				
			</aa:zone>
		</s:if>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
