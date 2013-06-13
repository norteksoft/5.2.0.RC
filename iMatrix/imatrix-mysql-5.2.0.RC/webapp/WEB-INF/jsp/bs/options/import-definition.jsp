<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript">
		//通用消息提示
		function showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			setTimeout('$("#'+id+'").hide("show");',3000);
		}

		//新建
		function createImport(url){
			ajaxSubmit("defaultForm",url,"import_main",createImportCallback);
		}
		function createImportCallback(){
			validateImport();
			getContentHeight();
		}
		//验证
		function validateImport(){
			$("#inputForm").validate({
				submitHandler: function() {
					$("#inputForm").ajaxSubmit(function (id){
						if(id.indexOf('ok')>-1){
							$("#importDefinitionId").attr("value",id.replace("ok",""));
							showMessage("message", "<font color=\"green\">保存成功</font>");
						}else if(id.indexOf('no')>-1){
							showMessage("message", "<font color=\"red\">保存失败,编号已存在</font>");
						}
					});
				},
				rules: {
					code: "required",
					alias: "required"
				},
				messages: {
					code: "必填",
					alias: "必填"
				}
			});
		}

		//页签跳转设置
		function pageUlChange(flag){
			var id = $("#importDefinitionId").val();
			if(flag == 'b'){
				if(id == ""||typeof(id)=='undefined'){
					showMessage("message", "<font color=\"red\">先保存数据表信息后，才能设置字段信息</font>");
				}else{
					ajaxSubmit("inputForm",webRoot+"/options/import-definition-column.htm","btnZone,importContext",definitionColumnBack);
				}
			}else{
				ajaxSubmit("inputForm",webRoot+"/options/import-definition-input.htm","btnZone,importContext", createImportCallback);
			}
		}

		function definitionColumnBack(){
			setFormgridHeight('importColumnId',$(window).height()-140);
		}
		//保存基本信息
		function saveImportDefinition(url){
			$("#inputForm").attr("action",url);
			$("#inputForm").submit();
		}

		//修改
		function updateImport(url){
			var ids = jQuery("#importDefinitionTable").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">请选择一条数据</font>");
			}else if(ids.length > 1){
				showMessage("message", "<font color=\"red\">只能选择一条数据</font>");
			}else if(ids.length == 1){
				ajaxSubmit("defaultForm",url+"?importDefinitionId="+ids[0],"import_main",createImportCallback);
			}
		}

		//删除
		function deleteImport(url){
			var ids = jQuery("#importDefinitionTable").getGridParam('selarrrow');
			if(ids.length<=0){
				showMessage("message", "<font color=\"red\">请选择数据</font>");
			}else {
				if(confirm("确定要删除吗？")){
					$.ajax({
						data:{ids:ids.join(",")},
						type:"post",
						url:url,
						beforeSend:function(XMLHttpRequest){},
						success:function(data, textStatus){
							ajaxSubmit("defaultForm",webRoot+'/options/import-definition.htm',"import_main",deleteColumnsCallBack);
							
						},
						complete:function(XMLHttpRequest, textStatus){},
				        error:function(){
			
						}
					});
				}
			}
		}

		function deleteColumnsCallBack(){
			showMessage("message", "<font color=\"green\">删除成功</font>");
		}

		//保存导入列
		function saveColumns(){
			var cansave=iMatrix.getFormGridDatas("inputForm","importColumnId");
			if(cansave){
				ajaxSubmit("inputForm",  webRoot+"/options/import-definition-column-save.htm", "importContext", saveColumnsCallBack);
			}
		}	

		function saveColumnsCallBack(){
			setFormgridHeight('importColumnId',$(window).height()-140);
			showMessage("message", "<font color=\"green\">保存成功</font>");
		}	

		//导入
		function importData(url){
			var ids = jQuery("#importDefinitionTable").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">请选择一条数据</font>");
			}else if(ids.length > 1){
				showMessage("message", "<font color=\"red\">只能选择一条数据</font>");
			}else if(ids.length == 1){
				$.colorbox({href:url+"?importDefinitionId="+ids[0],iframe:true, innerWidth:300, innerHeight:100,overlayClose:false,title:'导入'});
			}
			
		}
		function importTypeChange(obj){
			if('TXT_DIVIDE'==obj.value){
				$("#divideTr").show() ;
			}else{
				$("#divideTr").hide();
				$("#divide").attr("value","");
			}
		}
	</script>
	<title></title>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<form id="defaultForm" name="defaultForm"action="" method="post" ></form>
		<aa:zone name="import_main">
			<div class="opt-btn">
				<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
				<button class="btn" onclick="createImport('${settingCtx}/options/import-definition-input.htm');"><span><span>新建</span></span></button>
				<button class="btn" onclick="updateImport('${settingCtx}/options/import-definition-input.htm');"><span><span>修改</span></span></button>
				<button class="btn" onclick="deleteImport('${settingCtx}/options/import-definition-delete.htm');"><span><span >删除</span></span></button>
				<button class="btn" onclick="importData('${settingCtx}/options/import-definition-import.htm');"><span><span >导入</span></span></button>
			</div>
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<div id="opt-content" >
				<form action="" name="pageForm" id="pageForm" method="post">
					<view:jqGrid url="${settingCtx}/options/import-definition.htm" code="BS_IMPORT_DEFINITION" pageName="page" gridId="importDefinitionTable" submitForm="defaultForm"></view:jqGrid>
				</form>
			</div>
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>