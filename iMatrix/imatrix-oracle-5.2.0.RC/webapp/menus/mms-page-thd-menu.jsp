<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<div id="accordion" >
	<h3><a href="module-page-list.htm" id="page_manage">页面管理</a></h3>
	<div>
		<div class="demo" id="page_manage_content" style="margin-top: 10px;"></div>
	</div>
</div>

<script type="text/javascript">
	$(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		$("#myIFrame").attr("src",ui.newHeader.children("a").attr("href"));
	}
</script>