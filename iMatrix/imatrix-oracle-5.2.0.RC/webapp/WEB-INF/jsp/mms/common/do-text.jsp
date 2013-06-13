<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<aa:zone name="default_text_zone">
<div id="___text_zone_content">
<input type="hidden" id="taskId" value="${taskId }"></input>
  <table class="form-table-border-left"  style="width: 700px;">
	<thead>
		<tr>
			<th width="20%">文件名</th>
			<th>文件大小(K)</th>
			<th>文件类型</th>
			<th>上传日期</th>
			<th>上传环节名</th>
			<th >操作</th>
		</tr>
	</thead>
	<s:iterator value="offices"  >
     <tr id="${id}">
           <td  align="center">
            <s:if test="textRight.contains('edit')">
           		<a href="#" onclick="onlineEdit('${fileType }','${workflowId}','${id }');" style='text-decoration:underline;color:black;'>${fileName}</a>
           	</s:if><s:else>
           		${fileName}
           	</s:else>
           </td>
           <td align="center">${fileSize/1000}</td>
           <td align="center">${fileType}</td>
           <td align="center">${createDate}</td>
           <td>${taskName}</td>
           <td  align="center">
           <s:if test="textRight.contains('downLoad')|| monitorFlag">
           		<a href="#" onclick="downloadDoc('${id}','document');" style='text-decoration:underline;color:black;'>下载</a>
           	</s:if>
           <s:if test="textRight.contains('delete')">
           	<a  href="#" name="officeList_${id}" onclick="deleteText('${id}')"  style='text-decoration:underline;color:black;'>删除</a>
           </s:if>
           </td>
         </tr>
      </s:iterator>  
  </table>
  <div style="margin: 10px 0px 10px 20px;">
  	<s:if test="textRight.contains('create')">
		<div id="spanButtonPlaceholder" style="margin-left: 65px;"></div>
	</s:if><s:else><span style="display: none;"><span id="spanButtonPlaceholder"></span></span></s:else>
	<span id="divFileProgressContainer"></span>
</div>
</div>
<script type="text/javascript">
function onlineEdit(fileType,workflowId,id){
	openDocument(fileType,workflowId,$('#taskId').attr('value'),id);
}
</script>
</aa:zone>