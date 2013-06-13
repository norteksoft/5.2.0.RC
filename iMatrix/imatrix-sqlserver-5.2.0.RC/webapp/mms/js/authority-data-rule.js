function packagingOperator(dataType,conditionId,data){
	var event = '';
	if(data!=""){
		if(conditionId.indexOf("new")>=0){
			event = 'onclick="conditionValueEvent(\''+conditionId+'\')";';
		}else{
			event = 'onclick="conditionValueEvent('+conditionId+')";';
		}
	}
	var result='<option role="option" value="">请选择</option>';
	var tdContent='';
	if(dataType == 'TEXT'){
		result+='<option value="ET">等于</option><option value="CONTAIN">包含</option><option value="NOT_CONTAIN">不包含</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionValue" class="editable" onkeyup="validateFieldString(this);" type="text" name="conditionValue" style="width: 98%;" role="textbox">';
	}else if(dataType == 'ENUM'){
		result+='<option value="ET">等于</option><option value="NET">不等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input id="'+conditionId+'_conditionValue" class="editable"  type="text" name="conditionValue" style="width: 98%;" role="textbox">';
	}else if(dataType == 'BOOLEAN'){
		result+='<option value="ET">等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionValue" class="editable" onkeyup="validateFieldString(this);" type="text" name="conditionValue" style="width: 98%;" role="textbox">';
	}else if(dataType == 'NUMBER' || dataType == 'AMOUNT' || dataType == 'DOUBLE' || dataType == 'FLOAT'){
		result+='<option value="ET">等于</option><option value="GT">大于</option><option value="LT">小于</option><option value="GET">大于等于</option><option value="LET">小于等于</option><option value="NET">不等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input id="'+conditionId+'_conditionValue" class="editable" onkeyup="value=value.replace(/[^0-9\.]/g,\'\');" type="text" name="conditionValue" style="width: 98%;" role="textbox">';
	}else if(dataType == 'INTEGER' || dataType == 'LONG'){
		result+='<option value="ET">等于</option><option value="GT">大于</option><option value="LT">小于</option><option value="GET">大于等于</option><option value="LET">小于等于</option><option value="NET">不等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input id="'+conditionId+'_conditionValue" class="editable" onkeyup="value=value.replace(/[^0-9]/g,\'\');" type="text" name="conditionValue" style="width: 98%;" role="textbox">';
	}else if(dataType == 'DATE'){
		result+='<option value="ET">等于</option><option value="GT">大于</option><option value="LT">小于</option><option value="GET">大于等于</option><option value="LET">小于等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input id="'+conditionId+'_conditionValue" class="editable" readonly="readonly" type="text" name="conditionValue" style="width: 98%;" role="textbox">'
		+'<script type="text/javascript">'
        +'$("#'+conditionId+'_conditionValue").datepicker({'
        +'"dateFormat":"yy-mm-dd",'
		    +'  changeMonth:true,'
		     +' changeYear:true,'
			+'	showButtonPanel:"true"'
        +'});'                                       
     +'</script>';
	}else if(dataType == 'TIME'){
		result+='<option value="ET">等于</option><option value="GT">大于</option><option value="LT">小于</option><option value="GET">大于等于</option><option value="LET">小于等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input id="'+conditionId+'_conditionValue" readonly="readonly" class="editable" type="text" name="conditionValue" style="width: 98%;" role="textbox">'
		+'<script type="text/javascript">'
        +'$("#'+conditionId+'_conditionValue").datetimepicker({'
		    +'"dateFormat":"yy-mm-dd",'
			 +'      changeMonth:true,'
			  +'     changeYear:true,'
			  +'     showSecond: false,'
				+'	showMillisec: false,'
				+'	"timeFormat": "hh:mm"'
        +'});'
        +'</script>';
	}
	window.parent.$("#"+conditionId+"_operator").html(result);
	window.parent.$("#"+conditionId+"_conditionValue").parent().html(tdContent);
}

function packagingOperatorUpdate(dataType,conditionId,tableId){
	var event = '';
	if(conditionId.indexOf("new")>=0){
		event = 'onclick="conditionValueEvent(\''+conditionId+'\')";';
	}else{
		event = 'onclick="conditionValueEvent('+conditionId+')";';
	}
	var result='<option role="option" value="">请选择</option>';
	var tdContent='';
	if(dataType == 'TEXT'){
		result+='<option value="ET">等于</option><option value="CONTAIN">包含</option><option value="NOT_CONTAIN">不包含</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input '+event+'  id="'+conditionId+'_conditionValue" class="editable" onkeyup="validateFieldString(this);" type="text" name="conditionValue" style="width: 98%;" role="textbox">';
	}else if(dataType == 'ENUM'){
		result+='<option value="ET">等于</option><option value="NET">不等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input id="'+conditionId+'_conditionValue" class="editable"  type="text" name="conditionValue" style="width: 98%;" role="textbox">';
	}else if(dataType == 'BOOLEAN'){
		result+='<option value="ET">等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input '+event+' id="'+conditionId+'_conditionValue" class="editable" onkeyup="validateFieldString(this);" type="text" name="conditionValue" style="width: 98%;" role="textbox">';
	}else if(dataType == 'NUMBER' || dataType == 'AMOUNT' || dataType == 'DOUBLE' || dataType == 'FLOAT'){
		result+='<option value="ET">等于</option><option value="GT">大于</option><option value="LT">小于</option><option value="GET">大于等于</option><option value="LET">小于等于</option><option value="NET">不等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input id="'+conditionId+'_conditionValue" class="editable" onkeyup="value=value.replace(/[^0-9\.]/g,\'\');" type="text" name="conditionValue" style="width: 98%;" role="textbox">';
	}else if(dataType == 'INTEGER' || dataType == 'LONG'){
		result+='<option value="ET">等于</option><option value="GT">大于</option><option value="LT">小于</option><option value="GET">大于等于</option><option value="LET">小于等于</option><option value="NET">不等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input id="'+conditionId+'_conditionValue" class="editable" onkeyup="value=value.replace(/[^0-9]/g,\'\');" type="text" name="conditionValue" style="width: 98%;" role="textbox">';
	}else if(dataType == 'DATE'){
		result+='<option value="ET">等于</option><option value="GT">大于</option><option value="LT">小于</option><option value="GET">大于等于</option><option value="LET">小于等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input id="'+conditionId+'_conditionValue" class="editable" readonly="readonly" type="text" name="conditionValue" style="width: 98%;" role="textbox">'
		+'<script type="text/javascript">'
        +'$("#'+conditionId+'_conditionValue").datepicker({'
		    +'  changeMonth:true,'
		     +' changeYear:true,'
			+'	showButtonPanel:"true"'
        +'});'                                       
     +'</script>';
	}else if(dataType == 'TIME'){
		result+='<option value="ET">等于</option><option value="GT">大于</option><option value="LT">小于</option><option value="GET">大于等于</option><option value="LET">小于等于</option><option value="IS_NULL">为空</option><option value="NOT_NULL">不为空</option>';
		tdContent='<input id="'+conditionId+'_conditionValue" readonly="readonly" class="editable" type="text" name="conditionValue" style="width: 98%;" role="textbox">'
		+'<script type="text/javascript">'
        +'$("#'+conditionId+'_conditionValue").datetimepicker({'
		    +'"dateFormat":"yy-mm-dd",'
			 +'      changeMonth:true,'
			  +'     changeYear:true,'
			  +'     showSecond: false,'
				+'	showMillisec: false,'
				+'	"timeFormat": "hh:mm"'
        +'});'
        +'</script>';
	}
	var operator=$("#"+conditionId+"_operator").val();
	var conditionValue=$("#"+conditionId+"_conditionValue").val();
	$("#"+conditionId+"_operator").html(result);
	if(operator != '')
		$("#"+conditionId+"_operator").attr("value",operator);
	$("#"+conditionId+"_conditionValue").parent().html(tdContent);
	if(conditionValue != '')
		$("#"+conditionId+"_conditionValue").attr("value",conditionValue);
}


function setOperatorValue(conditionId,value){
	var event = '';
	if(conditionId.indexOf("new")>=0){
		event = 'onclick="conditionValueEvent(\''+conditionId+'\')";';
	}else{
		event = 'onclick="conditionValueEvent('+conditionId+')";';
	}
	var tdContent='<input '+event+' id="'+conditionId+'_conditionValue" value="'+value+'"  class="editable" onkeyup="validateFieldString(this);" type="text" name="conditionValue" style="width: 98%;" role="textbox">';
	window.parent.$("#"+conditionId+"_conditionValue").parent().html(tdContent);
}