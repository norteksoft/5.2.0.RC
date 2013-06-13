<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>列表复制</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>


	<script src="${mmsCtx}/js/form-view.js" type="text/javascript" charset="UTF-8"></script>		
	<script type="text/javascript">
	$(document).ready(function(){
		validateCopy();
	});
	function validateCopy(){
		$("#copyListForm").validate({
			submitHandler: function() {
			copySub();
			},
			rules: {
				code:"required",
				name: "required"
			},
			messages: {
				code:"必填",
				name: "必填"
			}
		});
		
		validateCopyListViewCode();
	}
	function okCopy(){
		$("#copyListForm").submit();
	}
	function copySub(){
		ajaxAnyWhereSubmit("copyListForm", "${mmsCtx }/form/list-view!savecopy.htm", "viewTable", backFunction);
	}
	function backFunction(){
		parent.$.colorbox.close();
		window.parent.back();
	}
	//验证编号唯一
	function validateCopyListViewCode(){
		$("#codeCopy").blur(function(){
			$.ajax({
				   type: "POST",
				   url: "${mmsCtx }/form/list-view!validateListFormCode.htm",
				   data: "soleCode="+$("#codeCopy").attr("value")+"&menuId="+$("#menee_id").val(),
				   success: function(data){
				   		if(data=="true"){
				   			alert('编号 '+$("#codeCopy").attr("value")+' 已存在');
			   				$("#codeCopy").attr("value","");
			   				$("#codeCopy").focus();
				   		}
				   }
				});
		});
	}
	
	</script>
	<style type="text/css">
		.form-table-without-border td input{
			width:250px;
		}
	</style>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<aa:zone name="viewTable">
		<div class="opt-btn">
			<button class="btn" onclick="okCopy();"><span><span>确定</span></span></button>
			<button class="btn" onclick="parent.$.colorbox.close();"><span><span >返回</span></span></button>
		</div>
		<div id="opt-content">
			<form id="copyListForm" name="copyListForm" action="" method="post">
				<input type="hidden" id="menee_id"  name="menuId" value="${menuId }"/>
				<input type="hidden" id="viewId" name="viewId" value="${view.id}" ></input> 
				<table class="form-table-without-border" >
					<tr>
						<td class="content-title">编号：</td>
						<td> <input id="codeCopy" name="code" maxlength="100"></input><span class="required">*</span> </td>
						<td> <span id="codeCopyTip"></span> </td>
					</tr>	
					<tr>
						<td class="content-title">名称：</td>
						<td> <input id="nameCopy" name="name"  maxlength="100"></input><span class="required">*</span> </td>
						<td> <span id="nameCopyTip"></span> </td>
					</tr>
				</table>
			</form>		
		</div>
		
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
