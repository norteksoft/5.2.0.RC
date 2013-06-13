<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<form name="parameterform" id="parameterform" method="post">

</form>

<aa:zone name="default_opinion_zone">
<div id="___opinion_zone_content">
	
	<table class="form-table-border-left" style="width:700px;" >
		<thead>
			<tr>
				<th style="width: 20%">办理人</th>
				<th style="width: 35%">意见</th>
				<th style="width: 25%">添加意见时间</th>
				<th style="width: 20%">环节名称</th>
			</tr>
		</thead>
		<tbody>
		<s:if test="opinionRight.contains('view')">
			<s:iterator value="opinions">
				<tr>
					<td>${transactor}</td>
					<td>${opinion}</td>
					<td><s:date name="createdTime"/></td>
					<td>${taskName}</td>
				</tr>
			</s:iterator>
		</s:if>
		</tbody>
	</table>
	<div style="margin-top: 10px; ">
		<s:if test="opinionRight.contains('edit')||opinionRight.contains('must')">
			<button onclick="addOpinion();" type="button" class="btn"><span><span>添加意见</span></span></button>
		</s:if>
	</div>
	
	
	<div id="editOpinion" style="margin: 10px 0 0 8px;display: none;height: 260;width: 480;" >
		<p  class="buttonP">
			<button href="#" onclick="saveOpinion();" type="button" class="btn"><span><span>确定</span></span></button>&nbsp;&nbsp;<button href="#" onclick="removeOpinion();" type="button" class="btn"><span><span>取消</span></span></button>
		</p>
		<p>意见：</p>
			<form id="approvalViews" name="approvalViews" action="">
				<input type="hidden" name="workflowId" value="${workflowId}"/>
				<input type="hidden" name="taskId" value="${taskId}"/>
				<input type="hidden" name="companyId" value="${companyId}"/>
				<textarea id="opinions" name="opinions" cols="50" rows="8" style="width: 700px;" ></textarea>
			</form>
			
	</div>
</div>
</aa:zone>