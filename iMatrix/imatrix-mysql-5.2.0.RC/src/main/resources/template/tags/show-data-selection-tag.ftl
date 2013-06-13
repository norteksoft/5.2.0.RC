<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
﻿<html>
	<head>
		<title>数据选择</title>
		<script type="text/javascript" src="${resourcesCtx}/js/jquery-all-1.0.js"></script>
		<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/colorbox/colorbox.css" />
		
		<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqgrid-all-1.0.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqGrid.custom.js"></script>
		<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/jqgrid/ui.jqgrid.css" />
		<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/css/${theme}/jquery-ui-1.8.16.custom.css" id="_style"/>
		
		<script type="text/javascript" src="${resourcesCtx}/js/aa.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/js/public.js" ></script>
		
		<script type="text/javascript" src="${resourcesCtx}/js/form.js"></script>
		<script type="text/javascript">
			function addValue(obj){
				var tds = $(obj).parent().parent().children();
				 var contral = $("#"+$("#controlId").attr("value"),window.parent.document);
				var data_control_ids = contral.attr("data_control");
				var data_control_id_arry = data_control_ids.split(',');
				for(var i=0;i<data_control_id_arry.length;i++){
					if(data_control_id_arry[i]=='')break;
					if($(tds[i+1]).html()=="&nbsp;"){
						$("#"+data_control_id_arry[i],window.parent.document).attr("value","");
					}else{
						$("#"+data_control_id_arry[i],window.parent.document).attr("value",$(tds[i+1]).html());
					}
				}
				parent.$.colorbox.close();
			}
			
			function changeHref(obj){
				var queryInputs=$("input[flag='query']");
				var url="${ctx}/portal/show-data-selection.action?code=${code}&version=${version}&formControlId=${formControlId}";
				for(var i=0;i<queryInputs.length;i++){
					url=url+"&"+$(queryInputs[i]).attr("name")+"="+$(queryInputs[i]).attr("value");
				}
				jQuery("#tb_dataSelect").jqGrid('setGridParam',{url:encodeURI(url),mtype:'post',page:1}).trigger("reloadGrid");
			}
			
		</script>
	</head>
	<body>
	<div class="opt-body">
	<div id="opt-content" class="form-bg">
		<form id="searchForm" name="searchForm" action="${ctx }/show-data-selection.htm" method="post" id="searchForm">
			<input type="hidden" name="code" value="${code }"/>
			<input type="hidden" name="version" value="${version }"/>
			<input type="hidden" name="formControlId" value="${formControlId }" id="controlId"/>
			<#assign _query_button="false">
			<#list properties?if_exists as querys>
				<#if querys[2]?if_exists=="1">
					<span>${querys[1]}：</span>
					<span><input type="text" name="${querys[0]}" id="${querys[0]}" flag="query"/></span>
					<#assign _query_button="true">
				</#if>
			</#list>
			<#if _query_button?if_exists=="true">
				<span>
					<a href="#" onclick="changeHref(this);" title="查询"  class="small-btn" ><span><span>查询</span></span></a>
				</span>
			</#if>
		</form>
		<table id="tb_dataSelect"></table>
		<div id="tb_dataSelect_pager"></div>
		<script>
			jQuery("#tb_dataSelect").jqGrid({
			<#if urlParam?if_exists!="">
				url:"${ctx}/portal/show-data-selection.action?code=${code}&version=${version}&formControlId=${formControlId}${urlParam}",
			<#else>
				url:"${ctx}/portal/show-data-selection.action?code=${code}&version=${version}&formControlId=${formControlId}",
			</#if>
				height:240,
				prmNames:{
					rows:'${pageName}.pageSize',
					page:'${pageName}.pageNo',
					sort:'${pageName}.orderBy',
					order:'${pageName}.order'
				},
				colNames:${colNames},
				colModel:${colModel},
				multiselect: false,
				pager: '#tb_dataSelect_pager',
				rownumbers:true
			});
		</script>
	</div>
	</div>
	</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
</html>