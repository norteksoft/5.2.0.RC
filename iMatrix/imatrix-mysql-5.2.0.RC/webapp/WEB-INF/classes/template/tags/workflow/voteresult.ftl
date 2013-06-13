<table  class="Table changeTR" >
	<thead>
		<tr>
			<th>办理人</th>
			<th>办理日期</th>
			<th>办理意见</th>
		</tr>
	</thead>
	<tbody>
		<#if temps?exists>
			<#if view>
				<#list temps as being>
					<tr style="height: 22px;">
						<th colspan="2"> ${being.name} </th>
						<th>(赞成：${being.yesNum}&nbsp;&nbsp;&nbsp;&nbsp;反对：${being.noNum} &nbsp;&nbsp;&nbsp;&nbsp;弃权：${being.invaNum} &nbsp;&nbsp;&nbsp;&nbsp;合计：${being.yesNum+being.noNum+being.invaNum})</th>
					</tr>
					<#list being.getTask() as been>
						<tr>
							<td width="200">${been.transactorName}</td>
							<td width="200">${been.transactDate?string("yyyy-MM-dd HH:mm")}</td>
							<td>
								<#if been.result='agreement'>
									赞成
								<#elseif been.result='oppose'>
									反对
								<#else>
									弃权
								</#if>
							</td>
						</tr>
					</#list>
				</#list>
			<#else>
				${message}
			</#if>
		</#if>
	</tbody>
</table>