//计算精度:num表示要四舍五入的数,v表示要保留的小数位数。
function decimal(num,v)
{
    var vv = Math.pow(10,v);
    return Math.round(num*vv)/vv;
}

//最大值
function MAX(){
	if(arguments.length==0)
	  return;
	var max_num=arguments[0];
	for(var i=0;i<arguments.length;i++)
	  max_num=Math.max(max_num,arguments[i]);
	return parseFloat(max_num);
}

//最小值
function MIN()
{
	if(arguments.length==0)
	  return;
	var min_num=arguments[0];
	for(var i=0;i<arguments.length;i++)
	  min_num=Math.min(min_num,arguments[i]);
	return parseFloat(min_num);
}

//绝对值
function ABS(val)
{
	if(val=="")val=0;
	return Math.abs(parseFloat(val));
}

//平均值
function AVG()
{
	if(arguments.length==0)
	  return;
	var sum=0;
	for(var i=0;i<arguments.length;i++)
	  sum+=parseFloat(arguments[i]);
	return parseFloat(sum/arguments.length);
}

//computational:计算公式
function init_parseComputational(){
	var inputCals=$("input[pluginType=CALCULATE_COMPONENT]");   
	var inputs=$("input[pluginType=TEXT]");
	for(var j=0;j<inputCals.length;j++){
		$(inputCals[j]).attr("value","");
//		var cal_id = $(inputCals[j]).attr("id");
//		var computational	=	$(inputCals[j]).attr("computational");
//		var arr=new Array();
//		for(var i=0;i<inputs.length;i++){
//			var re  = new RegExp("([\(\+\*\/\^,-]*)"+$(inputs[i]).attr("id")+"([\)\+\*\/\^,-]*)","g");
//			var initCom=computational;
//			computational = computational.replace(re,"$1"+"$('#"+$(inputs[i]).attr("id")+"').attr('value')"+"$2");
//			if(computational!=initCom){
//				arr.push($(inputs[i]).attr("id"));
//			}
//		}
//		$(inputCals[j]).attr("value",decimal(eval(computational),parseInt($(inputCals[j]).attr("prec"))));
//		if(isNaN($(inputCals[j]).attr("value")))$(inputCals[j]).attr("value","");
	}
}

function parseComputational(computational,id){
	var inputs=$("input[pluginType=TEXT]");
	for(var i=0;i<inputs.length;i++){
		var re  = new RegExp("([\(\+\*\/\^,-]*)"+$(inputs[i]).attr("id")+"([\)\+\*\/\^,-]*)","g");
		var initCom=computational;
		computational = computational.replace(re,"$1"+"parseFloat($('#"+$(inputs[i]).attr("id")+"').attr('value'))"+"$2");
	}
	$("#"+id).attr("value",decimal(eval(computational),parseInt($("#"+id).attr("prec"))));
	if(isNaN($("#"+id).attr("value")))$("#"+id).attr("value","");
}
//解析表达式
function parse(){
	//init_parseComputational();
}



function selectChange(parentValue,child,cur_val)
{
  var childArray=child.split(",");
  for(var i=0;i<childArray.length;i++)
  {
  	if(childArray[i]!="")
  	{
  		var arr=eval("arr_"+childArray[i]);
  		var optionStr=arr[childArray[i]][parentValue];
  		if(optionStr)
  		{
  			var optionArr=optionStr.split(",");
  			$('#'+childArray[i]) .find('option') .remove(); 
    		for(var j=0;j<optionArr.length;j++)
    		{
    			if(optionArr[j]!="")  
    			{
    				//添加option
  					var objOption = document.createElement("OPTION");
  					var valArr=optionArr[j].split(":");
  					var val="";
  					if(valArr.length==1){
  						objOption.text = valArr[0];
  	  					objOption.value = valArr[0];
  	  					val=valArr[0];
  					}else if(valArr.length>1){
  						objOption.text = valArr[1];
  	  					objOption.value = valArr[0];
  	  					val=valArr[0];
  					}
  					document.getElementById( childArray[i] ).options.add(objOption);
  					if(typeof cur_val!='undefined' && cur_val==val) $('#'+childArray[i]).attr("value",cur_val);
  				}
  			}
  		}
	}
  }
}

//初始化下拉菜单数组
function initSelect(selstr,parentObj)
{
	var selArray=selstr.split(",");
	for(var i=0;i<selArray.length;i++)
	{
		if(selArray[i]!="")
		{
		   var arr=eval("arr_"+selArray[i]);
	   	   arr[selArray[i]]=new Array();
	   	   var cur_val;
	       var temp = "#"+selArray[i];
	   	   var options = $(temp).children();
	       for(var j=0;j<options.length;j++)
	       {
	         var str=$(options[j]).attr("myvalue");
	         var strtext=$(options[j]).attr("text");
	         if(typeof str!='undefined' && str.indexOf("|")>=0)
	         {
	         	  //更新value和text
	        	 $(options[j]).attr("value",str.substring(0,str.indexOf("|")));
	        	// $(options[j]).attr("text",str.substring(0,str.indexOf("|")));
	         	  
	          	var father=str.substring(str.indexOf("|")+1,str.length);
	          	var optionValue=str.substring(0,str.indexOf("|"));
	          	//记录当前选中值
	   	        if($('#'+selArray[i]).get(0).selectedIndex==j)cur_val=optionValue;
	          	if(typeof arr[selArray[i]][father]=='undefined')
	          		arr[selArray[i]][father]="";
	        		arr[selArray[i]][father]+=optionValue+":"+strtext+",";
	       	 }else{
	       		$(options[j]).attr("value",str);
	       	 }
	       }
	       //重建子菜单 
	      selectChange($("#"+parentObj).attr("value"),selArray[i],cur_val);   
     }
   }
}

//数据获取/点击操作b,id,c,,age,a
function ajaxGetData(referenceControlId, formId,dataControl,dataField,controlId){
	if(controlsExistFun(referenceControlId) && controlsExistFun(dataControl)){
		$.ajax({
			   type: "POST",
			   url: webRoot+"/get-data.htm",
			   data:'referenceControlValue='+$("#"+referenceControlId).attr("value")+'&formId='+formId+'&formControlId='+controlId,
			   success: function(text, textStatus){
				   //text为json格式的数据
				if(text!=""){
					var json = eval("(" + text + ")");
					$.each(json, function(key,value){
						var arr=dataField.split(",");
						var dcArr=dataControl.split(",");
						for(var i=0;i<arr.length;i++){
							if(arr[i]!=""){
								if("dt_"+arr[i]==key.toLowerCase()){
									if(value==null){
										value="";
									}
									$("#"+dcArr[i]).attr("value",value);
								}else if(arr[i]==key){
									$("#"+dcArr[i]).attr("value",value);
								}
							}
						}
					});
				}else{
					var arr=dataField.split(",");
					var dcArr=dataControl.split(",");
					for(var i=0;i<arr.length;i++){
						if(arr[i]!=""){
							$("#"+dcArr[i]).attr("value","");
						}
					}
				}
		      },
				error : function(XMLHttpRequest, textStatus) {
					alert(textStatus);
				}
		  }); 
	}
}

//部门人员组件
//liudongxia		
function addUsers(treeType,multiple,value,resultId,hiddenResultId,inputType){
	if(resultId!=""){
		inputType=="textArea"?$("#"+resultId).html(""):$("#"+resultId).attr("value","");
	}
	var type="";
	if(treeType=="COMPANY" || treeType.substring(0,treeType.indexOf("_"))=="MAN"){
		type="user";
	}else if(treeType.substring(0,treeType.indexOf("_"))=="DEPARTMENT"){
		type="department";
	}else if(treeType.substring(0,treeType.indexOf("_"))=="GROUP"){
		type="workGroup";
	}
	var arr=eval(value);
	if(multiple=="true"){
		for(var i=0;i<arr.length;i++){
			if(type=="user" && (arr[i].type=="user" || arr[i].type=="allDepartment" || arr[i].type=="company")){
				if(arr[i].type=="user"){
					if(resultId!=""){
						inputType=="textArea"?$("#"+resultId).html($("#"+resultId).html()+arr[i].name+","):$("#"+resultId).attr("value",$("#"+resultId).attr("value")+arr[i].name+",");
					}
					$("#"+hiddenResultId).attr("value",$("#"+hiddenResultId).attr("value")+arr[i].loginName+",");
				}else if(arr[i].type=="allDepartment" || arr[i].type=="company"){
					if(resultId!=""){
						inputType=="textArea"?$("#"+resultId).html("所有人员,"):$("#"+resultId).attr("value","所有人员,");
					}
					$("#"+hiddenResultId).attr("value","all_user,");
					break;
				}
			}else if(type=="department" && (arr[i].type=="department" || arr[i].type=="allDepartment" || arr[i].type=="company")){
				if(arr[i].type=="department"){
					if(resultId!=""){
						inputType=="textArea"?$("#"+resultId).html($("#"+resultId).html()+arr[i].name+","):$("#"+resultId).attr("value",$("#"+resultId).attr("value")+arr[i].name+",");
					}
					$("#"+hiddenResultId).attr("value",$("#"+hiddenResultId).attr("value")+arr[i].id+",");
				}else if(arr[i].type=="allDepartment" || arr[i].type=="company"){
					inputType=="textArea"?$("#"+resultId).html("所有部门,"):$("#"+resultId).attr("value","所有部门,");
					$("#"+hiddenResultId).attr("value","all_department,");
					break;
				}
			}else if(type=="workGroup" && (arr[i].type=="workGroup" || arr[i].type=="allWorkGroup" || arr[i].type=="company")){
				if(arr[i].type=="workGroup"){
					if(resultId!=""){
						inputType=="textArea"?$("#"+resultId).html($("#"+resultId).html()+arr[i].name+","):$("#"+resultId).attr("value",$("#"+resultId).attr("value")+arr[i].name+",");
					}
					$("#"+hiddenResultId).attr("value",$("#"+hiddenResultId).attr("value")+arr[i].id+",");
				}else if(arr[i].type=="allWorkGroup" || arr[i].type=="company"){
					if(resultId!=""){
						inputType=="textArea"?$("#"+resultId).html("所有工作组,"):$("#"+resultId).attr("value","所有工作组,");
					}
					$("#"+hiddenResultId).attr("value","all_workGroup,");
					break;
				}
			}
		}
		if(inputType=="textArea"){
			if(resultId!="" && $("#"+resultId).html()!=""){
				$("#"+resultId).html($("#"+resultId).html().substring(0,$("#"+resultId).html().length-1));
			}
		}else{
			if(resultId!="" && $("#"+resultId).attr("value")!="" && $("#"+resultId).attr("value")!=null ){
				$("#"+resultId).attr("value",$("#"+resultId).attr("value").substring(0,$("#"+resultId).attr("value").length-1));
			}
		}
		if($("#"+hiddenResultId).attr("value")!="" && $("#"+hiddenResultId).attr("value")!=null){
			$("#"+hiddenResultId).attr("value",$("#"+hiddenResultId).attr("value").substring(0,$("#"+hiddenResultId).attr("value").length-1));
		}
	}else if(multiple=="false"){
		if(resultId!=""){
			if(inputType=="textArea"){
				$("#"+resultId).html(arr[0].name);
			}else{
				$("#"+resultId).attr("value",arr[0].name);
			}
		}
		if(type=="user" && arr[0].type=="user"){
			$("#"+hiddenResultId).attr("value",arr[0].loginName);
		}else if(type=="department" && arr[0].type=="department"){
			$("#"+hiddenResultId).attr("value",arr[0].id);
		}else if(type=="workGroup" && arr[0].type=="workGroup"){
			$("#"+hiddenResultId).attr("value",arr[0].id);
		}
	}
}

//隐藏所有的列表控件和查看带有列表控件的表单时的初始化
function initListControl(controlId){
	var listControls=$("input[pluginType=LIST_CONTROL]");
	for(var i=0;i<listControls.length;i++){
		if($(listControls[i]).attr("data_source")==controlId){
			$(listControls[i]).css("display","none");
			var tb_id="tb_"+$(listControls[i]).attr("id");
			var lv_sum=$(listControls[i]).attr("lv_sum");
			var lv_cal=$(listControls[i]).attr("lv_cal");
			var trs=$("#"+tb_id+" tbody tr");
			for(var j=0;j<trs.length;j++){
				addCalEvent(tb_id,lv_sum,j,lv_cal);
			}
		}
	}
}

//列表控件的初始化
//fieldRight:用户编辑字段的权限
function parseListControl(fieldRight){
	var listControls=$("input[pluginType=LIST_CONTROL]");
	for(var i=0;i<listControls.length;i++){
		$(listControls[i]).css("display","none");
		var childForm="<input type=hidden name='dataSrc_"+$(listControls[i]).attr("id")+"' id='dataSrc_"+$(listControls[i]).attr("id")+"' value='"+$(listControls[i]).attr("data_source")+"'>";
		$(listControls[i]).after(childForm);
		var lv_title=$(listControls[i]).attr("lv_title");var lv_size=$(listControls[i]).attr("lv_size");
		var lv_sum=$(listControls[i]).attr("lv_sum");var lv_cal=$(listControls[i]).attr("lv_cal");
		var lv_field=$(listControls[i]).attr("lv_field");
		var id=$(listControls[i]).attr("id");
		var table="";
		table="<table class='Table changeTR' id='tb_"+id+"'><thead><tr>";
		var lv_titles=lv_title.split(",");
		var lv_sizes=lv_size.split(",");
//		var lv_cals=lv_cal.split(",");
		for(var j=0;j<lv_titles.length;j++){
			if(lv_titles[j]!=""){
				table+="<th style='width:"+lv_sizes[j]+"px;'>";
				table+=lv_titles[j]+"</th>";
			}
		}
		table+="<th style='width:100px;'>操作</th></tr></thead><tbody></tbody></table>";
		if(fieldRight=="true"){
			table+='<input type=button value="新增"  onclick="listControlAddRow(\'tb_'+id+'\',\''+lv_sum+'\',\''+lv_field+'\',\''+lv_cal+'\',\''+fieldRight+'\');">';
		}
		$('#dataSrc_'+$(listControls[i]).attr("id")).after(table);
	}
}

//列表控件的"新增"
function listControlAddRow(tb_id,lv_sum,lv_field,lv_cal,fieldRight){
	var lv_fields=lv_field.split(",");
	$('"'+'tr[id^=\'listControl_tr_\']'+'"');
	var tr_num=$('"'+'tr[id^=\'listControl_tr_\']'+'"').length>0?$('"'+'tr[id^=\'listControl_tr_\']'+'"').length:0;
	var lv_cals=lv_cal.split(",");
	var tr = "<tr id='listControl_tr_"+tb_id+"_"+tr_num+"'>";
	for(var i=0;i<lv_fields.length;i++){
		//lv_fields[i]:enName:dataType
		if(lv_fields[i]!=""){
			var id=tb_id.substring(tb_id.indexOf("_")+1,tb_id.length);
			tr+="<td>";
			var dataType=lv_fields[i].substring(lv_fields[i].indexOf(":")+1,lv_fields[i].length);
//			onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',el:aa})" //日期控件
			var currentId="listControl_"+tb_id+"_"+tr_num+"_"+(i+1);
			if(dataType=="DATE" || dataType=="TIME"){
				if(lv_cals[i]=='0'){
					if(dataType=="DATE"){
						tr+="<input style='width:95%;' name='listControl_"+id+"_"+lv_fields[i]+"' onfocus='WdatePicker({dateFmt:\"yyyy-MM-dd\",el:"+currentId+"})' id='"+currentId+"' readonly";
					}else{
						tr+="<input style='width:95%;' name='listControl_"+id+"_"+lv_fields[i]+"' onfocus='WdatePicker({dateFmt:\"yyyy-MM-dd HH:mm\",el:"+currentId+"})' id='"+currentId+"' readonly";
					}
				}else{
					tr+="<input style='width:95%;' name='listControl_"+id+"_"+lv_fields[i]+"' id='listControl_"+tb_id+"_"+tr_num+"_"+(i+1)+"' readonly";
				}
			}else{
				tr+="<input style='width:95%;' name='listControl_"+id+"_"+lv_fields[i]+"' id='listControl_"+tb_id+"_"+tr_num+"_"+(i+1)+"'";
				if(lv_cals[i]!='0'){
					tr+=" readonly";
				}
			}
			tr+="></input></td>";
		}
	}
	tr+="<td>";
	if(fieldRight=="true"){
		tr+="<a href=\"#\" onclick=\"listControlDelRow(this,'"+tb_id+"','"+lv_sum+"')\">删除</a>" ;
	}
	tr+="</td></tr>";
	$("#"+tb_id).append(tr);
	var tr_arr=$("tr[id=listControlSum_"+tb_id+"]");
	if(tr_arr.length==0 ||(tr_arr.length>0 && $(tr_arr[0]).attr("id")!=("listControlSum_"+tb_id))){
		if(isNeedSum(lv_sum)){
			listControlAddSumRow(tb_id,lv_sum);
		}
	}
	if(tr_arr.length>0 && $(tr_arr[0]).attr("id")==("listControlSum_"+tb_id)){
		var tr_all_arr=$("#"+tb_id+" tbody tr");
		$(tr_arr[0]).insertAfter($(tr_all_arr[tr_all_arr.length-1]));
	}
	if($("#"+tb_id+" tbody tr").length>0){
		addCalEvent(tb_id,lv_sum,tr_num,lv_cal);
	}
}

//添加计算事件
function addCalEvent(tb_id,lv_sum,tr_num,lv_cal){
	var lv_cals=lv_cal.split(",");
	var trs=$("#"+tb_id+" tbody tr");
	var arr;
	if(isNeedSum(lv_sum)){
		arr=$(trs[trs.length-2]).find("input");
	}else{
		arr=$(trs[trs.length-1]).find("input");
	}
	for(var i=0;i<arr.length;i++){
		if($(arr[i]).attr("readonly")){
			var result=getLCComponentInputs(tb_id,lv_cals[i],$(arr[i]).attr("id"),tr_num);
			parseLCComputational(result[0],result[1],$(arr[i]).attr("id"),lv_sum,"#listControlSum_"+tb_id+"_td_","listControl_"+tb_id+"_",tb_id);
		}
	}
}

//是否需要合计
function isNeedSum(lv_sum){
	var lv_sums=lv_sum.split(",");
	for(var i=0;i<lv_sums.length;i++){
		if(lv_sums[i]!=""){
			if(lv_sums[i]=='1'){
				return true;
			}
		}
	}
	return false;
}

function getLCComponentInputs(tb_id,computational,id,tr_num){
	var inputs=$('"'+'input[id^=\'listControl_'+tb_id+"_"+tr_num+'\']'+'"');
	var initCom=computational;
	var result=new Array();
	var componentInputs=new Array();
	for(var i=1;i<50;i++){
		if(i>inputs.length){
			break;
		}else{
			if(!$(inputs[i-1]).attr("readonly")){
				var re  = new RegExp("([\(\+\*\/\^,-]*)[\[]+"+i+"[\]]+([\)\+\*\/\^,-]*)","g");
				var inputId=$(inputs[i-1]).attr("id");
				computational = computational.replace(re,"$1"+"(isNaN(parseInt($('#"+inputId+"').attr('value')))?0:parseInt($('#"+inputId+"').attr('value')))"+"$2");
				if(computational!=initCom){
					componentInputs.push($(inputs[i-1]));
				}
			}
		}
	}
	result.push(componentInputs);
	result.push(computational);
	return result;
}

//列表控件/解析表达式
function parseLCComputational(componentInputs,computational,id,lv_sum,preSumId,preInputCeil,tb_id){
	for(var i=0;i<componentInputs.length;i++){
		$(componentInputs[i]).change(function(){
			$("#"+id).attr("value",eval(computational));
			
			var lv_sums=lv_sum.split(",");
			for(var j=0;j<lv_sums.length;j++){
				if(lv_sums[j]!=""||lv_sums[j]!='0'){
					if(lv_sums[j]=='1'){
						var sum=0;
						var trs=$("#"+tb_id+" tbody tr");
						for(var n=0;n<trs.length-1;n++){
							var inputCeil=isNaN(parseInt($("#"+preInputCeil+n+"_"+(j+1)).attr("value")))?0:parseInt($("#"+preInputCeil+n+"_"+(j+1)).attr("value"));
							$(this).attr("value");
							sum+=inputCeil;
						}
						$(preSumId+(j+1)).attr("value",sum);
					}
				}
			}
		});
	}
}

//列表控件增加"合计"行
function listControlAddSumRow(tb_id,lv_sum){
	var lv_sums=lv_sum.split(",");
	var tr = "<tr id='listControlSum_"+tb_id+"'>";
	for(var i=0;i<lv_sums.length;i++){
		if(lv_sums[i]!=""){
			tr+="<td><input style='width:95%;' readonly " ;
			var id=tb_id.substring(tb_id.indexOf("_")+1,tb_id.length);
			if(lv_sums[i]=='1'){
				tr+="id='listControlSum_"+tb_id+"_td_"+(i+1)+"' name='listControlSum_"+id+"_"+(i+1)+"' value='0'";
			}
			tr+="></input></td>";
		}
	}
	tr+="<td></td></tr>";
	$("#"+tb_id).append(tr);
}

//列表控件的"删除"一行
function listControlDelRow(obj,tb_id,lv_sum)
{
	var tr_all_arr=$("#"+tb_id+" tbody tr");
	if(isNeedSum(lv_sum) && tr_all_arr.length<=2){
		$($("tr[id=listControlSum_"+tb_id+"]")[0]).remove();
	}
	$(obj).parent().parent().remove();  
}

function controlsExistFun(dataControls){
	var arr=dataControls.split(",");
	var isControlsExist=true;
	for(var i=0;i<arr.length;i++){
		if(arr[i]!=""){
			if($("#"+arr[i]).size()<=0 || $("input[id='"+arr[i]+"']").length<=0){
				alert("id为"+arr[i]+"的单行文本控件存在,请在表单编辑界面增加该控件.");
				isControlsExist=false;
				return;
			}
		}
	}
	return isControlsExist;
}

function dataSelectionClick(url,title,dataControls){
	if(controlsExistFun(dataControls)){
		$.colorbox({href:url,iframe:true, width:500, height:400,overlayClose:false,title:title});
//		init_tb(url,title);
	}
}


