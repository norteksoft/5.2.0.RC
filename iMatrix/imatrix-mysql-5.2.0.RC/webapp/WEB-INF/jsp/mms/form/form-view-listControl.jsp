<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>列表控件</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script language="javascript" type="text/javascript" src="${imatrixCtx}/widgets/formeditor/dataControl.js"></script>
	<!--该页面中的文本框 不能输入逗号;当某行失去焦点时，判断对应字段是否已选.需要控制 -->
	<script type="text/javascript">
	$(document).ready(function(){
		$("html").css("overflow","auto");//添加ie滚动条
		var len=$("#contentTable").find("tbody").find("tr").length;
		if(len<=0){
			listControlAddRow();
		}
	});
		function mytip(item)
		{
		  if($("#"+item).css("display")=="none")
			  $("#"+item).css("display","block");
		  else
			  $("#"+item).css("display","none");   
		}

		function generateHtml(){
		//	controlId,txtName,dataSrc,txtTitle,lv_title,lv_size,lv_sum,lv_cal,lv_field
			var len=$("#contentTable").find("tbody").find("tr").length;
			var lv_title="";var lv_size="";var lv_sum="";var lv_cal="";var lv_field="";
			for(var i=0;i<len;i++)
			{
				var title=$("#item_"+(i+1)).attr("value");
				var field=$("#dataField_"+(i+1)).attr("value");
				if(typeof title!="undefined" && (title!=""||(title!="" && (typeof field!="undefined") &&field!=""))){
					lv_title+=title+",";
					var size=$("#size_"+(i+1)).attr("value");
					lv_size+=(size==""?10:size)+",";
					lv_sum+=$("#sum_"+(i+1)).attr("value")+",";
					var cal=$("#cal_"+(i+1)).attr("value");
					lv_cal+=(cal==""?"0":cal)+",";
					lv_field+=field+",";
				}
			}
			window.parent.listControlHtml($("#controlId").attr("value"),
					$("#dataSrc").attr("value"),
					$("#txtTitle").attr("value"),
					lv_title,
					lv_size,
					lv_sum,
					lv_cal,
					lv_field
				);
		}
	</script>
</head>
<body  onload="getContentHeight();">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="$('#dataForm').submit();"><span><span>确定</span></span></button>
		<button class="btn" onclick='parent.$.colorbox.close();'><span><span >取消</span></span></button>
	</div>
	<div id="opt-content">
		<form name="dataForm" id="dataForm" action="${ctx }/form/form-view!text.htm">
				<s:hidden name="id"></s:hidden>
				<s:hidden id="formId" name="formId"></s:hidden>
				<table  class="form-table-without-border">
				    <tr>
						<td class="content-title">控件类型：</td>
						<td>
							<s:textfield theme="simple"  name="formControl.controlType.code" readonly="true"></s:textfield>
							<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
						</td>
						<td><span id="controlTypeTip"></span></td>	
					</tr>	
					<tr>
						<td class="content-title">控件标题：</td>
						<td>
							<s:textfield theme="simple" id="txtTitle" name="formControl.title" cssClass="{required:true,messages: {required:'必填'}}"></s:textfield><span class="required">*</span>
						</td>
						<td><span id="txtTitleTip"></span></td>	
					</tr>
					<tr>
						<td class="content-title">控件id：</td>
						<td>
							<s:textfield theme="simple" id="controlId" name="formControl.controlId" cssClass="{required:true,messages: {required:'必填'}}"></s:textfield><span class="required">*</span>
						</td>
						<td><span id="controlIdTip"></span></td>	
					</tr>
				    <tr>
				       <td nowrap colspan="3" style="text-align: left;"  class="content-title">数据来源：
				       	<s:select id="dataSrc" name="formControl.dataSrc" list="dataTableList" theme="simple" listKey="id" listValue="alias" headerKey="0" headerValue="请选择" onchange="getListControlData();"></s:select>
				       </td>
				    </tr>
				    </table>
				    <table style="width:580px;">
				     <tr><td colspan="6"><hr/></td></tr>
				     <tr><td colspan="6" style="text-align: left;"><a href="#" onClick="mytip('cal_tip')">计算公式说明</a></td></tr>
				     <tr id="cal_tip" style="display:none;">
				       <td nowrap colspan="6" style="font-size: 10pt;font-family:宋体;color:blue;text-align: left;">
				       用[1] [2] [3]等代表某列的数值。运算符支持+,-,*,/,%等。<br>
				       </td>
				     </tr>
				      <tr><td colspan="6"><hr/></td></tr>
				     </table>
				    
					<table class="form-table-border-left" cellspacing="3" cellpadding="0" border="0" id="contentTable">
						<thead>
							<tr><th  align="center">序号</th>
							<th  align="center" title="Tab键切换输入框">列表控件表头项目</th>
							<th  align="center" title="Tab键切换输入框">宽度</th>
							<th align="center" title="Tab键切换输入框">合计</th>
							<th align="center" title="Tab键切换输入框">计算公式</th>
							<th align="center" title="Tab键切换输入框">对应的字段</th>
							<th align="center" title="Tab键切换输入框">操作</th>
							</tr>
						</thead>
						<tbody>
						 <s:iterator value="dataSelectFields[0]" status="stat">
							  <tr>
					              <td align="center" id="serNum_${stat.index+1 }">${stat.index+1 }</td>
					              <td  align="center" title="Tab键切换输入框">
					              	<input id="item_${stat.index+1 }" type="text" size="25" value="${dataSelectFields[1][stat.index] }">
					              </td>
					              <td  align="center" title="Tab键切换输入框">
					              	<Input id="size_${stat.index+1 }" type="text" size="5" value="${dataSelectFields[3][stat.index] }">
					              </td>
					              <td  align="center" title="Tab键切换输入框">
					              	<Input type="checkbox" id="sum_${stat.index+1 }" value="${dataSelectFields[2][stat.index] }">
					              </td>
					              <td  align="center" title="Tab键切换输入框">
					              	<Input id="cal_${stat.index+1 }" type="text" size="15" value="${dataSelectFields[4][stat.index] }">
					              </td>
					              <td  style="text-align: left;">
						             <select id="dataField_${stat.index+1 }">
									   <option value="">请选择字段</option>
										<s:iterator value="columns">
										<s:if test="dataSelectFields[0][#stat.index]==columnName">
											<option value="${name }" selected="selected">${alias }</option>
										</s:if><s:else>
											<option value="${name }">${alias }</option>
										</s:else>
										</s:iterator>
									</select>
						           </td>
						           <td><a href="#" onclick="listControlAddRow();" title="增加"  class="small-btn"><span><span>增加</span></span></a><a href="#" onclick="listControlDeleteRow(this);" title="删除"  class="small-btn"><span><span>删除</span></span></a></td>
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
	</div>
</div>
	<table id="mata" style="display: none;">
		<tbody>
			 <tr>
	              <td  align="center" id="serNum">1</td>
	              <td  align="center" title="Tab键切换输入框">
	              	<input id="item" type="text" size="25">
	              </td>
	              <td  align="center" title="Tab键切换输入框">
	              	<Input id="size" type="text" size="5" value="10" >
	              </td>
	              <td  align="center" title="Tab键切换输入框">
	              	<Input type="checkbox" id="sum">
	              </td>
	              <td  align="center" title="Tab键切换输入框">
	              	<Input id="cal" type="text" size="15">
	              </td>
	              <td  style="text-align: left;">
		             <select id="dataField">
					   <option value="">请选择字段</option>
						<s:iterator value="columns">
							<option value="${name }">${alias }</option>
						</s:iterator>
					</select>
		           </td>
		           <td><a href="#" onclick="listControlAddRow();" title="增加"  class="small-btn"><span><span>增加</span></span></a><a href="#" onclick="listControlDeleteRow(this);" title="删除"  class="small-btn"><span><span>删除</span></span></a></td>
	          </tr>
		</tbody>
	</table>
</div>
</body>

</html>

