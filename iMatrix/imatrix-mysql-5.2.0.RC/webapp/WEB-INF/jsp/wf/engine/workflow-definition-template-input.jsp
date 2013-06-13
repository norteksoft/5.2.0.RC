<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>正文模板</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/_lib/css.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/tree/tree_component.css" />
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/tree_component.js"></script>
	
<script type="text/javascript">
</script>
</head>
<body>
<div class="opt-body">
<div class="ui-layout-center">
	<aa:zone name="wf_template">
		<div class="opt-btn">
			<security:authorize ifAnyGranted="wf_engine_workflow-definition-template-save">
				<button  class='btn' onclick="saveWorkflowDefinitionTemplate('${wfCtx}/engine/workflow-definition-template-save.htm');" hidefocus="true" type="button"><span><span>保存</span></span></button>
			</security:authorize>
			<button class='btn' onclick="setPageState();back();" hidefocus="true"><span><span>返回</span></span></button>
		</div>
		<div id="opt-content">
		<div id="message"style="display: none;"><font class="onSuccess"><nobr>保存成功!</nobr></font></div>
		<form id="templateForm" name="templateForm" action="" enctype="multipart/form-data" method="post" >
			<input id="id" name="id" value="${id }" type="hidden"/>
			<table>
					<tr>
						<td>所属类型：</td>
						<td><s:select   id="typeId" name="typeId" list="typeList" theme="simple" listKey="id" listValue="name" value="typeId"></s:select>
						</td>
						<td><span id="typeIdTip" style="width:250px"></span></td>
					</tr>
					<tr>
						<td>文件描述：</td>
						<td>
							<textarea id="discript" name="description" rows="5" cols="60" onblur="blurHandler();" onfocus="fouceHandler('discript','discriptTip',600);" >${description }</textarea>
						</td>
						<td id="discriptTip">
							最大输入600字
						</td>
					</tr>
			</table>
			<table >
				<tr>
					<td>模板文件：</td>
					<td ><input id="template_file_name" name="name" value="${name }" readonly="readonly">
						<span  id="spanButtonPlaceholder"></span>
					</td>
				</tr>
				<tr>
						<td></td>
						<td>
						</td>
					</tr>
			</table>	
			<table >
				<tr>
					<td>模板图片：</td>
					<td ><input id="template_file_icon" name="previewImageName" value="${previewImageName }" readonly="readonly">
						<span  id="spanButtonPlaceholder1"></span>
					</td>
				</tr>
				<tr>
						<td></td>
						<td>
						</td>
					</tr>
			</table>	
		</form>
	</div>
	</aa:zone>
</div>
</div>
</body>
</html>
