<div class="opt-btn">
	<#list buttons?if_exists as button>
		<#if button.toPage?if_exists!="">
		<#if button.code?if_exists!="">
			<button  id="${button.code?if_exists}" class="btn" href="#" pageid="${button.toPage.id?string('###0')}" onclick="buttonExecute(this, {${button.event?if_exists}});"><span><span>${button.name?if_exists}</span></span></button>
		</#if>
		<#else>
		<#if button.code?if_exists!="">
			<button  id="${button.code?if_exists}" class="btn" href="#" onclick="buttonExecute(this, {${button.event?if_exists}});"><span><span>${button.name?if_exists}</span></span></button>
		</#if>
		</#if>
	</#list>
</div>
