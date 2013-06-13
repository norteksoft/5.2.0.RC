//新建Word
function openDocument(fileType,workflowId,taskId,id,viewFlag){
	if(typeof workflowId =="undefined" || workflowId==""){
		alert("请先保存表单再创建附件。");
		return false;
	}
	if(typeof id=='undefined') id='';
	var url = imatrixRoot+"/wf/engine/office!createOffice.htm?fileType="+fileType+"&workflowId="+workflowId+"&taskId="+taskId+"&id="+id;
	if(viewFlag=="true"){
		url+="&viewFlag=true";
	}
	window.open(url,'',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=no,width="+screen.availWidth+",height="+screen.availHeight);
}


//删除正文
function deleteText(documentId){
	if(confirm("确认删除吗？")){
		$("#officeForm1").attr("action",imatrixRoot+"/mms/common/delete-document.htm?documentId="+documentId);
		ajaxAnywhere.formName = "officeForm1";
		ajaxAnywhere.getZonesToReload = function(){
			return "history_refresh_zone";
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			$("#"+documentId).remove();
		};
		ajaxAnywhere.submitAJAX();
	}
}


//弹窗关闭时刷新主页面使用
function freshParentDocumentListwj(url){
	$("#officeForm").attr("action",url);
	ajaxAnywhere.formName = "officeForm";
	ajaxAnywhere.getZonesToReload = function(){
		return "default_text_zone";
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
	   // $("#___text_zone_content").css("display","block");
		//$("#textImg").parent().attr("state", "2");
		//$("#textImg").attr("src", webRoot + "/images/x2.png");
	};
	ajaxAnywhere.submitAJAX();
	}

//上传附近
function openUploadDocument(taskId,workflowId,id){
	$("#"+id).colorbox({href:webRoot+"/engine/office!upload.htm?taskId="+taskId+"&workflowId="+workflowId,iframe:true, innerWidth:400, innerHeight:100,overlayClose:false,title:"上传",onClosed:function(){$("#"+id).removeClass("cboxElement");}});
}


