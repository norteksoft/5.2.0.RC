<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="user.addUser"/></title>
	
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="acs_content">
	<div class="opt-btn">
		<security:authorize ifAnyGranted="saveDepartment">
		    <button  class='btn' onclick="submitFormDept();"><span><span><s:text name="common.submit"/></span></span></button>
		    <s:if test="id==null">
	    	<button  class='btn' onclick="setPageState();cancel('${parentDepartment.id}');"><span><span><s:text name="common.cancel"/></span></span></button>
	    	</s:if><s:else>
	    	<button  class='btn' onclick="setPageState();cancel('${id}');"><span><span><s:text name="common.cancel"/></span></span></button>
	    	</s:else>
		</security:authorize>
		<script type="text/javascript">
		function cancel(id){
	     location.href=webRoot+"/organization/department.action?departmentId="+id;
		}
	   </script>
	</div>
	<div id="opt-content">
	  <form id="inputForm" name="inputForm" action="${acsCtx}/organization/department!saveDepartment.action" method="post">
			<input type="hidden" name="id" value="${id}" id="id" />
				<table class="form-table-without-border">
					<tr>
						<td class="content-title" >父部门:</td>
						<td>
							<input type="hidden" id="parentDepartmentId" name="parentId" value="${parent.id}"/> 
							<input id="parentDepartmentName" value="${parent.name}" disabled="disabled"/>
							<a href="#" onclick="$.colorbox({href:'${acsCtx}/organization/department!tree.action',iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:'请选择部门'}); " class="small-btn" ><span ><span>选择</span></span></a> 
						</td>
						<td class="content-title" ><s:text name="department.departmentCode"/>:</td>
						<td>
	                      <input  type="text" id="departmentCode"  name="code"  value="${code}"  onblur="checkDeptCode();"/><font color="red">*</font>
	                    </td>
					</tr>
					<tr>
						<td class="content-title" ><s:text name="department.departmentName"/>:</td>
						<td>
	                     <input  type="text" id="departmentName"   name="name" value="${name}" onblur="checkDeptName();"/><font color="red">*</font>
	                    </td>
						<td class="content-title" ><s:text name="department.shortTitle"/>:</td>
						<td><input type="text" name="shortTitle" id="shortTitle" value="${shortTitle}" /></td>
					</tr>
					<tr>
						<td class="content-title"><s:text name="department.weighing"/>:</td>
						<td>
						   <input type="text"  name="weight" value="${weight }" id="weighingId"/>
	                    </td>
						<td class="content-title"><s:text name="department.summary"/>:</td>
						<td><input type="text" name="summary" id="summary" value="${summary}" /></td>
					</tr>
				</table>
			</form>	
	</div>
</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
