<table id="${gridId}"></table>
<div id="${gridId}_pager"></div>
<div id="_sub_grid_div_script" style="display:none;">
	function getTableId(){
		return "${gridId}";
	}
	function getUrl(){
		return "${url}";
	}
	function getUrlParameter(){
		<#if urlParameter?if_exists !="">
			return "&${urlParameter}";
		<#else>
			return "";
		</#if>
	}
	function initializationSubGrid(){
			var lastselsub;
			jQuery("#${gridId}").jqGrid({
				<#if urlParameter?if_exists !="">
					url:"${url}?${urlParameter}",
				<#else>
					url:"${url}",
				</#if>
				<#if pageName?if_exists !="">
					prmNames:{
						rows:'${pageName}.pageSize',
						page:'${pageName}.pageNo',
						sort:'${pageName}.orderBy',
						order:'${pageName}.order'
					},
				</#if>
				
				colNames:[
					<#list columns?if_exists as item>
						<#if item.headerName?if_exists !="">
						'${item.internationName}',
					<#else>
					'',
				</#if>
					</#list>
				], 
				colModel:[
						<#list columns?if_exists as item>
							{<#if item.tableColumn?if_exists!="">
								name:'${item.tableColumn.name}'
								,index:'${item.tableColumn.name}'
								<#if item.editable>
									,editable:true
								</#if>
								<#if item.optionSet?if_exists!="">
										,edittype:'select'
										,formatter:'select'
										<#if item.controlValue?if_exists!=""&&item.controlValue?if_exists=='MULTISELECT'>
											,editoptions:{value:{${item.optionSet}},multiple:true}
											<#else>
											,editoptions:{value:{${item.optionSet}}}
										</#if>
									<#elseif item.tableColumn.dataType?if_exists=='BOOLEAN'>
										,edittype:'checkbox'
										,formatter:formatCheckbox
										,unformat:unFormatCheckbox
										,editoptions: {value:"true:false"}
									<#elseif item.controlValue?if_exists!=""&&item.controlValue?if_exists=='TEXTAREA'>
										,edittype:'textarea'
									<#else>
										<#if item.format?if_exists!="">
											<#assign _formatSetting="${item.format}">
											<#if _formatSetting?index_of("$#,##")!=-1>
												,formatter:'currency'
												<#if _formatSetting?index_of(".")!=-1>
													<#assign _formatSetting_length="${_formatSetting?length}">
													,formatoptions:{decimalSeparator:".", thousandsSeparator: ",", decimalPlaces: ${_formatSetting_length}-7, prefix: "$"}
												<#else>
													,formatoptions:{decimalSeparator:" ", thousandsSeparator: ",", decimalPlaces: 0, prefix: "$"}
												</#if>
											<#elseif _formatSetting?index_of("${renmibi}#,##")!=-1>
												,formatter:'currency'
												<#if _formatSetting?index_of(".")!=-1>
													<#assign _formatSetting_length="${_formatSetting?length}">
													,formatoptions:{decimalSeparator:".", thousandsSeparator: ",", decimalPlaces: ${_formatSetting_length}-7, prefix: "${renmibi}"}
												<#else>
													,formatoptions:{decimalSeparator:" ", thousandsSeparator: ",", decimalPlaces: 0, prefix: "${renmibi}"}
												</#if>
												
											<#elseif _formatSetting?index_of("%")!=-1>
												,formatter:'currency'
												<#if _formatSetting?index_of(".")!=-1>
													<#assign _formatSetting_length="${_formatSetting?length}">
													,formatoptions:{decimalSeparator:"00.", thousandsSeparator: "", decimalPlaces: ${_formatSetting_length}-3, suffix: "%"}
												<#else>
													,formatoptions:{decimalSeparator:"00", thousandsSeparator: "", decimalPlaces: 0, suffix: "%"}
												</#if>
											<#elseif _formatSetting?if_exists=="yyyy-m-d">
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y-m-d'}
											<#elseif _formatSetting?if_exists=="yyyy-m-d hh:mm:ss">
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y-m-d H:i:s'}
											<#elseif _formatSetting?if_exists=="yyyy-m">
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y-m'}
											<#elseif _formatSetting?if_exists=="m-d">
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'m-d'}
											<#elseif _formatSetting?if_exists=='yyyy${_year}m${_month}d${_day}'>
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y${_year}m${_month}d${_day}'}
											<#elseif _formatSetting?if_exists=='yyyy${_year}m${_month}d${_day}hh${_hour}mm${_minute}ss${_second}'>
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y${_year}m${_month}d${_day}H${_hour}i${_minute}s${_second}'}
											<#elseif _formatSetting?if_exists=='yyyy${_year}m${_month}'>
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y${_year}m${_month}'}
											<#elseif _formatSetting?if_exists=='m${_month}d${_day}'>
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'m${_month}d${_day}'}
											<#elseif _formatSetting?if_exists=='h:mm'>
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H:i'}
											<#elseif _formatSetting?if_exists=='h:mm:ss'>
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H:i:s'}
											<#elseif _formatSetting?if_exists=='h${_hour}mm${_minute}'>
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H${_hour}i${_minute}'}
											<#elseif _formatSetting?if_exists=='h${_hour}mm${_minute}ss${_second}'>
												,formatter:'date'
												,formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H${_hour}i${_minute}s${_second}'}
											<#elseif _formatSetting?index_of("func:")!=-1>
												<#assign _functionName="${_formatSetting?replace('func:','','f')}">
												,formatter:${_functionName}
											<#else>
												<#if _formatSetting?index_of("#,##")!=-1>
													,formatter:'number'
													<#if _formatSetting?index_of(".")!=-1>
														<#assign _formatSetting_length="${_formatSetting?length}">
														,formatoptions:{decimalSeparator:".", thousandsSeparator: ",", decimalPlaces: ${_formatSetting_length}-6, defaultValue: '0.00'}
													<#else>
														,formatoptions:{decimalSeparator:" ", thousandsSeparator: ",", decimalPlaces: 0, defaultValue: '0'}
													</#if>
												<#else>
													,formatter:'number'
													<#if _formatSetting?index_of(".")!=-1>
														<#assign _formatSetting_length="${_formatSetting?length}">
														,formatoptions:{decimalSeparator:".", thousandsSeparator: "", decimalPlaces: ${_formatSetting_length}-2, defaultValue: '0.00'}
													<#else>
														,formatoptions:{decimalSeparator:" ", thousandsSeparator: "", decimalPlaces: 0, defaultValue: '0'}
													</#if>
												</#if>
											</#if>
										</#if>
									</#if>
								<#else>
									name:'_temporary${item_index}'
									,index:'_temporary${item_index}'
									,sortable:false
									<#if item.format?if_exists!=""&& item.format?index_of("func:")!=-1>
										<#assign _functionName="${item.format?replace('func:','','f')}">
										,formatter:${_functionName}
									</#if>
								</#if>
								<#if item.headStyle?if_exists !="">
									, width:${item.headStyle}
								</#if>
								<#if item.sortable?if_exists >
									,sortable:true
									<#else>
									,sortable:false
								</#if>
								<#if item.visible?if_exists >
									,hidden:false
									<#else>
									,hidden:true
								</#if>
							},
						</#list>
						],
				<#if rowNumbers?if_exists>
					rownumbers:true,
				</#if>
				onSelectRow: function(ids) {
					if(ids && ids!==lastselsub){
						jQuery('#${gridId}').jqGrid('restoreRow',lastselsub);
						jQuery('#${gridId}').jqGrid('editRow',ids,true);
						lastselsub=ids;
					}
				},
				<#if editurl?if_exists !="">
					editurl: "${ctx}${editurl}",
				</#if>
				<#if rowNum?if_exists !="">
					rowNum:${rowNum},
				</#if>
				<#if rowList?if_exists !="">
					rowList:[${rowList}],
				</#if>
				<#if sortname?if_exists !="">
					sortname:"${sortname}",
					sortorder:"${sortorder}",
				</#if>
				<#if pagination?if_exists !="">
					pager: '#${gridId}_pager',
				</#if>
				<#if multiselect?if_exists !="">
					multiselect: ${multiselect},
				</#if>
				<#if multiboxSelectOnly?if_exists!="" >
					multiboxonly:${multiboxSelectOnly},
				</#if>
				<#if total?if_exists>
					footerrow : true,
					userDataOnFooter : true,
				</#if>
				postData: { _list_code: "${_list_code}" }
			})<#if pagination?if_exists !="">.navGrid('#${gridId}_pager',{edit:false,add:false,del:false,search:false})</#if>;
	}
</div>