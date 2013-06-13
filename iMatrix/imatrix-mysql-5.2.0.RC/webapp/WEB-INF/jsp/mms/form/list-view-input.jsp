<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>列表管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="viewList">
		<script type="text/javascript">
			$(document).ready(function() {
				$( "#tabs" ).tabs();
			});
		</script>
		<style type="text/css">
			#tabs,.ui-tabs .ui-tabs-nav li,.ui-jqgrid,.ui-jqgrid .ui-jqgrid-htable th div,.ui-jqgrid .ui-jqgrid-view,.ui-jqgrid .ui-jqgrid-hdiv,.ui-jqgrid .ui-jqgrid-bdiv{ position: static; }
		</style>
		<aa:zone name="btnZone">
			<div class="opt-btn">
				<button class="btn" onclick="saveView();"><span><span>保存</span></span></button>
				<button class="btn" onclick='setPageState();listViewBack("viewSaveForm","${mmsCtx }/form/list-view.htm");'><span><span >返回</span></span></button>
			</div>
		</aa:zone>
		<div id="opt-content">
				<form id="defaultForm1" name="defaultForm1"action="">
					<input id="view_id1" type="hidden" name="viewId" value="${viewId }"></input>
					<input id="column_id" type="hidden" name="columnId"></input>
					<input id="menuId" name="menuId" value="${menuId}" type="hidden"></input>
				</form>
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1" onclick="changeViewSet('basic',this);">基本信息</a></li>
						<li><a href="#tabs-1" onclick="changeViewSet('column',this);">字段信息</a></li>
						<li><a href="#tabs-1" onclick="changeViewSet('groupHeader',this);">组合表头</a></li>
					</ul>
					<div id="tabs-1">
						<aa:zone name="viewZone">
							<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
							<input id="frozenColumnAmount" value="${frozenColumnAmount }" type="hidden"/>
							<input id="haveGroupHeader" value="${haveGroupHeader }" type="hidden"/>
							<form action="" name="viewSaveForm" id="viewSaveForm" method="post"> 
								<input type="hidden" name="viewId" value="${viewId}" id="viewId"/>
								<input id="menuId" name="menuId" value="${menuId}" type="hidden"></input>
								<table class="form-table-without-border">
								<s:if test="id==null">
									<tr>
										<td class="content-title" style="width:200px">编号：</td>
							  			<td><s:textfield theme="simple" id="code" name="code" maxlength="64" size="60"></s:textfield><span class="required">*</span></td>
							  			<td><span id="codeTip"></span></td>
									</tr>
								</s:if><s:else>
									<tr>
										<td class="content-title" style="width:200px">编号：</td>
							  			<td><s:textfield readonly="true" theme="simple" id="code" name="code" maxlength="64" size="60"></s:textfield><span class="required">*</span></td>
							  			<td><span id="codeTip"></span></td>
									</tr>
								</s:else>
									<tr>
										<td class="content-title">名称：</td>
							  			<td><s:textfield  theme="simple" id="name" name="name" maxlength="64" size="60"></s:textfield><span class="required">*</span></td>
							  			<td><span id="nameTip"></span></td>
									</tr>
									<tr>
										<td class="content-title">是否多选：</td>
							  			<td>
							  				<s:select theme="simple" list="#{'true':'是','false':'否'}" name="multiSelect"></s:select>
										</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title">仅点击复选框时选中复选框：</td>
							  			<td>
							  				<s:select theme="simple" list="#{'true':'是','false':'否'}" name="multiboxSelectOnly"></s:select>
										</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title">是否显示序号：</td>
							  			<td><s:select theme="simple" list="#{false:'否', true:'是'}" name="rowNumbers"></s:select>
							  			</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title">是否可以操作：</td>
							  			<td><s:select theme="simple" list="#{false:'否',true:'是'}" name="editable"></s:select>
							  			</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title">操作列宽设置：</td>
							  			<td><s:textfield  theme="simple" id="actWidth" name="actWidth" maxlength="6" size="60"></s:textfield></td>
							  			<td><span id="actWidthTip"></span></td>
									</tr>
									<tr>
										<td class="content-title">是否分页：</td>
							  			<td><s:select theme="simple" list="#{true:'是',false:'否'}" name="pagination"></s:select>
							  			</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title">是否启用查询：</td>
										<td>
											<select name="startQuery">
												<s:iterator value="@com.norteksoft.mms.form.enumeration.StartQuery@values()" var="startQueryVar">
													<option <s:if test="#startQueryVar==startQuery">selected="selected"</s:if> value="${startQueryVar}"><s:text name="%{code}"></s:text></option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title">查询显示方式：</td>
										<td>
											<s:select theme="simple" list="#{true:'弹出式',false:'嵌入式'}" name="popUp"></s:select>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title">启用高级查询：</td>
										<td><s:select theme="simple" list="#{false:'否',true:'是'}" name="advancedQuery"></s:select></td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title">默认排序字段：</td>
										<td>
											<select name="defaultSortField">
												<option value="">请选择</option>
												<s:iterator value="tableColumns" var="tableColumnVar">
													<option <s:if test="#tableColumnVar.name==defaultSortField">selected="selected"</s:if> value="${tableColumnVar.name}">${tableColumnVar.alias }</option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title">默认排序方式：</td>
										<td>
											<select name="orderType">
												<s:iterator value="@com.norteksoft.mms.form.enumeration.OrderType@values()" var="orderTypeVar">
													<option <s:if test="#orderTypeVar==orderType">selected="selected"</s:if> value="${orderTypeVar}"><s:text name="%{code}"></s:text></option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title">合计方式：</td>
										<td>
											<select name="totalType">
												<s:iterator value="@com.norteksoft.mms.form.enumeration.TotalType@values()" var="totalTypeVar">
													<option <s:if test="#totalTypeVar==totalType">selected="selected"</s:if> value="${totalTypeVar}"><s:text name="%{code}"></s:text></option>
												</s:iterator>
											</select>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title">行顺序的字段名称：</td>
							  			<td><s:textfield  theme="simple" id="indexName" name="orderFieldName" maxlength="255" size="60"></s:textfield>
							  			</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title">保存行拖到后的顺序的url：</td>
							  			<td><s:textfield  theme="simple" id="dragRowUrl" name="dragRowUrl" maxlength="255" size="60"></s:textfield></td>
							  			<td><span id="dragRowUrlTip"></span></td>
									</tr>
									<tr>
										<td class="content-title">列表编辑时保存url：</td>
							  			<td><s:textfield  theme="simple" id="editorUrl" name="editUrl" maxlength="255" size="60"></s:textfield></td>
							  			<td><span id="editorUrlTip"></span></td>
									</tr>
									<tr>
										<td class="content-title">列表编辑时删除url：</td>
							  			<td><s:textfield  theme="simple" id="deleteUrl" name="deleteUrl" maxlength="255" size="60"></s:textfield></td>
							  			<td><span id="deleteUrlTip"></span></td>
									</tr>
									<tr>
										<td class="content-title">默认行数：</td>
							  			<td><s:textfield  theme="simple" id="rowNum" name="rowNum" maxlength="3" size="60"></s:textfield></td>
							  			<td><span id="rowNumTip"></span></td>
									</tr>
									<tr>
										<td class="content-title">可选行数：</td>
							  			<td><s:textfield  theme="simple" id="rowList" name="rowList" maxlength="64" size="60"></s:textfield></td>
							  			<td><span id="rowListTip"></span></td>
									</tr>
									<tr>
										<td class="content-title">冻结列数：</td>
							  			<td><s:textfield  theme="simple" id="frozenColumn" name="frozenColumn" maxlength="3" size="60"></s:textfield></td>
							  			<td><span id="frozenColumnTip"></span></td>
									</tr>
									<tr>
										<td class="content-title">是否显示列表总条数：</td>
							  			<td>
							  				<s:select theme="simple" list="#{'true':'是','false':'否'}" name="totalable"></s:select>
										</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title">查询时是否显示列表总条数：</td>
							  			<td>
							  				<s:select theme="simple" list="#{'true':'是','false':'否'}" name="searchTotalable"></s:select>
										</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title">是否启用模糊查询：</td>
							  			<td>
							  				<s:select theme="simple" list="#{'true':'是','false':'否'}" name="searchFaint"></s:select>
										</td>
							  			<td>
							  			</td>
									</tr>
									<tr>
										<td class="content-title" id="pos">属性自由扩展：</td>
										<td><view:formGrid gridId="propertyGridId" code="MMS_JQ_GRID_PROPERTY" entity="${view}" attributeName="jqGridPropertys"></view:formGrid>
										</td>
										<td></td>
									</tr>
									<tr>
										<td class="content-title">备注：</td>
										<td><s:textarea  cols="50" rows="10" id="remark" name="remark"></s:textarea></td>
										<td><span id="remarkTip"></span></td>
									</tr>
								</table>
							</form>	
							<script>
							$(function(){
								$("div.ui-jqgrid-bdiv div").css("position","static");
								$("#tabs,.ui-tabs .ui-tabs-nav li,.ui-jqgrid,.ui-jqgrid .ui-jqgrid-htable th div,.ui-jqgrid .ui-jqgrid-view,.ui-jqgrid .ui-jqgrid-hdiv,.ui-jqgrid .ui-jqgrid-bdiv").css("position","static");
							});
							</script>
						</aa:zone>
					</div>
				</div>
			
		</div>
	</aa:zone>
</div>
</body>
</html>
