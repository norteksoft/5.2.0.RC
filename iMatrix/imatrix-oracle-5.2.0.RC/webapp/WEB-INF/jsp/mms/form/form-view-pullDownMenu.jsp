<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	
	<script src="${imatrixCtx}/widgets/formeditor/pullDownMenu.js" type="text/javascript" charset="UTF-8"></script>
	<script type="text/javascript">
	var selectListHtml;
	function choiceControl(){
		selectListHtml=$("#selectList").html();
		if($("#tableColumnId").get(0).selectedIndex==0){
			$("#name").attr("value","");
		}
		ajaxAnyWhereSubmit("pullDownMenuForm", "", "controlContent",choiceControlCallBack);
	}
	function choiceControlCallBack(){
		$("#selectList").html(selectListHtml);
	}
	function generateHtml(){
		if("${standard}"=="true"){
			if($("#tableColumnId").get(0).selectedIndex==0){
				alert("请选择对应字段");
			}else{
				var select=document.getElementById("selectList");
				$("#selectValues").attr("value","");
				for(var i=0;i<select.length;i++){
					if($("#selectValues").attr("value")==""){
						$("#selectValues").attr("value",select.options[i].text+";"+select.options[i].value);
					}else{
						$("#selectValues").attr("value",$("#selectValues").attr("value")+","+select.options[i].text+";"+select.options[i].text);
					}
				}
				parent.pullDownMenuHtml($("#tableColumnId").attr("value")
							,$("#dataType").attr("value")
							,$("#controlType").attr("value")
							,$("#name").attr("value")
							,$("#title").attr("value")
							,$("#controlId").attr("value")
							,$("#childControlIds").attr("value")
							,$("#initSelectValue").attr("value")
							,document.getElementById("selectList")
							,$("#classId").attr("value")
							,$("#styleId").attr("value")
							,$("#dbName").attr("value")
				);
			}
		}else{
			parent.pullDownMenuHtml(""
					,$("#dataType").attr("value")
					,$("#controlType").attr("value")
					,$("#name").attr("value")
					,$("#title").attr("value")
					,$("#controlId").attr("value")
					,$("#childControlIds").attr("value")
					,$("#initSelectValue").attr("value")
					,document.getElementById("selectList")
					,$("#classId").attr("value")
					,$("#styleId").attr("value")
					,""
		);
		}
		
	}
	function mytip(item)
	{
	  if($("#"+item).css("display")=="none")
		  $("#"+item).css("display","block");
	  else
		  $("#"+item).css("display","none");   
	}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:80%;
	}
	</style>
	</head>
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
			<div class="opt-btn">
				<button class="btn" onclick="$('#pullDownMenuForm').submit();"><span><span>确定</span></span></button>
				<button class="btn" onclick='parent.$.colorbox.close();'><span><span >取消</span></span></button>
			</div>
			<div id="opt-content">
				<aa:zone name="controlContent">
					<form name="pullDownMenuForm" id="pullDownMenuForm" action="${mmsCtx }/form/form-view!text.htm">
						<s:hidden name="id"></s:hidden>
						<s:hidden id="formId" name="formId"></s:hidden>
						<s:hidden id="code" name="code"></s:hidden>
						<s:hidden id="version" name="version"></s:hidden>
						<s:hidden id="standard" name="standard"></s:hidden>
						<s:hidden id="occasion" name="occasion" value="changeSource"></s:hidden>
						<table class="form-table-without-border">
							<tr>
								<td class="content-title" style="width: 22%;">控件类型：</td>
								<td>
									<s:textfield theme="simple"  name="formControl.controlType.code" readonly="true"></s:textfield>
									<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
								</td>
								<td><span id="controlTypeTip"></span></td>	
							</tr>
							<s:if test="standard">
								<tr>
									<td class="content-title">对应字段：</td>
									<td>
										<s:hidden id="dataType" name="formControl.dataType"></s:hidden>
										<s:select onchange="choiceControl();" id="tableColumnId" name="tableColumnId" list="columns" theme="simple" listKey="id" listValue="displayName" headerKey="0" headerValue="请选择"></s:select>
									</td>
									<td></td>	
								</tr>
								<tr>
									<td class="content-title">字段名：</td>
									<td>
										<s:if test="tableColumnId==null||tableColumnId==0">
											<s:textfield theme="simple" id="name" name="formControl.name" cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
										</s:if>
										<s:else>
											<s:textfield theme="simple" id="name" name="formControl.name" readonly="true" cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
										</s:else>
										<s:hidden  theme="simple" name="formControl.dbName" id="dbName"/>
										<s:hidden  theme="simple" name="formControl.dataType" id="dataType"/>
										<span class="required">*</span>
									</td>
									<td>
										<span id="nameTip"></span>
									</td>	
								</tr>
							</s:if><s:else>
								<tr>
									<td class="content-title">字段名：</td>
									<td>
										<s:textfield theme="simple" id="name" maxlength="27" name="formControl.name"  cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
										<span class="required">*</span>
									</td>
									<td>
										<span id="nameTip"></span>
									</td>	
								</tr>
								<tr>	
									<td class="content-title">字段类型：</td>	
									<td>
										<s:select theme="simple" id="dataType" list="#{'TEXT':'文本','DATE':'日期','TIME':'时间','INTEGER':'整型','LONG':'长整型','DOUBLE':'双精度浮点数','FLOAT':'单精度浮点数','BOOLEAN':'布尔型','CLOB':'大文本'}" 
								 name="formControl.dataType"></s:select>
									</td>
								</tr>
							</s:else>
							<tr>
								<td class="content-title">字段别名：</td>
								<td>
									<s:textfield theme="simple" id="title" name="formControl.title" cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
									<span class="required">*</span>
								</td>
								<td><span id="titleTip"></span></td>	
							</tr>
							<tr>
								<td class="content-title">控件id：</td>
								<td>
									<s:textfield theme="simple" id="controlId" name="formControl.controlId" cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
									<span class="required">*</span>
								</td>
								<td><span id="controlIdTip"></span></td>	
							</tr>
							<tr>
								<td class="content-title">关联子菜单控件id：</td>
								<td >
					             	<s:textfield theme="simple" id="childControlIds" name="formControl.childControlIds"/>
				                </td>
				                <td >
					                <a href="#" onClick="mytip('tip')">说明</a>
					                <div id="tip" style="display:none">若关联子菜单，需要子下拉菜单设置的时候在每个选项后加上特殊标记以记录与父菜单关系，形如“子菜单项目|父菜单项目”，则父菜单发生变化，子菜单会随之自动刷新筛选。</div>
				                </td>
				             </tr>
							<tr>
								<td  class="content-title">初始选定：</td>
								<td ><s:textfield theme="simple" id="initSelectValue" name="formControl.initSelectValue" readonly="true"/></td>
							</tr>
							<tr>
								<td class="content-title">样式类名：</td>
								<td>
									<s:textfield theme="simple" id="classId" name="formControl.classStyle"></s:textfield>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title">内联样式：</td>
								<td>
									<s:textfield theme="simple" id="styleId" name="formControl.styleContent"></s:textfield>
								</td>
								<td></td>	
							</tr>	
						</table>
						<br>
						<table>
							<tr>
								<td>下拉菜单项目<br>
									文本:<input id="txtText" name="txtText" style="width:150px;"/>&nbsp;&nbsp;值:<input id="txtVal" name="txtVal" style="width:130px;"/>
								</td>
								<td vAlign="bottom" align="right">
									<a href="#" onclick="Add();" title="新增"  class="small-btn"><span><span>新增</span></span></a>
									<a href="#" onclick="Modify(this);" title="修改"  class="small-btn"><span><span>修改</span></span></a>
								</td>
							</tr>
							<tr>
								<td>
									<select id="selectList" style="WIDTH: 340px" onchange="$('#selectList').get(0).selectedIndex = this.selectedIndex;Select(this);"
										size="5" name="selectList">
									<s:iterator value="selectList" status="ind" var="list">
										<option value="${list[1]}">${list[0]}</option>
									</s:iterator>	
									</select>
									<s:hidden id="selectValues" name="formControl.selectValues"></s:hidden>
								</td>
								<td vAlign="bottom">
									<a href="#" onclick="Move(-1);" title="上移"  class="small-btn"><span><span>上移</span></span></a>
									<br/>
									<a href="#" onclick="Move(1);" title="下移"  class="small-btn"><span><span>下移</span></span></a>
								</td>
							</tr>
							<TR>
								<TD colSpan="2">
									<a href="#" onclick="SetSelectedValue();" title="上移"  class="small-btn"><span><span>设为初始化时选定值</span></span></a>
									<a href="#" onclick="Delete();" title="下移"  class="small-btn"><span><span>删除</span></span></a>
							</TR>
						</table>
					</form>
					<script type="text/javascript">
					function validateText(){
						$("#pullDownMenuForm").validate({
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
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
