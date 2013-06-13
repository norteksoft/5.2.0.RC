<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表字段</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"></script>
	<script src="${mmsCtx}/js/authority-data-rule.js" type="text/javascript"></script>
	<script type="text/javascript">
		function submitDataRule(){
			var id = jQuery("#tableColumnGrid").getGridParam('selrow');
			if(id==null){
				alert("请选择一条数据！");
			}else{
				var currentInputId="${currentInputId }";
				var conditionId=currentInputId.substring(0,currentInputId.lastIndexOf("_"));
				var dataType=jQuery("#tableColumnGrid").jqGrid('getCell',id,"dataType");
				window.parent.$("#"+currentInputId).attr("value",jQuery("#tableColumnGrid").jqGrid('getCell',id,"alias"));
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"field",jQuery("#tableColumnGrid").jqGrid('getCell',id,"name"));
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"dataType",jQuery("#tableColumnGrid").jqGrid('getCell',id,"dataType"));
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"enumPath","");
				var objectPath="";
				var enumPath="";
				if(dataType=="ENUM"){
					objectPath = jQuery("#tableColumnGrid").jqGrid('getCell',id,"objectPath");
					if(objectPath==""||typeof(objectPath)=='undefined'){
						var tableColumnId = jQuery("#tableColumnGrid").jqGrid('getCell',id,"id");
						$.ajax({
							data:{tableColumnId:tableColumnId},
							type:"post",
							url:webRoot+"/form/list-column!getValuesetByTableColumn.htm",
							beforeSend:function(XMLHttpRequest){},
							success:function(data, textStatus){
								if(data!=""&&typeof(data)!='undefined' && data.indexOf("enumname")>=0){
									enumPath = data.split(":")[1];
									window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"enumPath",enumPath);
									packagingOperator(dataType,conditionId);
									window.parent.$.colorbox.close();
								}
							},
							complete:function(XMLHttpRequest, textStatus){},
					        error:function(){}
						});
					}else{
						window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"enumPath",objectPath);
						packagingOperator(dataType,conditionId);
						window.parent.$.colorbox.close();
					}
				}else{
					var tableColumnId = jQuery("#tableColumnGrid").jqGrid('getCell',id,"id");
					$.ajax({
						data:{tableColumnId:tableColumnId},
						type:"post",
						url:webRoot+"/form/list-column!getValuesetByTableColumn.htm",
						beforeSend:function(XMLHttpRequest){},
						success:function(data, textStatus){
							if(data.indexOf("enumname:")<0&&data.indexOf("classname:")<0&&data.indexOf("beanname:")<0){//当列表字段信息中值设置不是这几种情况,即是key:'value',...时
								window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"enumPath",data);
								packagingOperator(dataType,conditionId,data);
								window.parent.$.colorbox.close();
							}else{
								packagingOperator(dataType,conditionId);
								window.parent.$.colorbox.close();
							}
						},
						complete:function(XMLHttpRequest, textStatus){},
				        error:function(){}
					});
				}
				
			}
		}

		function $getExtraParams(){
			return {tableId:"${tableId}",currentInputId:"${currentInputId }"};
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" ></form>
	<aa:zone name="main_zone">
		<div class="opt-btn">
			<button class="btn" onclick="submitDataRule();"><span><span >确定</span></span></button>
		</div>
		<div id="opt-content">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="" name="pageForm" id="pageForm" method="post">
				<view:jqGrid url="${mmsCtx}/authority/data-rule-selectColumn.htm" code="MMS_DATA_RULE_TABLE_COLUMN" gridId="tableColumnGrid" pageName="tableColumnPage"></view:jqGrid>
			</form>
		</div>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
