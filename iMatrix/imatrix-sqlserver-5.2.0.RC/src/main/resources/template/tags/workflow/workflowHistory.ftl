  <div style="width:auto; padding: 0; margin: 0;">
  	<div id="tabs-history">
		<ul>
			<li id="textli"><a href="#tabs-history-2" onclick="selectContact('textli');">${textContent}</a></li>
			<li id="flashli"><a href="#tabs-history-2" onclick="selectContact('flashli');">${flashContent}</a></li>
		</ul>
		<div id="tabs-history-2">
  	<#if view>
		<div class="demo" id="textContent" style="display: block; height: auto;"  >
           <table class="form-table-border-left">
				<thead>
				<tr>
				<th style="width: 5%">${sequence}</th>
				<th style="width: 15%">${name}</th>
				<th style="width: 35%">${history}</th>
				<th style="width: 45%">${opinion}</th>
				</tr>
				</thead>
				<tbody>
				<#list instanceHistory?if_exists as item>
				<tr>
				<td>
				 ${item_index+1}
				</td>
				<td>
				  <#if item.taskName?if_exists=="" > 
					<#if item.result?if_exists=="${end}">
					${end}
					<#else>
					${start}
					</#if>   
				  <#else>
				   ${item.taskName?if_exists }  
				  </#if> 
				</td>
				<td>
				 ${item.transactionResult?if_exists }
				</td>
				<td>
				 ${item.transactorOpinion?if_exists }
				</td>
				</tr>
				</#list>
				</tbody>   
			</table>
        </div>
	<#else>
              您没有查看流程流转历史权限
	</#if>
         <div class="demo" id="flashcontent" style="display: none; height:550px;" ></div>
		</div>
	</div>
</div>



<script type="text/javascript" >
	$().ready(function() {
		$( "#tabs-history" ).tabs();
	});
    function selectContact(id){
    	<#if view>
			if(id=="textli"){
				$("#textContent").show();
				$("#flashcontent").hide();
			}else if(id=="flashli"){
				$("#textContent").hide();
				$("#flashcontent").show();
				$(function(){$("#flashcontent").height($(window).height()-140);});
			}
		</#if>
	}

		var so = new SWFObject(
					"${url}",
					"FlowChartProject", "100%", "100%", "9", "#CCCCCC");
			so.addParam("quality", "high");
			so.addParam("name", "FlowChartProject");
			so.addParam("id", "FlowChartProject");
			so.addParam("AllowScriptAccess", "always");
			so.addParam("menu", "false");
			so.addVariable("webRoot", "${ctx}");
			so.addVariable("companyId", "${companyId }");
			so.addVariable("instanceId", "${workflowId }");
			so.addVariable("localeLanguage", "${locale }");
			
			so.addVariable("page", "viewHistoryProcess");
			so.write("flashcontent");
	function showHistoryList(){
		$.ajax({
				type : "POST",
				url : "${ctx}/engine/text-history.htm",
				data:{workflowId:'${workflowId }'},
				success : function(data){
		            $('#textContent').html(data);
				},
				error: function(){ $('#_textHistory').html('显示错误'); }
			});
	}
</script>