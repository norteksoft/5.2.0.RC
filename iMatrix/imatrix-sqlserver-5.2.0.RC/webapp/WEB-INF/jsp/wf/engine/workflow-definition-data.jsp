<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>工作流管理</title>
<%@ include file="/common/wf-iframe-meta.jsp"%>
<script src="${wfCtx }/js/workflow-definition.js" type="text/javascript"></script>

<script src="${wfCtx }/js/util.js" type="text/javascript"></script>

<script src="${wfCtx }/js/workflow.js" type="text/javascript"></script>

<script src="${imatrixCtx}/widgets/workflowEditor/rightClick.js" type="text/javascript"></script>
<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>

<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>

<script type="text/javascript">
	function option(opt, id){
		if(opt == "add"){
			if($("#wf_type").val()==""){
				$("#message").html("<font class=\"onError\"><nobr>请先选择具体类型</nobr></font>");
				showMsg("message");
				return;
			}
			$.colorbox({href:webRoot+'/engine/workflow-definition!template.htm?type='+$("#wf_type").val(),iframe:true, innerWidth:730, innerHeight:420,overlayClose:false,title:"选择模版"});
		}else if(opt == "deploy"){
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				$("#message").html("<font class=\"onError\"><nobr>请选择流程</nobr></font>");
				showMsg("message");
				return;
			}else if(ids.toString().indexOf(',')>0){
				$("#message").html("<font class=\"onError\"><nobr>只能启用/禁用一个流程</nobr></font>");
				showMsg("message");
				return;
			}
			var state=$("#main_table").jqGrid('getCell',ids,"enable");
			var message = "" 
			if(state=='DISABLE' || state=='DRAFT'){
				message="是否启用流程?";
			}else if(state=='ENABLE'){
				message="是否禁用流程?";
			}
			if(confirm(message)){
				$.post(webRoot+"/engine/workflow-definition!deploy.htm?wfdId="+ids, "", function(data) {
	            	$("#message").html(data);
					showMsg("message");
	    			jQuery("#main_table").jqGrid().trigger("reloadGrid");
	    		});
			}
		}else if(opt == "delete"){
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				$("#message").html("<font class=\"onError\"><nobr>请选择流程</nobr></font>");
				showMsg("message");
				return;
			}else{
                if(confirm("确定要删除？")){
                    var prmt = '';
                    for(var i=0;i<ids.length;i++){
                        if(prmt != '') prmt += '&';
                        prmt+=('wfdIds='+ids[i]);
                    }
                    $.post(webRoot+"/engine/workflow-definition!delete.htm?"+prmt, "", function(data) {
                    	$("#message").html("<font class=\"onError\"><nobr>"+data+"</nobr></font>");
        				showMsg("message");
        				setPageState();
            			jQuery("#main_table").jqGrid().trigger("reloadGrid");
            		});
                }else{
                    return;
                }
			}
		}else if(opt == "update" || opt=="view"){
			$("#wfd_option").attr("value",opt);
			if(opt == "view"){
				$("#wfd_Id").attr("value", id);
				ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition!update.htm","wfd_main",showViewFlash);
			}else{
				var ids = jQuery("#main_table").getGridParam('selarrrow');
				if(ids==''){
					$("#message").html("<font class=\"onError\"><nobr>请选择流程</nobr></font>");
					showMsg("message");
					return;
				}else if(ids.toString().indexOf(',')>0){
					$("#message").html("<font class=\"onError\"><nobr>只能选择一条</nobr></font>");
					showMsg("message");
					return;
				}
				var enable = $("#enable_" + $("#wfd_Id").attr("value")).attr("value");
				if(enable == 2|| enable == 3){
					if(!confirm("流程已经部署，将增加新版本?")){
						return;
					}
				}
				$("#wfd_Id").attr("value", ids);
				ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition!update.htm","wfd_main",showUpdateFlash);
			}
		}else if(opt == "updateBasic"){
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				$("#message").html("<font class=\"onError\"><nobr>请选择流程</nobr></font>");
				showMsg("message");
				return;
			}else if(ids.toString().indexOf(',')>0){
				$("#message").html("<font class=\"onError\"><nobr>只能选择一条</nobr></font>");
				showMsg("message");
				return;
			}
			$("#wfd_Id").attr("value", ids);
			ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition-basic-input.htm","wfd_main",validateBasic);
		}
	}

	function monitor(){
		var ids = jQuery("#main_table").getGridParam('selarrrow');
		if(ids==''){
			$("#message").html("<font class=\"onError\"><nobr>请选择流程</nobr></font>");
			showMsg("message");
			return;
		}else if(ids.toString().indexOf(',')>0){
			$("#message").html("<font class=\"onError\"><nobr>只能选择一条</nobr></font>");
			showMsg("message");
			return;
		}
		$("#wfdId").attr("value", ids);
		ajaxSubmit('wf_form', '${wfCtx}/engine/workflow-definition!monitor.htm', 'wfd_main',initBtnGroup); 
	}
	
	function intoInput(templateId,typeId){
		$("#templateId").attr("value",templateId);
		if(typeof(typeId)!="undefined"){
			$("#wf_type").attr("value",typeId);
		}
		ajaxSubmit("defaultForm","${wfCtx}/engine/workflow-definition!input.htm","wfd_main",showFlash);
	}

	function showFlash(){
		resizeFlasContent();
		parent.hideWestAndNorth();
		addSWf("add");
	}

	function showViewFlash(){
		$("#flashcontent").height($(window).height()+32);
		parent.hideWestAndNorth();
		addSWf("view");
	}

	function showUpdateFlash(){
		resizeFlasContent();
		parent.hideWestAndNorth();
		addSWf("update");
	}

	function resizeFlasContent(){
		$("#flashcontent").height($(window).height()+64);
	}

	function flexReturn(){
		goBackWfd("wfdForm","${wfCtx}/engine/workflow-definition-data.htm","wfd_main","wfdPage");
	}

	//返回调用,保持页数。
	function goBackWfd(form,url,zone,jemesaId){
		ajaxSubmit(form, url, zone, goBackCallback);
	}
	
	function goBackCallback(){
		parent.showWestAndNorth();
	}

	function monitorGoBack(){
		ajaxSubmit("wf_form","${wfCtx}/engine/workflow-definition-data.htm","wf_definition");
	}

	function viewWorkflow(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"option('view', "+opts.id+");\">" + ts1 + "</a>";
		return v;
	}

	//选择陪同人员
	function selectPerson(){
		var acsSystemUrl = "${wfCtx}";
		popTree({ title :'选择人员',
			innerWidth:'400',
			treeType:'MAN_DEPARTMENT_TREE',
			defaultTreeValue:'id',
			leafPage:'false',
			multiple:'false',
			hiddenInputId:"adminLoginName",
			showInputId:"adminName",
			acsSystemUrl:imatrixRoot,
			callBack:function(){selectPersonCallBack();}});
	}

	function selectPersonCallBack(){
		$('#adminLoginName').attr("value",jstree.getLoginName());
	}

	function validateBasic(){
		$.validator.addMethod("typeRequired", function(value, element) {
			var $element = $(element);
			if($element.val()=='请选择类型'||$element.val()==''||$element.val()=='0'){
				return false;
			}
			return true;
		}, "必填");
		$.validator.addMethod("systemRequired", function(value, element) {
			var $element = $(element);
			if($element.val()=='请选择系统'||$element.val()==''||$element.val()=='0'){
				return false;
			}
			return true;
		}, "必填");
		$("#inputForm").validate({
		submitHandler: function() {
			saveBasic();
		},
		rules: {
			name:"required",
			adminName:"required"
		},
		messages: {
			name:"必填",
			adminName:"必填"
		}
	});
	}

	function submitBasic(){
		$("#inputForm").submit();
	}

	function saveBasic(){
		ajaxSubmit("inputForm", "${wfCtx}/engine/workflow-definition-save-basic.htm", "wfd_main", saveFormCallBack);
	}

	function saveFormCallBack(){
		validateBasic();
		showMsg("message");
	}

	function basicGoBack(){
		ajaxSubmit("inputForm","${wfCtx}/engine/workflow-definition-data.htm","wfd_main");
	}
</script>
</head>
<body>
<div class="ui-layout-center">
<form id="defaultForm" action="" name="defaultForm" method="post">
	<input type="hidden" id="templateId" name="templateId" value=""/>
	<input id="wf_type" name="type" type="hidden" value="${type}"/>
	<input id="system_id" name="sysId" type="hidden" value="${sysId}"/>
	<input id="vertion_type" name="vertionType" type="hidden" value="${vertionType}"/>
	<input type="hidden" name="wfdId" id="wfd_Id" value="" />
	<input type="hidden" name="option" id="wfd_option" value="" />
</form>
<div class="opt-body">
	<aa:zone name="wfd_main">
		<div class="opt-btn">
			<button class='btn' onclick="iMatrix.showSearchDIV(this);" hidefocus="true"><span><span>查询</span></span></button>
			<button  class='btn' onclick="option('add');" hidefocus="true"><span><span>增加</span></span></button>
			<button class='btn' onclick="option('update');" hidefocus="true"><span><span>修改</span></span></button>
			<button class='btn' onclick="option('updateBasic');" hidefocus="true"><span><span>修改基本属性</span></span></button>
			<button class='btn' onclick="option('delete');" hidefocus="true"><span><span>删除</span></span></button>
			<button class='btn' onclick="option('deploy');" hidefocus="true"><span><span>启用/禁用</span></span></button>
			<button class='btn' onclick="monitor();" hidefocus="true"><span><span>流程监控</span></span></button>
		</div>
		<div id="opt-content" >
		<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
		<form id="wf_form" name="wf_form" method="post">
			<input type="hidden" name="wfdId" id="wfdId" value="" />
			<input type="hidden" name="type" id="type" value="${type}" />
			<input id="systemwf_id" name="sysId" type="hidden" value="${sysId}" />
            <input id="vertionwf_type" name="vertionType" value="${vertionType}" type="hidden"/>
		</form>
		<form name="dataForm" id="dataForm" method="post" action="">
			<form name="dataForm" id="dataForm" method="post" action="">
				<view:jqGrid url="${wfCtx}/engine/workflow-definition-data.htm?sysId=${sysId}&vertionType=${vertionType}&type=${type}" 
					pageName="wfdPage" code="WF_DEFINITION" gridId="main_table"></view:jqGrid>
			</form>
		</form>
		</div>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>