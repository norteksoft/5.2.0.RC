<div class="_custom_table">
<table id="${tableId}" newId='1' ></table>
</div>
<input id="_login_name" type="hidden" value="${loginName}"/>
<input id="_user_name" type="hidden" value="${userName}"/>
<script type="text/javascript">
var customGrid;
	$(document).ready(function(){
		<#if listView.deleteUrl?if_exists !="">
			<#if isSubSystem?if_exists=='true'>
				deleteUrl='${listView.deleteUrl}';
			<#else>
				deleteUrl='${ctx}${listView.deleteUrl}';
			</#if>			
		<#else>
			deleteUrl='';
		</#if>
		customGrid={
			datatype:'local',
			mtype:'POST',
			colNames:${colNames},
			colModel:${colModel},
			rowNum:300,
			editurl:'clientArray',
			onSelectRow: function(id,status){
				if(hasEdit){
					if(id && id!=lastsel){
						if(!isHasEdit('${tableId}')){//如果表格有验证，且验证失败时，则不进入该判断
							restoreOtherTable('${tableId}');
							saveRowWhenAdd(lastsel,'${tableId}');
							lastsel=0;//当新增一条记录后，当前选中的纪录设为非任何纪录
						}
					}
				}
			}
			<#if attributeName?if_exists!="">
				,myattrName:"${attributeName}"
			<#else>
				,myattrName:"customTable"
			</#if>
			<#if listView.multiSelect?if_exists>
				,multiselect: true
				<#if listView.multiboxSelectOnly?if_exists>
				,multiboxonly:true
				<#else>
				,multiboxonly:false
				</#if>
			<#else>
				,multiselect: false
			</#if>
			<#if jsonData?if_exists!="">
				,data:${jsonData}
			</#if>
			<#if listView.rowNumbers?if_exists>
				,rownumbers:true
			</#if>
			<#if footerDatas?if_exists!="">
				,footerrow : true
				,userDataOnFooter : true
				,altRows : true
			</#if>
			<#if listView.customProperty?if_exists!="">
				,${listView.customProperty}
			</#if>
			<#if listView.orderFieldName?if_exists!="">
				<#if editable?if_exists!="false">
					,indexname:'${listView.orderFieldName}'
				<#else>
					,indexname:'false'
				</#if>				
			<#else>
				,indexname:'false'
			</#if>
			<#if listView.editable?if_exists>
				,gridComplete: function(){
					newId='0';lastsel='0';hasEdit=false;
					var ids =jQuery("#${tableId}").jqGrid('getDataIDs');
					for(var i=0;i < ids.length;i++){
						var cl = ids[i];
						ae = "<a href='#pos' class='small-button-bg' onclick='myAddRow(\"1\",\""+cl+"\",\"${tableId}\");'><span class='ui-icon ui-icon-plusthick'></span></a>";
						<#if webCtx?if_exists!="">
							<#assign _imatrix="${webCtx}">
						<#else>
							<#assign _imatrix="">
						</#if>
						<#if isSubSystem?if_exists!="">
							<#assign _isSubSystem="${isSubSystem}">
						<#else>
							<#assign _isSubSystem="">
						</#if>
						if(deleteUrl!=''){
							de = "<a href='#pos' class='small-button-bg' onclick='deleteFormTableData(\"${tableId}\",\""+cl+"\",\""+deleteUrl+"\",\"${_isSubSystem}\",\"${_imatrix}\");'><span class='ui-icon  ui-icon-minusthick'></span></a>";
						}else{
							de = "<a href='#pos' class='small-button-bg' onclick='$deleteFormTableData(\"${tableId}\",\""+cl+"\",\"${_isSubSystem}\",\"${_imatrix}\");'><span class='ui-icon  ui-icon-minusthick'></span></a>";
						}
						edit = "<a href='#pos' class='small-button-bg' onclick=\"editClick('"+cl+"');\"><span class='ui-icon  ui-icon-pencil'></span></a>";
						jQuery("#${tableId}").jqGrid('setRowData',ids[i],{act:ae+' '+de+' '+edit+_getCustomeButtons('${tableId}',ids[i])});
					}
					$gridComplete('${tableId}');
				}
			</#if>
			,onCellSelect:$onCellClick
			,ondblClickRow: function(id){
				$ondblClick(id);
			}
			,onRightClickRow: function(id){
				$onRightClick(id);
			}
		};
		jQuery("#${tableId}").jqGrid(customGrid);
		<#if listView.orderFieldName?if_exists!="">
			<#if editable?if_exists!="false">
				  //可拖动的选择权限
				  var _rowId;
				var sortableOptions = {
					items : '.jqgrow:not(.unsortable)',
					start : function(event, ui) {
						_rowId = ui.item.attr('id');
						var originalIndex = jQuery("#${tableId}").jqGrid("getInd", _rowId);
						$sortableRowsStart(_rowId,originalIndex,'${tableId}');
					},
					stop : function(event, ui) {
						var newIndex = jQuery("#${tableId}").jqGrid("getInd", _rowId);
						$sortableRowsStop(_rowId,newIndex,'${tableId}');
					}
				};
				jQuery("#${tableId}").jqGrid('sortableRows',sortableOptions);
			</#if>
		</#if>
		
		<#if jsonData?if_exists!="">
			var mydata=${jsonData};
			for(var i=0;i<mydata.length;i++){
				if(mydata[i].id=='new_0'){
					jQuery('#${tableId}').jqGrid('editRow',mydata[i].id,true,function(){editFun(mydata[i].id);},function(){},'',{},
									function(){
										hasEdit=false;
										$editRowSave('new_0','${tableId}');
										lastsel=0;//当新增一条记录后，当前选中的纪录设为非任何纪录										
									},
									function(){},
									function(){
										hasEdit=false;
										$editRowRestore('new_0','${tableId}');
										lastsel=0;//当新增一条记录后，当前选中的纪录设为非任何纪录	
										
										jQuery("#${tableId}").jqGrid('delRowData','new_0');
										_add_row('${tableId}');
									}
								);
				}
			}
		</#if>
		<#if footerDatas?if_exists!="">
			jQuery("#${tableId}").jqGrid('footerData','set',${footerDatas});
		</#if>		
	});
	
	function editClick(id){
		restoreOtherTable('${tableId}');
		saveRowWhenAdd(lastsel,'${tableId}');
		if(!hasEdit){
			if(id && id!=lastsel){
				jQuery("#${tableId}").jqGrid('restoreRow',lastsel);
				customGridEditRow(id,'${tableId}');
				lastsel=id;
				$editClickCallback(id,'${tableId}');
			}
		}
	}
	
	function editFun(rowId){
		lastsel=rowId;hasEdit=true;
		<#list listView.columns?if_exists as item>
			<#if item.tableColumn?if_exists!="">
				<#global _columnName="${item.tableColumn.name}">
				<#if _columnName?if_exists?contains(".")>
					<#assign i=0>
					<#list _columnName?split('.') as names>
						<#if i==0>
							<#global _columnName="${names}">
						</#if>
						<#assign i=i+1>
						<#if i!=1>
							<#assign _capName="${names?cap_first}">
							<#global _columnName="${_columnName}${_capName}">
						</#if>
				 	</#list>
				</#if>
				<#if item.controlValue?if_exists!='CUSTOM'>
					<#if item.controlValue?if_exists=='CHECKBOX'>
						<#global _tagName="checkbox">
					<#elseif item.controlValue?if_exists=='MULTISELECT'||item.controlValue?if_exists=='SELECT'>
						<#global _tagName="select">
					<#elseif item.controlValue?if_exists=='TEXTAREA'>
						<#global _tagName="textarea">
					<#else>
						<#global _tagName="input">
					</#if>
					<#if item.controlValue?if_exists=='MULTISELECT'>
							jQuery("${_tagName}[id='"+rowId+"_${item.tableColumn.name}']","#${tableId}").multiselect({
								checkAllText:"全选",
								uncheckAllText:"清除",
								noneSelectedText:"请选择",
								selectedList:4
							});
					</#if>
					
					<#if item.tableColumn.dataType?if_exists=='DATE'>		
							jQuery('${_tagName}[id="'+rowId+'_${item.tableColumn.name}"]','#${tableId}').attr('readonly','readonly');
						 	jQuery('${_tagName}[id="'+rowId+'_${item.tableColumn.name}"]','#${tableId}').datepicker({
									           	"dateFormat":'yy-mm-dd',
											      changeMonth:true,
											      changeYear:true,
											      showButtonPanel:"true",
											      onSelect:function(dateText, inst){$dateOnSelect({rowid:rowId,currentInputId:rowId+'_${item.tableColumn.name}',dateText:dateText});},
											      onChangeMonthYear:function(){$dateOnChangeMonthYear({rowid:rowId,currentInputId:rowId+'_${item.tableColumn.name}'});},
											      onClose:function(){$dateOnClose({rowid:rowId,currentInputId:rowId+'_${item.tableColumn.name}'});}
									           });
					<#elseif item.tableColumn.dataType?if_exists=='TIME'>
							jQuery('${_tagName}[id="'+rowId+'_${item.tableColumn.name}"]','#${tableId}').attr('readonly','readonly');
							jQuery('${_tagName}[id="'+rowId+'_${item.tableColumn.name}"]','#${tableId}').datetimepicker({
												      "dateFormat":'yy-mm-dd',
												       changeMonth:true,
												       changeYear:true,
												       showSecond: false,
														showMillisec: false,
														"timeFormat": 'hh:mm',
													      onSelect:function(dateText, inst){$dateOnSelect({rowid:rowId,currentInputId:rowId+'_${item.tableColumn.name}',dateText:dateText});},
													      onChangeMonthYear:function(){$dateOnChangeMonthYear({rowid:rowId,currentInputId:rowId+'_${item.tableColumn.name}'});},
													      onClose:function(){$dateOnClose({rowid:rowId,currentInputId:rowId+'_${item.tableColumn.name}'});}
										           });
					<#elseif item.eventType?if_exists!="">
					 	<#list item.eventType?split(',') as eventItem>
						 	<#if eventItem?if_exists=="ONCLICK">
							 	jQuery('${_tagName}[id='+rowId+'_${item.tableColumn.name}]','#${tableId}').attr("readonly","readonly");
								jQuery('${_tagName}[id='+rowId+'_${item.tableColumn.name}]','#${tableId}').click(function(){${_columnName}Click({rowid:rowId,currentInputId:rowId+'_${item.tableColumn.name}'});});
						 	<#elseif eventItem?if_exists=="ONCHANGE">
						 		jQuery('${_tagName}[id='+rowId+'_${item.tableColumn.name}]','#${tableId}').change(function(){${_columnName}Change({rowid:rowId,currentInputId:rowId+'_${item.tableColumn.name}'});});
						 	<#elseif eventItem?if_exists=="ONDBLCLICK">	
								jQuery('${_tagName}[id='+rowId+'_${item.tableColumn.name}]','#${tableId}').dblclick(function(){${_columnName}Dblclick({rowid:rowId,currentInputId:rowId+'_${item.tableColumn.name}'});});
							<#elseif eventItem?if_exists=="BLUR">	
								jQuery('${_tagName}[id='+rowId+'_${item.tableColumn.name}]','#${tableId}').blur(function(){${_columnName}Blur({rowid:rowId,currentInputId:rowId+'_${item.tableColumn.name}'});});
						 	</#if>
					 	</#list>
					<#elseif item.controlValue?if_exists?contains("SELECT_TREE")>
						<#list item.controlValue?split(',') as value>
							<#if value_index==0>
								<#assign a="${value}">
							<#elseif value_index==1>
								<#assign b="${value}">
							<#elseif value_index==2>
								<#assign c="${value}">
							<#elseif value_index==3>
								<#assign d="${value}">
							</#if>
						</#list>
						jQuery('${_tagName}[id='+rowId+'_${item.tableColumn.name}]','#${tableId}').attr("readonly","readonly");
						jQuery('${_tagName}[id='+rowId+'_${item.tableColumn.name}]','#${tableId}').click(function(){formGridTree({rowid:rowId,currentInputId:rowId+'_${item.tableColumn.name}',hiddenField:'${b }',treeType:'${c }',isMutiply:'${d}',useType:true});});
					</#if>
				</#if>
			</#if>
		</#list>
	}
</script>