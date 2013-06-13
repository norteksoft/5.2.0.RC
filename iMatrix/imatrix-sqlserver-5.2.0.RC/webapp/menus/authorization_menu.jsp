<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<script type="text/javascript">
	$(function () {
		$("#authorization_accordion").accordion({fillSpace:true, change:_change_menu});
	});
	 function _change_menu(event,ui){
			var url=ui.newHeader.children("a").attr("href");
			if(url=="standard-role-data.action"){
					initSystemRoleTree();
			}else if(url=="role.action"){
					initSystemTree();
					//url=url+"?businessSystemId="+$("#role_tree").find(".jstree-clicked").parent().attr("id").split('_')[1];
			}
			$("#myIFrame").attr("src",url);
		}
		
	function treechange(type,id){
		var url="";
		if(type=="ROLE"){
			url=webRoot+"/authorization/standard-role!authoritys.action?roleId="+id+"&id="+id;
		}else if(type == "BUSINESSSYSTEM"){
			return;
		}
		$('#myIFrame').attr('src',url);
	}
	function treechange2(id){
		var url=webRoot+"/authorization/role.action?businessSystemId="+id;
		$('#myIFrame').attr('src',url);
	}
	//初始化授权管理树
	function initSystemRoleTree(){
		$.ajaxSetup({cache:false});
		$("#auth_role_tree").jstree({ 
			"plugins" : [ "themes", "json_data" ,"ui"],
			"themes" : {  
				"theme" : "default",  
				"dots" : true,  
				"icons" : true 
			},  
			"ui" : { "select_multiple_modifier" : "alt", "initially_select" : [ "ROLE_${roleId}" ] },
			"json_data" : {
				"ajax" : {
					"url" : "${acsCtx}/authorization/standard-role-tree.action",
					"data" : function (n) {
						return { currentId : n.attr ? n.attr("id") :"INITIALIZED_USERS" };
					}  
				}
			}
		}).bind("select_node.jstree",function(obj, check, e){
			selectNode(obj);
		});
	}
	//初始化角色管理树
	function initSystemTree(){
		$.ajaxSetup({cache:false});
		$("#role_tree").jstree({ 
			"plugins" : [ "themes", "json_data" ,"ui"],
			"themes" : {  
				"theme" : "default",  
				"dots" : true,  
				"icons" : true 
			},  
			"ui" : { "initially_select" : [  "BUSINESSSYSTEM_${businessSystemId}" ] },
			"json_data" : {
				"ajax" : {
					"url" : "${acsCtx}/authorization/role-systemTree.action",
					"data" : function (n) {
						return { currentId : n.attr ? n.attr("id") :"INITIALIZED_USERS" };
					}  
				}
			}
		}).bind("select_node.jstree",function(obj, check, e){
			selectNode2();
		});
	}
	
	var userTitle = '<s:text name="user.userName"/>';
	var deptTitle = '<s:text name="department.departmentName" />';
  	var wgTitle = '<s:text name="workGroup.workGroupName" />';
	function querySelectNode(treeId){
		$("#queryDiv").attr("class", "query_div");
		if(treeId == "ROLE_USER"){
			$("#acs_title").text(userTitle);
			$("#query_type").val("ROLE_USER");
		}else if(treeId == "ROLE_DEPARTMENT"){
			$("#ROLE_USER").children("a").removeClass("clicked");
			$("#acs_title").text(deptTitle);
			$("#query_type").val("ROLE_DEPARTMENT");
		}else if(treeId == "ROLE_WORKGROUP"){
			$("#ROLE_USER").children("a").removeClass("clicked");
			$("#acs_title").text(wgTitle);
			$("#query_type").val("ROLE_WORKGROUP");
		}
		$("#result").text("");
		$("#acs_name").val("");
	}
	//授权管理
	function selectNode(){
		treeselected($("#auth_role_tree").find(".jstree-clicked").parent().attr("id"));
	}
	function treeselected(typeAndId){
		var index = typeAndId.indexOf("_");
		treechange(typeAndId.substring(0, index), typeAndId.substring(index+1, typeAndId.length));
	}

	//角色管理
	function selectNode2(){
		treeselected2($("#role_tree").find(".jstree-clicked").parent().attr("id"));
	}
	function treeselected2(typeAndId){
		var index = typeAndId.indexOf("_");
		treechange2(typeAndId.substring(index+1, typeAndId.length));
	}
	//权限查询
	function leftMenu(url,queryType,queryTitle){
		if(queryType == "ROLE_USER"){
			$("#fourMenuUser").attr("class","four-menu-selected");
			$("#fourMenuDep").attr("class","four-menu");
			$("#fourMenuWork").attr("class","four-menu");
		}else if(queryType == "ROLE_DEPARTMENT"){
			$("#fourMenuUser").attr("class","four-menu");
			$("#fourMenuDep").attr("class","four-menu-selected");
			$("#fourMenuWork").attr("class","four-menu");
		}else if(queryType == "ROLE_WORKGROUP"){
			$("#fourMenuUser").attr("class","four-menu");
			$("#fourMenuDep").attr("class","four-menu");
			$("#fourMenuWork").attr("class","four-menu-selected");
		}
		$("#myIFrame").attr("src",url+"?queryType="+queryType+"&queryTitle="+queryTitle);
		}
</script>

<div id="authorization_accordion" class="basic">
	<security:authorize ifAnyGranted="standardTree">
	<h3><a href="standard-role-data.action" id="_authorization">授权管理</a></h3>
	<div>
		<div id="auth_role_tree" class="demo"></div>
	</div>
	</security:authorize>
	
	<security:authorize ifAnyGranted="roleManager">
	<h3><a href="role.action" id="_role_mgmt">角色管理</a></h3>
	<div>
		<div id="role_tree" class="demo"></div>
	</div>
	</security:authorize>
	<security:authorize ifAnyGranted="acs_role_query">
	<h3><a href="role!query.action" id="_authorization_query" onclick="leftMenu('role!query.action','ROLE_USER','用户名称')">权限查询</a></h3>
	<div>
		<div id="fourMenuUser" class="four-menu-selected">
			<a href="#"  onclick="leftMenu('role!query.action','ROLE_USER','用户名称')">用户权限</a>
		</div>
		<div id="fourMenuDep" class="four-menu">
			<a href="#" class="" onclick="leftMenu('role!query.action','ROLE_DEPARTMENT','部门名称')">部门权限</a>
		</div>
		<div id="fourMenuWork" class="four-menu">
			<a href="#" class="" onclick="leftMenu('role!query.action','ROLE_WORKGROUP','工作组名称')">工作组权限</a>
		</div>
	</div>
	</security:authorize>
</div>
