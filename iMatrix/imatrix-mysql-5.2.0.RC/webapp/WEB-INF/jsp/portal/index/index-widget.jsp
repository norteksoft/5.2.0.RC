<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/common/portal-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>添加小窗体</title>
	<%@ include file="/common/portal-meta.jsp"%>
	<style type="text/css">
		table.Table,table.views{ 
			width: 100%; 
			background-color:#FFFFFF;
			border:1px solid #DEDEDE;
			border-collapse:collapse;}
		table.Table tbody tr td, table.Table thead tr th,
		table.views tbody tr td, table.views tbody tr th{
			border:1px solid #DEDEDE;
		}
		table.Table thead tr th,table.views tbody tr th{ background-color: #ADD5EF; }
		table.Table tbody tr td,table.views tbody tr td{ padding: 4px 6px;}
		table.Table tbody tr.even,table.views tbody tr.even{ background-color: #EFF8FF; }
		table.Table tbody tr.odd,table.views tbody tr.odd{ }
		table.Table tbody tr:HOVER,table.views tbody tr:HOVER{ background-color: #60b0e3; }
	</style>
	<script type="text/javascript">
	isUsingComonLayout=false;
	</script>
</head>
<body style="padding: 6px;">

	<div class="opt_btn" style="margin-bottom: 5px;">
		<button id='savebtn' class='btn' onclick="doAddWidget();"><span><span>保存</span></span></button>
		<!-- <button  class='btn' onclick="parent.$.colorbox.close();"><span><span>取消</span></span></button> -->
	</div>
	<div id='msg' style="color: red;"></div>
	<form action="${portalCtx}/index/index!saveWidgetToPortal.htm" style="margin: 0;padding: 0;">
		<input type="hidden" name="webpageId" id="pageId" value="${webpageId}"/>
		<p style="margin: 4px 0;">
			<s:if test="webpage.columns!=1">
				<input type="radio" name="position" value="0" checked="checked"/>左栏
				<s:if test="webpage.columns==3">
					<input type="radio" name="position" value="1"/>中栏
				</s:if>
				<input type="radio" name="position" value="2"/>右栏
			</s:if>
		</p>
		<table class="Table">
			<thead>
			<tr>
				<th style="width: 30px;"></th>
				<th>小窗口名称</th>
			</tr>
			</thead>
			<tbody>
				<s:iterator value="widgets">
					<tr>
						<td align="center"><input type="checkbox" name="widgetIds" value="${id}" widgetName="${name}"/></td>
						<td>${name}</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</form>
	
	<script type="text/javascript">
		$().ready(function(){
			//widget-place-left=identifierwidget-4|widget-place-center=identifierwidget-1|widget-place-right=identifierwidget-2,identifierwidget-3
			var widgets = parent.$('#widgetPosition').val();
			widgets = widgets.replace(/\|/g,',')+',';
			var inputs = $("input[name='widgetIds']");
			var canAdd = false;
			for(var i=0; i<inputs.length; i++){
				var val = '-'+$(inputs[i]).val()+',';
				if(widgets.indexOf(val)>0){
					$(inputs[i]).attr('disabled','disabled');
				}else{
					canAdd = true;
				}
			}
			if(!canAdd){
				_showMessage('msg','您已经添加了所有的小窗体');
				$('#savebtn').attr('disabled','disabled');
			}
		});
	
		function doAddWidget(){
			var widgets = $('input[name="widgetIds"]:checked');
			if(widgets.length <= 0){
				_showMessage('msg','请选择要添加的小窗体');
				return;
			}
			var ids="",names="";
			for(var i=0;i<widgets.length;i++){
				ids+=$(widgets[i]).attr('value');
				names+=$(widgets[i]).attr('widgetName');
				if(i!=(widgets.length-1)){ids+=',';names+=',';}
			}
			$('#savebtn').attr('disabled','disabled');
			parent.addWidgetToPage(ids, names, $('input[name="position"]:checked').attr('value'), $('#widget-html').html());
			parent.$.colorbox.close();
		}
		
		function _showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			setTimeout('$("#'+id+'").hide("show");',3000);
		}
	</script>
	<div id="widget-html" style="display: none;">
			<div class="widget movable collapsable removable closeconfirm" id="identifierwidget-@id">
				<div class="widget-header">
					<h3>@name</h3>
				</div>
				<div class="widget-content" id="widget-content-@id">
					<table style="width: 100%;"><tr><td align="center"><img alt="" src="${portalCtx}/images/loading.gif"/>&emsp;正在加载...</td></tr></table>
				</div>
				<b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
			</div>
		</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>