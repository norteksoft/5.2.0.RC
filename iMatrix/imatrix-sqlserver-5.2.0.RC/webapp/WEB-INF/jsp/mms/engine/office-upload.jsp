<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<%@ include file="/common/mms-meta.jsp"%>
<link href="${imatrixCtx}/widgets/workflow-swfupload/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/workflow-attachment-handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/swfupload.js"></script>

<script type="text/javascript">
function initUpload(){
	var uploadUrl =webRoot + "/common/do-upload.htm";
	uploadFile1(uploadUrl);
}
var swfu;
function uploadFile1(url){
	swfu= new SWFUpload({
		// Backend Settings
		upload_url: url,
		post_params: {"taskId":"${taskId}"},
		
		
		// File Upload Settings
		file_size_limit : "10 MB",	// 限制文件大小
		file_types : "*.doc;*.xls;*.pdf",
		file_types_description : "正文",
		file_upload_limit : "0",
		
		// Event Handler Settings - these functions as defined in Handlers.js
		//  The handlers are not part of SWFUpload but are part of my website and control how
		//  my website reacts to the SWFUpload events.
		file_queue_error_handler : fileQueueError,
		file_dialog_complete_handler : fileDialogComplete,
		upload_progress_handler : uploadProgress,
		upload_error_handler : uploadError,
		upload_success_handler : uploadSuccess,
		upload_complete_handler : uploadComplete,
		
		// Button Settings
		button_image_url : webRoot + "/images/annex.gif",//按钮图片
		button_placeholder_id : "office_upload_spanButtonFileUpload",//按钮放在标签id
		button_width: 250,
		button_height: 18,
		button_text : '<span class="button">请选择文件<span class="buttonSmall">(最大10MB)</span></span>',
		button_text_style : '.button { font-family: Helvetica, Arial, sans-serif; font-size: 12pt; } .buttonSmall { font-size: 10pt; }',
		button_text_top_padding: 0,
		button_text_left_padding: 18,
		button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
		button_cursor: SWFUpload.CURSOR.HAND,
		
		// Flash Settings
		flash_url :  imatrixRoot +  "/widgets/workflow-swfupload/swfupload.swf",//上传swf的位置
		
		custom_settings : {
			upload_target : "office_file_upload_progress"//进度条反正标签的id
		},
		
		// Debug Settings
		debug: false
	});
}

function uploadSuccess(file, serverData) {
	try {
		var progress = new FileProgress(file,  this.customSettings.upload_target);
			progress.setStatus("文件上传完毕.");
			progress.toggleCancel(false);
	} catch (ex) {
		this.debug(ex);
	}
}

//重写
function rewriteMethod(){
	var workFlowInstanceId = $('#workFlowInstanceId').attr("value");
    var taskId= $('#taskId').attr("value");
	window.parent.freshParentDocumentListwj(webRoot+"/common/do-text.htm?workflowId="+workFlowInstanceId+"&taskId="+taskId);
	window.parent.$('#openUpload').colorbox.close();
	//window.parent.tb_remove();
}
</script>
</head>
<body onload="initUpload();">
<div class="ui-layout-center">
<div id="textDiv" >
 <input type="hidden" id="taskId" value="${taskId}"/>
 <input type="hidden" id="workFlowInstanceId" name="workFlowId" value="${workflowId}"/>
  <br/>
<form action="" id="textUploadForm" name="textUploadForm"  method="post"></form>
<div style="margin-left: 30px;" >
<div style="display: inline; border: solid 1px #7FAAFF; background-color: #C5D9FF; padding: 2px;margin: 1px 1px 1px 1px;">
	<span id="office_upload_spanButtonFileUpload"></span>
</div>
<a href="#" onclick="window.parent.$('#openUpload').colorbox.close();" class="btnStyle ">取消</a>
<span id="office_file_upload_progress"></span>
</div>
</div>
</div>
</body>
</html>

						