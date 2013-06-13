        <script>
		$(document).ready(function(){
			handleSelect();
		});
   		<#--处理下拉选中  --> 
  		function handleSelect(){
    		var id=$("#checkValue_${widgetId?if_exists?string('###0')}").val();
    		if(id!=''){
    		$("#"+id).attr("selected","selected");
    		}
     	}
    	</script>
        <div id="table_${widgetId?if_exists?string('###0')}">
	    <form action="${ctx}/portal/index/index!save.htm" method="post" id="parameterForm_${widgetId?if_exists?string('###0')}">
	    <div id="message_${widgetId?if_exists?string('###0')}" style="display:none;"></div>
			<table class="form-table-without-border" >
			<#list widgetParameterList as p>
				<#if p.controlType?if_exists=='parameter.type.checkbox'>
					 <tr>
						<td  style="width:30%;text-align:center;"> 显示内容:</td>
						<td>
							<input type="checkbox" name="${p.code}"   value="只显示通知标题"     id="${p.code}"  />只显示通知标题<br>
							<input type="checkbox" name="${p.code}"   value="只显示通知内容"     id="${p.code}"  />只显示通知内容<br>
							<input type="checkbox" name="${p.code}"   value="只显示通知时间"     id="${p.code}"  />只显示通知时间<br>
						</td>
					 </tr>
				<#elseif p.controlType?if_exists=='parameter.type.radio'>
					<tr>
						<td  style="width:30%;text-align:center;"> 是否改变页签顺序：</td>
						<td>
							<#list p.values as v>
								<input <#if v.value=='yes'>checked="checked"</#if> type="radio" name="${p.code}" id="" value="yes"/>是
								<input <#if v.value=='no'>checked="checked"</#if> type="radio" name="${p.code}" id="" value="no"/>否
							</#list>
						</td>
					</tr>
				<#elseif p.controlType?if_exists=='parameter.type.text'>
					<tr>
						<td  style="width:30%;text-align:center;">${p.title}：</td>
						<td>
							<#list p.parameterValues as v>
							   <#if v.userId==userId?if_exists >
							   <#if v.webPageId==webpageId?if_exists >
								<input style="width:80%;" type="text"	name="${p.code}" value="${v.value}"  id="textId_${widgetId?if_exists?string('###0')}" />
								<#assign _pv="${v.value}">
								<#else> 
								</#if>
								</#if>
							</#list>
							<#if _pv?if_exists=="">
							    <input style="width:80%;" type="text" 	name="${p.code}" value="${p.defaultValue?if_exists}"  id="otherTextId_${widgetId?if_exists?string('###0')}" /> 
							</#if>
						</td>
					</tr>
				<#elseif p.controlType?if_exists=='parameter.type.select'>
					<tr>
						<td  style="width:30%;text-align:center;">${p.title}:</td>
						<td >
							<select style="width:80%;" id="${p.code}" name="${p.code}" >
							    <#list p.options?if_exists as o>
							    <#if o.selected>
									<option  value="${o.value}" id="${o.value}" selected="selected">${o.name}</option>
								<#else>
								    <option  value="${o.value}" id="${o.value}" >${o.name}</option>
								</#if>	
								</#list> 
							</select>
							<#list p.parameterValues as v>
								<#if v.webPageId?if_exists==webpageId?if_exists >
								    <#assign _check_value="${v.value}">
								</#if>
							</#list>   
							<input type="hidden" value="${_check_value?if_exists}"  id="checkValue_${widgetId?if_exists?string('###0')}" /> 
						</td>
					</tr>
				</#if>
			</#list>
			</table>
			
			<div style="text-align:right;padding:10px 50px 0 0;" id="button">
			<a style="flort:right;" class="btn" href="#" onclick="doSubmit(${widgetId?if_exists?string('###0')});"><span><span >确定</span></span></a>
			</div>
				
	  		<input type="hidden" name="widgetId" value="${widgetId?if_exists?string('###0')}"/>
	  		<input type="hidden" name="webpageId" value="${webpageId?if_exists?string('###0')}"/>
		</form>
		</div>
