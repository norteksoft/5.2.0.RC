<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title><s:text name="company.companyManager"/></title>
	 <%@ include file="/common/acs-iframe-meta.jsp"%>
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
	function selectDepartment(){ }
    function selectedOK(){
    	node = $(".jstree-clicked").parent().attr("id");
		if(typeof(node)=='undefined'||node==''
			||node=='allDepartment_allDepartment=所有部门-部门'
				||node=='company_company=全公司-全公司'){
			 $("#msg").html("<span>请选择部门！</span>");
			 $("#msg").children("span").fadeOut(3500);
			return;
		}else if(node.indexOf('DEPARTMENTS')>=0){//表示公司名称，树的根节点
			parent.deleteUserToDept(0);
		}else{
			var deptId = node.split('=')[0].split('-')[1];
			parent.deleteUserToDept(deptId);
		}
		parent.$.colorbox.close();
    }
    function selectChangeMainDepartment(){
    	node = $(".jstree-clicked").parent().attr("id");
    	if(typeof(node)=='undefined'||node==''){
    		 $("#msg").html("<span style='color: red;'>请选择部门！</span>");
			 $("#msg").children("span").fadeOut(3500);
			return;
        }
    	rootNode=node.substring(0,node.indexOf('-'));
		if(node=='allDepartment_allDepartment=所有部门-部门'||node=='company_company=全公司-全公司'||rootNode=='DEPARTMENTS'){
			 $("#msg").html("<span style='color: red;'>请选择部门！</span>");
			 $("#msg").children("span").fadeOut(3500);
			return;
		}
		var deptId = node.split('=')[0].split('-')[1];
		parent.changeBatchUserMainDepartment(deptId);
		parent.$.colorbox.close();
    }
	</script>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<div class="opt-btn">
			<s:if test="fromChangeMainDepartment=='true'">
			<button  class='btn' onclick="selectChangeMainDepartment();"><span><span>确定</span></span></button>
			</s:if>
			<s:else>
			<button  class='btn' onclick="selectedOK();"><span><span>确定</span></span></button>
			</s:else>
		</div>
		<div id="opt-content">
			<div id='msg' style="padding-top: 4px;"></div>
			<div style="padding: 5px 10px;">
				<div id="_department_tree" class="demo"></div>
			</div>
		</div>
	</div>
</div>
</body>
</html>
