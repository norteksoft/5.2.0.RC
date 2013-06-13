<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<html>
<head>
	<title>委托管理</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="delegatemainlist">
<form action="" name="defaultForm" id="defaultForm" method="post"></form>
	<div class="opt-btn">
		<button class="btn" onclick="setPageState();ajaxSubmit('defaultForm','${wfCtx}/engine/delegate-main.htm','delegatemainlist');"><span><span >返回</span></span></button>
	</div>
	<div id="opt-content">
	<table class="Table">
	 		<tr style="height: 30px;">
				<td  style="width: 80px;">委托人：</td>
				<td>${trustorName }
				 </td>
		  	</tr>
		  	<tr style="height: 30px;">
				<td>受托人：</td>
				<td>${trusteeName }
				 </td>
		  	</tr>
	 		<tr style="height: 30px;">
				<td>生效日期：</td>
				<td><s:date name="beginTime"  format="yyyy-MM-dd" /> </td>
		  	</tr>
	 		<tr style="height: 30px;">
				<td>截止日期：</td>
				<td><s:date name="endTime"  format="yyyy-MM-dd" /> </td>
		  	</tr>
			<s:if test="style==1">
		 		<tr style="height: 30px;">
					<td>委托形式：</td>
				<td>
							指定流程
						
					</td>
			  	</tr>
			  	<tr   style="height: 30px;">
					<td>委托流程：</td>
				<td>
							${name}
						 
					</td>
			  	</tr>
		 		<tr   style="height: 30px;">
					<td>委托环节：</td>
				<td>
							${activityName}
						
					 </td>
			  	</tr>
		  	</s:if>
			<s:elseif test="style==2">
			     <tr style="height: 30px;">
					<td>委托形式：</td>
				<td>
							所有流程
						
					</td>
			  	</tr>        
			</s:elseif>
			<s:elseif test="style==3">
				<tr style="height: 30px;">
					<td>委托形式：</td>
				<td>
							  委托权限
						
					</td>
			  	</tr> 
			  	<tr style="height: 30px;">
					<td>委托权限：</td>
				<td>
							 ${selectedRoleNames }
						
					</td>
			  	</tr>   
			</s:elseif>
	 		
	 		<tr style="height: 60px;">
	 			  <td>说明：</td>
				<td width="380"><textarea name="remark" cols="50" rows="5" readonly="readonly">${remark}</textarea></td>
		  	</tr>
		</table>
		</div>
</aa:zone>
</div>
</body>
</html>
