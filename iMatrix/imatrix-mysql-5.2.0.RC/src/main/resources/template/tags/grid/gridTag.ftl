<table id="${gridId}"></table>
<div id="${gridId}_pager"></div>
<input id="_login_name" type="hidden" value="${loginName}"/>
<input id="_user_name" type="hidden" value="${userName}"/>
<input id="_exportable_sign" type="hidden" value="${export}"/>
<input id="_main_grid_id" type="hidden" value="${gridId}"/>
<#if subGrid?if_exists !="">
	<input id="_have_sub_grid" type="hidden" value="${subGrid}"/>
<#else>
	<input id="_have_sub_grid" type="hidden" value=""/>
</#if>
<div id="_grid_div_script" style="display:none;">
	jqGridOption={
		url:encodeURI("${url}"),
		<#if pageName?if_exists !="">
			prmNames:{
				rows:'${pageName}.pageSize',
				page:'${pageName}.pageNo',
				sort:'${pageName}.orderBy',
				order:'${pageName}.order'
			},
		</#if>
		
		gridComplete:function(){
			contentResize();
			validatePageInput();
			savePageInfo();
			<#if mergerCell?if_exists>
				mergerCell();
			</#if>
			<#if subGrid?if_exists !="">
				defaultSelectFirstRow();
			</#if>
			totalPageSetting();
			$gridComplete();
		},
		loadBeforeSend:function(xhr, settings){
			updateGridPageInfo(xhr, settings);
			$loadBeforeSend(xhr, settings);
		},
		
		colNames:[
			<#list columns?if_exists as item>
				<#if item.headerName?if_exists !="">
					'${item.internationName}',
				<#else>
					'',
				</#if>
			</#list>
			<#list dynamicColumn?if_exists as item>
				<#if item.colName?if_exists !="">
					'${item.colName}',
				</#if>
			</#list>
			
		], 
		dynamicColumnNames:[
			<#list dynamicColumn?if_exists as item>
				<#if item.colName?if_exists !="" && item.exportable?if_exists>
					'${item.colName}',
				</#if>
			</#list>
			
		], 
		colModel:[<#assign _frozenColumn='${frozenColumn}'?number>
				<#global _frozenColumn_amount=0>
				<#list columns?if_exists as item>
						{	<#if item.tableColumn?if_exists!="">
								name:'${item.tableColumn.name}'
								,index:'${item.tableColumn.name}'
								<#if item.mergerCell?if_exists>
									,cellattr: function(rowId, tv, rawObject, cm, rdata) {
			                            return 'id=\'${item.tableColumn.name}' + rowId + "\'";
			                        }
			                    <#else>
			                        ,cellattr: function(rowId, tv, rawObject, cm, rdata) {  
			                            return 'style=\'padding-left:15px;\'';
			                        }
								</#if>
								
								<#if item.editable>
									,editable:true
									<#if item.editRules?if_exists!="">
										<#assign _editRules="${item.editRules}">
									<#else>
										<#assign _editRules="">
									</#if>
									<#if item.tableColumn.dataType?if_exists=='NUMBER'||item.tableColumn.dataType?if_exists=='AMOUNT'||item.tableColumn.dataType?if_exists=='DOUBLE'||item.tableColumn.dataType?if_exists=='FLOAT'>
										<#if _editRules?if_exists!="">
											<#assign _editRules="${_editRules},">
										</#if>
										<#assign _editRules="${_editRules}number:true">
									<#elseif item.tableColumn.dataType?if_exists=='INTEGER'||item.tableColumn.dataType?if_exists=='LONG'>
										<#if _editRules?if_exists!="">
											<#assign _editRules="${_editRules},">
										</#if>
										<#assign _editRules="${_editRules}integer:true">
									</#if>
									<#if _editRules?if_exists!="">
										,editrules:{${_editRules}}
									</#if>
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
									<#if item.format?if_exists!="">
										<#assign _formatSetting="${item.format}">
										<#if _formatSetting?index_of("func:")!=-1>
											<#list _formatSetting?split(',') as names>
												<#if names_index==0>
													<#if names?index_of("unfunc:")!=-1>
														,unformat:${names?replace('unfunc:','','f')}
													<#elseif names?index_of("func:")!=-1>
														,formatter:${names?replace('func:','','f')}
													</#if>
												<#elseif names_index==1>
													,unformat:${names?replace('unfunc:','','f')}
												</#if>
										 	</#list>
										<#else>
											,edittype:'checkbox'
											,formatter:formatCheckbox
											,unformat:unFormatCheckbox
											,editoptions: {value:"true:false"}
										</#if>
									<#else>
										,edittype:'checkbox'
										,formatter:formatCheckbox
										,unformat:unFormatCheckbox
										,editoptions: {value:"true:false"}
									</#if>
									
								<#elseif item.controlValue?if_exists!=""&&item.controlValue?if_exists=='TEXTAREA'>
									,edittype:'textarea'
								<#elseif item.controlValue?if_exists!=""&&item.controlValue?if_exists=='CUSTOM'>
									,edittype:'custom'
									,editoptions: {custom_element: ${item.columnName}Element, custom_value:${item.columnName}Value}
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
											<#list _formatSetting?split(',') as names>
												<#if names_index==0>
													<#if names?index_of("unfunc:")!=-1>
														,unformat:${names?replace('unfunc:','','f')}
													<#elseif names?index_of("func:")!=-1>
														,formatter:${names?replace('func:','','f')}
													</#if>
												<#elseif names_index==1>
													,unformat:${names?replace('unfunc:','','f')}
												</#if>
										 	</#list>
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
								
								<#if item.defaultValue?if_exists=='CURRENT_DATE'>
									,unformat:unFormatCurrentDate
								<#elseif item.defaultValue?if_exists=='CURRENT_TIME'>
									,unformat:unFormatCurrentTime
								<#elseif item.defaultValue?if_exists=='CURRENT_USER_NAME'>
									,unformat:unFormatUserName
								<#elseif item.defaultValue?if_exists=='CURRENT_LOGIN_NAME'>
									,unformat:unFormatLoginName
								</#if>
							<#else>
								name:'_temporary${item_index}'
								,index:'_temporary${item_index}'
								,sortable:false
								<#if item.format?if_exists!=""&& item.format?index_of("func:")!=-1>
									<#list item.format?split(',') as names>
										<#if names_index==0&&names?index_of("unfunc:")==-1&&names?index_of("func:")!=-1>
											,formatter:${names?replace('func:','','f')}
										</#if>
								 	</#list>
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
							<#if (item.visible?if_exists)>
								<#global _frozenColumn_amount="${(_frozenColumn_amount+1)}"?number>
							</#if>
							<#if (_frozenColumn gte _frozenColumn_amount )>
								,frozen : true
							</#if>
						},
				</#list>
				<#list dynamicColumn?if_exists as item>
					{name:'${item.name}',index:'${item.name}'
						<#if item.editable?if_exists >
							,editable:true
							<#if item.editRules?if_exists!="">
								<#assign _editRules="${item.editRules}">
							<#else>
								<#assign _editRules="">
							</#if>
							<#if item.type?if_exists=='NUMBER'||item.type?if_exists=='AMOUNT'||item.type?if_exists=='DOUBLE'||item.type?if_exists=='FLOAT'>
									<#if _editRules?if_exists!="">
										<#assign _editRules="${_editRules},">
									</#if>
									<#assign _editRules="${_editRules}number:true">
								<#elseif item.type?if_exists=='INTEGER'||item.type?if_exists=='LONG'>
									<#if _editRules?if_exists!="">
										<#assign _editRules="${_editRules},">
									</#if>
									<#assign _editRules="${_editRules}integer:true">
								</#if>
								<#if _editRules?if_exists!="">
									,editrules:{${_editRules}}
								</#if>
						<#else>
							,editable:false
						</#if>
						<#if item.editoptions?if_exists!="">
							,edittype:'select'
							,formatter:'select'
							,editoptions:{value:{${item.editoptions}}}
						<#elseif item.type?if_exists=='BOOLEAN'>
							,edittype:'checkbox'
							,formatter:formatCheckbox
							,unformat:unFormatCheckbox
							,editoptions: {value:"true:false"}
						<#elseif item.edittype.code?if_exists=='edit.control.type.textarea'>
							,edittype:'textarea'
						</#if>
						<#if item.defaultValue?if_exists=='CURRENT_DATE'>
							,unformat:unFormatCurrentDate
						<#elseif item.defaultValue?if_exists=='CURRENT_TIME'>
							,unformat:unFormatCurrentTime
						<#elseif item.defaultValue?if_exists=='CURRENT_USER_NAME'>
							,unformat:unFormatUserName
						<#elseif item.defaultValue?if_exists=='CURRENT_LOGIN_NAME'>
							,unformat:unFormatLoginName
						</#if>
						<#if item.colWidth?if_exists !="">
							, width:${item.colWidth}
						</#if>
						<#if item.visible?if_exists >
							,hidden:false
							<#else>
							,hidden:true
						</#if>
						,sortable:false
					},
				</#list>
				],
		<#if rowNumbers?if_exists>
			rownumbers:true,
		</#if>
		<#if customProperty?if_exists!="">
			${customProperty},
		</#if>
		onSelectRow: function(ids) { 
			<#if subGrid?if_exists !="">
				if(ids == null) { ids=0; 
					if(jQuery("#"+getTableId()).jqGrid('getGridParam','records') >0 ) { 
						jQuery("#"+getTableId()).jqGrid('setGridParam',{url:getUrl()+"?q=1&id="+ids+getUrlParameter(),mtype:'post',postData:_temp_search_parameters,page:1}).trigger('reloadGrid');  
					} 
				} else { jQuery("#"+getTableId()).jqGrid('setGridParam',{url:getUrl()+"?q=1&id="+ids+getUrlParameter(),mtype:'post',postData:_temp_search_parameters,page:1}).trigger('reloadGrid'); }
			</#if>
		},
		onCellSelect:$onCellClick,
		ondblClickRow: editRow,
		<#if editurl?if_exists !="">
			editurl: "${ctx}${editurl}",
		</#if>
		<#if rowNum?if_exists !="">
			rowNum:${rowNum},
		</#if>
		<#if rowList?if_exists !="">
			rowList:[${rowList}],
		</#if>
		<#if multiselect?if_exists !="">
			multiselect: ${multiselect},
		</#if>
		<#if multiboxSelectOnly?if_exists!="" >
			multiboxonly:${multiboxSelectOnly},
		</#if>
		<#if sortname?if_exists !="">
			sortname:"${sortname}",
			sortorder:"${sortorder}",
		</#if>
		
		
		<#if pagination?if_exists !="">
			pager: '#${gridId}_pager',
		</#if>
		serializeRowData:function(data){
			if(data.id==0){
				data.id="";
			}
			var arr=$processRowData(data);
			return arr;
		},
		<#if total?if_exists>
			footerrow : true,
			userDataOnFooter : true,
		</#if>
		postData: { _list_code: "${_list_code}"
			<#if dynamicColumns?if_exists !="">
				,dynamicColumns:'${dynamicColumns}'
			</#if>
			,searchParameters:searchParameters
		}
	};
	var searchParameters="";
	$(document).ready(function(){
		<#if subGrid?if_exists !="">
			initializationSubGrid();
		</#if>
		if($("#___searchParameters").attr("id")=="___searchParameters"){
			searchParameters=$("#___searchParameters").attr("value");
			jqGridOption.postData.searchParameters=searchParameters;
		}
		initJqGrid({
			gridId:'${gridId}',
			deleteUrl:'${ctx}${deleteUrl}',
			<#if pagination?if_exists !="">
				sortUrl:'',
				havePage:true,
			<#else>
				<#if dragRowUrl?if_exists !="">
					sortUrl:'${ctx}${dragRowUrl}',
				<#else>
					sortUrl:'',
				</#if>
				havePage:false,
			</#if>
			extraParams:{_list_code: "${_list_code}"}
		});
		<#if (_frozenColumn gt 0 )>
			jQuery("#${gridId}").jqGrid('setFrozenColumns');
		</#if>
		<#if groupHeaderSign?if_exists !="">
			jQuery("#${gridId}").jqGrid('destroyGroupHeader');
			jQuery("#${gridId}").jqGrid('setGroupHeaders', {
			 useColSpanStyle: true, 
			 groupHeaders:[
			 <#list groupHeader?if_exists as group>
			 	<#if (group_index gt 0)>
			 		,
			 	</#if>
			 	{startColumnName: '${group.startColumnName}', numberOfColumns: ${group.numberOfColumns}, titleText: '${group.titleText}'}
			 </#list>
			 ] 
			});
		</#if>
		initSearch();		
	});
	
	function mergerCell(){
		var gridName = "${gridId}";
		<#list columns?if_exists as item>
			<#if item.mergerCell?if_exists && item.tableColumn?if_exists!="">
				<#if item.mainKey?if_exists!="">
					merger(gridName, '${item.tableColumn.name}','${item.mainKey.name}');
				<#else>
					merger(gridName, '${item.tableColumn.name}','');
				</#if>
			</#if>
		</#list>
	}
	
	function editFun(id){
		<#list columns?if_exists as item>
			<#if item.tableColumn?if_exists!="">
				<#if item.controlValue?if_exists!=""&&item.controlValue?if_exists=='CHECKBOX'>
					<#global _tagName="checkbox">
				<#elseif item.controlValue?if_exists!=""&&(item.controlValue?if_exists=='MULTISELECT'||item.controlValue?if_exists=='SELECT')>
					<#global _tagName="select">
				<#elseif item.controlValue?if_exists!=""&&item.controlValue?if_exists=='TEXTAREA'>
					<#global _tagName="textarea">
				<#else>
					<#global _tagName="input">
				</#if>
				<#if item.controlValue?if_exists!=""&&item.controlValue?if_exists=='MULTISELECT'>
						jQuery("${_tagName}[id='"+id+"_${item.tableColumn.name}']","#${gridId}").multiselect({
							checkAllText:"全选",
							uncheckAllText:"清除",
							noneSelectedText:"请选择",
							selectedList:4
						});
				</#if>
				<#if item.tableColumn.dataType?if_exists=='DATE'>
					jQuery("${_tagName}[id='"+id+"_${item.tableColumn.name}']","#${gridId}").attr("readonly","readonly");
					jQuery("${_tagName}[id='"+id+"_${item.tableColumn.name}']","#${gridId}").datepicker({
							           	"dateFormat":'yy-mm-dd',
									      changeMonth:true,
									      changeYear:true,
									      showButtonPanel:"true",
									      onSelect:function(dateText, inst){$dateOnSelect({rowid:id,currentInputId:id+'_${item.tableColumn.name}',dateText:dateText});},
									      onChangeMonthYear:function(){$dateOnChangeMonthYear({rowid:id,currentInputId:id+'_${item.tableColumn.name}'});},
									      onClose:function(){$dateOnClose({rowid:id,currentInputId:id+'_${item.tableColumn.name}'});}
							           });
					
				<#elseif item.tableColumn.dataType?if_exists=='TIME'>
					jQuery("${_tagName}[id='"+id+"_${item.tableColumn.name}']","#${gridId}").attr("readonly","readonly");
					jQuery("${_tagName}[id='"+id+"_${item.tableColumn.name}']","#${gridId}").datetimepicker({
										      "dateFormat":'yy-mm-dd',
										       changeMonth:true,
										       changeYear:true,
										       showSecond: false,
												showMillisec: false,
												"timeFormat": 'hh:mm',
												onSelect:function(dateText, inst){$dateOnSelect({rowid:id,currentInputId:id+'_${item.tableColumn.name}',dateText:dateText});},
											    onChangeMonthYear:function(){$dateOnChangeMonthYear({rowid:id,currentInputId:id+'_${item.tableColumn.name}'});},
											    onClose:function(){$dateOnClose({rowid:id,currentInputId:id+'_${item.tableColumn.name}'});}
								           });
				<#elseif item.eventType?if_exists!="">
				 	<#list item.eventType?split(',') as eventItem>
					 	<#if eventItem?if_exists=="ONCLICK">
						 	jQuery("${_tagName}[id='"+id+"_${item.tableColumn.name}']","#${gridId}").attr("readonly","readonly");
							jQuery("${_tagName}[id='"+id+"_${item.tableColumn.name}']","#${gridId}").click(function(){${item.columnName}Click({rowid:id,currentInputId:id+'_${item.tableColumn.name}'});});
					 	<#elseif eventItem?if_exists=="ONCHANGE">
					 		jQuery("${_tagName}[id='"+id+"_${item.tableColumn.name}']","#${gridId}").change(function(){${item.columnName}Change({rowid:id,currentInputId:id+'_${item.tableColumn.name}'});});
					 	<#elseif eventItem?if_exists=='ONDBLCLICK'>
							jQuery("${_tagName}[id='"+id+"_${item.tableColumn.name}']","#${gridId}").dblclick(function(){${item.columnName}Dblclick({rowid:id,currentInputId:id+'_${item.tableColumn.name}'});});
						<#elseif eventItem?if_exists=="BLUR">	
							jQuery("${_tagName}[id='"+id+"_${item.tableColumn.name}']","#${gridId}").blur(function(){${item.columnName}Blur({rowid:id,currentInputId:id+'_${item.tableColumn.name}'});});
					 	</#if>
				 	</#list>
				<#elseif item.controlValue?if_exists?contains("SELECT_TREE")>
					<#list item.controlValue?split(',') as value>
						<#if value_index==1>
							<#assign b="${value}">
						<#elseif value_index==2>
							<#assign c="${value}">
						<#elseif value_index==3>
							<#assign d="${value}">
						</#if>
					</#list>
					jQuery('${_tagName}[id="'+id+'_${item.tableColumn.name}"]','#${gridId}').attr("readonly","readonly");
					jQuery('${_tagName}[id='+id+'_${item.tableColumn.name}]','#${gridId}').click(function(){formGridTree({rowid:id,currentInputId:id+'_${item.tableColumn.name}',hiddenField:'${b }',treeType:'${c }',isMutiply:'${d}',useType:true});});
				</#if>
				
				
			</#if>
		</#list>
		<#list dynamicColumn?if_exists as item>
			<#if item.type?if_exists=='DATE'>
				jQuery('#'+id+'_${item.name}','#${gridId}').attr("readonly","readonly");
				jQuery('#'+id+'_${item.name}','#${gridId}').datepicker({
							           	"dateFormat":'yy-mm-dd',
									      changeMonth:true,
									      changeYear:true,
									      showButtonPanel:"true",
									      onSelect:function(dateText, inst){$dateOnSelect({rowid:id,currentInputId:id+'_${item.name}',dateText:dateText});},
									      onChangeMonthYear:function(){$dateOnChangeMonthYear({rowid:id,currentInputId:id+'_${item.name}'});},
									      onClose:function(){$dateOnClose({rowid:id,currentInputId:id+'_${item.name}'});}
							           });
			<#elseif item.type?if_exists=='TIME'>
				jQuery('#'+id+'_${item.name}','#${gridId}').attr("readonly","readonly");
				jQuery('#'+id+'_${item.name}','#${gridId}').datetimepicker({
										      "dateFormat":'yy-mm-dd',
										       changeMonth:true,
										       changeYear:true,
										       showSecond: false,
												showMillisec: false,
												"timeFormat": 'hh:mm',
												onSelect:function(dateText, inst){$dateOnSelect({rowid:id,currentInputId:id+'_${item.name}',dateText:dateText});},
										      	onChangeMonthYear:function(){$dateOnChangeMonthYear({rowid:id,currentInputId:id+'_${item.name}'});},
										      	onClose:function(){$dateOnClose({rowid:id,currentInputId:id+'_${item.name}'});}
								           });
								           
			<#else>
			 	<#if item.edittype?if_exists=='TEXT'>
					<#if item.eventType?if_exists=='ONCLICK'>
						jQuery('#'+id+'_${item.name}','#${gridId}').attr("readonly","readonly");
						jQuery('#'+id+'_${item.name}','#${gridId}').click(function(){${dynamicColumnName}Click({rowid:id,currentInputId:id+'_${item.name}'});});
					<#elseif item.eventType?if_exists=='ONDBLCLICK'>	
						jQuery('#'+id+'_${item.name}','#${gridId}').dblclick(function(){${dynamicColumnName}Dblclick({rowid:id,currentInputId:id+'_${item.name}'});});
					<#elseif eventItem?if_exists=="BLUR">	
						jQuery('#'+id+'_${item.name}','#${gridId}').blur(function(){${dynamicColumnName}Blur({rowid:id,currentInputId:id+'_${item.name}'});});
					</#if>
				<#elseif item.edittype?if_exists=='SELECT'>
					<#if item.eventType?if_exists=='ONCHANGE'>
						jQuery('#'+id+'_${item.name}','#${gridId}').change(function(){${dynamicColumnName}Change({rowid:id,currentInputId:id+'_${item.name}'});});
					</#if>
				</#if>
			</#if>
		</#list>
	}
</div>
<script type="text/javascript">
	$(document).ready(function(){
		<#if subGrid?if_exists !="">
			$("#_main_grid_id").after($("#_sub_grid_div_script"));
			var _customer_sub_grid_script = document.createElement("script");
			appendChild(_customer_sub_grid_script,$("#_sub_grid_div_script").text());
			eval($("#_sub_grid_div_script").text());
			$("#_sub_grid_div_script").html("");
			var _sub_grid_div_script = document.getElementById("_sub_grid_div_script");
			_sub_grid_div_script.appendChild(_customer_sub_grid_script);
		</#if>
		var customer_grid_script = document.createElement("script");
		appendChild(customer_grid_script,$("#_grid_div_script").text());
		eval($("#_grid_div_script").text());
		$("#_grid_div_script").html("");
		var _grid_div_script = document.getElementById("_grid_div_script");
		_grid_div_script.appendChild(customer_grid_script);
	
	});
</script>