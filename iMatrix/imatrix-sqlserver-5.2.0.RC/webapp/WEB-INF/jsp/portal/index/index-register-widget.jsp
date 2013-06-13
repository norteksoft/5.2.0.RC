<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/common/portal-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>注册小窗体</title>
	<%@ include file="/common/portal-meta.jsp"%>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/jqgrid/ui.jqgrid.css" />
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqgrid-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqGrid.custom.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/public.js"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js" ></script>
	<script src="${portalCtx}/js/index.js" type="text/javascript"></script>
	<script type="text/javascript">
	function changeSystem(id){
		$("div[id^='bs_']").removeClass('west-notree-selected');
		$('#bs_'+id).addClass('west-notree-selected');
		$("#systemId").attr("value",id);
		ajaxSubmit("defaultForm",webRoot+"/index/show-system-widget.htm", "widget-zones");
	}
	function createWidget(){
		$("#widgetId").attr("value","");
		ajaxSubmit("defaultForm",webRoot+"/index/register-widget-input.htm", "widget-zones",validateWidget);
	}

	function validateWidget(){
		$("#inputForm").validate({
			submitHandler: function() {
				$("#system_id").attr("value",$("#systemId").attr("value"));
				var canSave=iMatrix.getFormGridDatas("inputForm","parameterGrid");
				if(canSave){
					ajaxSubmit("inputForm",webRoot+"/index/save-widget.htm", "widget-zones",saveCallback);
				}
			},
			rules:{
				code:"required",
				name:"required",
				url:"required",
				roleNames:"required"
			},
			messages: {
				code:"必填",
				name:"必填",
				url:"必填",
				roleNames:"必填"
			}
		});
	}

	function saveCallback(){
		showMsg();
		validateWidget();
	}

	function backWidget(){
		ajaxSubmit("backForm",webRoot+"/index/show-system-widget.htm", "widget-zones");
	}

	//小窗体是否存在
	function isWidgetExist(){
		$.ajax({
			   type: "POST",
			   url: "validate-widget.htm",
			   data: "widgetCode="+$("#code").attr("value")+"&widgetId="+$($("form[id='inputForm']").find("input[id='widgetId']")[0]).attr("value"),
			   success: function(msg){
				   if(msg=="true"){
					  alert("该窗口编号已存在");
				   }else{
					   $("#inputForm").submit();
				   }
			   }
		});
	}
	
	function saveWidget(){
		if($("#code").attr("value")==""){
			$("#inputForm").submit();
		}else{
			isWidgetExist();
		}
		
	}

	function updateWiget(){
		var ids=jQuery("#widgetPage").getGridParam('selarrrow');
		if(ids.length<=0){
			alert("请选择需要编辑的记录！");
			return;
		}else if(ids.length>1){
			alert("请不要选择多条记录！");
			return;
		}else{
			$("#widgetId").attr("value",ids[0]);
			ajaxSubmit("defaultForm",webRoot+"/index/register-widget-input.htm", "widget-zones",validateWidget);
		}
	}

	function validateDeleteWidget(){
		var ids=jQuery("#widgetPage").getGridParam('selarrrow');
		if(ids.length<=0){
			alert("请选择需要删除的记录！");
			return;
		}
		$("#registerWidgetIds").attr("value",ids.join(","));
		$.ajax({
			   type: "POST",
			   url: "validate-delete-widget.htm",
			   data: "registerWidgetIds="+ids.join(","),
			   success: function(msg){
				   setPageState();
				   if(msg==""){
					  deleteWiget();
				   }else{
					   if(confirm(msg+"窗体正被使用,确定删除吗?")){
						   deleteWiget();
					   }
				   }
			   }
		});
	}

	function deleteWiget(){
		ajaxSubmit("defaultForm",webRoot+"/index/delete-widget.htm", "widget-zones");
	}

	function addRole(){
		custom_tree({url:webRoot+'/index/role-tree.htm',
			onsuccess:function(){closeFun();},
			width:300,
			height:400,
			title:'选择角色',
			postData:{systemId:$("#systemId").attr("value")},
			nodeInfo:['type','roleId','roleName'],
			multiple:true,
			webRoot:imatrixRoot
		});
	}

	function closeFun(){
		$("#roleIds").attr("value","");
		$("#roleNames").attr("value","");
		var roleIds=getSelectValue("roleId");
		if(roleIds!=""&&roleIds.length>0){
			if(roleIds.length==1){
				$("#roleIds").attr("value",roleIds[0]);
				var roleNames=getSelectValue("roleName");
				$("#roleNames").attr("value",roleNames);
			}else{
				$("#roleIds").attr("value",roleIds.join(","));
				var roleNames=getSelectValue("roleName");
				$("#roleNames").attr("value",roleNames.join(","));
			}
		}else{
			alert("请选择角色");
		}
		
	}

	function defaulteChecked(obj){
		$("#defaulted").attr("value",$(obj).attr("checked"));
	}
	
	function changePageVisible(obj){
		$("#pageVisible").attr("value",$(obj).attr("checked"));
	}
	function changeBorderVisible(obj){
		$("#borderVisible").attr("value",$(obj).attr("checked"));
	}
	function changeIframeable(obj){
		$("#iframeable").attr("value",$(obj).attr("checked"));
	}
	
	function setParamValue(cellvalue, options, rowObject){
       return "";
	}
	 function optionGroupNameClick(obj){
		 custom_tree({url:webRoot+'/index/option-group-tree.htm',
				onsuccess:function(){optionCloseFun(obj);},
				width:300,
				height:400,
				title:'选择角色',
				nodeInfo:['typeInfo','id','name'],
				multiple:false,
				webRoot:imatrixRoot
			});
		}
		
		function optionCloseFun(obj){
			var id=getSelectValue("id");
			var name=getSelectValue("name");
			$("#"+obj.rowid+"_optionGroupId").attr("value",id);
			$("#"+obj.currentInputId).attr("value",name);
		}
	
	</script>
</head>
<body>
<form action="" name="defaultForm" id="defaultForm" method="post">
	<input name="systemId" id="systemId" type="hidden"></input>
	<input name="widgetId" id="widgetId" type="hidden"></input>
	<input name="registerWidgetIds" id="registerWidgetIds" type="hidden"></input>
</form>
	<div class="ui-layout-west">
		<div style="display: block; height: 10px;"></div>
		<s:iterator value="businessSystems">
			<div id="bs_${id }" class="west-notree" onclick="changeSystem('${id}');"><a>${name }</a></div>
		</s:iterator>
	</div>
	<div class="ui-layout-center">
		<div class="opt-body">
			<aa:zone name="widget-zones">
				<div class="opt-btn">
					<button class="btn" onclick='createWidget();' id="create"><span><span >新建</span></span></button>
					<button class="btn" onclick="updateWiget();"><span><span >修改</span></span></button>
					<button class="btn" onclick="validateDeleteWidget();"><span><span >删除</span></span></button>
				</div>
				<aa:zone name="viewTable">
					<div id="opt-content">
						<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
						<form action="${portalCtx}/index/show-system-widget.htm?systemId=${systemId }" name="pageForm" id="pageForm" method="post">
							<grid:jqGrid url="${portalCtx}/index/show-system-widget.htm?systemId=${systemId }" code="PORTAL_WIDGET" gridId="widgetPage" pageName="widgetPage" ></grid:jqGrid>
						</form>
					</div>
				</aa:zone>
			</aa:zone>
		</div>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>