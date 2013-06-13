function format(obj){
	var rootUrl=webRoot;if(typeof(appRoot)!='undefined'&&appRoot!=''){rootUrl=appRoot;}
	if(typeof(obj.currentInputId)=="undefined" || obj.currentInputId==''){
		alert("请录入currentInputId的值！");
		return;
	}
	$.colorbox({href:rootUrl+"/portal/format.action?currentInputId="+obj.currentInputId,
		iframe:true, 
		innerWidth:400, 
		innerHeight:248,
		overlayClose:false,
		title:"格式设置"
	});
}

//列表管理/字段信息/格式设置列编辑时的文本框的onclick事件
//obj:{rowid:id,currentInputId:id_formatSetting}
function formatClick(obj){
	format({
		currentInputId:obj.currentInputId
	});
}