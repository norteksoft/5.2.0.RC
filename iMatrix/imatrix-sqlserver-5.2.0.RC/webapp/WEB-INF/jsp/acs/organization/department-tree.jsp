<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="company.companyManager"/></title>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<div id="opt-content">
			<div id="_department_tree" class="demo"></div>
		</div>
	</div>
<script type="text/javascript">
$(function () {
	$.ajaxSetup({cache:false});
	$("#_department_tree").bind("select_node.jstree",function(obj, check, e){
		selectDepartment();}).jstree({ 
		"plugins" : [ "themes", "json_data" ,"ui"],
		 "themes" : {  
			 "theme" : "default",  
			 "dots" : true,  
			 "icons" : true 
		},  
		"ui" : {    
			 "select_multiple_modifier" : "alt",
			"initially_select" : [ "INITIALIZED_USERS" ]  
		},
		"json_data" : { 
			"ajax" : {
				"url" : "${acsCtx}/organization/load-tree!loadDepartment.action",
				"data" : function (n) {
					return { currentId : n.attr ? n.attr("id") :"INITIALIZED" };
				}  
			}
		}
	});
});
function selectDepartment(){
	var currentDeptId = parent.$("#id").attr("value");
	node = $(".jstree-clicked").parent().attr("id");
	if(node!='undefined'&&node!=''&&node.indexOf('USERSBYDEPARTMENT')>=0){
		var deptId = node.split('-')[1].split('=')[0];
		if(currentDeptId==deptId){
			alert("不能选择当前部门为父部门！");
			return;
		}
		var name = node.split('=')[1];
		parent.setParentDeptInfo(deptId, name);
		window.parent.$.colorbox.close();
	}else if(node.indexOf('DEPARTMENTS')>=0){//表示公司名称，树的根节点
		var deptId = 0;
		var name = node.split('=')[1];
		parent.setParentDeptInfo(deptId, name);
		window.parent.$.colorbox.close();
	}else{
		alert('请选择部门！');
	}
}
</script>	
</div>
</body>
</html>
