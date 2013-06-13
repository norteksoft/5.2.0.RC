<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	<title>委托管理</title>
	
	<script language="javascript" type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<script src="${wfCtx}/js/workflowTag.js"></script>
	<script language="javascript" type="text/javascript" src="${wfCtx}/js/delegate-main.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js" ></script>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"> </script>
	<script type="text/javascript">
	//给隐藏域附值
	function insertInputValue(loginName,name){
		$("#trustee").attr("value",loginName);
		$("#trusteeName").attr("value",name);
	}

	//获取已分配的角色
	function getAssignedRoles(id){
		$.ajax({
			data:{id:id},
			type:"post",
			url:'${ctx}/engine/delegate-main!getRolesByUser.htm',
			success:function(data, textStatus){
				$("#rolesId").html(data);
				var selectRoleName=$("#selectedRoleNames").val();
				var srn=selectRoleName.split(",");
				var rolesName=$($("#rolesId").children()).children("li");
				for(var i=0;i<rolesName.length;i++){
                   for(var j=0 ;j<srn.length;j++){
                       var html=$(rolesName[i]).html();
                       if(html.indexOf(srn[j])>=0){
                    	   $(rolesName[i]).children("input").attr("checked","checked");
                           }
                       }
					}
				
			},
	        error:function(){alert("出错了");}
		});
	}
	//用户选的角色 动态赋值
	function changeCheckbox(roleId){
       var inputValue = $('#'+roleId).attr("value");
       if(inputValue==""||inputValue==undefined){
    	   $('#'+roleId).attr("value",roleId);
       }else{
    	   $('#'+roleId).attr("value","");
       }
	}
	
	function validMsg(message){
		$("#message").html('<font style="color:red;"><nobr>'+message+'</nobr></font>');
		$("#message").show("show");
		setTimeout('$("#message").hide("show");',5000);
	}

	//去多少页 
	function go(url){
		var a =$("#goPage").attr("value");
		var b =$("#totalPages").attr("value");
		if(Number(a)<=Number(b)){
			createForm(url+$("#goPage").attr("value"));
		}
	}

	function ajax(formName, fromId, url, zone, callback){
		$("#"+fromId).attr("action", url);
		ajaxAnywhere.formName = formName;
		ajaxAnywhere.getZonesToReload = function() {
			return zone;
		};
		ajaxAnywhere.onAfterResponseProcessing = function () {
			if(typeof callback == "function"){
				callback();
			}
		};
		ajaxAnywhere.submitAJAX();
	}
	function jmesaSubmit() {
		ajaxSubmit("delegatemainForm", "", "delegatemainlist");
	}

	function treeReturn(){
		var lis = $("#delegate-tree").children("ul").children("li");
		setTree(lis);
	}
	
	function setTree(list){
		var selected = false;
		for(var i = 0; i < list.length; i++){
			//是否本元素
			if($(list[i]).children('a').attr('class').indexOf('clicked') != -1){
				$($(list[i]).children('a')).click();
				selected = true;
			}else{
				//是否有子元素
				var subList = $(list[i]).children('ul').children("li");
				if(subList.length == 0){
					selected = false;
				}else{
					selected = setTree(subList);
				}
			}
			if(selected) break;
		}
		return selected;
	}
function onClosedFun(){
	//alert(getUserInfos());
	var loginName = jstree.getLoginName();
	$("#trustee").attr("value",loginName);
}
	function selectUser(id){
		    popTree({ title :'选择',
					innerWidth:'400',
					treeType:'MAN_DEPARTMENT_TREE',
					defaultTreeValue:'id',
					leafPage:'false',
					multiple:'false',
					hiddenInputId:'trustee',
					showInputId:'trusteeName',
					acsSystemUrl:acsSystemUrl,
					isAppend:"false",
					callBack:function(){insertInputValue(jstree.getLoginName(),jstree.getName());}});
	}
	//当委托形式的一些条件不满足(例如:结束时间小于开始时间)时   赋值保证正确的验证验证
	function validateForEntrust(){
		if($("#styleSelect").val()==3){
			$("#inputForValidate3").attr("value","yes");
		}
	}
	function back(){
		goBack("backForm", "", "delegatemainlist","page");
	}

	function selectTache(){
		var processId=$("#processId").attr("value");
		if(processId=="0"){
			alert("请选择流程");
		}else{
			custom_tree({url:imatrixRoot+'/engine/delegate-main!getLink.htm',
				inputObj:'activityName',
				width:500,
				height:400,
				title:'选择环节',
				postData:{processDefinitionId:$("#processId").attr("value")},
				nodeInfo:['type','id'],
				multiple:true,
				webRoot:imatrixRoot
			});
		}

	}
	</script>
</head>
<body>
<div class="ui-layout-center">	
	<div class="opt-body">
		<form action="${wfCtx}/engine/delegate-main.htm" name="backForm" id="backForm" method="post"></form>
		<form action="${wfCtx}/engine/delegate-main.htm" name="delegatemain" id="delegatemain" method="post">
			<input type="hidden" name="addOrEdit" id="addOrEdit" value=""></input>
		</form>
		<aa:zone name="delegatemainlist">
			<div class="opt-btn">
				<button class="btn" onclick="addForm('${wfCtx}/engine/delegate-main!input.htm');"><span><span >增加</span></span></button>
				<button class="btn" onclick="updateForm('${wfCtx}/engine/delegate-main!input.htm');"><span><span >修改</span></span></button>
				<button class="btn" onclick="deleteForm('${wfCtx}/engine/delegate-main!delete.htm');"><span><span >删除</span></span></button>
				<button class="btn" onclick="enalbeDelegateMain('${wfCtx}/engine/delegate-main!start.htm');"><span><span >启用</span></span></button>
				<button class="btn" onclick="enalbeDelegateMain('${wfCtx}/engine/delegate-main!end.htm');"><span><span >取消</span></span></button>
			</div>
			<div id="message" style="display: none" ><s:actionmessage theme="mytheme" /></div>
			<div id="opt-content" >
				<form action="${wfCtx }/engine/delegate-main.htm" name="delegatemainForm" id="delegatemainForm" method="post">
					<view:jqGrid url="${wfCtx}/engine/delegate-main.htm" code="WF_DELEGATEMAIN" gridId="main_table"></view:jqGrid>
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
