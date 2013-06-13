<#if haveTask?if_exists=="yes">
<script type="text/javascript">
	$(document).ready(function() {
		  var currentClickLeafId = parent.$("#task_detail_leaf").val();
		  var type = 0;
		  if(currentClickLeafId!=''){
		     type = currentClickLeafId.substring(currentClickLeafId.length-1,currentClickLeafId.length);
		     changePage(type);
		  }
		  $( "#tabs" ).tabs({selected:type});
	});
	function changePage(type){
		var contentAmount="${contentAmount}"
		for(var i=0;i<contentAmount;i++){
			if(i==type){
			    parent.$("#task_detail_leaf").attr("value","contentId"+i);
				$("#contentId"+i).show();
			}else{
				$("#contentId"+i).hide();
			}
		}
	}
</script>
<div>
	<div id="tabs">
		<ul>
			<#list taskTypes?if_exists as taskType>
				<li><a href="#tabs-1" onclick='changePage("${taskType_index}")'>${taskType[0]}</a></li>
			</#list>
		</ul>
		<div id="tabs-1">
			<#list taskContents?if_exists as taskContent>
				<#if taskContent_index==0>
					<div id="contentId${taskContent_index}" class="contentleadTable">${taskContent}</div>
				<#else>
					<div id="contentId${taskContent_index}" style="display: none;" class="contentleadTable">${taskContent}</div>
				</#if>
			</#list>
		</div>
	</div>
</div>
<#else>
	<div style="text-align: center">无任务</div>
</#if>
