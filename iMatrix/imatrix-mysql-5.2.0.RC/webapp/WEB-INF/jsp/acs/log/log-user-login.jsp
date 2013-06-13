<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
   <head>
   
	<title><s:text name="decorators.logManager"/></title>
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
	function deleteUserLogin(){
		var uIds = getSelectedIds();
   		if(uIds == "" || uIds.length<=0){
  			alert('<s:text name="common.selectOne"/>');
  			return;
  		}else{
			if(!confirm("确定删除吗？")){ return; }
		}

		var url="${acsCtx}/log/log!deleteUserLoginLog.action";
  		$.ajax({
			data:{loginLogIds:uIds},
			type:"post",
			url:url,
			beforeSend:function(XMLHttpRequest){},
			success:function(data, textStatus){
				setPageState();
	 			$("#log_form").attr("action", "${acsCtx}/log/log!lookUserLoginLog.action");
	 	   		ajaxAnywhere.formName = "log_form";
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

    function deleteAllUserLogin(){
  	   if(!confirm("确定全部删除吗？")){ return; }
   		var url="${acsCtx}/log/log!deleteUserLoginLog.action";
  		$.ajax({
			data:{deleteAll:"yes"},
			type:"post",
			url:url,
			beforeSend:function(XMLHttpRequest){},
			success:function(data, textStatus){
				setPageState();
	 			$("#log_form").attr("action", "${acsCtx}/log/log!lookUserLoginLog.action");
	 	   		ajaxAnywhere.formName = "log_form";
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
  //复选框至少有一个被选中
    function getSelectedIds() {
    	var ids = jQuery("#login_log_table").getGridParam('selarrrow');
       var result = "";
       for(var i = 0; i < ids.length; i++){
           result += ids[i];
           result += ',';
       }
       return result;
	}
	function _exportLoginLog(){
		if($('#login_log_table').children('tbody').children('tr').length<=1){
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
		var coll=jQuery("#"+editableGridOptions.gridId).jqGrid("getGridParam", "postData");
		input += '<input type="hidden" name="_list_code" value="'+$(coll).attr('_list_code')+'"> ';
		$('#exportForm').html(input);
		$('#exportForm').submit();
	}
	function _format_data(ts,content,opts){
      if(ts=='1970-01-01 08:00'){
        return "&nbsp;";
      }
      return ts;
	}
	</script>
	
	
</head>

<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<div class="opt-btn">
			<security:authorize ifAnyGranted="userLoginLog"><button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button></security:authorize>
			<security:authorize ifAnyGranted="delete_loginLog"><button  class='btn' onclick="deleteUserLogin();"><span><span>删除</span></span></button></security:authorize>
			<security:authorize ifAnyGranted="delete_loginLog"><button  class='btn' onclick="deleteAllUserLogin();"><span><span>删除所有</span></span></button></security:authorize>
			<security:authorize ifAnyGranted="acs_log_export_login_log"><button  class='btn' onclick="_exportLoginLog();"><span><span>导出</span></span></button></security:authorize>
		</div>
		<div id="opt-content">
			<form id="defaultForm" name="defaultForm"action="" method="post" ></form>
			<aa:zone name="acs_content">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<div style="display: none;"><form target="_blank" action="${acsCtx}/log/log!exportLoginLog.action" id="exportForm" name="exportForm"></form></div>
			    <form id="ajax_from" name="ajax_from" action="" method="post"></form>
			    <form id="log_form" name="log_form" action="" method="post">
				    <input type="hidden" id="loginLogIds" name="loginLogIds" value=""></input>
				    <input type="hidden" id="deleteAll" name="deleteAll" value=""></input>
			    	<view:jqGrid url="${acsCtx}/log/log!lookUserLoginLog.action" code="ACS_LOG_LOGIN" gridId="login_log_table" pageName="userLoginPage"></view:jqGrid>
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