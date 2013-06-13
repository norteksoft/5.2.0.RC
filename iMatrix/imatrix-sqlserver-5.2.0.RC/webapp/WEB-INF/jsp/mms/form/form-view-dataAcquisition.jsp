<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
	<head>
	<title>数据选择控件设定</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	
	<script language="javascript" type="text/javascript" src="${imatrixCtx}/widgets/formeditor/dataControl.js"></script>
	<script type="text/javascript"> 
	function generateHtml(){
		var data_control="";var data_fld_name="";var data_field="";
		var rows=$('#map_tbl').find("tr");
		if(rows.length<=1){
			alert("请添加字段设置");
		}else{
			for(var i=1;i<rows.length;i++)
		  	{
				  var tds=$(rows[i]).find("td");
				  data_field+=$(tds[0]).text()+",";
				  data_fld_name+=$(tds[1]).text()+",";
				  data_control+=$(tds[2]).text()+",";
			}
			parent.dataAcquisitionHtml($("#controlType").attr("value")
						,$("#name").attr("value")
						,$("#controlId").attr("value")
						,$("#dataSrc").attr("value")
						,$("#dataSrcName").attr("value")
						,data_fld_name
						,data_field
						,data_control
						,$("#referenceControl").attr("value")
						,$("#queryProperty").attr("value")
			);
		}
	}
	
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
	</head>
	 
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
	<div class="opt-body">
		<div class="opt-btn">
			<button class="btn" onclick="$('#dataForm').submit();"><span><span>确定</span></span></button>
			<button class="btn" onclick='parent.$.colorbox.close();'><span><span >取消</span></span></button>
		</div>
		<div id="opt-content">
			<aa:zone name="controlContent">
					<form name="dataForm" id="dataForm" action="${mmsCtx }/form/form-view!text.htm">
						<s:hidden name="id"></s:hidden>
						<s:hidden id="formId" name="formId"></s:hidden>
						<input id="code" type="hidden" name="code" value="${code }"/>
						<input id="version" type="hidden" name="version" value="${version}"/>
						<table class="form-table-without-border">
							<tr>
								<td class="content-title">控件类型：</td>
								<td>
									<s:textfield theme="simple"  name="formControl.controlType.code" readonly="true"></s:textfield>
									<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
								</td>
								<td><span id="controlTypeTip"></span></td>	
							</tr>	
							<tr>
								<td class="content-title">控件名称：</td>
								<td>
									<s:textfield theme="simple" id="name" name="formControl.name" cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
							<tr>
								<td class="content-title">控件id：</td>
								<td>
									<s:textfield theme="simple" id="controlId" name="formControl.controlId" cssClass="{required:true,messages: {required:'必填'}}"></s:textfield>
								</td>
								<td><span id="controlIdTip"></span></td>	
							</tr>
						  <tr>
						    <td class="content-title">数据来源：</td>
						    <td>
						    	<s:select id="dataSrc" name="formControl.dataSrc" list="dataTableList" theme="simple" listKey="name" listValue="alias" headerKey="0" headerValue="请选择" onchange="getData('DATA_ACQUISITION');"></s:select>
						    	<s:hidden theme="simple"  name="formControl.dataSrcName" id="dataSrcName" ></s:hidden>
						    </td>
						  </tr>
						  <tr>
						  	<td class="content-title">查询字段名称：</td>
						  	<td>
							  	 <select name="formControl.queryProperty" id="queryProperty" style="width: 100px;">
								 	<option value="">请选择字段</option>
							 	 <s:iterator value="columns" >
							 	 	<s:if test="dbColumnName.contains('dt_')">
										<option value="${dbColumnName }" <s:if test="dbColumnName==formControl.queryProperty">selected="selected"</s:if>>${alias }</option>
							 	 	</s:if>
								</s:iterator>
								</select>
						    </td>
						    <td>最好选择能唯一确定一条记录的字段.</td>
						  </tr>
						  <tr>
						  	<td class="content-title">单行文本控件id：</td>
						  	<td>
						  		<s:textfield theme="simple" id="referenceControl" name="formControl.referenceControl" ></s:textfield>
						    </td>
						  </tr>
						</table> 
						<table style="width: 550px;">  
						 <tr style="width: 550px;">
						  	<td style="width: 60px;">字段名称：</td>
						  	<td style="width: 100px;">
						  		 <select name="dataField" id="dataField" style="width: 100px;">
							  <option value="">请选择字段</option>
						 	 <s:iterator value="columns">
						 	 	<s:if test="dbColumnName.contains('dt_')">
									<option value="${dbColumnName }">${alias }</option>
						 	 	</s:if>
							</s:iterator>
							</select>
							</td>
							<td style="width: 108px;">—单行文本控件id：</td><td style="width: 80px;"><input type="text" name="itemTitle" id="itemTitle"/></td><td style="width: 100px;"> <input type="checkbox" name="isQuery" id="isQuery" title="是否作为查询字段" style="display: none;"/>
							<input onclick="add();" type="button"  value="添加"/>
						    </td>
						  </tr>
						</table>
						  <table id="map_tbl" class="form-table-border-left" >  
							  <thead >
								  <tr><th>字段</th>
								      <th>字段名称</th>
								      <th >单行文本控件id</th>
								      <th >操作</th>
							      </tr>
							  </thead>
							  <tbody>
								  <s:iterator value="dataSelectFields[0]" status="stat">
									  <tr>
										  <td>${dataSelectFields[0][stat.index] }</td>
										  <td>${dataSelectFields[1][stat.index] }</td>
										  <td>${dataSelectFields[2][stat.index] }</td>
										  <td><a href="#" onclick="del(this);">删除</a></td>
									  </tr>
								  </s:iterator>
							  </tbody>
						</table>
					</form>
					<script type="text/javascript">
					function validateText(){
						$("#dataForm").validate({
							submitHandler: function() {
								generateHtml();
							}
						});
					}
					validateText();
					</script>
			</aa:zone>
		</div>
	</div>
	</div>
	</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
