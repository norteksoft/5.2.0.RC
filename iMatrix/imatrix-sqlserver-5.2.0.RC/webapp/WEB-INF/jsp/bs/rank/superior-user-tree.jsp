<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>数据字典</title>
	<%@ include file="/common/setting-colorbox-meta.jsp"%>
	
	<style type="text/css">
		a{text-decoration:none;}
	</style>
	<script type="text/javascript">
	function selectSuperiorUsers(){
		var info=getInfo("user");
		if(info!=""&&info!="[]"){
			var user=eval(info);
			var type=user[0].type;
			if(type == "user"){
				window.parent.$("#superiorUserId").attr("value",user[0].id);
				window.parent.$("#name").attr("value",user[0].name);
				window.parent.$("#superiorLoginName").attr("value",user[0].loginName);
				window.parent.$("#title").focus();
				window.parent.$("#selectSuperiorUser").colorbox.close();
			}else{
				alert("请选择人员");
			}
		}else{
			alert("请选择人员");
		}
	}
	</script>
</head>
<body style="padding: 5px;">
	<aa:zone name="wf_task">
		<div style="margin-left: 10px;margin-top: 10px;">
			<div class="opt-btn">
				<button class="btn" onclick="selectSuperiorUsers();"><span><span>确定</span></span></button>
			</div>
			<div style="margin-top: 20px;">
				<acsTags:tree defaultable="true" treeId="user_tree" treeType="MAN_DEPARTMENT_GROUP_TREE" multiple="false"></acsTags:tree>
			</div>
		</div>
		<style type="text/css"> #user_tree{background: none;} </style>
	</aa:zone>
</body>
</html>