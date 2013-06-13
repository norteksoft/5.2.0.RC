function viewMsg(obj,taskId){
	if(taskId==""){
		alert("请先保存");
	}else{
		if($(obj).attr("state") == "1"){
			$("#comments").css("display", "block");
			$(obj).attr("state", "2")
			$("#viewImg").attr("src", webRoot + "/images/x2.png");
			var url =webRoot+"/engine/task!opinion.htm";
			$("#parameterform").attr("action",url);
			ajaxAnywhere.formName = "parameterform";
			ajaxAnywhere.getZonesToReload = function(){
				return "opinionList";
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				automaticHeight();
			}
			ajaxAnywhere.submitAJAX();
		}else{
			$("#comments").css("display", "none");
			$(obj).attr("state", "1");
			$("#viewImg").attr("src", webRoot + "/images/x1.png");
		}
	}
}
function viewOfficialtext(obj){
	if($(obj).attr("state") == "1"){
		$("#officialtext").css("display", "block");
		$(obj).attr("state", "2");
		$("#textImg").attr("src", webRoot + "/images/x2.png");
	}else{
		$("#officialtext").css("display", "none");
		$(obj).attr("state", "1");
		$("#textImg").attr("src", webRoot + "/images/x1.png");
	}
}

function viewText(obj,url,taskId){
	if(taskId==""){
		alert("请先保存");
	}else{
		ajaxAnyWhereViewText(obj,url);
	}
}
//删除正文
function deleteText(documentId){
	if(confirm("确认删除吗？")){
		$("#officeForm1").attr("action",webRoot+"/engine/office!deleteText.htm?id="+documentId);
		ajaxAnywhere.formName = "officeForm1";
		ajaxAnywhere.getZonesToReload = function(){
			return "officelist";
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			$("#"+documentId).remove();
		};
		ajaxAnywhere.submitAJAX();
	}
}

function ajaxAnyWhere_workflow(url,zone,obj){
	if($("#taskbutton").size()>0){
		if($(obj).html()=="表单信息"){
			$("#taskbutton").css("display","block");
			$("#closeBtn").css("display","none");
		}else{
			$("#closeBtn").css("display","block");
			$("#taskbutton").css("display","none");
		}
	}
	$("#defaultForm").attr("action",url);
	ajaxAnywhere.formName = "defaultForm";
	ajaxAnywhere.getZonesToReload = function(){
		return zone;
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		if($("#flashcontent").lenght==1){
			addSWf();
		}
		automaticHeight();
	};
	ajaxAnywhere.submitAJAX();

}

function ajaxAnyWhere_changeurl(url,zone,obj){
	$("#defaultForm").attr("action",url);
	ajaxAnywhere.formName = "defaultForm";
	
	ajaxAnywhere.getZonesToReload = function(){
		return zone;
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		change_backurl(url);
		automaticHeight();
	};
	ajaxAnywhere.submitAJAX();
}

function back_wf_task(){
	if($("#backurlform").length==1){
		ajaxAnywhere.formName = "backurlform";
		ajaxAnywhere.getZonesToReload = function(){
			return "wf_task";
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			automaticHeight();
		}
		ajaxAnywhere.submitAJAX();
	}
}

function change_backurl(url){
	if($("#backurlform").length==1){
		$("#backurlform").attr("action",url);
	}
}

function ajaxAnyWhere_zone(url,zone,form){
	$("#"+form).attr("action",url);
	ajaxAnywhere.formName = form;
	ajaxAnywhere.getZonesToReload = function(){
		return zone;
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		automaticHeight();
		var message=$("#message").attr("value");
		if(message.indexOf("不能被删除！")>0){
			$("#note").html(message);
			$("#note").attr("style","");
			setTimeout('$("#note").hide();',3000);
		}
	}
	ajaxAnywhere.submitAJAX();
}


/**附件的
 * ==================================================================================
 * @return
 */


function uploadFile1(url){
	swfu = new SWFUpload({
		upload_url: url,
		post_params: {"name" : "参数"},

		file_post_name : "Filedata", //是POST过去的$_FILES的数组名   () 建议使用这个默认值
		
		// File Upload Settings
		file_size_limit : "10 MB",	// 1000MB
		file_types : "*.*",
		file_types_description : "所有文件",
		file_upload_limit : "0",
						
		file_queue_error_handler : fileQueueError,
		file_dialog_complete_handler : fileDialogComplete,//选择好文件后提交
		file_queued_handler : fileQueued,
		upload_progress_handler : uploadProgress,
		upload_error_handler : uploadError,
		upload_success_handler : uploadSuccess,
		upload_complete_handler : uploadComplete1,

		// Button Settings
		button_image_url : webRoot + "/images/SmallSpyGlassWithTransperancy_17x18.png",
		button_placeholder_id : "spanButtonPlaceholder",
		button_width: 180,
		button_height: 18,
		button_text : '<span class="button">请点击这里选择文件 <span class="buttonSmall">(10 MB Max)</span></span>',
		button_text_style : '.button { font-family: Helvetica, Arial, sans-serif; font-size: 12pt; } .buttonSmall { font-size: 10pt; }',
		button_text_top_padding: 0,
		button_text_left_padding: 18,
		button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
		button_cursor: SWFUpload.CURSOR.HAND,
		
		// Flash Settings
		flash_url : webRoot + "/widgets/swfupload/swfupload.swf",

		custom_settings : {
			upload_target : "divFileProgressContainer"
		},
		// Debug Settings
		debug: false  //是否显示调试窗口
	});
}

function deleteCallBack(fileId){
	var td = $('#'+fileId).children()[1];
	var input = $(td).children('input');
	var type = $(input).attr('myType');
	if(type == 'PICTURE'){
		deletePicture($(input).attr('value'));
	}else if(type == 'MANUAL'){
		deleteManual($(input).attr('value'));
	}
	$('#'+fileId).remove();
}

//删除附件
function deleteUpload(url,id){
//	if(confirm("确定要删除该附件吗？")) {
//		$.ajax({url: url, 
//			type: 'POST', 
//			data:{id:id}, 
//			dataType: 'text', 
//			timeout: 1000, 
//			error: function(){alert('Error');}, 
//			success: function(result){
//				$("#upload_tr_"+id).remove();
//				ajaxAnyWhereViewAccessory(webRoot+"/engine/upload.htm");
//			} 
//		});
//	}
	
	if(confirm("确定要删除该附件吗？")){
		$("#workflow_attachments_form").attr("action",url+"?documentId="+id);
		ajaxAnywhere.formName = "workflow_attachments_form";
		ajaxAnywhere.getZonesToReload = function(){
			return "history_refresh_zone";
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			$("#upload_tr_"+id).remove();
		};
		ajaxAnywhere.submitAJAX();
	}
}


/*
 *成功后返回服务器端的值 
 */
function uploadSuccess_callback(file,serverData){
		deleteFile(file.id);
		$("upload_success_table").show();
		var html_ = $("#upload_success_list").html();
		var newhtml = '<tr id=\'upload_tr_'+serverData+'\'><td width="380">'+file.name+'</td><td width="150"><a href="#"  onclick="deleteUpload(\''+webRoot+'/engine/upload!delete.htm\','+serverData+')">删除</a></td></tr>';
		html_ = html_ + newhtml;
		$("#upload_success_list").html(html_);
		if($("#workflowId_upload").val()==""){
			var tempHtml = $("#upload_success_list_temp").html();
			tempHtml = tempHtml + newhtml;
			$("#upload_success_list_temp").html(tempHtml);
			var value = $("#wf_upload_ids").attr("value");
			if(value!="") value = value+",";
			value = value + serverData;
			$("#wf_upload_ids").attr("value",value);
		}
	automaticHeight();
}

function openUploadDocument(taskId,workflowId,id){
	$("#"+id).colorbox({href:webRoot+"/engine/office!upload.htm?taskId="+taskId+"&workflowId="+workflowId,iframe:true, innerWidth:400, innerHeight:100,overlayClose:false,title:"上传",onClosed:function(){$("#"+id).removeClass("cboxElement");}});
}
//正文-列表
function ajaxAnyWhereViewText(obj,url){
	if($(obj).attr("state") == "1"){
		$("#textDiv").css("display", "block");
		$(obj).attr("state", "2");
		$("#textImg").attr("src", webRoot + "/images/x2.png");
		$("#officeForm").attr("action",url);
		ajaxAnywhere.formName = "officeForm";
		ajaxAnywhere.getZonesToReload = function(){
			return "officelist";
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
		};
		ajaxAnywhere.submitAJAX();
	}else{
		$("#textDiv").css("display", "none");
		$(obj).attr("state", "1");
		$("#textImg").attr("src", webRoot + "/images/x1.png");
	}

}

//弹窗关闭时刷新主页面使用
function freshParentDocumentList(url){
	$("#officeForm").attr("action",url);
	ajaxAnywhere.formName = "officeForm";
	ajaxAnywhere.getZonesToReload = function(){
		return "officelist";
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		$("#textDiv").css("display", "block");
		$("#textImg").parent().attr("state", "2");
		$("#textImg").attr("src", webRoot + "/images/x2.png");
	};
	ajaxAnywhere.submitAJAX();
	}

/**
 * ==================================================================================
 */

//添加流程定义编辑器
function addSWf(type){
	//$("#flashcontent").height($("#col3").height()-60);
	var so = new SWFObject(imatrixRoot + "/widgets/workflowEditor/FlowChartProject.swf", "FlowChartProject", "100%","100%", "10", "#CCCCCC");//$(document).height()-100
	so.addParam("quality", "high");
	so.addParam("name", "FlowChartProject");
	so.addParam("id", "FlowChartProject");
	so.addParam("AllowScriptAccess", "always");
	so.addParam("menu", "false");
	so.addVariable("webRoot", imatrixRoot);
	so.addVariable("companyId", $("#companyId").val());
	so.addVariable("creator", $("#creator").val());
	so.addVariable("systemId", $("#systemId").val());
	if($("#localeLang").val()=="en"){
		so.addVariable("localeLanguage", "en_US");
	}else if($("#localeLang").val()=="zh"){
		so.addVariable("localeLanguage", "zh_CN");
	}
	if(type=="add"){
		so.addVariable("type", $("#type").val());
		so.addVariable("templateId", $("#templateId").val());
		so.addVariable("page", "addProcess");
	}else if(type=="update"){
		so.addVariable("type", $("#type").val());
		so.addVariable("wfId", $("#wfdId").val());
		so.addVariable("page", "updateProcess");
	}else if(type=="view"){
		so.addVariable("type", $("#type").val());
		so.addVariable("wfId", $("#wfdId").val());
		so.addVariable("page", "viewProcess");
	}
	so.write("flashcontent");
}