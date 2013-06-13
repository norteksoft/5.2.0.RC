<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<%@ page import="com.norteksoft.product.util.WebContextUtils"%>

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
			    node = $(".jstree-clicked").parent().attr("id");
			    var departmentName = window.parent.$("#departmentName").val();
			    if(node!='undefined'&&node!=''&&node.indexOf('USERSBYDEPARTMENT')>=0){
			    	var deptId = node.split('-')[1].split('=')[0];
					var name = node.split('=')[1];
					window.parent.$("#oneDid").attr("value",deptId);
					if(isHasRepeat(name,departmentName)){
		                 alert(name+"已被选为兼职部门!");
		                 return;
					}
					window.parent.$("#mainDepartmentName").attr("value",name);
					window.parent.$.colorbox.close();
			    }else{
			    	alert('请选择部门！');
				}
		    }
		    function isHasRepeat(target,testStr){
	         var test = testStr.split(",");
	         var flag = false;
	         for(var i=0;i<test.length;i++){
	            if(test[i]==target) flag=true;   
	         }
	         return flag;
			}
			</script>
		</div>
  </div>
</div>	
</body>
</html>
