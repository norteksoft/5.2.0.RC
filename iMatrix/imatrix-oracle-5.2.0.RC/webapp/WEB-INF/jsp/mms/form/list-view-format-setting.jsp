<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript">
		$(document).ready(function(){
			analysis();
		});

		function selectClassify(){
			selectType();
			clearAway();
		}
		
		function selectType(){
			var classify=$("#classify").val();
			if(classify=="NUMERICAL_VALUE"){//数值
				$("#decimalsTr").show();
				$("#thousandSeparateTr").show();
				$("#currencySignTr").hide();
				$("#dateTypeTr").hide();
				$("#timeTypeTr").hide();
				$("#functionNameTr").hide();
			}else if(classify=="CURRENCY"){//货币
				$("#decimalsTr").show();
				$("#currencySignTr").show();
				$("#thousandSeparateTr").hide();
				$("#dateTypeTr").hide();
				$("#timeTypeTr").hide();
				$("#functionNameTr").hide();
			}else if(classify=="DATE"){//日期
				$("#dateTypeTr").show();
				$("#decimalsTr").hide();
				$("#thousandSeparateTr").hide();
				$("#currencySignTr").hide();
				$("#timeTypeTr").hide();
				$("#functionNameTr").hide();
			}else if(classify=="TIME"){//时间
				$("#timeTypeTr").show();
				$("#decimalsTr").hide();
				$("#thousandSeparateTr").hide();
				$("#currencySignTr").hide();
				$("#dateTypeTr").hide();
				$("#functionNameTr").hide();
			}else if(classify=="PERCENT"){//百分比
				$("#decimalsTr").show();
				$("#thousandSeparateTr").hide();
				$("#currencySignTr").hide();
				$("#dateTypeTr").hide();
				$("#timeTypeTr").hide();
				$("#functionNameTr").hide();
			}else if(classify=="CUSTOM"){//自定义
				$("#functionNameTr").show();
				$("#decimalsTr").hide();
				$("#thousandSeparateTr").hide();
				$("#currencySignTr").hide();
				$("#dateTypeTr").hide();
				$("#timeTypeTr").hide();
			}
		}
	
		function clearAway(){
			$("#decimals").attr("value","");
			$("#thousandSeparate").attr("checked","");
			$("#functionName").attr("value","");
		}

		/**
		 * 保存格式设置
		 */
		function saveFormatSetting(){
			var classify=$("#classify").val();
			var formatSetting="";
			if(classify=="NUMERICAL_VALUE"){//数值
				formatSetting=getNumericalValue();
			}else if(classify=="CURRENCY"){//货币
				formatSetting=$("#currencySign").val()+"#,##"+getDecimals();
			}else if(classify=="DATE"){//日期
				formatSetting=$("#dateType").val();
			}else if(classify=="TIME"){//时间
				formatSetting=$("#timeType").val();
			}else if(classify=="PERCENT"){//百分比
				formatSetting=getDecimals()+"%";
			}else if(classify=="CUSTOM"&&$("#functionName").val()!=""){
				formatSetting="func:"+$("#functionName").val();
			}
			parent.$("#"+"${currentInputId}").attr("value",formatSetting);
			parent.$.colorbox.close();
		}

		/**
		 * 获得小数位数
		 */
		function getDecimals(){
			var decimals=0;
			var formatSetting="";
			if($("#decimals").val()!=""){
				decimals=parseInt($("#decimals").val());
			}
			if(decimals>0){
				for(var i=0;i<decimals;i++){
					formatSetting+="0";
				}
			}
			if(formatSetting==""){
				formatSetting="0";
			}else{
				formatSetting="0."+formatSetting;
			}
			return formatSetting;
		}
		
		/**
		 * 获得数值格式
		 */
		function getNumericalValue(){
			var thousandSeparateLength=$("input[name=thousandSeparate]:checked").length;
			var formatSetting=getDecimals();
			if(thousandSeparateLength==1){
				formatSetting="#,##"+formatSetting;
			}
			return formatSetting;
		}

		/**
		 * 清除格式设置
		 */
		function clearAwayFormat(){
			parent.$("#"+"${currentInputId}").attr("value","");
			parent.$.colorbox.close();
		}

		/**
		 * 解析格式设置
		 */
		function analysis(){
			var formatSetting=parent.$("#"+"${currentInputId}").attr("value");
			if(formatSetting!=''){
				if(formatSetting.indexOf('$#,##')!=-1){
					$("#classify").attr("value","CURRENCY");
					if(formatSetting.indexOf(".")!=-1){
						$("#decimals").attr("value",formatSetting.length-7);
					}
					$("#currencySign").attr("value","$");
				}else if(formatSetting.indexOf('￥#,##')!=-1){
					$("#classify").attr("value","CURRENCY");
					if(formatSetting.indexOf(".")!=-1){
						$("#decimals").attr("value",formatSetting.length-7);
					}
					$("#currencySign").attr("value","￥");
				}else if(formatSetting.indexOf("%")!=-1){
					$("#classify").attr("value","PERCENT");
					if(formatSetting.indexOf(".")!=-1){
						$("#decimals").attr("value",formatSetting.length-3);
					}
				}else if(formatSetting=="yyyy-m-d"){
					$("#classify").attr("value","DATE");
					$("#dateType").attr("value","yyyy-m-d");
				}else if(formatSetting=="yyyy-m-d hh:mm:ss"){
					$("#classify").attr("value","DATE");
					$("#dateType").attr("value","yyyy-m-d hh:mm:ss");
				}else if(formatSetting=="yyyy-m"){
					$("#classify").attr("value","DATE");
					$("#dateType").attr("value","yyyy-m");
				}else if(formatSetting=="m-d"){
					$("#classify").attr("value","DATE");
					$("#dateType").attr("value","m-d");
				}else if(formatSetting=="yyyy年m月d日"){
					$("#classify").attr("value","DATE");
					$("#dateType").attr("value","yyyy年m月d日");
				}else if(formatSetting=="yyyy年m月d日hh时mm分ss秒"){
					$("#classify").attr("value","DATE");
					$("#dateType").attr("value","yyyy年m月d日hh时mm分ss秒");
				}else if(formatSetting=="yyyy年m月"){
					$("#classify").attr("value","DATE");
					$("#dateType").attr("value","yyyy年m月");
				}else if(formatSetting=="m月d日"){
					var classify=$("#classify").attr("value","DATE");
					$("#dateType").attr("value","m月d日");
				}else if(formatSetting=="h:mm"){
					$("#classify").attr("value","TIME");
					$("#timeType").attr("value","h:mm");
				}else if(formatSetting=="h:mm:ss"){
					$("#classify").attr("value","TIME");
					$("#timeType").attr("value","h:mm:ss");
				}else if(formatSetting=="h时mm分"){
					$("#classify").attr("value","TIME");
					$("#timeType").attr("value","h时mm分");
				}else if(formatSetting=="h时mm分ss秒"){
					$("#classify").attr("value","TIME");
					$("#timeType").attr("value","h时mm分ss秒");
				}else if(formatSetting.indexOf("func:")!=-1){
					$("#classify").attr("value","CUSTOM");
					var functionName=formatSetting.replace("func:","");
					$("#functionName").attr("value",functionName);
				}else{
					var classify=$("#classify").attr("value","NUMERICAL_VALUE");
					if(formatSetting.indexOf("#,##")!=-1){
						if(formatSetting.indexOf(".")!=-1){
							$("#decimals").attr("value",formatSetting.length-6);
						}
						$("#thousandSeparate").attr("checked","checked");
					}else{
						if(formatSetting.indexOf(".")!=-1){
							$("#decimals").attr("value",formatSetting.length-2);
						}
					}
				}
			}
			selectType();
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="saveFormatSetting();"><span><span>确定</span></span></button>
		<button class="btn" onclick='clearAwayFormat();'><span><span >清空</span></span></button>
	</div>
	<div id="opt-content">
		<aa:zone name="controlContent">
			<form name="textForm" id="textForm" action="${mmsCtx }/form/form-view!text.htm">
				<table class="form-table-without-border">
				<tbody>
					<tr>
						<td class="content-title">分类：</td>
						<td>
							<select id="classify" onchange="selectClassify();">
								<s:iterator value="@com.norteksoft.mms.form.enumeration.Classify@values()" var="classifyVar">
									<option <s:if test="#classifyVar==classify">selected="selected"</s:if> value="${classifyVar}"><s:text name="%{code}"></s:text></option>
								</s:iterator>
							</select>
						</td>
						<td>
						</td>	
					</tr>	
					<tr id="decimalsTr"  style="display: none">
						<td class="content-title">小数位数：</td>
						<td>
						<input id="decimals" style="width: 146px;"/>
						</td>
						<td></td>	
					</tr>
					<tr id="thousandSeparateTr"  style="display: none">
						<td class="content-title" style="width: 146px;">使用千位分隔符（,）：</td>
						<td>
							<input id="thousandSeparate" name="thousandSeparate" type="checkbox">
						</td>
						<td></td>	
					</tr>
					<tr id="currencySignTr" style="display: none">
						<td class="content-title">货币符号：</td>
						<td>
							<select id="currencySign">
								<option value="￥">￥</option>
								<option value="$">$</option>
							</select>
						</td>
						<td></td>	
					</tr>
					<tr id="dateTypeTr" style="display: none">
						<td class="content-title">类型：</td>
						<td>
							<select id="dateType">
								<option value="yyyy-m-d">yyyy-m-d</option>
								<option value="yyyy-m-d hh:mm:ss">yyyy-m-d hh:mm:ss</option>
								<option value="yyyy-m">yyyy-m</option>
								<option value="m-d">m-d</option>
								<option value='yyyy年m月d日'>yyyy年m月d日</option>
								<option value='yyyy年m月d日hh时mm分ss秒'>yyyy年m月d日hh时mm分ss秒</option>
								<option value='yyyy年m月'>yyyy年m月</option>
								<option value='m月d日'>m月d日</option>
							</select>
						</td>
						<td></td>	
					</tr>
					<tr id="timeTypeTr" style="display: none">
						<td class="content-title">类型：</td>
						<td>
							<select id="timeType">
								<option value="h:mm">h:mm</option>
								<option value="h:mm:ss">h:mm:ss</option>
								<option value='h时mm分'>h时mm分</option>
								<option value='h时mm分ss秒'>h时mm分ss秒</option>
							</select>
						</td>
						<td></td>	
					</tr>
					<tr id="functionNameTr"  style="display: none">
						<td class="content-title">方法名：</td>
						<td>
						<input id="functionName" style="width: 146px;"/>
						</td>
						<td></td>	
					</tr>
				</tbody>
			</table>
			</form>
				
		</aa:zone>
	</div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
