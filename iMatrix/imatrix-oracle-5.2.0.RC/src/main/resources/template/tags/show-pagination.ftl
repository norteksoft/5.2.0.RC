<table style="width:100%;font-size:12px;">
	<tr>
		<td style="text-align:center;">
			第&nbsp;${pageNo}&nbsp;页&emsp;
			<#if pageNo?if_exists gt 1>
				<a onclick="loadWidgetContent('${id}', 'widget-content-${id}',1);" href="#">首页</a>
				<a onclick="loadWidgetContent('${id}', 'widget-content-${id}',${pageNo-1});" href="#">上一页</a>
			<#else>
				<a style="color:#dedede;" href="#">首页</a>
				<a style="color:#dedede;" href="#">上一页</a>
			</#if>
			<#if pageNo == totalNo || totalNo==1>
				<a style="color:#dedede;" href="#">下一页</a>
				<a style="color:#dedede;" href="#">尾页</a>
			<#else>
				<a onclick="loadWidgetContent('${id}', 'widget-content-${id}',${pageNo+1});" href="#">下一页</a>
				<a onclick="loadWidgetContent('${id}', 'widget-content-${id}',${totalNo});" href="#">尾页</a>
			</#if>
		</td>
	</tr>
</table>
