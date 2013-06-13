var __signatureVisible=false;//表单标签的属性[是否显示签章]
var __signatureFields="";//签章字段,以逗号隔开.当表单标签的属性[是否显示签章]为true时,签章字段的必填验证不会起作用.
function addFormValidate(setting,formId){
	if(typeof(setting)=='undefined' || setting=="") return;
	
	if('[{request:"false",readonly:"true",controltype:"allreadolny"}]'==setting.toLowerCase()){
		allFieldForbidden(formId);
	}else{	
		var json = eval("(" + setting + ")");
		$.each(json, function(key,values){
			validateHandler(this,formId);			
		});
	}
}
function validateHandler(obj,formId){
	if(obj.controlType.toLowerCase()=="radio"  ){
		//单选多选按钮
		var name = obj.name;
		var inputs = $("input:radio[name="+name+"]");
		if(inputs.length==0)return;
		if(obj.readonly=="true"){
			$(inputs).attr('disabled', 'disabled');
		}else{//非禁止编辑
			if(obj.request=="true"){//必填
				var spans=$(inputs[inputs.length-1]).next("span");
				if(spans.length>0)return;
				$(inputs[inputs.length-1]).after('<span style="color:red;">*</span>');
				addCheckboxRadioRule($(inputs[0]));
			}
		}
	}else if(obj.controlType.toLowerCase()=="checkbox"){
		var name = obj.name;
		var inputs = $("input:checkbox[name="+name+"]");
		if(inputs.length==0)return;
		if(obj.readonly=="true"){//禁止编辑
			//禁止编辑字段替换为input,因为如果是disabled时，无法将值传到后台
			replaceCheckboxForbiddenField(inputs);
			$(inputs).attr("name","");
			$(inputs).attr('disabled', 'disabled');
		}else{//非禁止编辑
			if(obj.request=="true"){//必填
				var spans=$(inputs[inputs.length-1]).next("span");
				if(spans.length>0)return;
				$(inputs[inputs.length-1]).after('<span style="color:red;">*</span>');
				addCheckboxRadioRule($(inputs[0]));
			}
		}		
	}else{
		if(obj.id==""){ return ; }
		var inputobj = $("input[id="+obj.id+"]");
		if(obj.controlType.toLowerCase()=="textarea"){
			inputobj = $("textarea[id="+obj.id+"]");
		}else if(obj.controlType.toLowerCase()=="select"){
			inputobj = $("select[id="+obj.id+"]");
		}
		if(obj.datatype=="DATE" || obj.datatype=="TIME"){//日期时加事件
			 if(obj.datatype=="DATE"){
				 $(inputobj).datepicker({
					  "dateFormat":'yy-mm-dd',
					  changeMonth:true,
					  changeYear:true,
					  showButtonPanel:"true"
				   });
			 }else{
				  $(inputobj).datetimepicker({
					  "dateFormat":'yy-mm-dd',
					   changeMonth:true,
					   changeYear:true,
					   showSecond: false,
						showMillisec: false,
						"timeFormat": 'hh:mm'
				   });
			 }
		}else if(obj.datatype=="INTEGER"){
			addRule($(inputobj), 'digits', '请输入整数');
		}else if(obj.datatype=="LONG"){
			addRule($(inputobj), 'digits', '请输入整数');
		}else if(obj.datatype=="DOUBLE"){
			addRule($(inputobj), 'number', '请输入数字');
		}else{
			var formatType = obj.formatType;
			if(formatType=='string'||formatType=='enum'){
				addRule($(inputobj), obj.format, '请输入'+obj.formatTip);
			}
		}
		var maxLen = $(inputobj).attr("maxlength");
		//ie中没有maxlength属性时获得的值为2147483647,firefox中maxlength属性时获得的值为-1,谷歌浏览器是524228
		if(maxLen>0&&maxLen!=2147483647&&typeof(maxLen)!='undefined'&&maxLen!=524288){
			addRule($(inputobj), 'maxlength', '超过最大长度'+maxLen);
		}
		if(obj.request=="true"){//必填
			var name = obj.name;
			if(typeof name=='undefined'||name==null||name==""){
				name=obj.id;
			}
			var shouldAddAsterisk=false;//必填时是否应该增加星号，true表示加星号，false表示不加星号.考虑到日期类型时如果是禁止编辑的则不加必填验证，也不用加星号
			if(!isSignatureField(name)){//如果是签章字段,则不添加必填验证
				var formatType = obj.formatType;
				if(formatType=='null'){
					if(obj.datatype=="DATE" || obj.datatype=="TIME"){
						if(obj.readonly!=true&&obj.readonly!="true"){//日期时，如果是禁止编辑和必填同时存在时，以禁止编辑为准
							addRule($(inputobj), 'required', '必填');
							shouldAddAsterisk=true;
						}
					}else if(obj.datatype=="INTEGER"){
						addRule($(inputobj), 'required', '必填');
						shouldAddAsterisk=true;
					}else if(obj.datatype=="LONG"){
						addRule($(inputobj), 'required', '必填');
						shouldAddAsterisk=true;
					}else if(obj.datatype=="DOUBLE"){
						addRule($(inputobj), 'required', '必填');
						shouldAddAsterisk=true;
					}else{
						addRule($(inputobj), 'required', '必填');
						shouldAddAsterisk=true;
					}
				}else{
					addRule($(inputobj), 'required', '必填');
					shouldAddAsterisk=true;
				}
				if(shouldAddAsterisk){
					if($(inputobj).next("span").length<=0){//判断是否已经添加了必填验证样式
						$(inputobj).after('<span style="color:red;">*</span>');
					}	
				}
				
			}	
		}
		
		if(obj.readonly=="true"){//禁止编辑		
			if(obj.controlType.toLowerCase()!="radio"&&obj.controlType.toLowerCase()!="checkbox"){
				if(obj.controlType.toLowerCase()=="select"){
					//$(inputobj).attr('disabled', 'disabled');
					
					//禁止编辑字段替换为input,因为如果是disabled时，无法将值传到后台
					replaceForbiddenField(inputobj);
				}else if(obj.datatype=="DATE" || obj.datatype=="TIME"){
					$(inputobj).attr('onclick','');
					
					//禁止编辑字段替换为input,因为如果是disabled时，无法将值传到后台
					replaceForbiddenField(inputobj);
					//$(inputobj).attr('disabled', 'disabled');
				}else{
					$(inputobj).attr('readonly','readonly');
					//判断是否存在有此控件绑定的按钮，如果有，此控件为禁止编辑时，此按钮也隐藏
					var buttons = $("input[hiddenid="+$(inputobj).attr('id')+"]");
					if(buttons.length>=1){
						$("input[hiddenid="+$(inputobj).attr('id')+"]").css("display","none");
					}
				}
			}
		}
	}
}

//禁止编辑字段替换为input,因为如果是disabled时，无法将值传到后台
function replaceForbiddenField(inputobj){
   var name = $(inputobj).attr("name");
	if(typeof name=='undefined'||name==null||name==""){
		name=$(inputobj).attr("id");
	}
	$(inputobj).attr("name","");
	$(inputobj).attr('disabled', 'disabled');
	var disableFieldInput=$("input[name='"+name+"'][disableField='true']");
	if(disableFieldInput.length<=0){
		$(inputobj).after("<input name='"+name+"'  type='hidden' disableField='true' readonly='readonly' value='"+$(inputobj).val()+"'/>");
	}			
}

function replaceCheckboxForbiddenField(inputobjs){
	if(inputobjs.length>0){
		var name = $(inputobjs[0]).attr("name");
		if(name!=""&&typeof(name)!='undefined'&&name!=null ){
			var disableFieldInput=$("input[name='"+name+"'][disableField='true']");
			//如果不存在该复选或单选放值的文本框
			if(disableFieldInput.length<=0){
				var val = "";
				for(var i=0;i<inputobjs.length;i++){
					if($(inputobjs[i]).attr("checked")){
						if(val==""){
							val = $(inputobjs[i]).val();
						}else{
							val = val + "," + $(inputobjs[i]).val();
						}
					}
				}
				$(inputobjs[0]).after("<input name='"+name+"'  type='hidden' disableField='true' readonly='readonly' value='"+val+"'/>");
			}
		}
		
	}
}

//签章字段时，确定该字段是否添加必填验证
function isSignatureField(fieldName){
var isSignature=false;
	if(__signatureVisible){//显示签章
		var __signatureFieldArr=__signatureFields.split(",");
		for(var i=0;i<__signatureFieldArr.length;i++){
			if(__signatureFieldArr[i]==fieldName){//是签章字段
				isSignature = true;
			}
		}
	}
	return isSignature;
}

function allFieldForbidden(formId){
	//获得所有不是禁止编辑字段替换的input框
	var inputs = $("#"+formId+" input[name!='transitionName'][disableField!='true']");
	for(var j = 0; j < inputs.length; j++){
		if($(inputs[j]).attr("type").toLowerCase()=="radio"||$(inputs[j]).attr("type").toLowerCase()=="checkbox"){
			if($(inputs[j]).attr("type").toLowerCase()=="checkbox"){
				var name = $(inputs[j]).attr("name");
				var inputChecks  = $("input:checkbox[name='"+name+"']");
				//禁止编辑字段替换为input,因为如果是disabled时，无法将值传到后台
				replaceCheckboxForbiddenField(inputChecks);
			}	
			$(inputs[j]).attr('disabled', 'disabled');
						
		}else if($(inputs[j]).attr("datatype")=="DATE" || $(inputs[j]).attr("datatype")=="TIME"){
			//$(inputs[j]).attr('disabled', 'disabled');
			//禁止编辑字段替换为input,因为如果是disabled时，无法将值传到后台
			replaceForbiddenField(inputs[j]);
			
		}else{
			$(inputs[j]).attr( "onfocus","" );
			$(inputs[j]).attr('onclick','');
			$(inputs[j]).attr('readonly', 'readonly');
		}
		if($("#"+$(inputs[j]).attr("id")+"Div").length==1){
			$("#"+$(inputs[j]).attr("id")+"Div").hide();
		}
		
		//判断是否存在有此控件绑定的按钮，如果有，此控件为禁止编辑时，此按钮也隐藏
		var buttons = $("input[hiddenid="+$(inputs[j]).attr('id')+"]");
		if(buttons.length>=1){
			$("input[hiddenid="+$(inputs[j]).attr('id')+"]").css("display","none");
		}
	}
	//与replaceCheckboxForbiddenField配合使用
	inputs = $("#"+formId+" input[type='checkbox']");
	for(var i=0;i<inputs.length;i++){
		$(inputs[i]).attr("name","");
	}
			
	inputs = $("#"+formId+" textarea");
	for(var j = 0; j < inputs.length; j++){
		$(inputs[j]).attr('readonly', 'readonly');
	}
	inputs = $("#"+formId+" select");
	for(var j = 0; j < inputs.length; j++){
		//禁止编辑字段替换为input,因为如果是disabled时，无法将值传到后台
		replaceForbiddenField(inputs[j]);
		//$(inputs[j]).attr('disabled', 'disabled');
	}
	
}

function addCheckboxRadioRule(obj){
	$.validator.addMethod("checkboxRequired", function(value, element) {
		var name = $(element).attr("name");// 当控件类型是radio或checkbox时 id存的是控件的name属性
		var type=$(element).attr("type");		
		var inputs = $("input:"+type+"[name="+name+"]");
		var success=false;
		if($(element).attr("checked")){
			return true;
		}
		for(var i=0;i<inputs.length;i++){
			if($(inputs[i]).attr("checked")){
				success=true;
				return true;
			}
		}
		if(!success)return false;
	});	
	$(obj).attr('class', 'checkboxRequired');
	$(obj).attr('title', '必填');
}

function addRule(obj, type, msg){
	var c = $(obj).attr('class');
	if(typeof(c)=="undefined"){ c=""; }
	var vc = c.match(/\{[\W\w]+\}/ig);
	if(vc==null||vc==''){
		c = c+' {'+type+':true, messages:{'+type+':\''+msg+'\'}}';
	}else{
		var vo = eval('('+vc+')');
		vo[type]=true;
		if(typeof(vo['messages'])=='undefined'){
			vo['messages']={};
		}
		vo['messages'][type]=msg;
		c = c.replace(vc, json2str(vo));
	}
	$(obj).attr('class', c);
}
function json2str(o) {
	var arr = [];
	var fmt = function(s) {
		if (typeof s == 'object' && s != null) return json2str(s);
		return /^(string|number)$/.test(typeof s) ? "'" + s + "'" : s;
	};
	for (var i in o) arr.push("" + i + ":" + fmt(o[i]));
	return '{' + arr.join(',') + '}';
}


