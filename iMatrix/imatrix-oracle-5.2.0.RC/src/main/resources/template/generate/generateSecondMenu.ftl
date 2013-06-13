<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<div id="secNav">
	<menu:secondMenu></menu:secondMenu>
	<div class="hid-header" onclick="headerChange(this);" title="隐藏"></div>
	<script>
		var secondMenu ;
		$("#" + secondMenu).addClass('sec-selected');
	</script>
</div>