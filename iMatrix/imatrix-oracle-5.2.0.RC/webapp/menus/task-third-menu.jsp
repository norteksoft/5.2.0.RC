<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/task-taglibs.jsp"%>
<div id="accordion" >
	<h3><a href="${taskCtx}/task/task.htm" id="task-thirdMenu1">待办事宜</a></h3>
	<div>
		<div class="demo" id="unhandle_bussiness_content" style="margin-top: 10px;"></div>
	</div>
		
		
	<h3><a href="${taskCtx}/task/task-completed-list.htm?taskCategory=complete" id="task-thirdMenu2">已完成事宜</a></h3>
	<div>
		<div class="demo" id="handle_bussiness_content" style="margin-top: 10px;"></div>
	</div>
	
	<h3><a href="${taskCtx}/task/task-canceled-list.htm?taskCategory=cancel" id="task-thirdMenu3">已取消事宜</a></h3>
	<div>
		<div class="demo" id="canceled_bussiness_content" style="margin-top: 10px;"></div>
	</div>
</div>

<script type="text/javascript">
	$(function () {
		var url=window.location.href;
		if(url.indexOf("/task/task.htm")>=0){
			$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 0});
		}else if(url.indexOf("/task/task-completed-list.htm")>=0){
			$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 1});
		}else if(url.indexOf("/task/task-canceled-list.htm")>=0){
			$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 2});
		}
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		window.location = url;
	}


</script>
