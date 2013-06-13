function saveGroupHeader(){
	var cansave=iMatrix.getFormGridDatas("columnSaveForm","groupHeaderGridId");
	if(cansave){
		ajaxSubmit("columnSaveForm",  webRoot+"/form/group-header-save.htm", "columnTable", saveGroupHeaderCallBack);
	}
}

function saveGroupHeaderCallBack(){
	setFormgridHeight('groupHeaderGridId',$(window).height()-140);
	showMsg();
}