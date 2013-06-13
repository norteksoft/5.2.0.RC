<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="company.companyManager"/></title>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="acs_content">
<div class="opt-btn">
<button  class='btn' onclick="deptAddUserSubmit();"><span><span><s:text name="common.submit"/></span></span></button>
<button  class='btn' onclick="setPageState();cancel('${departmentId}');"><span><span><s:text name="common.cancel"/></span></span></button>
<script type="text/javascript">
function cancel(id){
    location.href=webRoot+"/organization/department.action?departmentId="+id;
}
</script>
</div>
<div id="opt-content">
<form id="deptAddUserForm" name="inputForm" action="department!departmentAddUser.action" method="post">
   	<input type="hidden" id="departmentId" name="departmentId" value="${departmentId}" />
	<div class="content">
		<acsTags:tree treeId="user_tree" defaultable="true" treeType="MAN_DEPARTMENT_TREE" multiple="true" userWithoutDeptVisible="true"></acsTags:tree>
	</div>
</form>
</div>
</aa:zone>












	<div class="page_margins">
		<div class="page">
			<!-- head start ======================  -->
			<%@ include file="/menus/header.jsp"%>
			
		    <!-- content start ======================  -->
			<div id="main">
				<div id="col1">
				</div>
				<div id="col3">
					<div id="col3_content">
						<div id="button_list" class="button_list">
						<div class="block"></div>
							<aa:zone name="acs_button">
								<a id="aaa" name="aaa" href="#" onclick="deptAddUserSubmit()"><span id="aaaa"><s:text name="common.submit"/></span></a>
								<a href="${ctx}/organization/department.action?departmentId=${departmentId}" id="dept_user_cancel" name="dept_user_cancel"><span><s:text name="common.cancel"/></span></a>
						  	</aa:zone>
				    	</div>
				    	
				    	<div class="content_r">
						<!-- table------------------------------------- -->
							<div class="subcolumns">
								<div id="menu_hidden" class="menu_hidden">
									<img id="ctl_img" src="${ctx}/images/triangle_1.gif"/>
								</div>
								<div class="content_div">
									<aa:zone name="acs_content">
										<form id="deptAddUserForm" name="inputForm" action="department!departmentAddUser.action" method="post">
									    	<input type="hidden" id="departmentId" name="departmentId" value="${departmentId}" />
											<div class="content">
												<div id="user_tree" class="demo"></div>
											</div>
										</form>
									</aa:zone>
								</div>
							</div>
							<aa:zone name="acs_footer"></aa:zone>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>
