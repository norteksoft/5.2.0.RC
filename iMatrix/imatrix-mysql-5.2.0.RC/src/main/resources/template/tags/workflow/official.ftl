<form id="officeForm1" name="officeForm1" action="" method="post">
	<input type="hidden" id="workflowId" name="workflowId" value="${workflowId}">
	<input type="hidden" id="taskId" name="taskId" value="#{taskId}">
</form>
<table class="Table" >
	<thead>
		<tr>
			<th width="20%">文件名</th><th>文件大小(K)</th><th >文件类型</th><th >上传日期</th><th>上传人</th><th>上传环节名</th><th >操作</th>
		</tr>
	</thead>
	<tbody>
	<#if offices?exists>
      <#list offices as being>
         <tr id="${being.id}">
           <td  align="center">
           		<a href="#" onclick="openDocument('${being.fileType }','${being.workflowId}','#{taskId}','#{(being.id) }')">${being.fileName}</a>
           </td>
           <td align="center">${being.fileSize/1000}</td>
           <td align="center">${being.fileType}</td>
           <td align="center">${being.createdTime?string("yyyy-MM-dd HH:mm")}</td>
           <td align="center">${being.creatorName }</td>
           	<td>${being.taskName}</td>
           <td  align="center">
           <#if deleteRight>
           	<a name="#officeList#{being.id}" href="#officeList#{being.id}" onclick="deleteText(#{(being.id)})">删除</a>
           </#if>
           </td>
         </tr>
      </#list>
  </#if>
</tbody>
</table>
<#if createRight>
	<input type="button" value="新建Word" onclick="openDocument('.doc','${workflowId}','#{taskId}')"/>
	<input type="button" value="新建Excel" onclick="openDocument('.xls','${workflowId}','#{taskId}');"/>
	<input type="button" value="新建WPS文字" onclick="openDocument('.wps','${workflowId}','#{taskId}')"/>
	<input type="button" value="新建WPS表格" onclick="openDocument('.et','${workflowId}','#{taskId}')"/>
	<input type="button" value="新建PDF" onclick="openDocument('pdf','${workflowId}','#{taskId}');"/>
	
	<input type="button" value="上传文件" onclick='openUploadDocument(#{taskId},"${workflowId}","openUpload");' id="openUpload"/>
</#if>
