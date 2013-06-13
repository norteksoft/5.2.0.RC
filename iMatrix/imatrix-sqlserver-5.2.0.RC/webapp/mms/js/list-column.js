function saveColumn(){
	var cansave=iMatrix.getFormGridDatas("columnSaveForm","listColumnId");
	if(cansave){
		ajaxSubmit("columnSaveForm",  webRoot+"/form/list-column!saveColumns.htm", "columnTable", saveColumnCallBack);
	}
}

function saveColumnCallBack(){
	setFormgridHeight('listColumnId',$(window).height()-140);
	showMsg();
}

function successSave(){
	ajaxSubmit("columnSaveForm",  webRoot+"/form/list-column!saveColumns.htm", "columnTable", saveColumnCallBack);
}

