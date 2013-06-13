/**
*增加流程定义模板
*/
function createWorkflowDefinitionTemplate(url){
	ajaxSubmit("defaultForm",url,"wf_template",backCall);
}

function backCall(){
	uploadTemplateXml();
	uploadTemplatePicture();
	validateTemplate();
}

function validateTemplate(){
	$("#templateForm").validate({
		submitHandler: function() {
				$("#templateForm").ajaxSubmit(function (id){
					$("#id").attr("value",id);
					$("#message").show("show");
					setTimeout('$("#message").hide("show");',3000);
				});
		}
	});
}
/**
*修改流程定义模板
*/
function updateWorkflowDefinitionTemplate(url){
	var ids=jQuery("#main_table").getGridParam('selarrrow');
	if(ids.length<=0){
		alert("请选择需要编辑的记录！");
		return;
	}else if(ids.length>1){
		alert("请不要选择多条记录！");
		return;
	}else{
		ajaxSubmit("updateForm",url+"?id="+ids,"wf_template",backCall);
	}
}
/**
*删除流程定义模板
*/
function deleteWorkflowDefinitionTemplate(url){
	var ids=jQuery("#main_table").getGridParam('selarrrow');
	if(ids.length<=0){
		alert("请选择需要删除的记录！");
		return;
	}else{
		if(confirm("确定要删除吗？")){
			$.ajax({
				data:{ids:ids.join(",")},
				type:"post",
				url:url,
				beforeSend:function(XMLHttpRequest){},
				success:function(data, textStatus){
					if(data=='ok'){
						setPageState();
						ajaxSubmit("defaultForm",webRoot+'/engine/workflow-definition-template-list.htm',"wf_template");
					}else{
						alert(data);
					}
				},
				complete:function(XMLHttpRequest, textStatus){},
		        error:function(){
	
				}
			});
		}
	}
}
/**
*保存流程定义模板
*/
function saveWorkflowDefinitionTemplate(url){
	$("#templateForm").attr("action",url);
	$("#templateForm").submit();
}

/**
*上传流程定义模板xml
*/
var swfu;
function uploadTemplateXml(){
	swfu = new SWFUpload({
		upload_url: webRoot+"/engine/upload-xml.htm",
		post_params: {"name" : "参数"},

		file_post_name : "Filedata", //是POST过去的$_FILES的数组名   () 建议使用这个默认值
		
		// File Upload Settings
		file_size_limit : "30 MB",	// 1000MB
		file_types : "*.xml",
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
		button_text : '<span class="button">请点击上传模板文件(最大30MB)</span>',
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
/**
*上传流程定义模板图片
*/
var swfu1;
function uploadTemplatePicture(){
	swfu1 = new SWFUpload({
		upload_url: webRoot+"/engine/upload-picture.htm",
		post_params: {"name" : "参数"},

		file_post_name : "Filedata", //是POST过去的$_FILES的数组名   () 建议使用这个默认值
		
		// File Upload Settings
		file_size_limit : "30 MB",	// 1000MB
		file_types : "*.bmp;*.gif;*.img;*.jpg;*.png",
		file_types_description : "所有文件",
		file_upload_limit : "0",
						
		file_queue_error_handler : fileQueueError,
		file_dialog_complete_handler : fileDialogComplete,//选择好文件后提交
		file_queued_handler : fileQueued1,
		upload_progress_handler : uploadProgress,
		upload_error_handler : uploadError,
		upload_success_handler : uploadSuccess,
		upload_complete_handler : uploadComplete,

		// Button Settings
		button_image_url : webRoot+"/images/annex.gif",
		button_placeholder_id : "spanButtonPlaceholder1",
		button_width: 250,
		button_height: 18,
		button_text : '<span class="button">请点击这里上传图片(最大30MB)</span>',
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

function fileQueued1(file){
	fileDialogBefore1(file);
	if(this.customSettings.isUpload!=undefined&&!this.customSettings.isUpload){
		this.cancelUpload(file.id, false);
	}
	addReadyFileInfo(file.id,file.name,"成功加载到上传队列");
}
/*---------------------------------------------------------
函数名称:fileDialogBefore
参          数:url
功          能:选好文件之后调用
------------------------------------------------------------*/
function fileDialogBefore1(file){
	var id=$("#id").attr("value");
	if(id==""||id==null){
		swfu1.customSettings.isUpload=false;
		alert("请先保存此表单后,再上传模板!");
		swfu1.eventQueue=[];
	}else{
		validateSign=true;
		swfu1.customSettings.isUpload=true;
		swfu1.setPostParams({"id":id});
		swfu1.startUpload();
		$("#template_file_icon").attr("value",file.name);
	}
}
/**
*返回
*/
function back(){
	swfu.destroy();		
	swfu1.destroy();		
	ajaxSubmit('defaultForm',webRoot +'/engine/workflow-definition-template-list.htm', 'wf_template'); 
}

