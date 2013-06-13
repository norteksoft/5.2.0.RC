<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>紧急程度设置控件</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>

	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	 <style type="text/css" >
      .first{
      	width: 10px;
      }
      .selected_td .first{
      	background-color:#2971A7;
      }
     
    </style>
	<script type="text/javascript">
	var arry = ["紧急程度最高:红旗","蓝旗","黄旗","绿旗","橙旗","紫旗","紧急程度最低:无"];
	function removeOption(){
		$("#contentTable").find("tr").each(function(i){
			if($(this).hasClass("selected_td")){
				$(this).remove();
			}
		});

	}

	function addOption(){
		var val = $("#urgencyDescribe").val();
		if(val==''){
			alert("请输入紧急程度的文字描述");
			return;
		}

		var redio = $("input[name='flag']:checked");
		if(redio.length==0){
			alert("请选择对应旗子");
			return;
		}
		
		addContent($(redio).next()[0].outerHTML,val,$(redio).attr("value"));
		$("#urgencyDescribe").attr("value","");
		$(redio).attr("checked",false);
		$(redio).next().next().attr("checked",true);
	}

	function addContent(img,text,value){
		var html = "<tr onclick=\"select(this);\"><td class=\"first\">"+img+"</td><td value=\""+value+"\">"+text+"</td><tr>";
		$("#contentTable").append(html);
	}
	function select(obj){
		$(obj).parent().children().each(function(){
			$(this).removeClass("selected_td");
		});
		$(obj).addClass("selected_td");
	}
	function generateHtml(){
		parent.urgencyHtml($("#controlType").attr("value")
					,document.getElementById("contentTable")
					);
	}
	</script>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="generateHtml();"><span><span>确定</span></span></button>
		<button class="btn" onclick='parent.$.colorbox.close();'><span><span >取消</span></span></button>
	</div>
	<div id="opt-content">
		<aa:zone name="controlContent">
			<form name="urgencyForm" id="urgencyForm" action="" method="post">
				<s:hidden name="id"></s:hidden>
				<input id="dataType" name="formControl.dataType" value="NUMBER" type="hidden"></input>
				<table  class="form-table-without-border">
				    <tr>
				      <td colspan="3" style="text-align:left">
				      	请输入紧急程度，并选择标志紧急程度的旗子。<br/>
				      	红旗紧急程度最高，无表示紧急程度最低。
				      </td>
				    </tr>
				    <tr>
						<td class="content-title">控件类型：</td>
						<td>
							<s:textfield theme="simple"  name="formControl.controlType.code" readonly="true"></s:textfield>
							<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
						</td>
						<td><span id="controlTypeTip"></span></td>	
					</tr>	
				    <tr>
				      <td  style="text-align:right"  class="content-title">紧急程度：</td>
				      <td ><s:textfield theme="simple" name="urgencyDescribe" id="urgencyDescribe" maxlength="15" /></td>
				      <td >文字描述(最多15字)</td>
				    </tr>
				    <tr >
				      <td  style="text-align:right"  class="content-title">
							对应旗子：
					</td>
				      <td colspan="2">
					      <input type="radio" name="flag" value="0" title="紧急程度最高:红旗" checked="checked"/><img alt="紧急程度最高:红旗" src="${mmsCtx}/images/0.gif">
					      <input type="radio" name="flag" value="1" title="蓝旗"/><img alt="蓝旗" src="${mmsCtx}/images/1.gif">
					      <input type="radio" name="flag" value="2" title="黄旗"/><img alt="黄旗" src="${mmsCtx}/images/2.gif">
					      <input type="radio" name="flag" value="3" title="绿旗"/><img alt="绿旗" src="${mmsCtx}/images/3.gif">
					      <input type="radio" name="flag" value="4" title="橙旗"/><img alt="橙旗" src="${mmsCtx}/images/4.gif">
					      <input type="radio" name="flag" value="5" title="紫旗"/><img alt="紫旗" src="${mmsCtx}/images/5.gif">
					      <input type="radio" name="flag" value="6" title="紧急程度最低:无"/>无<img alt="紫旗" src="${mmsCtx}/images/6.gif">
					</td>
				    </tr>
				    <tr>
				      <td rowspan="4" align="right" valign="top">已有选项：</td>
				      <td rowspan="4" valign="top">
				      	<div style="float: left; border: 1px solid gray; width: 100%; height: 200px; overflow: auto;">
							<table id="contentTable" style="width: 100%;">
							<s:iterator value="urgencyList[0]" status="stat">
								<tr onclick="select(this);">
									<td class="first"><img alt="arry[${urgencyList[0][stat.index] }]" src="${mmsCtx}/images/${urgencyList[0][stat.index] }.gif"></img></td>
									<td value="${urgencyList[0][stat.index] }">${urgencyList[1][stat.index] }</td>
								</tr>
							</s:iterator>
				      		</table>
						</div>
				      </td>
				      <td style="border:none">
					      <a href="#" onclick="addOption();" title="增加"  class="small-btn" id="add_button"><span><span>增加</span></span></a>
					      <a href="#" onclick="removeOption();" title="删除"  class="small-btn" id="delete_button"><span><span>删除</span></span></a>
				      </td>
				    </tr>
				  </table>
			</form>
		</aa:zone>
	</div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
