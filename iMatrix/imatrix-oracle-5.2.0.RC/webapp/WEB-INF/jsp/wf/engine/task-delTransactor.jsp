<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>选择办理人</title>
	<%@ include file="/common/wf-colorbox-meta.jsp"%>
	
	<script src="${resourcesCtx}/js/public.js?a=1" type="text/javascript"></script>
	<script src="${wfCtx }/js/wf.js" type="text/javascript"></script>
	
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	<script>
	$(function () {
		userTree();
	});
	function userTree(){  
		$.ajaxSetup({cache:false});
		$("#userTree").jstree({
			"json_data":{
				"ajax" : { "url" : "${wfCtx}/engine/task!delTransactorTree.htm",
							"data" : function (n) {  
								 return { currentId : n!=-1 ? n.attr("id") : 0,workflowId:"${workflowId}" };   
							}
						}
			   },
			   "themes" : {  
				   "theme" : "classic",  
					"dots" : true,  
					"icons" : true 
				}, 
				"types" :{
					 "valid_children" : [ "root" ],  
						"types" : {
							"company" : {
								"icon" : {
									"image" : "${resourcesCtx}/widgets/jstree/themes/root.png"
								}
							},
							"user" : {
								"icon" : {
									"image" : "${resourcesCtx}/widgets/jstree/themes/file.png"
								}
							}
						}
					 }, 
			   "plugins" : [ "themes", "json_data","checkbox","types" ]
			});
		
	}

	function selectUser(){
		var arr=eval(getUserJsonValue(getSelectUsers()));
		$("#transactorForm").append('<input type="hidden" name="workflowId" value="${workflowId}"/>');
		for(var i=0; i<arr.length; i++){
			$("#transactorForm").append('<input type="hidden" name="transactors" value="'+arr[i].loginName+'"/>');
		}
	}

	function getUserJsonValue(arr){
		var info="[";
     	for(var i=0; i<arr.length; i++){
	     	var type=arr[i].substring(0,arr[i].lastIndexOf("_"));
	     	if(type!="company"){
			  info+="{type:"+"\"user\""
			     +",loginName:"+"\""+arr[i].substring(arr[i].indexOf("-")+1,arr[i].length)+"\""
			     +",name:"+"\""+arr[i].substring(arr[i].indexOf("_")+1,arr[i].indexOf("-"))+"\""+"},";
			}
	    }
	    if(info.indexOf(",")>=0){
	    	info=info.substring(0,info.length-1);
	    }
       info+="]";
       return info;
	}

	function getSelectUsers(){
    	var lists = $("#userTree").find("li.jstree-checked");
		var v="" ;
		for(var i=0; i<lists.length; i++){
			v+=$(lists[i]).attr("id");
			if(i!=lists.length-1)
				v+=";";
		}
		if(v!=""){
			var arr=v.split(";");
			return arr;
		}else{
			return "";
		}
	}
	

	//用户树多选 ；
	function OK(){
		var info=getUserJsonValue(getSelectUsers());
		var arr=eval(info);
		if(info!=""){
			if(arr.length=="${transactorNum}"){
				alert("不能把全部办理人都去掉");
			}else{
				$("#transactorForm").append('<input type="hidden" name="workflowId" value="${workflowId}"/>');
				for(var i=0; i<arr.length; i++){
					$("#transactorForm").append('<input type="hidden" name="transactors" value="'+arr[i].loginName+'"/>');
				}
				ajaxSubmit('transactorForm','${wfCtx}/engine/task!delTransactorSave.htm','wf_task',postSuccess);
			}
		}else if(arr.length==0){
			window.parent.$.colorbox.close();
		}
	}
	function postSuccess(){
		window.parent.$.colorbox.close();
	}
	function closeBtn(){
		window.parent.$.colorbox.close();
	}
	</script>
								
</head>
<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
		<form action="" id="pageForm" method="post"></form>
		<aa:zone name="wf_task">
			<s:if test="hasActivitySubProcess">
				<div class="opt-btn">
					<button class='btn' onclick="closeBtn();" hidefocus="true"><span><span>关闭</span></span></button>
				</div>
				<div id="opt-content">
					<font color="red">当前任务为子流程，不能进行减少办理人操作</font>
				</div>
			</s:if>
			<s:elseif test="transactorNum==0">
				<div class="opt-btn">
					<button class='btn' onclick="closeBtn();" hidefocus="true"><span><span>关闭</span></span></button>
				</div>
				<div id="opt-content">
					<div style="margin-top: 5px ;margin-left: 5px;">
						<font color="red">当前流程已结束或已取消或当前任务为并发环节，不能进行减少办理人操作</font>
					</div>
					
				</div>
			</s:elseif>
			<s:elseif test="transactorNum==1">
				<div class="opt-btn">
					<button class='btn' onclick="closeBtn();" hidefocus="true"><span><span>关闭</span></span></button>
				</div>
				<div id="opt-content">
					<div style="margin-top: 5px ;margin-left: 5px;">
						<font color="red">当前任务只有一个办理人,不能再减少</font><br /><br />
						办理人为：${transactor }
					</div>
				</div>
			</s:elseif>
			<s:else>
				<div style="width:auto; padding: 0; margin: 0;text-align: left;" id="leafTree">
					<div class="opt-btn">
						<button class='btn' onclick="OK();" hidefocus="true"><span><span>确定</span></span></button>
					</div>
					<div id="opt-content">
						<div id="userTree" class="demo"></div>
					</div>
				</div>
			</s:else>
			<form id="transactorForm" action="post" name="transactorForm">
			</form>
			</aa:zone>
		</div>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
