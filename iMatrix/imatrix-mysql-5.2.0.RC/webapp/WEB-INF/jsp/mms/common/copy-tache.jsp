<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<html>
<head>
	<title>流程管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${mmsCtx}/widgets/tree/_lib/css.js"></script>
	<link rel="stylesheet" type="text/css" href="${mmsCtx}/widgets/tree/tree_component.css" />
	<script type="text/javascript" src="${mmsCtx}/widgets/tree/tree_component.js"></script>
	<style type="text/css">
		a{text-decoration:none;}
	</style>
	<script type="text/javascript">
		function completeCopyTache(value, position){
			var arr=eval(value);
			if(typeof(arr)!='undefined'){
				for(var i=0;i<arr.length;i++){
					if(arr[i].type=="user" || arr[i].type=="allDepartment" || arr[i].type=="company"){
						if(arr[i].type=="user"){
							$('#copy_tache_form').append('<input name="transactors" type="hidden" value="'+arr[i].loginName+'" />');
						}else if(arr[i].type=="allDepartment" || arr[i].type=="company"){
							$('#copy_tache_form').append('<input name="transactors" type="hidden" value="all_user" />');
							break;
						}
					}
				}
			}
			var tors=$("input[name=transactors]");
			if(tors.length>0){
				if(position=="copyTache"){
					ajaxAnyWhereSubmit("copy_tache_form", "${mmsCtx}/common/copy.htm", "", copyOk);
				}else{
				}
			}else{
				alert('请选择用户');
			}
		}
		
		function copyOk(){
			parent.__show_message('opt_message','抄送成功');
			parent.tb_remove();
		}
	</script>
</head>
<body style="padding: 5px;">
<div class="ui-layout-center">
	<aa:zone name="wf_task">
		<div style="margin-left: 10px;margin-top: 10px;">
			<p class="buttonP">
				<a href="#" onclick="completeCopyTache(getInfo(),'copyTache');" class="btnStyle ">确定</a>
			</p>
			<div style="margin-top: 20px;">
				<acsTags:tree defaultable="true" treeId="user_tree" treeType="MAN_DEPARTMENT_TREE" multiple="true"></acsTags:tree>
			</div>
			<form id="copy_tache_form">
				<input name="taskId" type="hidden" value="${taskId}"/>
			</form>
		</div>
	</aa:zone>
</div>
</body>
</html>