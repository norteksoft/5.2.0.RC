
//---附件---
var swfu;
function uploadTemplate(){
	swfu = new SWFUpload({
		upload_url: webRoot+"/engine/office-template!upload.htm",
		post_params: {"name" : "参数"},

		file_post_name : "Filedata", //是POST过去的$_FILES的数组名   () 建议使用这个默认值
		
		// File Upload Settings
		file_size_limit : "30 MB",	// 1000MB
		file_types : "*.dot;*.doc;*.xls;*.xlt",
		file_types_description : "所有文件",
		file_upload_limit : "0",
						
		file_queue_error_handler : fileQueueError,
		file_dialog_complete_handler : fileDialogComplete,//选择好文件后提交
		file_queued_handler : fileQueued,
		upload_progress_handler : uploadProgress,
		upload_error_handler : uploadError,
		upload_success_handler : uploadSuccess,
		upload_complete_handler : uploadComplete,

		// Button Settings
		button_image_url : webRoot+"/images/annex.gif",
		button_placeholder_id : "spanButtonPlaceholder",
		button_width: 250,
		button_height: 18,
		button_text : '<span class="button">点击上传模板文件(最大30MB)</span>',
		button_text_style : '.button {border:1px solid #91B8D2;color:#2970A6;  }',
		button_text_top_padding: 0,
		button_text_left_padding: 18,
		button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
		button_cursor: SWFUpload.CURSOR.HAND,
		
		// Flash Settings
		flash_url : imatrixRoot+"/widgets/swfupload/swfupload.swf",

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
功          能:选好文件之后调用
------------------------------------------------------------*/
function fileDialogBefore(file){
	var id=$("#id").attr("value");
	if(id==""||id==null){
		swfu.customSettings.isUpload=false;
		alert("请先保存此表单后,再上传模板!");
		swfu.eventQueue=[];
	}else{
		validateSign=true;
		swfu.customSettings.isUpload=true;
		swfu.setPostParams({"id":id});
		swfu.startUpload();
		$("#template_file_name").attr("value",file.name);
	}
}

function back_office_template(){
	ajaxSubmit("defaultForm",webRoot+'/engine/office-template.htm','wf_template');
}

function fileDialogStart_document_input() { 
	if($("#template_file_id").attr("value")!=""&&$("#template_file_id").attr("name")!=""){
		SWFUpload.instances[$("#template_file_id").attr("name")].cancelUpload($("#template_file_id").attr("value"), false);
	}
}
function fileQueued_document_input(file) {
	try {
		$("#template_file_name").attr("value",file.name);
		$("#template_file_id").attr("value",file.id);
		$("#template_file_id").attr("value",file.id);
		$("#template_file_id").attr("name",this.movieName);
	} catch (e) {
	}
	
}
	function class_go(url,pagesize,zone,form){
		var str=$("#class_go_text").attr("value");
		if(str==null&&str==""){
			return;
		}
		if(str<=0){
			$("#class_go_text").attr("value","");
			return;
		}
		if(str>pagesize){
			str=pagesize;
		}
		ajaxSubmit(form,url+str,zone);
	}
	
	function back(form,url,zoons){
		swfu.destroy();		
		ajaxSubmit('backForm',webRoot +'/engine/office-template.htm?typeId='+$("#backTypeId").val(), 'wf_template'); 
	}

	function save_form(){
		$("#templateForm").submit();
	}

	//保存
	function ajax_submit_form(){
		$('#saveBtn').unbind();
		ajaxSubmit('templateForm', webRoot+'/engine/office-template!save.htm','form_content', saveCallBack);
	}

	function saveCallBack(){
		showMsg("message");
		//上传附件
		swfu.setPostParams({"templateId":$('#id').attr('value')});
		swfu.startUpload();
		validate();
	}
	//页面验证
	function  validate(){
		$.formValidator.initConfig({formid:"templateForm",onsuccess: function() {ajax_submit_form();return false;},onerror:function(){}});
		//$("#template_file_name").formValidator({onshow:'请选择模板',onfocus:'请选择模板',oncorrect:'成功'}).inputValidator({min:1,empty:{emptyerror:'必须选择模板文件'},onerror:'错误'});
	 }

	function inputCallBack(){
		uploadTemplate();
		validate();
	}
	function deleteCallBack(){
		showMsg("message");
	}

function option(opt){
	var ids = jQuery("#main_table").getGridParam('selarrrow');

	if(opt == "add"){
		ajaxSubmit("defaultForm",webRoot +"/engine/office-template!input.htm","wf_template",inputCallBack);
	}else if(opt == "update"){
		//var rds = $("input[name='templateIds']:checked");
		if(ids==''){
			alert("请选择模板");
			return;
		}else if(ids.length>=2){
			alert("只能选择一条！");
			return;
		}else{
			ajaxSubmit("updateForm",webRoot +"/engine/office-template!input.htm?id="+ids,"wf_template",inputCallBack);
		}
	}else if(opt == "delete"){
		if(ids==''){
			alert("请选择模板");
			return;
		}else{
			if(confirm("确认删除吗？")){
				for(var i=0; i<ids.length; i++){
					$("#templateForm").html($("#templateForm").html()+'<input type="hidden" name="templateIds" value="'+ids[i]+'">');
				}
				ajaxSubmit("templateForm",webRoot +"/engine/office-template!delete.htm","wf_template",deleteCallBack);
			}
		}
	}
}

function validateReadio(){
	var rds = $("input[name='templateIds']");
	for(var i = 0; i < rds.length; i++){
		if($(rds[i]).attr("checked") || $(rds[i]).attr("checked") == "checked"){
			$("#templateId").attr("value", $(rds[i]).attr("value"));
			return true;
		}	
	}
	return false;
}
function treechange(id){
	$("#wf_type").attr('value', id.split('_')[1]);
	ajaxSubmit('defaultForm',webRoot +'/engine/office-template.htm', 'wf_template',resetJmessaPageNo); 
}

function uploadSuccess(file, serverData) {
	try {
		file.id = "singlefile";	// This makes it so FileProgress only makes a single UI element, instead of one for each file
		var progress = new FileProgress(file, this.customSettings.progress_target);
		progress.setComplete();
		progress.setStatus("Complete.");
		progress.toggleCancel(false);
		
		if (serverData === " ") {
			this.customSettings.upload_successful = false;
		} else {
			this.customSettings.upload_successful = true;
			document.getElementById("hidFileID").value = serverData;
		}
		
	} catch (e) {
	}
}

function uploadComplete(file) {
	try {
		if (this.customSettings.upload_successful) {
			this.setButtonDisabled(true);
			uploadDone();
		} else {
			file.id = "singlefile";	// This makes it so FileProgress only makes a single UI element, instead of one for each file
			var progress = new FileProgress(file, this.customSettings.progress_target);
			progress.setError();
			progress.setStatus("File rejected");
			progress.toggleCancel(false);
			
			var txtFileName = document.getElementById("txtFileName");
			txtFileName.value = "";
			validateForm();

			alert("There was a problem with the upload.\nThe server did not accept it.");
		}
	} catch (e) {
	}
}
function uploadError(file, errorCode, message) {
	try {
		
		if (errorCode === SWFUpload.UPLOAD_ERROR.FILE_CANCELLED) {
			// Don't show cancelled error boxes
			return;
		}
		
		var txtFileName = document.getElementById("txtFileName");
		txtFileName.value = "";
		validateForm();
		
		// Handle this error separately because we don't want to create a FileProgress element for it.
		switch (errorCode) {
		case SWFUpload.UPLOAD_ERROR.MISSING_UPLOAD_URL:
			alert("There was a configuration error.  You will not be able to upload a resume at this time.");
			this.debug("Error Code: No backend file, File name: " + file.name + ", Message: " + message);
			return;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
			alert("You may only upload 1 file.");
			this.debug("Error Code: Upload Limit Exceeded, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			return;
		case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
		case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED:
			break;
		default:
			alert("An error occurred in the upload. Try again later.");
			this.debug("Error Code: " + errorCode + ", File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			return;
		}

		file.id = "singlefile";	// This makes it so FileProgress only makes a single UI element, instead of one for each file
		var progress = new FileProgress(file, this.customSettings.progress_target);
		progress.setError();
		progress.toggleCancel(false);

		switch (errorCode) {
		case SWFUpload.UPLOAD_ERROR.HTTP_ERROR:
			progress.setStatus("Upload Error");
			this.debug("Error Code: HTTP Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_FAILED:
			progress.setStatus("Upload Failed.");
			this.debug("Error Code: Upload Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.IO_ERROR:
			progress.setStatus("Server (IO) Error");
			this.debug("Error Code: IO Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.SECURITY_ERROR:
			progress.setStatus("Security Error");
			this.debug("Error Code: Security Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
			progress.setStatus("Upload Cancelled");
			this.debug("Error Code: Upload Cancelled, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED:
			progress.setStatus("Upload Stopped");
			this.debug("Error Code: Upload Stopped, File name: " + file.name + ", Message: " + message);
			break;
		}
	} catch (ex) {
	}
}

function uploadProgress(file, bytesLoaded, bytesTotal) {

	try {
		var percent = Math.ceil((bytesLoaded / bytesTotal) * 100);

		file.id = "singlefile";	// This makes it so FileProgress only makes a single UI element, instead of one for each file
		var progress = new FileProgress(file, this.customSettings.progress_target);
		progress.setProgress(percent);
		progress.setStatus("Uploading...");
	} catch (e) {
	}
}

function fileQueueError(file, errorCode, message) {
	try {
		var errormessage = "出错啦！";
		var errorName = "";
		if (errorCode === SWFUpload.errorCode_QUEUE_LIMIT_EXCEEDED) {
			errorName = "你上传的文件太多拉.";
		}

		if (errorName !== "") {
			alert(errorName);
			return;
		}

		switch (errorCode) {
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			errormessage = "上传文件大小为0字节";
			break;
		case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
			errormessage = "上传文件超出大小限制！";
			break;
		case SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED:
			errormessage = "超出一次允许上传文件的个数，每次允许上传的文件个数是"+message;
			break;
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
			errormessage = "无效的文件类型！";
			break;
		default:
			errormessage = message;
			break;
		}

		alert(errormessage);

	} catch (ex) {
		this.debug(ex);
	}
}

function uploadStart(file) {
	try {
		var progress = new FileProgress(file, this.movieName, this.customSettings.progressTarget);
		progress.setFileName(file.name);
		progress.setStatus("正在上传 ...");
		progress.toggleCancel(true, this);
		this.addPostParam('fileSize', file.size);
	} catch (ex) {
	}
	
	return true;
}
