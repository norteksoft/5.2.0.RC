<div id="topnav" role="contentinfo">
	<a href="#" class="top-nav-first">
		<span></span>
	<#list menus?if_exists as item>
		<#if item_index<showNum>
		</a><a 
				<#if item.openWay.code=="firstMenu.open.way.currentPageOpen">
					href="${item.url?if_exists}?menuId=${item.lastMenuId?if_exists?string('###0')}" 
				<#elseif item.openWay.code=="firstMenu.open.way.newPageOpen">
					target="_blank" href="${item.url?if_exists}?menuId=${item.lastMenuId?if_exists?string('###0')}" 
				<#else>
					href="#" onclick="${item.event?if_exists}" 
				</#if>
			<#if item_index==showNum-1>id="lastSys"</#if> 
			class="<#if item.id?if_exists==firstMenuId?if_exists>top-selected</#if>">
			<#if item.type.code?if_exists=='menu.type.standard'>
				<span><span 
						<#if item.imageUrl?if_exists!="">style="background: url('${imatrixUrl}/icons/${item.imageUrl}') no-repeat;width:0.5cm;height:0.5cm;padding-top:5px;"
						<#else>class="${item.code?if_exists}"
						</#if> 
						></span>${item.name?if_exists}</span>
			<#else>
				<span><span 
						<#if item.imageUrl?if_exists!="">style="background: url('${imatrixUrl}/icons/${item.imageUrl}') no-repeat;width:0.5cm;height:0.5cm;padding-top:5px;"
						<#else>class="custom"
						</#if>
						></span>${item.name?if_exists}</span>
			</#if>
		</#if>
	</#list>
	</a><#if showNum<menuSize><a id="selectNumen" class="top-nav-last">
				<span>${moreSystem}</span></a></#if>
</div>
<script type="text/javascript">
	function selectSystems(id){
		$('#styleList').hide();
		if($('#sysTableDiv').attr('id')!='sysTableDiv'){
			var table = "<div id='sysTableDiv'><table id='systemTable'><tbody>"+
			<#list menus?if_exists as item>
				<#if (item_index>=showNum)>
						"<tr><td>"+
						"<a "+
						<#if item.openWay.code=="firstMenu.open.way.currentPageOpen">
							"href=\"${item.url?if_exists}?menuId=${item.lastMenuId?if_exists?string('###0')}\" "+
						<#elseif item.openWay.code=="firstMenu.open.way.newPageOpen">
							"target=\"_blank\" href=\"${item.url?if_exists}?menuId=${item.lastMenuId?if_exists?string('###0')}\" "+
						<#else>
							"href=\"#\" onclick=\"${item.event?if_exists}\" "+
						</#if>
						"<#if item_index==showNum-1>id=\"lastSys\"</#if> "+
						"class=\"<#if item.id?if_exists==firstMenuId?if_exists>top-selected</#if>\">"+
						<#if item.type.code?if_exists=='menu.type.standard'>
							"<span><span></span>${item.name?if_exists}</span>"+
						<#else>
							"<span><span class=\"custom\"></span>${item.name?if_exists}</span>"+
						</#if>
					"</a>"+
					"</td></tr>"+
					
				</#if>
			</#list>
			"</tbody></table></div>";
			$('body').append(table);
			
		}
		$('#sysTableDiv').show();
		var position = $("#"+id).position();
		$('#sysTableDiv').css('top', (position.top+36)+'px');
		$('#sysTableDiv').css('right', '0px');
	}
	
</script>