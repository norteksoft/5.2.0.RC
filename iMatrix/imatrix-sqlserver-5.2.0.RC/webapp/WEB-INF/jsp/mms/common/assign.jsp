<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">

<html>
<head>
<title>选择办理人</title>
<%@ include file="/common/mms-iframe-meta.jsp"%>
<script type="text/javascript">
	function setTransactor(){
		var names = "";
		if($("#isMultiple").attr("value")=='true'){//表示多选树
			var arr=eval(getInfo());
			for(var i=0;i<arr.length;i++){
				if(arr[i].type=="user" || arr[i].type=="allDepartment" || arr[i].type=="company" ){
					if(arr[i].type=="user"){
						names+=arr[i].loginName+",";
					}else if(arr[i].type=="allDepartment" || arr[i].type=="company"){//表示全选部门或公司
						names='allCompany';
					}else if(arr[i].type=="allWorkGroup"){//表示选择所有工作组
						names='allGroup';
					}
				}
			}
		}else{
			if(getType()=='user'){
				names=getLoginName();
			}
		}
		if(names!=""&&typeof(names) != "undefined"){
			$("#transactor").attr("value",names);
			ajaxAnyWhereSubmit("_condidates_form", "${mmsCtx}/common/assign.htm", "", __setOk);
		}else{
			alert("请选择人员");
		}
	}
	function __setOk(){
		parent.__show_message('opt_message','提交成功');
		window.parent.$.colorbox.close();
		if($("#closeFlag").attr('value')=='false'){
			window.parent.location.reload(true);
		}else{
			window.parent.parent.close();
			window.parent.location.reload(true);
		}
	}
</script>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
			<div class="opt-btn">
				<button onclick="setTransactor()" class="btnStyle "><span><span>确定</span></span></button>
				<a id="backbutton" style="display: none;" href="#" onclick="back_wf_task();" class="btnStyle ">返回</a>
			</div>
			<div id="opt-content">
				<div id="message" style="display: none;"><s:actionmessage theme="mytheme"/></div>
				<form id="_condidates_form" action="" method="post">
				<input type="hidden" name="taskId" value="${taskId}"/>
				<input id="closeFlag" type="hidden"  value="${closeFlag}"/>
				<input name="transactor" id="transactor" type="hidden" value="${transactor}"/>
					<s:iterator value="candidates.keySet()" id="targetTask">
							<input name="targetNames" type="hidden" value="${targetTask[1]}"/>
							<input id="isMultiple" type="hidden" value="${targetTask[0]}"/>
							<s:if test="#targetTask[0]">
								<acsTags:tree defaultable="true" multiple="true" treeType="COMPANY" treeId="companyTree"></acsTags:tree>
							</s:if><s:else>
								<acsTags:tree defaultable="true" multiple="false" treeType="COMPANY" treeId="companyTree"></acsTags:tree>
							</s:else>
					</s:iterator>
				</form>
			</div>
	</div>	
</div>
</body>
</html>
