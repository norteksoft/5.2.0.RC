<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/common/portal-taglibs.jsp"%>
<aa:zone name="widget-zones">
	<div class="opt-btn">
		<button class="btn" onclick='saveWidget();' id="create"><span><span >保存</span></span></button>
		<button class="btn" onclick="setPageState();backWidget();"><span><span >返回</span></span></button>
	</div>
	<div id="opt-content">
	<form id="backForm" name="backForm" action="" method="post">
		<input type="hidden" id="system_id"  name="systemId" value="${systemId}"/>
		<input type="hidden" name="widgetId" id="widgetId" value="${id}"/>
	</form>
	<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
		<form id="inputForm" name="inputForm" action="" method="post">
			<input type="hidden" id="system_id"  name="systemId" value="${systemId}"/>
			<input type="hidden" name="widgetId" id="widgetId" value="${id}"/>
			<table class="form-table-without-border">
				<tr>
					<td style="width: 20%">窗口编码：</td>
					<td style="width: 80%"> <input id="code" name="code" value="${code}"  maxlength="255" ></input><span class="required">*</span></td>
				</tr>	
				<tr>
					<td >窗口名称：</td>
					<td> <input id="name" name="name" value="${name}"  maxlength="255"></input><span class="required">*</span></td>
				</tr>	
				<tr>
					<td >窗口内容的URL：</td>
					<td> <input id="url" name="url" value="${url}"  maxlength="255" ></input><span class="required">*</span></td>
				</tr>	
				<!--
				<tr>
					<td >方法名：</td>
					<td> <input id="methodName" name="methodName" value="${methodName}" maxlength="255"></input></td>
				</tr>	
				 -->
				<tr>
					<td  >是否默认显示：</td>
					<td> <input name="acquiescent" id="defaulted" type="hidden" value="${acquiescent }"></input><input id="ifDefaulted" <s:if test="acquiescent">checked="checked"</s:if> type="checkbox" onclick="defaulteChecked(this);"></input></td>
				</tr>	
				<tr>
					<td  >是否显示分页：</td>
					<td> 
						<input name="pageVisible" id="pageVisible" type="hidden" value="${pageVisible }"></input><input id="ifPageVisible" <s:if test="pageVisible">checked="checked"</s:if> type="checkbox" onclick="changePageVisible(this);"></input>
						<span style="color: red;">*（分页时需要配置窗口参数：每页显示条数）</span>
					</td>
				</tr>	
				<tr>
					<td  >小窗体是否显示边框：</td>
					<td> 
						<input name="borderVisible" id="borderVisible" type="hidden" value="${borderVisible }"></input><input <s:if test="borderVisible">checked="checked"</s:if> type="checkbox" onclick="changeBorderVisible(this);"></input>
						<span style="color: red;">（只适用于一栏的页签）</span>
					</td>
				</tr>	
				<tr>
					<td  >小窗体内容是否以iframe方式获得：</td>
					<td> 
						<input name="iframeable" id="iframeable" type="hidden" value="${iframeable }"></input><input <s:if test="iframeable">checked="checked"</s:if> type="checkbox" onclick="changeIframeable(this);"></input>
					</td>
				</tr>	
				<!-- 
				<tr>
					<td >特殊窗体设置的url：</td>
					<td> <input id="parameterUrl" name="parameterUrl" value="${parameterUrl}" maxlength="255" ></input></td>
				</tr>	
				 -->
				<tr>
					<td >权限设置：</td>
					<td> <input id="roleNames" name="roleNames" value="${roleNames}"  readonly="readonly"></input><input id="roleIds" name="roleIds" value="${roleIds}"  type="hidden"></input><span class="required">*</span>&nbsp;&nbsp;<a href="#" onclick="addRole();" class="small-btn"><span><span>添加</span></span></a></td>
				</tr>	
				<tr>
					<td >窗口参数：</td>
					<td> </td>
				</tr>	
			</table>
			<grid:formGrid gridId="parameterGrid" code="PORTAL_WIDGET_PARAMETER" attributeName="parameters" entity="${widget}"></grid:formGrid>
		</form>
	</div>
</aa:zone>
