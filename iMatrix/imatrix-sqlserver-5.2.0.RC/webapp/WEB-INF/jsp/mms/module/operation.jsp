<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>功能管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript">
	function createOperation(){
		$("#entiyId").attr("value","");
		ajaxSubmit("defaultForm", "${mmsCtx}/module/operation-input.htm", "pageTable",createCallback);
	}
	function createCallback(){
		getContentHeight();
		validateOperation();
	}
	function validateOperation(){
		$("#viewSaveForm").validate({
			submitHandler: function() {
				var cansave=iMatrix.getFormGridDatas("viewSaveForm","childGridId");
				if(cansave){
					ajaxSubmit('viewSaveForm','${mmsCtx}/module/operation-save.htm','pageTablelist',saveCallback);
				}
			},
			rules: {
				code: "required",
				name:"required"
			},
			messages: {
				code: "必填",
				name:"必填"
			}
		});
	}
	function saveCallback(){
		showMsg();
		validateOperation();
	}
	function updateOperation(){
		var boxes = jQuery("#operationGridId").jqGrid("getGridParam",'selarrrow');
		if(boxes.length<=0){
			alert("请选择记录");
			return;
		}else if(boxes.length>1){
			alert("只能选择一条记录");
			return;
		}else{
			var parentRowData = jQuery("#operationGridId").jqGrid("getRowData",boxes[0]);
			var parentId=parentRowData['parent.id'];
			if(parentId!=''){
				$("#entiyId").attr("value",parentRowData['parent.id']);
			}else{
				$("#entiyId").attr("value",boxes[0]);
			}
			
			ajaxSubmit("defaultForm", "${mmsCtx}/module/operation-input.htm", "pageTable",updateCallBack);
		}
	}
	function updateCallBack(){
		getContentHeight();
		validateOperation();
	}
	function deleteOperations(){
		var boxes = jQuery("#operationGridId").jqGrid("getGridParam",'selarrrow');
		if(boxes.length<=0){
			alert("请选择记录");
			return;
		}else{
			var parentIds=new Array();
			for(var i=0;i<boxes.length;i++){
				var parentRowData = jQuery("#operationGridId").jqGrid("getRowData",boxes[i]);
				var parentId=parentRowData['parent.id'];
				if(parentId!=''){
					if(!contains(parentIds,parentId)){
						parentIds.push(parentId);
					}
				}else{
					if(!contains(parentIds,boxes[i])){
						parentIds.push(boxes[i]);
					}
				}
			}
			$("#ids").attr("value",parentIds.join(','));
			ajaxSubmit("defaultForm", "${mmsCtx}/module/operation-delete.htm", "pageTablelist");
		}
	}

	function contains(parentIds,parentId){
		for(var i=0;i<parentIds.length;i++){
			if(parentIds[i]==parentId)return true;
		}
		return false;
	}

	function saveOperation(){
		checkCode($("#code").attr("value"),$("#operationId").attr("value"),true,false,true);
	}
	function backPage(){
		setPageState();
		ajaxSubmit("backForm", "${mmsCtx}/module/operation.htm", "pageTable");
	}

	function parentCodeBlur(){
		checkCode($("#code").attr("value"),$("#operationId").attr("value"),true,false,false);
	}

	function codeBlur(obj){
		var rowid=obj.rowid;
		var code=$("#"+obj.currentInputId).attr("value");
		checkCode(code,rowid,false,false,false);
	}
	//code:编码，operationId：功能id，isParent：是否是父功能编码处的onblur事件,isEditCallback:是否是编辑行时键盘事件的回调方法,isSubmit:是否提交时验证
	function checkCode(code,operationId,isParent,isEditCallback,isSubmit){
		var isExist=false;
		if(code!=""){
			//验证当前表格中的编码是否有与 当前编辑行code或父功能编码code重复的记录
			var rows=$("#childGridId").jqGrid("getRowData");
			for(var i=0;i<rows.length;i++){
				if(rows[i].id!=operationId){
					if(rows[i].code==code){
						isExist=true;
						if(isEditCallback){
							$("#childGridId").jqGrid("delRowData",operationId);
						}
						alert("该编号"+code+"已存在");
						return;
					}
				}
			}
			//如果不存在
			if(!isExist){
				//验证当前编辑行code是否和父功能编码重复
				if(!isParent){
					var parentCode=$("#code").attr("value");
					if(parentCode==code){
						if(isEditCallback){
							$("#childGridId").jqGrid("delRowData",operationId);
						}
					isExist=true;alert("该编号"+code+"已存在");return;}
				}
				//如果不存在
				if(!isExist){
					var oraginalRowId=operationId;
					if(operationId.indexOf("new_")>=0){
						operationId="";
					}
					//查询
					$.ajax({
						type : "post",
						dataType : "json",
						data : "operationCode="+code+"&operationId="+operationId+"&systemId="+$("#_systemId").val(),
						url : "${mmsCtx}/module/operation-check-code.htm",
						success : function(data) {
							if(data){
								if(!isParent){//不是父功能编码的onblur事件时
									if(isEditCallback){//是编辑行时键盘事件的回调方法
										$("#childGridId").jqGrid("delRowData",oraginalRowId);
									}
								}
								isExist=true;
							  alert("该编号"+code+"已存在");
						   }else{
							   if(isSubmit){
								   $("#viewSaveForm").submit();
							   }
						   }
						},
						error : function() {
							alert("服务器繁忙，请稍后再操作...");
						}
					});
				}
			}
		}else{//直接走验证
			$("#viewSaveForm").submit();
		}
		return isExist;
	}
	function $editRowSave(rowid,tableId){
		var row=$("#childGridId").jqGrid("getRowData",rowid);
		checkCode(row.code,rowid,false,true,false);
	}
	function $editRowRestore(rowid,tableId){
		var row=$("#childGridId").jqGrid("getRowData",rowid);
		checkCode(row.code,rowid,false,true,false);
	}
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<form id="defaultForm" name="defaultForm"action="" method="post" >
			<input type="hidden" id="systemId"  name="systemId" value="${systemId }"/>
			<input type="hidden" id="entiyId"  name="operationId" />
			<input type="hidden" id="ids"  name="ids" />
		</form>
		<aa:zone name="pageTable">
			<div class="opt-btn">
				<button class="btn" onclick="createOperation();"><span><span>新建</span></span></button>
				<button class="btn" onclick="updateOperation();"><span><span >修改</span></span></button>
				<button class="btn" onclick="deleteOperations();"><span><span >删除</span></span></button>
			</div>
			<div id="opt-content">
				<aa:zone name="pageTablelist">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<form action="" name="pageForm" id="pageForm" method="post">
					<view:jqGrid url="${mmsCtx}/module/operation.htm?systemId=${systemId }" code="MMS_OPERATION" pageName="pages" gridId="operationGridId"></view:jqGrid>
				</form>
				</aa:zone>
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