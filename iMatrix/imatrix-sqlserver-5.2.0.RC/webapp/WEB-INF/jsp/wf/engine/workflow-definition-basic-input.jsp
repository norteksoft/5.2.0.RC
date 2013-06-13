<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<html>
<div class="ui-layout-center">
	<aa:zone name="wfd_main">
		<div class="opt-btn">
			<button  class='btn' onclick="submitBasic();" hidefocus="true"><span><span>保存</span></span></button>
			<button class='btn' onclick="basicGoBack();" hidefocus="true"><span><span>返回</span></span></button>
		</div>
		<div id="opt-content">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form id="inputForm" name="inputForm" action="" method="post">
				<input type="hidden" name="type" value="${type }"/>
				<input type="hidden" name="wfdId" value="${wfdId }"/>
				<input id="system_id" name="sysId" type="hidden" value="${sysId }"/>
				<input id="vertion_type" name="vertionType" type="hidden" value="${vertionType }"/>
				<table class="form-table-without-border">
					<tr>
						<td class="content-title">编&nbsp;&nbsp;码：</td>
						<td> ${code } </td>
						<td></td>
					</tr>
					<tr>
						<td class="content-title" style="width: 90px;">名&nbsp;&nbsp;称：</td>
						<td> <input name="name" value="${name}" ></input><span class="required">*</span></td>
						<td></td>
					</tr>	
					<tr>
						<td class="content-title" style="width: 90px;">自定义类别：</td>
						<td> <input name="customType" value="${customType}" ></input></td>
						<td></td>
					</tr>	
					
					<tr>
						<td class="content-title">管理员：</td>
						<td><input name="adminName" value="${adminName}"  id="adminName" readonly="readonly"></input><input name="adminLoginName" value="${adminLoginName}" type="hidden" id="adminLoginName"></input><span class="required">*</span><a href="#" onclick='selectPerson();' title="选择"  class="small-btn" id="selectBtn"><span><span>选择</span></span></a></td>
						<td></td>
					</tr>
				
					<tr>
						<td class="content-title">类&nbsp;&nbsp;型：</td>
						<td>
							<select name="typeId" id="typeId" class="typeRequired">
								<option value="">请选择类型</option>
								<s:iterator value="typeList">
									<s:if test="typeId==id">
										<option value="${id }" selected="selected">${name }</option>
									</s:if><s:else>
										<option value="${id }">${name }</option>
									</s:else>
								</s:iterator>
							</select> 
							<span class="required">*</span>
						</td>
						<td></td>
					</tr>
					<tr>
						<td class="content-title">所属系统：</td>
						<td>
							<select name="systemId" id="systemId" class="systemRequired">
								<option value="">请选择系统</option>
								<s:iterator value="systems">
									<s:if test="systemId==id">
										<option value="${id }" selected="selected">${name }</option>
									</s:if><s:else>
										<option value="${id }">${name }</option>
									</s:else>
								</s:iterator>
							</select> 
							<span class="required">*</span>
							 </td>
						<td></td>
					</tr>
				</table>
			</form>	
	</div>	
	</aa:zone>
</div>	
</html>
