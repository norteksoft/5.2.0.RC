<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<script type="text/javascript">
	$(function () {
		$.ajaxSetup({cache:false});
		//initUserTree();
		});
	$(function () {
		$("#query_accordion").accordion({fillSpace:true});
	});
</script>
<div id="query_accordion" class="basic">
	<h3><a href="${acsCtx}/query/query.action" id="_query_total"">在线用户查询</a></h3>
	<div>
		<div  id="query_tree" class="demo"></div>
	</div>
	
</div>