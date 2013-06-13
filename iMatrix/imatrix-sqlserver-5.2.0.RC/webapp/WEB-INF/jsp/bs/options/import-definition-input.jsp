<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>导入管理</title>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="import_main">
		<script type="text/javascript">
			$(document).ready(function() {
				$( "#tabs" ).tabs();
			});
		</script>
		<aa:zone name="btnZone">
			<div class="opt-btn">
				<button class="btn" onclick="saveImportDefinition('${settingCtx}/options/import-definition-save.htm');"><span><span>保存</span></span></button>
				<button class="btn" onclick='setPageState();ajaxSubmit("defaultForm","${settingCtx}/options/import-definition.htm","import_main");'><span><span >返回</span></span></button>
			</div>
		</aa:zone>
		<div id="opt-content">
			<div id="tabs">
				<ul>
					<li><a href="#tabs-1" onclick="pageUlChange('a');">基本信息</a></li>
					<li><a href="#tabs-1" onclick="pageUlChange('b')">字段信息</a></li>
				</ul>
				<div id="tabs-1">
					<aa:zone name="importContext">
							<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
							<form id="inputForm" name="inputForm" action="" method="post">
								<input type="hidden" name="importDefinitionId" id="importDefinitionId" value="${importDefinitionId}"/>
								<table class="form-table-without-border">
									<tr>
										<td class="content-title" style="width: 90px;">编号：</td>
										<td> <input id="code" name="code" value="${code}" maxlength="100" ></input><span class="required">*</span> </td>
										<td></td>
									</tr>	
									<tr>
										<td class="content-title">别名：</td>
										<td> <input id="alias" name="alias" value="${alias}" maxlength="255"></input><span class="required">*</span> </td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title">数据表名：</td>
										<td><input id="name" name="name" value="${name}"  maxlength="255"></input></td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title">文本导入类型：</td>
										<td>
											<select name="importType" onchange="importTypeChange(this);">
												<option value="">请选择</option>
												<s:iterator value="@com.norteksoft.bs.options.enumeration.ImportType@values()" var="importTypeVar">
													<option <s:if test="#importTypeVar==importType">selected="selected"</s:if> value="${importTypeVar}"><s:text name="%{code}"></s:text></option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									
									<tr class="content-title" id="divideTr" style="display: none;">
										<td class="content-title">分隔符：</td>
										<td><input id="divide" name="divide" value="${divide}"  maxlength="255"></input></td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title">导入方式：</td>
										<td>
											<select name="importWay">
												<s:iterator value="@com.norteksoft.bs.options.enumeration.ImportWay@values()" var="importWayVar">
													<option <s:if test="#importWayVar==importWay">selected="selected"</s:if> value="${importWayVar}"><s:text name="%{code}"></s:text></option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title">关联表名：</td>
										<td><input id="relevanceName" name="relevanceName" value="${relevanceName}"  maxlength="255"></input></td>
										<td></td>
									</tr>
									<tr id="content-title">
										<td class="content-title">外键：</td>
										<td><input id="foreignKey" name="foreignKey" value="${foreignKey}"  maxlength="255"></input></td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title">备注：</td>
										<td> </td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title"></td>
										<td colspan="2"> 
											<textarea id="remark" name="remark" cols="55" rows="10" style="font-family:Arial,Helvetica,sans-serif;overflow: auto;">${remark}</textarea>
										</td>
									</tr>
								</table>
							</form>	
							<script type="text/javascript">
								$(document).ready(function() {
									var importType="${importType}";
									if('TXT_DIVIDE'==importType){
										$("#divideTr").show();
									}
								});
							</script>
					</aa:zone>
				</div>
			</div>
		</div>
	</aa:zone>
</div>
</body>
</html>