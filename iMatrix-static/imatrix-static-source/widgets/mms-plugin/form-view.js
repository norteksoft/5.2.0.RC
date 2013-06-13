//树跳转
function treechange(currentId){
	ajaxSubmit("defaultForm",webRoot+"/form/form-view.htm", "viewTable",validateNext);
}

function controlClick(id,controlType){
	var height=0;
	if(controlType=="PULLDOWNMENU"){
		height=400;
	}else if(controlType=="LIST_CONTROL"){
		height=400;
	}else{
		height=400;
	}
	$.colorbox({href:webRoot+"/form/form-view!text.htm?formControl.controlType="+controlType+"&formId="+$("#formId").val(),iframe:true, innerWidth:600, innerHeight:height,overlayClose:false});
}
function controlProperty(urlParam){
	var url = webRoot+"/form/form-view!text.htm?"
	+urlParam;
	$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:350,overlayClose:false});
}

function controlDeptProperty(name,controlType,showDeptControlValue,showDeptControlId,showDeptCotrolType,saveDeptControlValue,saveDeptControlId,deptTreeType,deptMultiple,style,classStyle){
	var url = webRoot+"/form/form-view!text.htm?"
	+"formControl.controlType="+controlType
	+"&formControl.name="+name
	+"&formControl.showDeptControlId="+showDeptControlId 
	+"&formControl.showDeptCotrolType="+showDeptCotrolType 
	+"&formControl.saveDeptControlId="+saveDeptControlId  
	+"&formControl.deptTreeType="+deptTreeType  
	+"&formControl.deptMultiple="+deptMultiple
	+"&formControl.styleContent="+style 
	+"&formControl.classStyle="+classStyle  
	+"&occasion="+"update";
	if(saveDeptControlValue==""){
		url=url
		+"&standard=false";
	}else{
		url=url
		+"&standard=true"
		+"&formControl.showDeptControlValue="+showDeptControlValue
		+"&formControl.saveDeptControlValue="+saveDeptControlValue;
	}
	$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:350,overlayClose:false});
}

function controlCalculateProperty(tableColumnId,controlType,name,title,controlId,computational,precision,maxlen,style,classStyle){
	var url = webRoot+"/form/form-view!text.htm?"
	+"formControl.controlType="+controlType
	+"&formControl.name="+name
	+"&formControl.title="+title
	+"&formControl.controlId="+controlId
	+"&formControl.computational="+computational
	+"&formControl.precision="+precision 
	+"&formControl.styleContent="+style 
	+"&formControl.classStyle="+classStyle  
	+"&occasion="+"update";
	if(maxlen==""){
		url=url;
	}else{
		url=url
		+"&formControl.maxLength="+maxlen;
	}
	if(tableColumnId==""){
		url=url
		+"&standard=false";
	}else{
		url=url
		+"&standard=true"
		+"&tableColumnId="+tableColumnId;
	}
	$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:350,overlayClose:false});
}

function controlPullDownMenuProperty(tableColumnId,controlType,name,title,controlId,childControlIds,initSelectValue,selectValues,dataType,style,classStyle){
	var url = webRoot+"/form/form-view!text.htm?"
	+"formControl.controlType="+controlType
	+"&formControl.name="+name
	+"&formControl.title="+title
	+"&formControl.controlId="+controlId
	+"&formControl.childControlIds="+childControlIds
	+"&formControl.initSelectValue="+initSelectValue 
	+"&formControl.selectValues="+selectValues
	+"&formControl.styleContent="+style 
	+"&formControl.classStyle="+classStyle  
	+"&formControl.dataType="+dataType
	+"&occasion="+"update";
	if(tableColumnId==""){
		url=url
		+"&standard=false";
	}else{
		url=url
		+"&standard=true"
		+"&tableColumnId="+tableColumnId;
	}
	$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:450,overlayClose:false});
}

function controlDataSelectionProperty(controlType,name,controlId,dataSrc,dataSrcName,data_fld_name,data_field,data_control,data_query){
	var url = webRoot+"/form/form-view!text.htm?"
	+"formControl.controlType="+controlType
	+"&formControl.name="+name
	+"&formControl.controlId="+controlId
	+"&formControl.dataSrc="+dataSrc
	+"&formControl.dataSrcName="+dataSrcName
	+"&formControl.dataFieldNames="+data_fld_name  
	+"&formControl.dataFields="+data_field
	+"&formControl.dataControlIds="+data_control
	+"&formControl.dataQuerys="+data_query;
	$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:450,overlayClose:false});
}

function controlDataAcquisitionProperty(controlType,name,controlId,dataSrc,dataSrcName,data_fld_name,data_field,data_control,referenceControl,queryProperty){
	var url = webRoot+"/form/form-view!text.htm?"
	+"formControl.controlType="+controlType
	+"&formControl.name="+name
	+"&formControl.controlId="+controlId
	+"&formControl.dataSrc="+dataSrc
	+"&formControl.dataSrcName="+dataSrcName
	+"&formControl.dataFieldNames="+data_fld_name  
	+"&formControl.dataFields="+data_field
	+"&formControl.dataControlIds="+data_control
	+"&formControl.referenceControl="+referenceControl
	+"&formControl.queryProperty="+queryProperty;
	$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:450,overlayClose:false});
}

function controlUrgencyProperty(controlType,values,describes){
	var url = webRoot+"/form/form-view!text.htm?"
	+"formControl.controlType="+controlType
	+"&formControl.urgencyValues="+values
	+"&formControl.urgencyDescribes="+describes;
	$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:450,overlayClose:false});
}

function controlListControlProperty(controlType,controlId,dataSrc,title,lcTitles,lcSums,lcSizes,lcCals,dataFields){
	var url = webRoot+"/form/form-view!text.htm?"
	+"formControl.controlType="+controlType
	+"&formControl.controlId="+controlId
	+"&formControl.dataSrc="+dataSrc
	+"&formControl.title="+title
	+"&formControl.lcTitles="+lcTitles
	+"&formControl.lcSums="+lcSums
	+"&formControl.lcSizes="+lcSizes
	+"&formControl.lcCals="+lcCals  
	+"&formControl.dataFields="+dataFields;
	$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:450,overlayClose:false});
}

function controlStandardListControlProperty(controlType,controlId,title,name,dataType,tableColumnId,listViewId){
	var url = webRoot+"/form/form-view!text.htm?"
	+"formControl.controlType="+controlType
	+"&formControl.controlId="+controlId
	+"&formControl.title="+title
	+"&formControl.name="+name
	+"&formControl.dataType="+dataType
	+"&standard=true"
	+"&tableColumnId="+tableColumnId
	+"&listViewId="+listViewId
	+"&formId="+$("#formId").val()
	+"&occasion="+"update";;
	$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:450,overlayClose:false});
}

function controlButtonProperty(controlType,controlId,classStyle,styleContent,name,event){
	var url = webRoot+"/form/form-view!text.htm?"
	+"formControl.controlType="+controlType
	+"&formControl.controlId="+controlId
	+"&formControl.name="+name
	+"&formControl.classStyle="+classStyle
	+"&formControl.styleContent="+styleContent
	+"&formControl.clickEvent="+event;
	$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:450,overlayClose:false});
}

function timeHtml(html){
	KE_InsertHtml(html);
	$.colorbox.close();
}
function html(html){
	KE_InsertHtml(html);
	$.colorbox.close();
}
function textAreaHtml(tableColumnId,controlId,name,title,controlValue,maxLen,dataType,classStyle,styleContent){
	var html ="";
	var style="";
	var myClassStyle="";
	if(classStyle!=""){
		myClassStyle=" class='"+classStyle+"'";
	}
	if(styleContent!=""){
		style=" style='"+styleContent+"'";
	}
	if(tableColumnId==""){
		html ="<textarea  pluginType='textarea' id='"+controlId+"'"+style+myClassStyle+"' maxLen='"+maxLen +"' dataType='"+dataType+"' name='"+name 
		+"' title='"+title+"'>"+controlValue+"</textarea>";
	}else{
		html ="<textarea  pluginType='textarea' id='"+controlId+"' maxLen='"+maxLen+"' dataType='"+dataType+"' name='"+name +"' columnId='"+tableColumnId
		+"' title='"+title+"'>"+controlValue+"</textarea>";
	}
	KE_InsertHtml(html);
	$.colorbox.close();
}
function KE_InsertHtml(html){
	var id="content";
	KE.util.insertHtml(id, html);
	KE.util.focus(id);
}

function deptHtml(name,controlType,showDeptControlValue,showDeptControlId,showDeptCotrolType,saveDeptControlValue,saveDeptControlId,deptTreeType,deptMultiple,classStyle,styleContent){
	var id="content";
	var html;
	var style="";
	var myClassStyle="";
	if(classStyle!=""){
		myClassStyle=" class='"+classStyle+"'";
	}
	if(styleContent!=""){
		style=" style='"+styleContent+"'";
	}
	var script="popTree({ title :'选择人员',"
		+"innerWidth:'500',"
		+"treeType:'"+deptTreeType+"',"
		+"defaultTreeValue:'id',"
		+"leafPage:'false',"
		+"multiple:'"+deptMultiple+"',"
		+"hiddenInputId:'"+saveDeptControlId+"',"
		+"showInputId:'"+showDeptControlId+"',"
		+"acsSystemUrl:webRoot,"
		+"callBack:function(){}});";
	//var script="$.colorbox({href:webRoot+'/create-tree.htm?resultId="
	//+showDeptControlId+"&inputType="+showDeptCotrolType+"&hiddenResultId="+saveDeptControlId+"&treeType="+deptTreeType
	//+"&multiple="+deptMultiple+"',iframe:true, innerWidth:800, innerHeight:550,overlayClose:false,title:'选择部门或人员'});";
	html = ' <input type="button" pluginType="'+controlType+'" treeType="'
		+deptTreeType+'" resultId="'+showDeptControlId+'" inputType="'+showDeptCotrolType+'" hiddenId="'
		+saveDeptControlId+'" value="'+name+'" selectType="'+deptMultiple+'" resultName="'
		+showDeptControlValue+'" hiddenResultName="'+saveDeptControlValue+'"'+style+myClassStyle+'onclick="javascript:'
		+script+'"/>';
	KE.util.insertHtml(id, html);
	KE.util.focus(id);
	$.colorbox.close();
}

function calculateHtml(tableColumnId,dataType,controlType,name,title,controlId,computational,precision,maxlen,classStyle,styleContent){
	var id="content";
	var html;
	var script="";
	var style="";
	var myClassStyle="";
	if(classStyle!=""){
		myClassStyle=" class='"+classStyle+"'";
	}
	if(styleContent!=""){
		style=" style='"+styleContent+"'";
	}
	var tempPrecision=0;
	if(precision==""){
		tempPrecision=4;
	}else{
		tempPrecision=precision;
	}
	script=' onclick="parseComputational(\''+computational+'\',\''+controlId+'\');"';
	html=' <input pluginType="'+controlType+'" type="'+controlType+'" dataType="'+dataType+'" prec="'
		+tempPrecision+'"'+style+myClassStyle+'" computational="'+computational
		+'" title="'+title+'" id="'+controlId+'" maxLen="'+maxlen+'" name="'+name+'" columnId="'+tableColumnId+'"'+script+'/> ';
	KE.util.insertHtml(id, html);
	KE.util.focus(id);
	$.colorbox.close();
}

function pullDownMenuHtml(tableColumnId,dataType,controlType,name,title,controlId,childControlIds,initSelectValue,select,classStyle,styleContent){
	var id="content";
	var html;
	var style="";
	var myClassStyle="";
	if(classStyle!=""){
		myClassStyle=" class='"+classStyle+"'";
	}
	if(styleContent!=""){
		style=" style='"+styleContent+"'";
	}
	var child="";
	var onchange="";
	var script="";
	if(typeof(childControlIds)=='undefined'){
		childControlIds="";
	}
		
	if(childControlIds!=""){
		child=' child="'+childControlIds+'"';
		onchange=' onchange="selectChange(this.value,\''+childControlIds+'\',\''+initSelectValue+'\');"';
		var arr=childControlIds.split(",");
		var scriptArr="";
		var arr=childControlIds.split(",");
		for(var i=0;i<arr.length;i++){
			scriptArr+='var arr_'+arr[i]+'=new Array();';
		}
		script=scriptArr+"initSelect('"+childControlIds+"','"+controlId+"');";
		script=" scriptContent=\""+script+"\"";
	}
	html =  '<select pluginType="'+controlType+'"'+style+myClassStyle+' name="'+name+'"'+child+' id="'+controlId+'" sInit="'+initSelectValue+'" dataType="'+dataType+'"'+script+onchange+' title="'+title+'" columnId="'+tableColumnId+'" ><option value="">请选择</option>';
	for(var i=0;i<select.length;i++){
		var val=select.options[i].value;
		if(val.indexOf("|")){
			val=val.substring(0,val.indexOf("|"));
		}
		if(select.options[i].value==initSelectValue){
			html+="<option myvalue='"+select.options[i].value+"' value='"+val+"' selected='selected'>"+select.options[i].text+"</option>";
		}else{
			html+="<option myvalue='"+select.options[i].value+"' value='"+val+"'>"+select.options[i].text+"</option>";
		}
	}
	html+="</select>";
	KE.util.insertHtml(id, html);
	KE.util.focus(id);
	$.colorbox.close();
}

function dataSelectionHtml(controlType,name,controlId,dataSrc,dataSrcName,data_fld_name,data_field,data_control,data_query){
	var id="content";
	var script="dataSelectionClick(webRoot+'"+"/show-data-selection.action?formControlId="+controlId+"&formId=${formId}"+"','"+dataSrcName+"','"+data_control+"');";
	var html = ' <input type="button" pluginType="'+controlType+'" data_table="'+dataSrc+'" data_table_name="'+dataSrcName+'" data_fld_name="'+data_fld_name+'" data_field="'+data_field+'" data_control="'+data_control+'" data_query="'+data_query+'" onclick="'+script+'" id="'+controlId+'" value="'+name+'"/>';
	KE.util.insertHtml(id, html);
	KE.util.focus(id);
	$.colorbox.close();
}

function dataAcquisitionHtml(controlType,name,controlId,dataSrc,dataSrcName,data_fld_name,data_field,data_control,referenceControl,queryProperty){
	var id="content";
	var script="ajaxGetData('"+referenceControl+"','${formId}','"+data_control+"','"+data_field+"','"+controlId+"')";
	var html = ' <input type="button" pluginType="'+controlType+'" data_table="'+dataSrc+'" data_table_name="'+dataSrcName+'" data_fld_name="'+data_fld_name+'" data_field="'+data_field+'" data_control="'+data_control+'" data_ref_control="'+referenceControl+'" onclick="'+script+'" id="'+controlId+'" value="'+name+'" query_property="'+queryProperty+'"/>';
	KE.util.insertHtml(id, html);
	KE.util.focus(id);
	$.colorbox.close();
}

function urgencyHtml(controlType,table){
	var id="content";
	var html =  '<select pluginType="'+controlType+'" name="priority" id="priority" dataType="INTEGER" title="紧急程度" >';
	
	$(table).find('tr td').each(function(i){
			if(i==1&&$(this).html()!='---请选择---')html+="<option value='6'>---请选择---</option>"	;
			if(i%2==1){
				html+="<option value='"+$(this).attr("value")+"'>"+$(this).html()+"</option>"	;
			}
		}
	);
	html+="</select>";
	KE.util.insertHtml(id, html);
	KE.util.focus(id);
	$.colorbox.close();
}

function specialTaskHtml(tableColumnId){
	var id="content";
	var html =  '';
	if(tableColumnId==""){
		html =  ' <input pluginType="CREATE_SPECIAL_TASK" title="是否进行特事特办" name="CREATE_SPECIAL_TASK"  dataType="BOOLEAN" type="checkbox" id="create_special_task" value="1"/> ';
	}else{
		html =  ' <input pluginType="CREATE_SPECIAL_TASK" title="是否进行特事特办" name="CREATE_SPECIAL_TASK"  dataType="BOOLEAN" type="checkbox" id="create_special_task" value="1"'+' columnId="'+tableColumnId+'"/> ';
	}
	KE.util.insertHtml(id, html);
	KE.util.focus(id);
	$.colorbox.close();
}
function specialTaskTransactorHtml(){
	var id="content";
	var html =  ' <input  pluginType="SPECIAL_TASK_TRANSACTOR"  title="特事特办人员选择" name="special_task_transactor"  dataType="TEXT" type="text" /> ';
	KE.util.insertHtml(id, html);
	KE.util.focus(id);
	$.colorbox.close();
}

function toPreview(){
	var url=webRoot+"/form/form-view!preview.htm?formId="+$("#formId").val();
	var preViewWindow = window.open(url,'preViewWindow',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width="+screen.availWidth+",height="+screen.availHeight);
}

function listControlHtml(controlId,dataSrc,txtTitle,lv_title,lv_size,lv_sum,lv_cal,lv_field){
	var id="content";
	var html = ' <input type="button" pluginType="LIST_CONTROL" data_source="'+dataSrc+'" lv_title="'+lv_title+'" lv_size="'+lv_size+'" lv_sum="'+lv_sum+'" lv_cal="'+lv_cal+'" lv_field="'+lv_field+'" id="'+controlId+'" value="'+txtTitle+'"/>';
	KE.util.insertHtml(id, html);
	KE.util.focus(id);
	$.colorbox.close();
}

function standardListControlHtml(controlId,name,title,dataType,listViewId,columnId){
	var id="content";
	var html = ' <input type="button" pluginType="STANDARD_LIST_CONTROL" id="'+controlId+'" columnId="'+columnId+'" name="'+name+'" title="'+title+'" dataType="'+dataType+'" value="'+title+'" listViewId="'+listViewId+'"/>';
	KE.util.insertHtml(id, html);
	KE.util.focus(id);
	$.colorbox.close();
}

