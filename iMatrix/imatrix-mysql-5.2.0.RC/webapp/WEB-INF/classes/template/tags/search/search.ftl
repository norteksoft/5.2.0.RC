<div id="search_shade"></div>
<div id="search_box" style="display: none;">
	<div id="search_header">
		<div id="search_title">查询条件</div>
		<div id="search_close" onclick="removeSearchBox();"> </div>
	</div>
	<input id="containerSearchInput" value="${containerId}" type="hidden" />
	<input id="advancedSearchInput" value="${advancedSearch}" type="hidden" />
	<input id="fixedSearchSignInput" value="${fixedSearchSign}" type="hidden" />
	<input id="submitForm" value="${submitForm}" type="hidden" />	
	<#if fixedField?size gt 0>
		<div id="fixedSearchZoon" style="display: block;padding:6px 10px 6px 10px;">
			<table id="parameter_Table"  class="fix-searchtable" style="width:100%;">
			  <#assign _index=0>
			   <#assign indexForTd=0>	
			 	<#assign addTdNumber=4-fixedField?size%4>
				<#list fixedField?chunk(4) as fields>
				  <#assign _index=_index+10>
				  	 <#assign indexForTd=indexForTd+1>	
					<tr >
						<#list fields as field>
							<#list field.enName?split('.') as value>
								<#assign _enName="${value}">
							</#list>
							<td  class="content-title" align="right">${field.chName}</td>
							<#if field.propertyType == 'STRING'>
								<#if field.optionsCode?if_exists != "">
									<td>
										<select id="condition_${field_index+_index}" name="${field.enName}" dbName="${field.dbName}" myType="xiala" dataType="${field.propertyType}" class="searchInputClass" 
										<#if field.eventType?if_exists != "">
											<#if field.eventType=="ONCHANGE">
												 onchange="${_enName}QueryChange('condition_${field_index+_index}');"
											</#if>
										</#if>
										>
											<option value="" selected="selected">请选择</option>
											<#list field.defaultValues?if_exists as option>
												<option value="${option.value}">${option.name}</option>										
											</#list>
										</select>
									</td>
								<#else>
									<td  >
										<input id="condition_${field_index+_index}" name="${field.enName}" dbName="${field.dbName}" dataType="STRING" class="searchInputClass"
										<#if field.eventType?if_exists != "">
											<#if field.eventType=="ONCLICK">
												 onclick="${_enName}QueryClick('condition_${field_index+_index}');" 
												 readonly="readonly" 
											<#elseif field.eventType=="BLUR">
												 onblur="${_enName}QueryBlur('condition_${field_index+_index}');"
											<#elseif field.eventType?index_of("/")!=-1>
												<#list field.eventType?split('/') as value>
													<#if value_index==0>
														<#assign c="${value}">
													<#elseif value_index==1>
														<#assign d="${value}">
													</#if>
												</#list>
												onclick="formGridTree({currentInputId:'condition_${field_index+_index}',treeType:'${c }',isMutiply:'${d}',useType:'false'});"
												readonly="readonly" 
											</#if>
										</#if>
										/>
									</td>
								</#if>
							<#elseif field.propertyType == 'INTEGER'||field.propertyType == 'LONG'>
								<#if field.optionsCode?if_exists != "">
									<td>
										<select id="condition_${field_index+_index}" name="${field.enName}" dbName="${field.dbName}" class="searchInputClass" dataType="${field.propertyType}" 
										<#if field.eventType?if_exists != "">
											<#if field.eventType=="ONCHANGE">
												 onchange="${_enName}QueryChange('condition_${field_index+_index}');"
											</#if>
										</#if>
										>
											<option value="" selected="selected">请选择</option>
											<#list field.defaultValues?if_exists as option>
												<option value="${option.value}">${option.name}</option>										
											</#list>
										</select>
									</td>
								<#else>
									<td >
										<input id="condition_${field_index+_index}" name="${field.enName}" dbName="${field.dbName}" dataType="${field.propertyType}" class="searchInputClass" 
										<#if field.eventType?if_exists != "">
											<#if field.eventType=="ONCLICK">
												 onclick="${_enName}QueryClick('condition_${field_index+_index}');"
												 readonly="readonly" 
											<#elseif field.eventType=="BLUR">
												 onblur="${_enName}QueryBlur('condition_${field_index+_index}');"
											<#elseif field.eventType?index_of("/")!=-1>
												<#list field.eventType?split('/') as value>
													<#if value_index==0>
														<#assign c="${value}">
													<#elseif value_index==1>
														<#assign d="${value}">
													</#if>
												</#list>
												onclick="formGridTree({currentInputId:'condition_${field_index+_index}',treeType:'${c }',isMutiply:'${d}',useType:'false'});"
												readonly="readonly" 
											</#if>
										</#if>
										/>
									</td>
								</#if>
							<#elseif field.propertyType == 'DOUBLE'||field.propertyType == 'FLOAT'||field.propertyType == 'NUMBER'||field.propertyType == 'AMOUNT'>
								<#if field.optionsCode?if_exists != "">
									<td>
										<select id="condition_${field_index+_index}" name="${field.enName}" dbName="${field.dbName}" dataType="${field.propertyType}" class="searchInputClass"
										<#if field.eventType?if_exists != "">
											<#if field.eventType=="ONCHANGE">
												 onchange="${_enName}QueryChange('condition_${field_index+_index}');"
											</#if>
										</#if>
										>
											<option value="" selected="selected">请选择</option>
											<#list field.defaultValues?if_exists as option>
												<option value="${option.value}">${option.name}</option>										
											</#list>
										</select>
									</td>
								<#else>
									<td >
										<input id="condition_${field_index+_index}" name="${field.enName}" dbName="${field.dbName}" dataType="${field.propertyType}" class="searchInputClass" onkeyup="value=value.replace(/[^0-9\.]/g,'');" 
										<#if field.eventType?if_exists != "">
											<#if field.eventType=="ONCLICK">
												 onclick="${_enName}QueryClick('condition_${field_index+_index}');"
												 readonly="readonly" 
											<#elseif field.eventType=="BLUR">
												 onblur="${_enName}QueryBlur('condition_${field_index+_index}');"
											<#elseif field.eventType?index_of("/")!=-1>
												<#list field.eventType?split('/') as value>
													<#if value_index==0>
														<#assign c="${value}">
													<#elseif value_index==1>
														<#assign d="${value}">
													</#if>
												</#list>
												onclick="formGridTree({currentInputId:'condition_${field_index+_index}',treeType:'${c }',isMutiply:'${d}',useType:'false'});"
												readonly="readonly" 
											</#if>
										</#if>
										/>
									</td>
								</#if>
							<#elseif field.propertyType == 'BOOLEAN'>
									<td>
										<select id="condition_${field_index+_index}" name="${field.enName}" dbName="${field.dbName}" dataType="${field.propertyType}" class="searchInputClass"
										<#if field.eventType?if_exists != "">
											<#if field.eventType=="ONCHANGE">
												 onchange="${_enName}QueryChange('condition_${field_index+_index}');"
											</#if>
										</#if>
										>
											<option value="" selected="selected">请选择</option>
											<#list field.defaultValues?if_exists as option>
												<option value="${option.value}">${option.name}</option>										
											</#list>
										</select>
									</td>
							<#elseif field.propertyType == 'ENUM'>
									<td>
										<select id="condition_${field_index+_index}" name="${field.enName}" dbName="${field.dbName}" dataType="${field.propertyType}" class="searchInputClass" enumName="${field.enumName}"
										<#if field.eventType?if_exists != "">
											<#if field.eventType=="ONCHANGE">
												 onchange="${_enName}QueryChange('condition_${field_index+_index}');"
											</#if>
										</#if>
										>
											<option value="" selected="selected">请选择</option>
											<#list field.defaultValues?if_exists as option>
												<option value="${option.value}">${option.name}</option>										
											</#list>
										</select>
									</td>		
							<#elseif field.propertyType == 'DATE'>
								<td>
									从<input id="condition_${field_index+_index}_b" name="${field.enName}" dbName="${field.dbName}" dataType="${field.propertyType}-first" class="searchInputClass" readonly="readonly" size="10" />
									<br/>
									到<input id="condition_${field_index+_index}_e" name="${field.enName}" dbName="${field.dbName}" dataType="${field.propertyType}-second" class="searchInputClass" readonly="readonly" size="10" />
                                    <script type="text/javascript">
                                       $('#condition_${field_index+_index}_b').datepicker({
							           	"dateFormat":'yy-mm-dd',
									      changeMonth:true,
									      changeYear:true,
									      showButtonPanel:"true"
							           });
                                       $('#condition_${field_index+_index}_e').datepicker({
                                       "dateFormat":'yy-mm-dd',
									      changeMonth:true,
									      changeYear:true,
									      showButtonPanel:"true"
							           });
					                </script>
								</td>
							<#elseif field.propertyType == 'TIME'>
								<td>
									从<input id="condition_${field_index+_index}_b" name="${field.enName}" dbName="${field.dbName}" dataType="${field.propertyType}-first" class="searchInputClass" readonly="readonly" size="10"/>
									<br/>
									到<input id="condition_${field_index+_index}_e" name="${field.enName}" dbName="${field.dbName}" dataType="${field.propertyType}-second" class="searchInputClass" readonly="readonly" size="10"/>
                                     <script type="text/javascript">
								           $('#condition_${field_index+_index}_b').datetimepicker({
										      "dateFormat":'yy-mm-dd',
										       changeMonth:true,
										       changeYear:true,
										       showSecond: false,
												showMillisec: false,
												"timeFormat": 'hh:mm'
								           });
                                           $('#condition_${field_index+_index}_e').datetimepicker({
									     	 "dateFormat":'yy-mm-dd',
                                              changeMonth:true,
								      		  changeYear:true,
								      		  showSecond: false,
												showMillisec: false,
												"timeFormat": 'hh:mm'
								           });
								
					                </script>
								</td>
							</#if>
						</#list>
						
					</tr>
				</#list>
				<tr>
					<#if fixedField?size gt 2>
						<td colspan="8" align="center" >
					<#else>
						<td colspan="${fixedField?size*2}" align="center">
					</#if>
						<button class='btn' onclick="fixedSearchSubmit('${tableId}', '${url}');"><span><span>确认</span></span></button>
						<button class='btn' onclick="clearParameter();"><span><span>清空</span></span></button>
						<#if advancedSearch == "true">
							<span ><a onclick="fixedToDynamic();" href="#"  > 高级查询>></a></span>
						</#if>	
					</td>
				</tr>
			</table>
		</div>
	</#if>
	<div id="searchZoon" style="display: none; margin-top: 6px;">
		<div id="_query_smessage" style="color: red; margin: 0px 0 0 20px;font-size:13px;"></div>
		<textarea style="display:none;" id="field_list" >${fieldList}</textarea>
		<p style="margin: 2px 0 2px 0;*margin-top:-14px;">
			&nbsp;
			<select id="left_bracket">
				<option value="" selected="selected"></option>
				<option value="(">(</option>
				<option value="((">((</option>
			</select>
			&nbsp;
			<select id="search_name" onchange="changeField();" style='width:100px'>
			</select>
			&nbsp;
			<select id="num_sign" onchange="_optChange(this);" style='width:80px'></select>
			&nbsp;
			<span id="search_value" style='width:160px;'></span>
			&nbsp;
			<select id="right_bracket">
				<option value="" selected="selected"></option>
				<option value=")">)</option>
				<option value="))">))</option>
			</select>
			&nbsp;
			<select id="rel_sign" style='width:50px'>
				<option value="and">并且</option>
				<option value="or">或者</option>
			</select>
			&nbsp;
			<button class='btn' onclick="addParameter();"><span><span>添加</span></span></button>
			<button class='btn' onclick="doSearch('${tableId}', '${url}');"><span><span>确认</span></span></button>
			<button class='btn' onclick="resetParameter();"><span><span>清空</span></span></button>
		</p>
		
		<form id="search_From" name="search_From" method="post">
			<input type="hidden" id="search_Parameters" name="searchParameters" />
		</form>
		
		<form id="default_Form" name="default_Form">	
			<table id="advanced_search_table_id" style="border: 0px; WIDTH: 100%;float:center;padding:0 10px;">
				<tr>
					<td style="border: 0px;">
						<div  class="searchtable">
							<table class="" id="search_Table" >
								<tr class="str"  >
									<th width="15px" style="border-right: 0px;"></th>
									<th width="140px" style="border-left: 0px;"> 字段</th>
									<th width="60px">运算符</th>
									<th style="border-right: 0px;">值</th>
									<th  width="15px" style="border-left: 0px;"></th>
									<th  width="60px">关系符</th>
									<th  width="70px">删除&nbsp;&nbsp;</th>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</form>
		<#if fixedField?size gt 0>
			<a  onclick="dynamicToFixed();" href="#" style="padding-left:12px;" > <<固定查询 </a>
		</#if>
	</div>
</div>

