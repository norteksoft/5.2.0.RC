<table class="form-table-border-left" style="width:630px;word-wrap:break-word;word-break:break-all;" >
	<thead>
		<tr>
			<th style="width: 20%">承办人</th>
			<th style="width: 35%">内容</th>
			<th style="width: 25%">日期</th>
			<th style="width: 20%">环节</th>
		</tr>
	</thead>
	<tbody>
	<#if opinions?exists>
		<#if view>
			<#list opinions as being>
				<tr>
					<td style="width: 20%">${being.creatorName}</td>
					<td style="width: 35%">${being.opinion}</td>
					<td style="width: 25%">${being.createdTime?string("yyyy-MM-dd HH:mm")}</td>
					<td style="width: 20%">${being.taskName}</td>
				</tr>
			</#list>
		</#if>
	</#if>
	</tbody>
</table>
