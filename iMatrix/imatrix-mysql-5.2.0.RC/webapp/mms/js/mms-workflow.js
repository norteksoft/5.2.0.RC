/**************自定义工作流**********/
function _view_opinion(obj,taskId){
	if(taskId==""){
		alert("请先保存");
	}else{
		if($(obj).attr("state") == "1"){
			$(obj).attr("state", "2");
			$("#___opinion_zone_content").css("display","block");
			$("#viewImg").attr("src", webRoot + "/images/x2.png");
			ajaxAnyWhereSubmit("opinion_form", webRoot+"/common/opinion.htm", "default_opinion_zone");
		}else{
			$(obj).attr("state", "1");
			$("#viewImg").attr("src", webRoot + "/images/x1.png");
			$("#___opinion_zone_content").html("");
		}
	}
}
function _view_text(obj,taskId){
	if(taskId==""){
		alert("请先保存");
	}else{
		if($(obj).attr("state") == "1"){
			$(obj).attr("state", "2");
			$("#___text_zone_content").css("display","block");
			$("#textImg").attr("src", webRoot + "/images/x2.png");
			ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/do-text.htm", "default_text_zone",uploadDocument);
		}else{
			$(obj).attr("state", "1");
			$("#textImg").attr("src", webRoot + "/images/x1.png");
			$("#___text_zone_content").html("");
		}
	}
}

function _view_accessory(obj,taskId){
	if(taskId==""){
		alert("请先保存");
	}else{
		if($(obj).attr("state") == "1"){
			$(obj).attr("state", "2");
			$("#___attachment_zone_content").css("display","block");
			$("#accImg").attr("src", webRoot + "/images/x2.png");
			ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/do-attachment.htm", "default_accessory_zone",uploadAttachment);
		}else{
			$(obj).attr("state", "1");
			$("#accImg").attr("src", webRoot + "/images/x1.png");
			$("#___attachment_zone_content").html("");
		}
	}
}

//添加意见
function addOpinion(){
	var editOpinion = $("#editOpinion");
	var html = $('#saveOpinionFormContent').html();
	$('#saveOpinionFormContent').html('');
	$('#saveOpinionFormContent').append('<form id="saveOpinionForm" name="saveOpinionForm" >'+html+'</form>');
	editOpinion.show();
}

//保存意见
function saveOpinion(){
	if($("#opinions").attr("value")==""){
		alert("请填写意见");
	}else{
		$("#opinion").attr("value",$("#opinions").attr("value"));
		ajaxAnyWhereSubmit("officeForm", webRoot+"/common/save-opinion.htm", "default_opinion_zone");
	}
}

//取回
workflowButtonGroup.btnGetBackTask.click = function(taskId){
	$.ajax({
		data:{taskId: taskId},
		type:"post",
		url:webRoot + "/common/get-back.htm",
		beforeSend:function(XMLHttpRequest){},
		success:function(data, textStatus){
			if(data=="任务已取回"){
				window.location.reload(false);
			}else{
				alert(data);
			}
		},
		complete:function(XMLHttpRequest, textStatus){},
        error:function(){

		}
	});
};


/************************************************************
上传正文
************************************************************/
var documentswfu;
/*---------------------------------------------------------
函数名称:uploadFile_d
参          数:url
功          能:正文上传初始化
------------------------------------------------------------*/
function uploadDocument(){
	documentswfu = new SWFUpload({
		upload_url: imatrixRoot+"/mms/common/do-upload.htm",
		post_params: {"name" : "参数"},
		
		file_post_name : "Filedata", //是POST过去的$_FILES的数组名   () 建议使用这个默认值
		
		file_size_limit : "100 MB",	// 1000MB
		file_types : "*.doc;*.pdf;*.xls;*.wps;*.et;",
		file_types_description : "word,excel,pdf,wps,et",
		file_upload_limit : "0",
		
		file_queue_error_handler : fileQueueError,
		file_dialog_complete_handler : fileDialogComplete,//选择好文件后提交
		file_queued_handler : fileQueued,
		upload_progress_handler : uploadProgress,
		upload_error_handler : uploadError,
		upload_success_handler : uploadSuccess,
		upload_complete_handler : uploadComplete,
		
		// Button Settings
		button_image_url : imatrixRoot + "/images/annex.gif",
		button_placeholder_id : "spanButtonPlaceholder",
		button_width: 250,
		button_height: 18,
		button_text : '<span class="button">请上传文件(最大100MB)</span>',
		button_text_style : '.button {border:1px solid #91B8D2;color:#2970A6;  }',
		button_text_top_padding: 0,
		button_text_left_padding: 18,
		button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
		button_cursor: SWFUpload.CURSOR.HAND,
		
		// Flash Settings
		flash_url : imatrixRoot + "/widgets/swfupload/swfupload.swf",
		
		custom_settings : {
			upload_target : "divFileProgressContainer",
			isUpload : true
		},
		// Debug Settings
		debug: false  //是否显示调试窗口
	});
}

/*---------------------------------------------------------
函数名称:fileDialogBefore
参          数:url
功          能:所好文件之后调用
------------------------------------------------------------*/
function fileDialogBefore(){
	var id=$("#id").attr("value");
	var taskId=$("#taskId").attr("value");
	if(id==""||id==null){
		documentswfu.customSettings.isUpload=false;
		alert("请先保存,再上传!");
		documentswfu.eventQueue=[];
	}else{
		documentswfu.customSettings.isUpload=true;
		documentswfu.setPostParams({"id":id,"taskId":taskId});
		documentswfu.startUpload();
	}
}

/*---------------------------------------------------------
函数名称:rewriteMethod
参          数:url
功          能:所有文件上传之后调用
------------------------------------------------------------*/
function rewriteMethod(){
	ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/do-text.htm", "default_text_zone",uploadDocument);
}


/************************************************************
上传附件
************************************************************/
var attachmentswfu;
/*---------------------------------------------------------
函数名称:uploadFile_d
参          数:url
功          能:附件上传初始化
------------------------------------------------------------*/
function uploadAttachment(){
	attachmentswfu = new SWFUpload({
		upload_url: imatrixRoot+"/mms/common/do-attache-upload.htm",
		post_params: {"name" : "参数"},
		
		file_post_name : "Filedata", //是POST过去的$_FILES的数组名   () 建议使用这个默认值
		
		file_size_limit : "100 MB",	// 1000MB
		file_types : "*.doc;*.pdf;*.xls;*.wps;*.et;",
		file_types_description : "word,excel,pdf,wps,et",
		file_upload_limit : "0",
		
		file_queue_error_handler : fileQueueError2,
		file_dialog_complete_handler : fileDialogComplete2,//选择好文件后提交
		file_queued_handler : fileQueued2,
		upload_progress_handler : uploadProgress2,
		upload_error_handler : uploadError2,
		upload_success_handler : uploadSuccess2,
		upload_complete_handler : uploadComplete2,
		
		// Button Settings
		button_image_url : imatrixRoot + "/images/annex.gif",
		button_placeholder_id : "workflow_attachment_spanButtonFileUpload",
		button_width: 250,
		button_height: 18,
		button_text : '<span class="button">请上传文件(最大100MB)</span>',
		button_text_style : '.button {border:1px solid #91B8D2;color:#2970A6;  }',
		button_text_top_padding: 0,
		button_text_left_padding: 18,
		button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
		button_cursor: SWFUpload.CURSOR.HAND,
		
		// Flash Settings
		flash_url : imatrixRoot + "/widgets/swfupload/swfupload.swf",
		
		custom_settings : {
			upload_target : "workflow_attachment_file_upload_progress",
			isUpload : true
		},
		// Debug Settings
		debug: false  //是否显示调试窗口
	});
}

/*---------------------------------------------------------
函数名称:fileDialogBefore
参          数:url
功          能:所好文件之后调用
------------------------------------------------------------*/
function fileDialogBefore2(){
	var id=$("#id").attr("value");
	var taskId=$("#taskId").attr("value");
	var companyId=$("#companyId").attr("value"); 
	if(id==""||id==null){
		documentswfu.customSettings.isUpload=false;
		alert("请先保存,再上传!");
		documentswfu.eventQueue=[];
	}else{
		attachmentswfu.customSettings.isUpload=true;
		attachmentswfu.setPostParams({"taskId":taskId,"companyId":companyId});
		attachmentswfu.startUpload();
	}
}

/*---------------------------------------------------------
函数名称:rewriteMethod
参          数:url
功          能:所有文件上传之后调用
------------------------------------------------------------*/
function rewriteMethod2(){
	ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/do-attachment.htm", "default_accessory_zone",uploadAttachment);
}

//下载文档
function downloadDoc(id,type){
	if(type=='document'){
		window.open(webRoot+"/common/download-document.htm?documentId="+id);
	}else{
		window.open(webRoot+"/common/download-attachment.htm?documentId="+id);
	}
}
