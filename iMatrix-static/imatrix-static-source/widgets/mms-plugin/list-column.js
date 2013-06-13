function saveColumn(){
	var cansave=getFormGridDatas("columnSaveForm","listColumnId");
	if(cansave){
		ajaxSubmit("columnSaveForm",  webRoot+"/form/list-column!saveColumns.htm", "columnTable", saveColumnCallBack);
	}
}

function saveColumnCallBack(){
	showMsg();
}

function successSave(){
	ajaxSubmit("columnSaveForm",  webRoot+"/form/list-column!saveColumns.htm", "columnTable", saveColumnCallBack);
}

function changeOrderType(obj){
	var arr=$("select[name='orderType']");
	if($(obj).val()!="NO"){
		for(var i=0;i<arr.length;i++){
			$(arr[i]).attr("disabled","disabled");
		}
		$(obj).removeAttr("disabled");
	}else{
		for(var i=0;i<arr.length;i++){
			$(arr[i]).removeAttr("disabled");
		}
	}
}

function initOrderType(){
	var arr=$("select[name='orderType']");
	var isHasOrder=false;
	for(var i=0;i<arr.length;i++){
		if($(arr[i]).val()!="NO"){
			isHasOrder=true;
			break;
		}
	}
	if(isHasOrder){
		for(var i=0;i<arr.length;i++){
			if($(arr[i]).val()=="NO"){
				$(arr[i]).attr("disabled","disabled");
			}
		}
	}
}

function changeColumn(obj){
	var id=$(obj).attr("id");
	var num=id.substring(12,id.length);
	$("#headname"+num).attr("value",$(obj).find("option:selected").text());
}
