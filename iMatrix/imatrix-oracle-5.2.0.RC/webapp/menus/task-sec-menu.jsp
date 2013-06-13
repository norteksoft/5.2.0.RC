<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/task-taglibs.jsp"%>
<div id="secNav">
	<menu:secondMenu code="task"></menu:secondMenu>
	<div class="hid-header" onclick="headerChange(this);" title="隐藏"></div>
	<script>
		var secondMenu ;
		$("#" + secondMenu).addClass('sec-selected');
	</script>
</div>
