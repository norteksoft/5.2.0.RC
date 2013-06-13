<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>菜单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script src="${mmsCtx}/js/menu.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			validate_menu();
			$.extend($.validator, {
				messages: {
					accept: "图片(png,jpg,gif)"
				}
			});
		});
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:250px;
	}
	label.error{margin-left: 6px;}
	.form-table-without-border td select{
		width:250px;
	}
	</style>
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
   	<s:if test="parent.layer==4">
   		<font class="onError"><nobr>4级菜单下不能再新建子菜单</nobr></font>
   	</s:if>
   	<s:else>
   		<div class="opt-btn">
			<button class="btn" onclick="save();"><span><span>保存</span></span></button>
			<button class="btn" onclick="saveAndEnable();"><span><span >保存并启用</span></span></button>
			<button class="btn" onclick="parent.$.colorbox.close();"><span><span >返回</span></span></button>
		</div>
		<div id="message" style="display:none;"></div>
		<div id="msgDiv"></div>
			<form id="menuForm" action="${mmsCtx }/module/menu!save.htm" method="post" name="menuForm" enctype="application/x-www-form-urlencoded">
			<input type="hidden" name="parentMenuId" id="parentMenuId" value="${parent.id }">
			<input type="hidden" name="menuId" id="menuId" value="${menuId }">
			<input type="hidden" name="enableState" id="enableState" value="${enableState }"/>
			<input type="hidden" name="choseSystemId" id="choseSystemId" value="${choseSystemId }"/>
			<input type="hidden" name="isCreateSystem" id="isCreateSystem" value="${isCreateSystem }"/>
			<input type="hidden" name="iconName" id="iconName" value="${iconName }"/>
			<input type="hidden" name="imageUrl" id="imageUrl" value="${imageUrl }"/>
			  	<table class="form-table-without-border" >
			  		<s:if test="parent!=null">
			  		<tr>
			  			<td class="content-title">父菜单：</td>
			  			<td class="no-edit">${parentMenuName}</td>
			  		</tr>
			  		</s:if>
			  		<tr>
			  			<td class="content-title">编号：</td>
			  			<td><s:textfield  theme="simple" id="code" name="code" size="50" readonly="id!=null" maxlength="64"></s:textfield><span class="required">*</span></td>
			  		</tr>
			  		<tr>
			  			<td class="content-title">名称：</td>
			  			<td><s:textfield  theme="simple" id="name" name="name"  maxlength="64" size="50"></s:textfield><span class="required">*</span></td>
			  		</tr>
			  		<tr>
			  			<td class="content-title">序号：</td>
			  			<td><s:textfield  theme="simple" id="serialNumber" name="displayOrder" size="50"  maxlength="9" onkeyup="value=value.replace(/[^\d]/g,'')"></s:textfield><span class="required">*</span></td>
			  		</tr>
			  		<tr>
			  			<td class="content-title">链接地址：</td>
			  			<td><s:textfield  theme="simple" id="url" name="url"   maxlength="255" size="50"></s:textfield></td>
			  		</tr>
			  		<tr>
			  			<td class="content-title">事件：</td>
			  			<td><s:textfield  theme="simple" id="event" name="event" maxlength="255" size="50"></s:textfield></td>
			  		</tr>
			  		<s:if test="parent==null">
			  		<tr>
			  			<td class="content-title">打开方式：</td>
			  			<td>
			  				<select name="openWay">
								<s:iterator value="@com.norteksoft.mms.base.OpenWay@values()" var="openWayVar">
									<option <s:if test="#openWayVar==openWay">selected="selected"</s:if> value="${openWayVar}"><s:text name="%{code}"></s:text></option>
								</s:iterator>
							</select>
			  		</tr>
			  		<tr>
			  			<td>当前图标：</td>
			  			<td><input size="50" id="currentIconName" value="${iconName}" readonly="readonly"/><a class="btn" href="#" onclick="$('#iconName').attr('value','');$('#imageUrl').attr('value','');$('#currentIconName').attr('value','');"><span><span>清空</span></span></a></td>
			  			<td></td>
			  		</tr>
			  		<s:file label="更新图标" accept=".png,.jpg,.gif" size="26" name="file"></s:file>
			  		</s:if>
		  		</table>
		  </form>
		</s:else>
		</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
