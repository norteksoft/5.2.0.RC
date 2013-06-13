<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<script type="text/javascript">
	$(function () {
		$.ajaxSetup({cache:false});
		//initUserTree();
		});
	$(function () {
		$("#accordion1").accordion({fillSpace:true});
	});
</script>
<div id="accordion1" class="basic">
	<h3><a href="${acsCtx}/syssetting/security-set.action" id="_security_set" >参数设置</a></h3>
	<div>
		<div  id="security_set_tree" class="demo"></div>
	</div>
	
</div>



