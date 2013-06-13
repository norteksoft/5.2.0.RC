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