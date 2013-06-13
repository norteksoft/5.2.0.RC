<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@ include file="/common/mms-iframe-meta.jsp"%>
		<script type="text/javascript">
		//提交
		function submitForm(url){
			$("#expenseLeaderForm").attr("action",url);
				$("#leaderName").attr("value",name);
				var a = $("[name='loginName']");
				var cutPersons = "";
				for(i=0;i<a.length;i++){
					if($(a[i]).attr("checked")){
						cutPersons+= $(a[i]).attr("value")+",";
					}
				}
				$("#cutPersons").attr("value",cutPersons.substring(0,cutPersons.length-1));
				$("#expenseLeaderForm").ajaxSubmit(function (id){
					alert(id);
					window.parent.$.colorbox.close();
				});
				
		}

		//全选
		function selectAll(){
			var a = $("[name='loginName']");
			for(i=0;i<a.length;i++){
				$(a[i]).attr("checked",true);
			}
		}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
							<button class='btn' onclick="submitForm('${ctx}/expense-report/cut.htm')"><span><span>提交</span></span></button>
					</div>
					<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
					<div id="opt-content" class="form-bg">
						<form  id="expenseLeaderForm" name="expenseLeaderForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"></input>
							<input type="hidden" name="taskId" id="taskId" value="${taskId }"/>
							<input type="hidden" name="cutPersons" id="cutPersons"></input>
							<table class="form-table-border-left" style="width: 250px;">
								<tr>
									<td><input type="checkbox" onclick="selectAll();" /></td>
									<td>人员名称</td>
								</tr>
								<s:iterator value="handerList" var="bean">
									<s:if test="userLoginName!=#bean[0]">
										<tr>
											<td><input name="loginName" type="checkbox" value="${bean[0] }"/></td>
											<td>${bean[1] }</td>
										</tr>
									</s:if>
								</s:iterator>
							</table>
						</form>
					</div>
				</aa:zone>
			</div>
		</div>
	</body>
</html>