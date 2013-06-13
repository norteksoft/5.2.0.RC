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
<div class="ui-layout-center">
<div class="opt-body">
	<aa:zone name="wf_template">
		<div class="opt-btn">
			<security:authorize ifAnyGranted="wf_engine_office_template_save">
				<button  class='btn' onclick="save_form();" hidefocus="true"><span><span>保存</span></span></button>
			</security:authorize>
			<button class='btn' onclick="setPageState();back($('#wf_type').val());" hidefocus="true"><span><span>返回</span></span></button>
		</div>
		<form id="defaultForm" action="" name="defaultForm" method="post">
			<input name="typeId" id="wf_type" value="${typeId }" type="hidden"/>
			<input name="id" id="templateId" value="${id }" type="hidden"/>
		</form>
		
		<aa:zone name="form_content">
		<div id="message" class="onSuccess" style="display: none;"><s:actionmessage theme="mytheme" /></div>
		<form id="templateForm" name="templateForm" action="${wfCtx }/engine/office-template!save.htm" enctype="multipart/form-data" method="post" >
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
		</form>
	</aa:zone>	
	<table >
		<tr>
			<td>模板文件：</td>
			<td ><input id="template_file_name" name="fileName" value="${fileName }" readonly="readonly">
				<input id="template_file_id" value="" type="hidden"></input>
				<span  id="spanButtonPlaceholder"></span>
			</td>
			<td><span id="template_file_nameTip" style="width:250px"></span></td>
		</tr>
		<tr>
				<td></td>
				<td  id="divFileProgressContainer">
				</td>
			</tr>
	</table>	
		
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
