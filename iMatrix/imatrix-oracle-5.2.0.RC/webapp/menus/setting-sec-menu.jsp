<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<div id="secNav">
	<menu:secondMenu code="bs"></menu:secondMenu>
	<div class="hid-header" onclick=headerChange(this); title="隐藏"></div>
	<script>
		$('#' + secMenu).addClass('sec-selected');
	</script>
</div>