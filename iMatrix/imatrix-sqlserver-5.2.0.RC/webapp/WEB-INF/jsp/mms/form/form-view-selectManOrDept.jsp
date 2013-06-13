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
	
	<script type="text/javascript">
		function choiceControl(type){
			$("#infoType").attr("value",type);
			ajaxAnyWhereSubmit("deptForm", "", "controlContent");
		}
		function generateHtml(){
			if("${standard}"=="true"){
				if($("#saveDeptControlValue").get(0).selectedIndex==0){
					alert("请选择保存信息的字段");
					return;
				}else{
					parent.deptHtml($("#name").attr("value")
							,$("#controlType").attr("value")
							,$("#showDeptControlValue").attr("value")
							,$("#showDeptControlId").attr("value")
							,$("#showDeptCotrolType").attr("value")
							,$("#saveDeptControlValue").attr("value")
							,$("#saveDeptControlId").attr("value")
							,$("#deptTreeType").attr("value")
							,$("#deptMultiple").attr("value")
							,$("#classId").attr("value")
							,$("#styleId").attr("value")
							,$("#dbName").attr("value"));
				}
			}else{
				parent.deptHtml($("#name").attr("value")
						,$("#controlType").attr("value")
						,""
						,$("#showDeptControlId").attr("value")
						,$("#showDeptCotrolType").attr("value")
						,""
						,$("#saveDeptControlId").attr("value")
						,$("#deptTreeType").attr("value")
						,$("#deptMultiple").attr("value")
						,$("#classId").attr("value")
						,$("#styleId").attr("value")
						,"");
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
		<button class="btn" onclick="$('#deptForm').submit();"><span><span>确定</span></span></button>
		<button class="btn" onclick='parent.$.colorbox.close();'><span><span >取消</span></span></button>
	</div>
	<div id="opt-content">
		<aa:zone name="controlContent">
			<form name="deptForm" id="deptForm" action="${mmsCtx }/form/form-view!text.htm">
				<s:hidden name="id"></s:hidden>
				<s:hidden id="formId" name="formId"></s:hidden>
				<s:hidden id="code" name="code"></s:hidden>
				<s:hidden id="version" name="version"></s:hidden>
				<s:hidden id="standard" name="standard"></s:hidden>
				<s:hidden id="infoType" name="infoType"></s:hidden>
				<s:hidden id="occasion" name="occasion" value="changeSource"></s:hidden>
				 <table >
				     <tr>
				      		<td>控件名称：</td>
				      		<td><s:textfield  theme="simple" name="formControl.name" id="name"  cssClass="{required:true,messages: {required:'必填'}}"/></td>
				      		<td><span id="nameTip"></span></td>
				      </tr>
				      <tr>
							<td>控件类型：</td>
							<td>
								<s:textfield theme="simple"  name="formControl.controlType.code" readonly="true"></s:textfield>
								<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
							</td>
							<td><span id="controlTypeTip"></span></td>	
						</tr>
						<s:if test="standard">
						  <tr>
					      	<td>显示信息的字段名称：</td>
					      	<td>
								<s:select onchange="choiceControl('show');" id="showDeptControlValue" name="formControl.showDeptControlValue" list="columns" theme="simple" listKey="name" listValue="alias" headerKey="0" headerValue="请选择"></s:select>
							</td>
					      	<td></td>
					      </tr>
					      <tr >
					      		<td>显示信息的输入框的控件id：</td>
					      		<td><s:textfield  theme="simple" name="formControl.showDeptControlId" id="showDeptControlId"/></td>
					      		<td><span id="showDeptControlIdTip"></span></td>
					      </tr>
					      <tr>
					     	<td>显示信息的输入框类型：</td>
					     	<td>
						     	<s:select id="showDeptCotrolType" theme="simple" list="#{'input':'文本框','textArea':'文本域'}" name="formControl.showDeptCotrolType" >
								</s:select>
					   		</td>
					     </tr>
					      <tr >
					      		<td>保存信息的字段名称：</td>
					      		<td>
									<s:select onchange="choiceControl('save');" id="saveDeptControlValue" name="formControl.saveDeptControlValue" list="columns" theme="simple" listKey="name" listValue="alias" headerKey="0" headerValue="请选择"></s:select>
								</td>
					      		<td></td>
					      </tr>
					      <tr >
					      		<td>保存信息的输入框的控件id：</td>
					      		<td><s:textfield  theme="simple" name="formControl.saveDeptControlId" id="saveDeptControlId"  cssClass="{required:true,messages: {required:'必填'}}"/><s:hidden  theme="simple" name="formControl.dbName" id="dbName"/></td>
					      		<td><span id="saveDeptControlIdTip"></span></td>
					      </tr>
						</s:if><s:else>
						<tr >
				      		<td>显示信息的输入框的控件id：</td>
				      		<td><s:textfield  theme="simple" name="formControl.showDeptControlId" id="showDeptControlId"/></td>
				      		<td><span id="showDeptControlIdTip"></span></td>
					      </tr>
					      <tr>
					     	<td>显示信息的输入框类型：</td>
					     	<td>
						     	<s:select id="showDeptCotrolType" theme="simple" list="#{'input':'文本框','textArea':'文本域'}" name="formControl.showDeptCotrolType" >
								</s:select>
					   		</td>
					     </tr>
					     <tr >
				      		<td>保存信息的输入框的控件id：</td>
				      		<td><s:textfield  theme="simple" name="formControl.saveDeptControlId" id="saveDeptControlId" cssClass="{required:true,messages: {required:'必填'}}"/></td>
				      		<td><span id="saveDeptControlIdTip"></span></td>
					      </tr>
						</s:else>
				     <tr>
				     	<td>选择类型：</td>
				     	<td>
				   			<select id="deptTreeType" name="formControl.deptTreeType">
				   				<option value="COMPANY">公司树</option>
				   				<option value="MAN_DEPARTMENT_GROUP_TREE">人员部门和工作组树</option>
				   				<option value="MAN_DEPARTMENT_TREE">人员部门树</option>
				   				<option value="MAN_GROUP_TREE">人员工作组树</option>
				   				<option value="DEPARTMENT_TREE">部门树</option>
				   				<option value="DEPARTMENT_WORKGROUP_TREE">部门工作组树</option>
				   				<option value="GROUP_TREE">工作组树</option>
				   			</select>
				   			<script type="text/javascript">
				   			function initTreeType(){
				   				$("#deptTreeType").attr("value","${formControl.deptTreeType}");
					   			if($("#deptTreeType").attr("value")==""){
					   				$("#deptTreeType").attr("value","COMPANY");
					   			}
				   			}
				   			initTreeType();
				   			</script>
				   		</td>
				     </tr>
				     <tr>
				     	<td>类型：</td>
				     	<td>
				     		<s:select id="deptMultiple" theme="simple" list="#{'false':'单选','true':'多选'}" name="formControl.deptMultiple" >
							</s:select>
				   		</td>
				     </tr>
				     <tr>
						<td>样式类名：</td>
						<td>
							<s:textfield theme="simple" id="classId" name="formControl.classStyle"></s:textfield>
						</td>
						<td></td>	
					</tr>
					<tr>
						<td>内联样式：</td>
						<td>
							<s:textfield theme="simple" id="styleId" name="formControl.styleContent"></s:textfield>
						</td>
						<td></td>	
					</tr>	
			    </table>
			</form>
			<script type="text/javascript">
			function validateText(){
				$("#deptForm").validate({
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
