<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<aa:zone name="default_accessory_zone">
<div id="___attachment_zone_content">
  <table class="form-table-border-left" style="width: 700px;">
		<thead>
			<tr>
				<td width="380">已上传文件队列</td><td width="150">操作</td>
			</tr>
		</thead>
		<tbody >
		<s:iterator value="WorkflowAttachments">
					<tr id="upload_tr_${id}">
						<td width="350">
						<s:if test="textRight.contains('downLoad') || monitorFlag">
							<a href="#" onclick="downloadDoc('${id}','attachment');" style='text-decoration:underline;color:black;'>${fileName}</a> 
						</s:if><s:else>
						   ${fileName}
						</s:else>
						</td>
						<td width="150">
						<s:if test="textRight.contains('delete')">
							<a href="#" onclick="deleteUpload('${mmsCtx}/common/delete-attachment.htm','${id}')" style='text-decoration:underline;color:black;'>删除</a>
						</s:if>
						</td>
					</tr>
		</s:iterator>
		</tbody>
	</table>
	
	<div style="margin: 10px 0px 10px 20px;">
  	<s:if test="textRight.contains('create')">
		<div id="workflow_attachment_spanButtonFileUpload" style="margin-left: 65px;"></div>
	</s:if><s:else><span style="display: none;"><span id="workflow_attachment_spanButtonFileUpload"></span></span></s:else>
	<span id="workflow_attachment_file_upload_progress"></span>
</div>
</div>


</aa:zone>