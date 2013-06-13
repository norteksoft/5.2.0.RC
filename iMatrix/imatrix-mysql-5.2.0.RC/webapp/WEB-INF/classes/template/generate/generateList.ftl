<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title></title>
	<%@include file="/common/meta.jsp" %>
	<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/workflowTag.js"></script>
	<script type="text/javascript">
	<#if containWorkflow?if_exists>
		var buttonSign="";
	</#if>
		//新建
		function create${entityName}(url){
			ajaxSubmit("defaultForm",url,"main",create${entityName}Callback);
		}
		function create${entityName}Callback(){
			validate${entityName}();
		}
		//验证
		function validate${entityName}(){
			$("#inputForm").validate({
				submitHandler: function() {
					$("#inputForm").ajaxSubmit(function (id){
						$("#id").attr("value",id);
						$("#message").show("show");
						setTimeout('$("#message").hide("show");',3000);
						<#if containWorkflow?if_exists>
						if(buttonSign=="firstSubmit"){
							buttonSign="";
							setPageState();
							ajaxSubmit("defaultForm","${ctx}/${namespace}/${entityAttribute}-list.htm","main");
						}
						</#if>
					});
				},
				rules: {
					
				},
				messages: {
					
				}
			});
		}
		
		//修改
		function update${entityName}(url){
			var ids = jQuery("#${entityAttribute}GridId").getGridParam('selarrrow');
			if(ids==""){
				alert("请选择一条数据");
			}else if(ids.length > 1){
				alert("只能选择一条数据");
			}else if(ids.length == 1){
				ajaxSubmit("defaultForm",url+"?id="+ids[0],"main",create${entityName}Callback);
			}
		}
		
		//删除
		function delete${entityName}(url){
			var ids = jQuery("#${entityAttribute}GridId").getGridParam('selarrrow');
			if(ids.length<=0){
				alert("请选择数据");
			}else {
				aa.submit('defaultForm', url+'?ids='+ids.join(','), 'main');
			}
		}

		<#if containWorkflow?if_exists>
		workflowButtonGroup.btnStartWorkflow.click = function(taskId){
			save${entityName}('${ctx}/${namespace}/${entityAttribute}-save.htm');
		};
		workflowButtonGroup.btnSubmitWorkflow.click = function(taskId){
			submit${entityName}('${ctx}/${namespace}/${entityAttribute}-submitProcess.htm');
		};
		
		function submit${entityName}(url){
			buttonSign="firstSubmit";
			$('#taskTransact').val("SUBMIT");
			$("#inputForm").attr("action",url);
			$("#inputForm").submit();
		}
		</#if>
		function save${entityName}(url){
			$("#inputForm").attr("action",url);
			$("#inputForm").submit();
		}
	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="product";
	</script>
	
	<%@ include file="/menus/header.jsp" %>

	<%@ include file="/menus/second-menu.jsp" %>
	
	<div class="ui-layout-west">
		<%@ include file="/menus/third-menu.jsp" %>
	</div>
	
	<div class="ui-layout-center">
		<div class="opt-body">
			<form id="defaultForm" name="defaultForm" method="post"  action=""></form>
			<aa:zone name="main">
				<div class="opt-btn">
					<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
					<button class="btn" onclick="create${entityName}('${ctx}/${namespace}/${entityAttribute}-input.htm');"><span><span>新建</span></span></button>
					<button class="btn" onclick="update${entityName}('${ctx}/${namespace}/${entityAttribute}-input.htm');"><span><span>修改</span></span></button>
					<button class="btn" onclick="delete${entityName}('${ctx}/${namespace}/${entityAttribute}-delete.htm');"><span><span >删除</span></span></button>
				</div>
				<div id="message"><s:actionmessage theme="mytheme" /></div>	
				<script type="text/javascript">setTimeout("$('#message').hide('show');",3000);</script>
				<div id="opt-content" >
					<form id="contentForm" name="contentForm" method="post"  action="">
						<grid:jqGrid gridId="${entityAttribute}GridId" url="${ctx}/${namespace}/${entityAttribute}-listDatas.htm" submitForm="defaultForm" code="${listCode}" ></grid:jqGrid>
					</form>
				</div>
			</aa:zone>
		</div>
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>