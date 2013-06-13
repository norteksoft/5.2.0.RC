<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<html>
	<head>
		<title>数据字典类型管理</title>
	</head>
	<body>
	<div class="ui-layout-center">
		<aa:zone name="dict_zone">
				<div class="opt-btn">
					<security:authorize ifAnyGranted="wf_engine_dict_type_save">
						<button  class='btn' onclick="save_type_form();" hidefocus="true"><span><span>保存</span></span></button>
					</security:authorize>	
					<button class='btn' onclick='setPageState();dataDicBack("defaultForm","${wfCtx}/engine/data-dictionary-type.htm","dict_zone","page");' hidefocus="true"><span><span>返回</span></span></button>
				</div>
				<div id="backTypeMsg" style="margin-left: 12px;color: green;"><s:actionmessage theme="mytheme"/></div>
			<form action="${wfCtx}/engine/workflow-type!save.htm" name="dictTypeForm" id="dictTypeForm" method="post">
					<input name="id" id="id" type="hidden" value="${id}"/>
					<br/>
					<table>
						<tr>
							<td>类型编号：</td>
							<td>
								<s:if test="id==null">
									<input id="no" name="no"  type="text" value="${no }" onblur="validateTypeNo();" maxlength="30"/><span class="required">*</span>
								</s:if><s:else>
									<input id="no" name="no"  type="text" value="${no }" disabled="disabled" maxlength="30"/>
								</s:else>
							</td>
							<td></td>
						</tr>
						<tr>
							<td>类型名称：</td>
							<td><input id="name" name="name"  type="text" value="${name }" maxlength="30"/><span class="required">*</span></td>
							<td></td>
						</tr>
						<tr>
							<td>包含类型：</td>
							<td>
								 <table style="border: 0 solid;">
									<tbody>
										<tr>
											<td style="widows: 200px;">
												<s:iterator value="typeList" status="index">
													<s:if test="#index.index!=0&&#index.index%4==0">
														<br/>
													</s:if>
													<c:choose>
														<c:when test="${fn:contains(types, id)}">
															<input name="typeIdList"  type="checkbox" value="${id }" checked="checked"/>${name }&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														</c:when>
														<c:otherwise>
															<input name="typeIdList"  type="checkbox" value="${id }"/>${name }&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														</c:otherwise>
													</c:choose>
												</s:iterator>
											</td>
										</tr>
									</tbody>
								</table>
							</td>
							<td></td>
						</tr>
						<tr></tr>
					</table>
			</form>
		</aa:zone>
	</div>
	</body>
</html>
