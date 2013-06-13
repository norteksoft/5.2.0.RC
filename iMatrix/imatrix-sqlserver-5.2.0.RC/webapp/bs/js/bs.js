function ajaxSubmit(form, url, zoons, ajaxCallback,arg){
		if(typeof(ajaxCallback) == "function"){
			if(arg!=""){
				ajaxAnyWhereSubmit(form, url, zoons, ajaxCallback,arg);
			}else if(typeof(arg)!="undefined"){
				ajaxAnyWhereSubmit(form, url, zoons, ajaxCallback,arg);
			}else{
				ajaxAnyWhereSubmit(form, url, zoons, ajaxCallback);
			}
		}else{
			ajaxAnyWhereSubmit(form, url, zoons);
		}
}

function resetJmessaPageNo(arg){
	if(arg=="" || typeof(arg)=="undefined")	arg="page";	
	changePageNoState(arg);
}

//返回调用,保持页数。
function goBack(form,url,zone,jemesaId){
	getPageStateAttr(jemesaId, form);
	ajaxSubmit(form, url, zone,automaticHeight);
}