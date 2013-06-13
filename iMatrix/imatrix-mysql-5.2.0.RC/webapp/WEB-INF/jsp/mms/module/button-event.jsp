<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>页面管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script src="${mmsCtx}/js/module-page.js" type="text/javascript" charset="UTF-8"></script>
	
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/hl/style.css" media="all" />
    <script src="${resourcesCtx}/widgets/hl/highlight.js"></script>
    <script src="${resourcesCtx}/widgets/hl/lang-js.js"></script>
    <script src="${resourcesCtx}/widgets/hl/lang-xml.js"></script>
    <script src="${resourcesCtx}/widgets/hl/lang-html.js"></script>
    <script src="${resourcesCtx}/widgets/hl/lang-css.js"></script>
    <script src="${resourcesCtx}/widgets/hl/helpers.js"></script>
	<script type="text/javascript">
	$(document).ready(function(){
		$("#jsContent").attr("value",window.parent.$("#${rowid}_event").val());
		//初始化页签
		$( "#tabs" ).tabs({select:function(event,ui){
		}});
	});
	function changeSelected(id){
		$(".selected_div").css("display", "none");
		$("#div_"+id).css("display", "block");
	}
	function preview() {
	    var code = $("#jsContent").attr("value");
	    var hl = new DlHighlight({ lang        : "js",
	                               lineNumbers : false });
	    var formatted = hl.doItNow(code);
	    // need to insert it in a <pre> because otherwise IE compresses whitespace
	    $("#output").html( "<pre>" + formatted + "</pre>");
	  }

	  function ok(){
		  window.parent.addEvent($("#jsContent").attr("value"),'${rowid}');
		  window.parent.$("#${rowid}_event").focus();
		  window.parent.$.colorbox.close();
	  }
	</script>
</head>
<body style="padding: 12px;">
<div class="ui-layout-center">
	<div class="page_margins" style="min-width: 0px;">
		<div class="opt-btn">
			<button class="btn" onclick="ok();"><span><span >确定</span></span></button>
		</div>
		<div id="msgDiv"></div>
		<div id="tabs" style="height:auto;">
			<ul>
				<li ><a href="#tabs-1" onclick="changeSelected('edit');">编辑</a></li>
				<li ><a href="#tabs-1" onclick="changeSelected('preview');preview();">预览</a></li>
			</ul>
			<div id="tabs-1">
				<div id="div_edit" class="selected_div">
					<textarea id="jsContent" rows="15" cols="80"></textarea>
<pre>
如：
1、点击按钮时执行：  execute: toUpdateForm ，其中“toUpdateForm”为api中可执行的方法
2、点击按钮时执行并在执行前或执行后执行某方法：
	execute: toUpdateForm,
	before : selectOne,
	after  : selectOne
</pre>
<pre>
api:	toQuery		执行查询	
	toCreateFrom	新建
	toUpdateForm	修改	
	saveForm	保存
	deleteList	删除	
	toListPage	返回列表
	selectOne	至少选择一项		
	selectOneOrMore	选择一项或多项
</pre>		
	
				</div>
				<div id="div_preview" class="selected_div" style="display: none;" >
					<pre id="output" class="DlHighlight">
					
					</pre>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>