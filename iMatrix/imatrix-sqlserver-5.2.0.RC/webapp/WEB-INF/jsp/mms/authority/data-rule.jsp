<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据规则</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"></script>
	<script src="${mmsCtx}/js/authority-data-rule.js" type="text/javascript"></script>
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
		function createDataRule(url){
			
			if($("#ruletypeId").val()==''){
				alert("请选择对应的规则类别！");
			}else{
				ajaxSubmit("defaultForm",url,"main_zone",validateDataRule);
			}
		}
		//验证
		function validateDataRule(){
			$("#saveForm").validate({
				submitHandler: function() {
				var cansave=iMatrix.getFormGridDatas("saveForm","conditionGrid");
					if(cansave){
						$("#saveForm").attr('action','${mmsCtx}/authority/validate-only-code.htm');
						$("#saveForm").ajaxSubmit(function (id){
							if(id=='ok'){
								ajaxSubmit('saveForm','${mmsCtx}/authority/data-rule-save.htm','main_zone',saveCallback);
							}else if(id=='no'){
								showMessage("message", "<font color=\"red\">保存失败,编号已存在</font>");
							}
						});
					}
				},
				rules: {
					ruleTypeName: "required",
					code: "required",
					name: "required",
					dataTableName: "required"
				},
				messages: {
					ruleTypeName: "必填",
					code: "必填",
					name: "必填",
					dataTableName: "必填"
				}
			});
		}
		function saveCallback(){
			showMsg();
			validateDataRule();
		}
		//保存
		function saveDataRule(url){
			$("#saveForm").attr("action",url);
			$("#saveForm").submit();
		}
		//修改
		function updateDataRule(url){
			var ids = jQuery("#dataRuleTable").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">请选择一条数据</font>");
			}else if(ids.length > 1){
				showMessage("message", "<font color=\"red\">只能选择一条数据</font>");
			}else if(ids.length == 1){
				ajaxSubmit("defaultForm",url+"?dataRuleId="+ids[0],"main_zone",validateDataRule);
			}
		}
		//删除
		function deleteDataRule(url){
			var ids = jQuery("#dataRuleTable").getGridParam('selarrrow');
			if(ids.length<=0){
				showMessage("message", "<font color=\"red\">请选择数据</font>");
			}else {
				$.ajax({
					data:{ids:ids.join(",")},
					type:"post",
					url:webRoot+"/authority/data-rule-validateDelete.htm",
					beforeSend:function(XMLHttpRequest){},
					success:function(data, textStatus){
						if(data=='ok'){
							if(confirm("确定要删除吗？")){
								confirmDelete(ids,url);
							}
						}else{
							if(confirm(data)){
								confirmDelete(ids,url);
							}
						}
					},
					complete:function(XMLHttpRequest, textStatus){},
			        error:function(){
		
					}
				});
			}
		}

		function confirmDelete(ids,url){
			
				$.ajax({
					data:{ids:ids.join(",")},
					type:"post",
					url:url,
					beforeSend:function(XMLHttpRequest){},
					success:function(data, textStatus){
						ajaxSubmit("defaultForm",webRoot+'/authority/data-rule.htm',"main_zone",deleteCallBack);
					},
					complete:function(XMLHttpRequest, textStatus){},
			        error:function(){
		
					}
				});
		}

		function deleteCallBack(){
			showMessage("message", "<font color=\"green\">删除成功</font>");
		}

		function selectRuleType(){
			custom_tree({url:webRoot+'/authority/rule-type-tree.htm',
				onsuccess:function(){backRuleType(getSelectValue('id'),getSelectNodeTitle());},//回调方法
				width:500,
				height:320,
				title:'规则类别',
				nodeInfo:['id'],
				multiple:false,
				webRoot:imatrixRoot
			});
		}
		function backRuleType(ruleTypeId,ruleTypeName){
			$("#ruleTypeId").attr("value",ruleTypeId);
			$("#ruleTypeName").attr("value",ruleTypeName);
		}
		function selectDataTable(){
			$.colorbox({href:webRoot+"/authority/data-rule-selectDataTable.htm",iframe:true, innerWidth:600, innerHeight:400,overlayClose:false,title:"数据表"});
		}
		//obj:{rowid:id,currentInputId:id_fieldName}
		function fieldNameClick(obj){
			var dataTableId=$("#dataTableId").val();
			if($("#dataTableId").val()!=''){
				$.colorbox({href:webRoot+"/authority/data-rule-selectColumn.htm?tableId="+dataTableId+"&currentInputId="+obj.currentInputId,iframe:true, innerWidth:600, innerHeight:400,overlayClose:false,title:"数据表"});
			}else{
				alert("请先选择表单！");
			}
		}

		//点击value值事件
		function conditionValueEvent(rowId){
			var enumPath = jQuery("#conditionGrid").jqGrid('getCell',rowId,"enumPath");
			if(typeof(enumPath)!='boolean'){
				if(enumPath.indexOf("enumname:")<0&&enumPath.indexOf("classname:")<0&&enumPath.indexOf("beanname:")<0&&enumPath.indexOf(":")>=0){//当列表字段信息中值设置不是这几种情况,即是key:'value',...时
					$.colorbox({href:webRoot+"/authority/data-rule-setValue.htm?dataValue="+enumPath+"&currentInputId="+rowId,iframe:true, innerWidth:300, innerHeight:200,overlayClose:false,title:"请选择"});
				}
			}
		}
		//验证d字符参数
		function validateFieldString(obj){
			var value = $(obj).val().replace(' ', '');
			if(value.length<=0 || value.length>20){
				alert("条件不能为空且长度不大20");
				$(obj).attr("value", value.substring(0,20));
			}else{
				var b;
				if((b=value.indexOf("{"))>=0 || (b=value.indexOf("}"))>=0 || (b=value.indexOf("\'"))>=0 || (b=value.indexOf("\""))>=0){
					alert("条件不能包含空格、单引号、双引号和大括号");
					$(obj).attr("value", value.substring(0, b));
				}
			}
		}

		function $editClickCallback(rowid,tableId){
			var fieldName=$("#"+rowid+"_fieldName").val();
			if(fieldName != ''){
				var dataType=jQuery("#"+tableId).jqGrid('getCell',rowid,"dataType");
				packagingOperatorUpdate(dataType,rowid,tableId);
			}
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
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" >
		<input type="hidden" id="ruletypeId" name="ruletypeId" value="${ruletypeId }"/>
	</form>
	<aa:zone name="main_zone">
		<div class="opt-btn">
			<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
			<button class="btn" onclick='createDataRule("${mmsCtx}/authority/data-rule-input.htm");'><span><span >新建</span></span></button>
			<button class="btn" onclick="updateDataRule('${mmsCtx}/authority/data-rule-input.htm');"><span><span >修改</span></span></button>
			<button class="btn" onclick="deleteDataRule('${mmsCtx}/authority/data-rule-delete.htm');"><span><span >删除</span></span></button>
		</div>
		<div id="opt-content">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="" name="pageForm" id="pageForm" method="post">
				<view:jqGrid url="${mmsCtx}/authority/data-rule.htm?ruletypeId=${ruletypeId }" code="MMS_DATA_RULE" gridId="dataRuleTable" pageName="page"></view:jqGrid>
			</form>
		</div>
	</aa:zone>
</div>
</div>
</body>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
