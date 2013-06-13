<div id="accordion" >
  	<#list thirdMenus?if_exists as item>
		<h3><a href="${sysUrl?if_exists}${item.url?if_exists}?menuId=${item.lastMenuId?if_exists?string('###0')}" id="${item.code?if_exists}">${item.name?if_exists}</a></h3>
		<div>
			<#if fourMenus?size gt 0>
				<#list fourMenus?if_exists as being>
						<#if being.event?if_exists=="">
							<#if menuId?if_exists==being.id?if_exists>
								<div class="four-menu-selected">
									<a href="${sysUrl?if_exists}${being.url?if_exists}?menuId=${being.lastMenuId?if_exists?string('###0')}">${being.name?if_exists}</a>
								</div>
							<#else>
								<div class="four-menu">
									<a href="${sysUrl?if_exists}${being.url?if_exists}?menuId=${being.lastMenuId?if_exists?string('###0')}">${being.name?if_exists}</a>
								</div>
							</#if>
						<#else>
							<#if menuId?if_exists==being.id?if_exists>
								<div class="four-menu-selected">
									<a href="#this" onclick="${being.event?if_exists}">${being.name?if_exists}</a>
								</div>
							<#else>
								<div class="four-menu">
									<a href="#this" onclick="${being.event?if_exists}">${being.name?if_exists}</a>
								</div>
							</#if>
						</#if>
				</#list>
			<#else>
				<div class="demo" id="${item.code?if_exists}_content" style="margin-top: 10px;"></div>
			</#if>
		</div>
	</#list>
</div>
<script type="text/javascript">
var thirdMenu = "${thirdMenuCode}";
$(document).ready(function() {
	initAccordion();
});

function initAccordion(){
	$("#accordion").accordion({
		fillSpace: true,
		active: getIndex('#accordion'),
		change: function(event, ui) {
			location.href=$($(ui.newHeader[0]).children()[1]).attr('href');
		}
	});
}
function getIndex(id){
	var subs = $(id).children("h3");
	for(var i = 0; i < subs.length; i++){
		var hs0 = $($(subs[i]).children('a')[0]).attr('id');
			if(thirdMenu==hs0){
				return i;
			}
	}
	return 0;
}
</script>