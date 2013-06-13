<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<script type="text/javascript">
$(function () {
	$.ajaxSetup({cache:false});
	initUserTree();
	});

$(function () {
	$("#accordion1").accordion({fillSpace:true, change:accordionChange});
});
function accordionChange(event,ui){
	var url=ui.newHeader.children("a").attr("href");
	if(url=="user.action"){//用户管理
		//if($("#company_user").html()==""){
        	initUserTree();
		//}
	}else if(url=="department!list.action"){//部门管理
		//if($("#company_department").html()==""){
			initDepartmentTree();
		//}
	}else if(url=="work-group.action"){//工作组管理
		if($("#company_group").html()==""){
			initWorkgroupTree();
		}
	}
	$("#myIFrame").attr("src",url);
}
/**
 * 初始化用户树
 */
function initUserTree(){
	$.ajaxSetup({cache:false});
	$("#company_user").jstree({ 
		"plugins" : [ "themes", "json_data" ,"ui"],
		"themes" : {  
			"theme" : "default",  
			"dots" : true,  
			"icons" : true 
		},  
		"ui" : { "select_multiple_modifier" : "alt",
			 "initially_select" : [ "DEPARTMENTS-${companyId}"  ] },
		"json_data" : {
			"ajax" : {
				"url" : "${acsCtx}/organization/load-tree!loadDepartmentTree.action",
				"data" : function (n) {
					return { currentId : n.attr ? n.attr("id") :"INITIALIZED_USERS" };
				}  
			}
		}
	}).bind("select_node.jstree",function(obj, check, e){
		selectUserNode("company_user");
	}).bind("loaded.jstree",function(obj, check, e){
		$("#oType").attr("value","company");
	});
}
function reinitUserTree(){
	jQuery.jstree._reference("#company_user").destroy();
	initUserTree();
}
// 用户树选择事件
function selectUserNode(treeid){
	var node = $("#"+treeid).find(".jstree-clicked").parent().attr("id");
	if(node=='undefined'||node==''){ return; }
	var id = node.split('-')[1];
	var type = node.split('-')[0];

	//$("#departId").attr("value",id);
	//$("#oType").attr("value",type);
	//$("#flag").attr("value","false");
	//$("#searchFlag").attr("value","false");
	var url="";//alert(type);
	if (type == 'USERSBYDEPARTMENT') {
		$("#comy").attr("value","");
		$("#departId").attr("value",id);
		//ajax_new('defaultForm', 'defaultForm', getUrlByType(type), 'acs_content', initUserTable);
		url = '${acsCtx}/organization/user!getUserByDepartment.action?oldDid='+id+"&oldType="+type+"&departmentId="+id;
	}else if (type == 'DELETED_USER') {
		$("#comy").attr("value","");
		$("#departId").attr("value",'');
		//ajax_new('defaultForm', 'defaultForm', getUrlByType(type), 'acs_content', initUserTable);
		url = '${acsCtx}/organization/user!deleteList.action?oldType='+type;
	}else if(type=='NODEPARTMENT_USER'){
		//$("#comy").attr("value","company");
		//$("#flag").attr("value","true");
		//$("#departId").attr("value",'');
		//ajax_new('defaultForm', 'defaultForm', getUrlByType(type), 'acs_content', initUserTable);
		url = '${acsCtx}/organization/user!getNoDepartmentUsers.action?comy='+"company"+"&oldType="+type+"&departmType=NODEPARTMENT";
	}else if(type=='DEPARTMENTS'){
		//$("#comy").attr("value","company");
		//$("#departId").attr("value",'');
		//ajax_new('defaultForm', 'defaultForm', getUrlByType(type), 'acs_content', initUserTable);
		url = '${acsCtx}/organization/user!getUserByCompany.action?comy='+"company"+"&oldType="+type;
	}
	$("#myIFrame").attr("src",url);
}

function initDepartmentTree(){
	$.ajaxSetup({cache:false});
	$("#company_department").jstree({ 
	"plugins" : [ "themes", "json_data","ui" ],
	 "themes" : {  
		 "theme" : "default",  
		 "dots" : true,  
		 "icons" : true 
	},  
	"ui" : {
		"initially_select" : [ getdeptId() ]  
	},
	"json_data" : { 
		"ajax" : {
			"url" : "${acsCtx}/organization/load-tree!loadDepartmentTree.action",
			"data" : function (n) {
				return { currentId : n.attr ? n.attr("id") :"INITIALIZED" };
			}  
		}
	}
	}).bind("select_node.jstree",function(obj, check, e){
		selectDeptNode("company_department");
	});		
}
function getdeptId(){
	if('${departmentId}' == '' || '${departmentId}' == 'null'){ 
		return "DEPARTMENTS-${companyId}";
	}else{
		return "USERSBYDEPARTMENT-${departmentId}";
	}
}
function selectDeptNode(treeid){
	var node = $("#"+treeid).find(".jstree-clicked").parent().attr("id");
	if(node=='undefined'||node==''){ return; }
	var id = node.split('-')[1];
	var type = node.split('-')[0];
	//$("#flag").attr("value",id);
	//$('#tree_selected_id').attr('value', id);
	//$('#tree_selected_node').attr('value', node);
	var url="";
	if (type == 'USERSBYDEPARTMENT') {
		//jQuery("#user_table").jqGrid('setGridParam',{url:'${acsCtx}/organization/department.action',page:1, postData:{ departmentId: id }}).trigger("reloadGrid");
		url=webRoot+"/organization/department.action?departmentId="+id+"&treeSelectedNode="+node;
	}else if(type=='DEPARTMENTS'){
		$('#tree_selected_id').attr('value', '');
		//jQuery("#user_table").jqGrid('setGridParam',{url:'${acsCtx}/organization/department.action',page:1, postData:{ departmentId: '' }}).trigger("reloadGrid");
		url=webRoot+"/organization/department.action?treeSelectedNode="+node;
	}
	$("#myIFrame").attr("src",url);
}

function initWorkgroupTree(){
	$.ajaxSetup({cache:false});
	$("#company_group").bind("select_node.jstree",function(obj, check, e){
		selectWorkgroupNode("company_group");
	}).jstree({ 
		"plugins" : [ "themes", "json_data" ,"ui"],
		 "themes" : {  
			 "theme" : "default",  
			 "dots" : true,  
			 "icons" : true 
		},  
		"ui" : {    
			"initially_select" : [ "WORKGROUPS-${companyId}" ]  
		},
		"json_data" : { 
			"ajax" : {
				"url" : "${acsCtx}/organization/load-tree!loadWorkgroupTree.action",
				"data" : function (n) {
					return { currentId : n.attr ? n.attr("id") :"INITIALIZED" };
				}  
			}
		}
	});
}

function selectWorkgroupNode(treeid){
	var node = $("#"+treeid).find(".jstree-clicked").parent().attr("id");
	if(node=='undefined'||node==''){ return; }
	var id = node.split('-')[1];
	var type = node.split('-')[0];
	$("#_wf_type").attr("value",type);
	$('#_wf_Id').attr('value', id);
	var url="";
	if(type=='WORKGROUPS'){
		//ajax_new('defaultForm', 'defaultForm', '${acsCtx}/organization/work-group.action', 'acs_content', initGroupTable);
		url=webRoot+"/organization/work-group.action?workGroupId="+id+"&wfType="+type;
	}else if(type=='USERSBYWORKGROUP'){
		//ajax_new('defaultForm', 'defaultForm', '${acsCtx}/organization/user!getUserByWorkGroup.action', 'acs_content', initUserTable);
		url=webRoot+"/organization/work-group!getUserByWorkGroup.action?workGroupId="+id;
	}
	$("#myIFrame").attr("src",url);
}
</script>
<div id="accordion1" class="basic">
	<security:authorize ifAnyGranted="userList">
		<h3><a href="user.action" id="_user" >用户管理</a></h3>
		<div>
			<div id="company_user" class="demo"></div>
		</div>
	</security:authorize>
	<security:authorize ifAnyGranted="departmentManager">
		<h3><a href="department!list.action" id="_department" >部门管理</a></h3>
		<div>
			<div id="company_department" class="demo"></div>
		</div>
	</security:authorize>
	<security:authorize ifAnyGranted="listWorkGroup">
		<h3><a href="work-group.action" id="_group" >工作组管理</a></h3>
		<div>
			<div id="company_group" class="demo"></div>
		</div>
	</security:authorize>
</div>