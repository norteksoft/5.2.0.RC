<a class="scroll-left-btn" onclick="_scrollLeft();">&lt;&lt;</a>
<div class="fix-menu">
<ul class="scroll-menu">
	<#list menus?if_exists as item>
		<#if item.id?if_exists?string==secondMenuId?if_exists?string>
			<li id="${item.code?if_exists}" class="sec-selected">
			<span>
				<span>
					<a 
						<#if item.event?has_content>
							href="#" onclick="${item.event?if_exists}" 
						<#else>
							<#if subSysable?if_exists>
								href="${sysUrl}/${systemCode}${item.url?if_exists}?menuId=${item.lastMenuId?if_exists?string('###0')}"
							<#else>
								href="${sysUrl}${item.url?if_exists}?menuId=${item.lastMenuId?if_exists?string('###0')}"
							</#if>
						</#if>
						>${item.name?if_exists}</a>
				</span>
			</span>
			</li>
		<#else>
			<li id="${item.code?if_exists}">
				<span>
					<span>
						<a 
						<#if item.event?has_content>
							href="#" onclick="${item.event?if_exists}" 
						<#else>
							<#if subSysable?if_exists>
								href="${sysUrl}/${systemCode}${item.url?if_exists}?menuId=${item.lastMenuId?if_exists?string('###0')}"
							<#else>
								href="${sysUrl}${item.url?if_exists}?menuId=${item.lastMenuId?if_exists?string('###0')}"
							</#if>
						</#if>
						>${item.name?if_exists}</a>
					</span>
				</span>
			</li>
		</#if>   
	</#list>
</ul>
</div>
<a class="scroll-right-btn" onclick="_scrollRight();">&gt;&gt;</a>
