<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>

<title>流程管理</title>
<%@ include file="/common/wf-iframe-meta.jsp"%>

<style type="text/css">
* html ul,* html ol,* html dl {
	position: fixed;
}

ul,ui {
	margin: 0;
	padding: 0;
	list-style: none;
}

li {
	float: left;
	padding: 2px;
	padding-right: 5px;
	background-color: white;
	color: gray;
	cursor: pointer;
}

li.tabin {
	background-color: #f2f6fb;
	border: 1px solid gray;
	border-bottom: 0;
	z-index: 6;
	position: relative;
}

td.select span {
	padding: 2px;
	background-color: #2971A7;
}
.pageContent{
	padding-top: 10px; 
	z-index: 1; 
	background-color: #f2f6fb; 
	width: 700px; 
	height: 320px; 
	padding: 10px; 
	border: solid gray 1px;
	position: relative; 
	top: -1px;

}
.preview{
	padding: 2px;
	overflow-y: scroll; 
	width: 400px; 
	height: 310px;
	float: left;
	border: 1px solid gray; 
	background-color: white;
}
.viewImg{
	width: 280px; 
	height: 310px; 
	float: left; 
	border: 1px solid gray; 
	margin-left: 5px; 
	background-color: white;
}
</style>

<script type="text/javascript">
	$(function(){
		$("li").each(function(index){
			if($("#typeId").attr("value")==0){
				if(index==0){
					$(this).addClass("tabin");
				}
			}
			$(this).click(function(){
				$("li.tabin").removeClass("tabin");
				$(this).addClass("tabin");
				var typeId = $(this).attr("value");
				$("#typeId").attr("value",typeId);
				ajaxanywhere_template('${wfCtx}/engine/workflow-definition!templateList.htm',"defaultForm","template");
			});
		});

		addeven();
		
	});


	function addeven(){
		$("td.template").each(function(index){
			$(this).click(function(){
				$("td.select").removeClass("select");
				$(this).addClass("select");

				var path = $(this).attr("path");
				var value = $(this).attr("value");
				var str = '<img style="width: 260px;height: 260px;" src="${wfCtx}/'+path +'"/>';
				$("#previewImg").html(str);
				$("#templateId").attr("value",value);
			});
		});

	}
	
	function ajaxanywhere_template(url,formname,zone){
		$("#"+formname).attr("action",url);
		ajaxAnywhere.formName = formname;
		ajaxAnywhere.getZonesToReload = function(){
			return zone;
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			addeven();
		 };
		ajaxAnywhere.submitAJAX();
	}

	function ok(){
		window.parent.intoInput($("#templateId").attr("value"),$("#typeId").val());
		//parent.window.location.href= "${wfCtx}/engine/workflow-definition!input.htm?templateId="+$("#templateId").attr("value")+"&type="+$("#typeId").val();
		window.parent.$("#add").colorbox.close();
	}
	</script>

</head>
<body>
<div class="ui-layout-center">
<aa:zone name="wfd_main">
<form name="defaultForm" id="defaultForm" method="post">
<input type="hidden" id="typeId" name="type" value="${type }"></form>
<form action="" id="templateForm" name="templateForm" method="post">
<input  name="templateId" id="templateId" type="hidden"/> 
<input type="hidden" name="type" value="${type }" /></form>
		<div class="opt-btn">
			<button id="createInst"  class='btn' onclick="ok();" hidefocus="true"><span><span>确定</span></span></button>
			<button class='btn' onclick="window.parent.$('#add').colorbox.close();" hidefocus="true"><span><span>取消</span></span></button>
		</div>
		<ul>
			<s:iterator value="typeList">
				<s:if test="id==type">
					<li value="${id }" class="tabin">${name }</li>
				</s:if><s:else>
					<li value="${id }">${name }</li>
				</s:else>
			</s:iterator>
		</ul>
		<br style="clear: both; line-height: 0px;">
		<div class="pageContent" style="">
		<aa:zone name="template">
			<div class="preview">
			<table style="border: 0px;">
				<tr>
					<s:iterator value="templates" status="index">
						<td class="template"
							style="border: 0px; text-align: center; padding: 5px; width: 80px; height: 80px"
							path="${previewImage }" value="${id }" valign="top">
						<p><img src="${wfCtx}/images/E.jpg" /></p>
						<span style="font-size: 10pt;">${name }</span></td>
						<s:if test="%{(#index.index+1)%5==0}">
				</tr>
				<tr>
					</s:if>
					</s:iterator>
				</tr>
			</table>
			</div>
			<div class="viewImg">
				<p style="color: #4D87C7; margin-left: 20px; margin-top: 2px">预览图</p>
				<div id="previewImg" style="width: 280px; height: 260px;"></div>
			</div>
		</aa:zone>
		</div>
</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>