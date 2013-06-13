<div id="aazone.workflow_attachments_list" style="DISPLAY: inline">

	<span id="workflow_attachment_spanButtonFileUpload"></span>
	<table  style="border: 1px solid rgb(127, 170, 255); padding: 2px; display: block;  margin-top: 8px;" width="530" border="0">
		<thead>
			<tr>
				<td width="380">已上传文件队列</td><td width="150">操作</td>
			</tr>
		</thead>
		<tbody >
			<#if attachments?exists>
				<#list attachments as being>
					<tr id="upload_tr_#{being.id}">
						<td width="350">
						<#if downloadRight>
							<a href="${ctx}/engine/upload!download.htm?id=#{being.id}">${being.fileName}</a> 
						<#else>
							${being.fileName}
						</#if>
						</td>
						<td width="150">
						<#if deleteRight>
							<a href="#" onclick="deleteUpload('${ctx}/engine/upload!delete.htm',#{being.id})">删除</a>
						</#if>
						</td>
					</tr>
				</#list>
			</#if>
		</tbody>
	</table>
	<span id="workflow_attachment_file_upload_progress"></span>

</div>

<form action="" name="workflow_attachments_form" id="workflow_attachments_form" method="post">
	<input type="hidden" name="taskId" value="${taskId }"/>
	<input type="hidden" name="companyId" value="${companyId }"/>
</form>

<script type="text/javascript" >
var swfu;
$(function(){
	if("${addRight}"=="true"){
		swfu= new SWFUpload({
			// Backend Settings
			upload_url: webRoot + "/engine/upload!save.htm",
			post_params: {"taskId" : "${taskId}","companyId":"${companyId}"},
			
			// File Upload Settings
			file_size_limit : "${fileSizeLimit}" + " MB",	// 限制文件大小
			file_types : "${fileTypes}",
			file_types_description : "${fileTypesDescription}",
			file_upload_limit : "${fileUploadLimit}",
			
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
			button_image_url : webRoot + "${imageUrl}",//按钮图片
			button_placeholder_id : "workflow_attachment_spanButtonFileUpload",//按钮放在标签id
			button_width: 250,
			button_height: 18,
			button_text : '<span class="button">请选择文件<span class="buttonSmall">(最大10MB)</span></span>',
			button_text_style : '.button { font-family: Helvetica, Arial, sans-serif; font-size: 12pt; } .buttonSmall { font-size: 10pt; }',
			button_text_top_padding: 0,
			button_text_left_padding: 18,
			button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
			button_cursor: SWFUpload.CURSOR.HAND,
			
			// Flash Settings
			flash_url :  webRoot + "${moduleUrl}" + "swfupload.swf",//上传swf的位置
			
			custom_settings : {
				upload_target : "workflow_attachment_file_upload_progress"//进度条反正标签的id
			},
			
			// Debug Settings
			debug: false
		});
	}
});
 
</script>
