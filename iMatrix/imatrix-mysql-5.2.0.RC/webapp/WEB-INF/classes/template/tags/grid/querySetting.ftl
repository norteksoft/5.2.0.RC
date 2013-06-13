<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>查询设置</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    
	<script type="text/javascript" src="${resourceCtx}/js/jquery-all-1.0.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/widgets/colorbox/colorbox.css" />
	
	
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/widgets/jqgrid/ui.jqgrid.css" />
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/css/black/jquery-ui-1.8.16.custom.css" id="_style"/>
	<script type="text/javascript">
		$(document).ready(function(){
			analysis();
		});

		function selectQueryType(){
			var queryType=$("#queryType").val();
			if('NONE'==queryType){
				$("#eventTypeTr").hide();
				$("#eventType").attr("value","");
			}else{
				$("#eventTypeTr").show();
				$("#eventType").attr("value","");
			}
		}

		/**
		 * 保存查询设置
		 */
		function savequerySetting(){
			var queryType=$("#queryType").val();
			var querySettingName="";
			var querySettingValue="";
			if(queryType=="FIXED"){//普通查询
				querySettingName='普通查询';
				querySettingValue=setQuerySettingValue('FIXED',$("#eventType").val());
			}else if(queryType=="CUSTOM"){//高级查询
				querySettingName='高级查询';
				querySettingValue=setQuerySettingValue('CUSTOM',$("#eventType").val());
			}else if(queryType=="NONE"){//不查询
				querySettingName='不查询';
				querySettingValue='NONE';
			}
			var currentInputId="${currentInputId}";
			var rowid=currentInputId.substring(0,currentInputId.lastIndexOf("_"));
			parent.$("#"+currentInputId).attr("value",querySettingName);
			parent.$("#"+rowid+"_querySettingValue").attr("value",querySettingValue);
			parent.jQuery("#listColumnId").jqGrid('setCell',rowid,"querySettingValue",querySettingValue);
			parent.$.colorbox.close();
		}
		
		function setQuerySettingValue(queryType,eventType){
			if('' != $("#eventType").val())
					queryType+=','+eventType;
			return queryType;
		}

		/**
		 * 解析格式设置
		 */
		function analysis(){
			var currentInputId="${currentInputId}";
			var rowid=currentInputId.substring(0,currentInputId.lastIndexOf("_"));
			var querySettingValue=parent.jQuery("#listColumnId").jqGrid('getCell',rowid,"querySettingValue");
			if(querySettingValue!=''){
				var querySetting=querySettingValue.split(',');
				if(querySetting.length>1){
					setQuerySetting(querySetting[0],querySetting[1]);
				}else{
					setQuerySetting(querySetting[0],'');
				}
			}
				
		}
		
		function setQuerySetting(queryType,eventType){
			if('NONE'==queryType){
				$("#eventTypeTr").hide();
			}
			$("#queryType").attr("value",queryType);
			$("#eventType").attr("value",eventType);
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body>
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" onclick="savequerySetting();"><span><span>确定</span></span></button>
	</div>
	<div id="opt-content">
		<table class="form-table-without-border">
			<tbody>
				<tr>
					<td class="content-title">查询类型：</td>
					<td>
						<select id="queryType" onchange="selectQueryType();">
							<option value="FIXED">普通查询</option>
							<option value="CUSTOM">高级查询</option>
							<option value="NONE">不查询</option>
						</select>
					</td>
				</tr>	
				<tr id="eventTypeTr">
					<td class="content-title">事件类型：</td>
					<td>
						<select id="eventType">
							<option value="">请选择</option>
							<option value="ONCLICK">单击事件</option>
							<option value="ONCHANGE">下拉框切换</option>
							<option value="BLUR">失去焦点</option>
						</select>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourceCtx}/widgets/colorbox/jquery.colorbox.js"></script>
</html>
