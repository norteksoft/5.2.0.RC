//默认长度设置
function defautMaxlengthSet(){
	var dataType = $("#dataType").val();
	var maxlen=$("#maxLengthExist").val();
	if(typeof(maxlen)=="undefined"||maxlen==""||maxlen==0){
		//控制最大长度
		if(dataType=="TEXT"){
			$("#maxLength").attr("value",255);
		}else if(dataType=="DOUBLE"||dataType=="FLOAT"||dataType=="AMOUNT"){
			$("#maxLength").attr("value",25);
		}else if(dataType=="INTEGER"||dataType=="NUMBER"){
			$("#maxLength").attr("value",10);
		}else if(dataType=="LONG"){
			$("#maxLength").attr("value",19);
		}else if(dataType=="BOOLEAN"){
			$("#maxLength").attr("value",1);
		}else if(dataType=="BLOB"||dataType=="CLOB"||dataType=="COLLECTION"||dataType=="ENUM"||dataType=="REFERENCE"||dataType=="TIME"){
			$("#maxLength").attr("value","");
		}
	}
}


