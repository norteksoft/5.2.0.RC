<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
   
	<title><s:text name="role.roleManager"/></title>
    <%@ include file="/common/acs-iframe-meta.jsp"%>
    <script  type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	
	<script type="text/javascript">
	//通用消息提示
	function showMessage(id, msg){
		if(msg != ""){
			$("#"+id).html(msg);
		}
		$("#"+id).show("show");
		setTimeout('$("#'+id+'").hide("show");',3000);
	}
	//复选框至少有一个被选中
    function getSelectedLogIds() {
    	var ids = jQuery("#log_table").getGridParam('selarrrow');
       var result = "";
       for(var i = 0; i < ids.length; i++){
          result += ids[i];
          result += ',';
       }
       return result;
	}
   //删除系统日志
	function deleteSysLogin(){
		var uIds = getSelectedLogIds();
		var sysId=$("#sysId").attr("value");
		if(sysId==""){
			sysId=$("#systemId").attr("value");
		}
   		if(uIds == "" || uIds.length<=0){
 			alert('<s:text name="common.selectOne"/>');
 			return;
 		}else{
             if(!confirm("确定删除吗？")){ return; }
		}

 		var url="${acsCtx}/log/log!deleteSysLoginLog.action";
  		$.ajax({
			data:{sysId:$("#sysId").val(),syIds:uIds+"="+sysId},
			type:"post",
			url:url,
			beforeSend:function(XMLHttpRequest){},
			success:function(data, textStatus){
				setPageState();
	 			$("#sys_Log").attr("action", "${acsCtx}/log/log-data.action?sysId="+$("#sysId").val());
	 	   		ajaxAnywhere.formName = "sys_Log";
	 	 		ajaxAnywhere.getZonesToReload = function() {
	 	 			return "acs_content";
	 	 		};
	 	 		ajaxAnywhere.onAfterResponseProcessing = function() {
	 	 			showMessage("message", "<font color=\"green\">"+data+"</font>");
	 	 		};
	 	 		ajaxAnywhere.submitAJAX();
			},
			complete:function(XMLHttpRequest, textStatus){},
	        error:function(){

			}
		});
	}
	 
	//删除所有系统日志
	function deleteAllSysLogin(){
	  	 var sysId=$("#sysId").attr("value");
	  	 if(sysId==""){
          	sysId=$("#systemId").attr("value");
	   	 }
	   	if(!confirm("确定删除吗？")){
           return;
        }
 		var url="${acsCtx}/log/log!deleteSysLoginLog.action";
  		$.ajax({
			data:{sysId:$("#sysId").val(),deleteAllSysLog:"yes",syIds:sysId},
			type:"post",
			url:url,
			beforeSend:function(XMLHttpRequest){},
			success:function(data, textStatus){
				setPageState();
	 			$("#sys_Log").attr("action", "${acsCtx}/log/log-data.action?sysId="+$("#sysId").val());
	 	   		ajaxAnywhere.formName = "sys_Log";
	 	 		ajaxAnywhere.getZonesToReload = function() {
	 	 			return "acs_content";
	 	 		};
	 	 		ajaxAnywhere.onAfterResponseProcessing = function() {
	 	 			showMessage("message", "<font color=\"green\">"+data+"</font>");
	 	 		};
	 	 		ajaxAnywhere.submitAJAX();
			},
			complete:function(XMLHttpRequest, textStatus){},
	        error:function(){

			}
		});
	}
	function _exportLog(){
		if($('#log_table').children('tbody').children('tr').length<=1){
			alert('没有可以导出的结果');
			return;
		}
		var searchParameters = $("#__search_parameters").val();
		var input = "";
		if(searchParameters!=""&&searchParameters!=null&&searchParameters!=undefined){
			input = "<input type='hidden' name='searchParameters' value='"+searchParameters+"'/>";
		}else{
			input = "";
		}
		input += '<input type="hidden" name="sysId" value="'+$('#sysId').attr('value')+'"> ';
		var coll=jQuery("#"+editableGridOptions.gridId).jqGrid("getGridParam", "postData");
		input += '<input type="hidden" name="_list_code" value="'+$(coll).attr('_list_code')+'"> ';
		$('#exportForm').html(input);
		$('#exportForm').submit();
	}
	</script>
</head>

<body>
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<security:authorize ifAnyGranted="searchLog"><button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button></security:authorize>
		<security:authorize ifAnyGranted="delete_sysLogs"><button  class='btn' onclick="deleteSysLogin();"><span><span>删除</span></span></button></security:authorize>
		<security:authorize ifAnyGranted="delete_sysLogs"><button  class='btn' onclick="deleteAllSysLogin();"><span><span>删除所有</span></span></button></security:authorize>
		<security:authorize ifAnyGranted="acs_export_log"><button  class='btn' onclick="_exportLog();"><span><span>导出</span></span></button></security:authorize>
	</div>
	<div id="opt-content">
		<aa:zone name="acs_content">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<div style="display: none;"><form target="_blank" action="${acsCtx}/log/log!export.action" id="exportForm" name="exportForm"></form></div>
			<form id="ajax_from" name="ajax_from" action="" method="post">
		        <input type="hidden" id="sysId" name="sysId" value="${sysId}">
		    </form >
			<form id="sys_Log" name="sys_Log" action=""  method="post">
			    <input id="syIds" name="syIds" type="hidden" value=""></input>
			    <input id="deleteAllSysLog" name="deleteAllSysLog" type="hidden" value=""></input>
				<view:jqGrid url="${acsCtx}/log/log-data.action?sysId=${sysId}" code="ACS_LOGS" gridId="log_table" pageName="page"></view:jqGrid>
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
	