<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<div id="secNav">
   	<menu:secondMenu code="wf"></menu:secondMenu>
	<div class="hid-header" onclick=headerChange(this); title="隐藏"></div>
	<script>
		$('#' + secondMenu).addClass('sec-selected');
	</script>
</div>  
   	
