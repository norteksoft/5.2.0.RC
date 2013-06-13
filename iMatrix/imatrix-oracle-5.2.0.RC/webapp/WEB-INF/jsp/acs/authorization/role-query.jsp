<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ taglib uri="http://ajaxanywhere.sourceforge.net/" prefix="aa" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
   <head>
	<title><s:text name="role.roleManager"/></title>
    <%@ include file="/common/acs-iframe-meta.jsp"%>
	
	<link href="${resourcesCtx}/widgets/colorbox/colorbox.css" rel="stylesheet" type="text/css"/>
	<script src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js" type="text/javascript"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/jstree/jquery.jstree.js"></script>
      
	
	<script type="text/javascript">
		function loadContent(){}
		function query(){
			if($("#query_type").val()==""){
				$("#query_type").attr("value","ROLE_USER")
				}
			var value = $("#acs_name").val();
			if($.trim(value).length == 0){
				alert("请输入查询条件");
				return;
			}
			ajaxAnywhere.formName = "ajax_from";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				var f=$("#flag").val();
				if(f=="false"){
					$("#sTable").remove();
					$("#message").html("<span style=\"color : red\">此用户，部门或工作组没有注册！</span>");
					$("#message").show();
					setTimeout('$("#message").hide("slow")',3000);
				}
				getRoleQueryContentHeight();
			};
			ajaxAnywhere.submitAJAX();
		}

		function getRoleQueryContentHeight(){
			var h = $('.ui-layout-center').height();
			$("#opt-content").css("height",h-110); 
		}
	</script>
	<style type="text/css">
		table.full{ 
			border:1px solid #C5DBEC;
			border-collapse:collapse;
			width:100%;
		}
		table.full thead tr {
			background:url("../../images/ui-bg_glass_85_dfeffc_1x400.png") repeat-x scroll 50% 50% #DFEFFC;
			color:#2E6E9E;
			font-weight:bold;
		}
		td ul{ padding-left: 8px; margin: 2px 0; }
		td ul li{ list-style: none; }
	</style>
</head>

<body>
<div class="ui-layout-center">
			<div class="opt-body">
				<div class="opt-btn">
					<button  class='btn' onclick="query();"><span><span>查询</span></span></button>
				</div>
				<form id="ajax_from" name="ajax_from" action="${acsCtx}/authorization/role!query.action" method="post">
					<div id="queryDiv" class="query_div"  style="padding: 5px;"><span>${queryTitle }</span>：
						<input id="query_type" name="queryType" type="hidden" value="${queryType }"/> 
						<input name="queryTitle" type="hidden" value="${queryTitle }"/> 
						<input id="acs_name" name="queryName" type="text"/><br/>
						<span style="display: none;" id="message"></span>
					</div>
				</form>
				<div id="opt-content" >
					<aa:zone name="acs_content">
						<div id="result" style="padding: 5px;">
						<!-- 用户  -->
						<input id="flag" name="flag" type="hidden" value="${isHave }"/>
							<s:if test="queryType=='ROLE_USER'">
								<s:iterator value="userRoles.keySet()" id="user">
									<span>${name }</span>
									<table border="1" cellpadding="0" cellspacing="0" class="leadTable">
										<thead>
											<tr>
												<s:iterator value="systems">
													<th>
													${name }
													</th>
												</s:iterator>
											</tr>
										</thead>
										<tbody>
											<tr> 
												<s:iterator value="userRoles.get(#user)" id="roleList">
												<td valign="top">
													<ul style="margin-left: 0;">
													<s:iterator value="roleList">
														<li style="margin: 3px;">${name}</li>
													</s:iterator>
													</ul>
												</td>
												</s:iterator>
											</tr>
										</tbody>
									</table>
								</s:iterator>
							</s:if>
							<!-- 部门和工作组 -->
							<s:else>
								<table border="1" id="sTable" cellpadding="0" cellspacing="0" class="leadTable" >
									<thead>
										<tr>
											<s:iterator value="systems">
												<th>
												${name }
												</th>
											</s:iterator>
										</tr>
									</thead>
									<tbody>
										<tr>
											<s:iterator value="allRoles" id="roleList">
											<td valign="top">
												<ul style="margin-left: 0;">
												<s:iterator value="roleList">
													<li style="margin: 3px;">${name}</li>
												</s:iterator>
												</ul>
											</td>
											</s:iterator>
										</tr>
									</tbody>
								</table>
							</s:else>
						</div>
					</aa:zone>
				</div>
			</div>
</div>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</body>
</html>
	