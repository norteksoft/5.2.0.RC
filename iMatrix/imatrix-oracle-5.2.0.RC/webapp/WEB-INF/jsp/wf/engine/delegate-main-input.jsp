<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>委托管理</title>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="delegatemainlist">
	<script type="text/javascript">
	
	$(function(){
		getContentHeight();
		$('#beginTime').datepicker({
			showButtonPanel:"true",
			"dateFormat":'yy-mm-dd',
		      changeMonth:true,
		      changeYear:true,
		      onSelect: function(dateText, inst){
				var beginDate=formatStrToDate(dateText);
				var endStr=$('#endTime').attr("value");
				if(endStr!=""&&typeof(endStr)!="undefined"){
			      	var endDate=formatStrToDate(endStr);
				      if(beginDate.getTime()>=endDate.getTime()){
				    	  alert("生效日期应小于截止日期");
				    	  $('#beginTime').attr("value","");
				    	  return;
				      }
			      }

				var cDate=new Date();
				var currentDate=formatStrToDate(cDate.getFullYear()+"-"+(cDate.getMonth()+1)+"-"+cDate.getDate());
			    if(beginDate.getTime()<currentDate.getTime()){
					alert("生效日期应大于等于当前日期");
					$('#beginTime').attr("value","");
				}
				
				
			  }
		});
		$('#endTime').datepicker({
			showButtonPanel:"true",
			"dateFormat":'yy-mm-dd',
		      changeMonth:true,
		      changeYear:true,
		      onSelect: function(dateText, inst){
				var endDate=formatStrToDate(dateText);
				
		      	var beginStr=$('#beginTime').attr("value");
			     if(beginStr!=""&&typeof(beginStr)!="undefined"){
				      var beginDate=formatStrToDate(beginStr);
				      if(beginDate.getTime()>=endDate.getTime()){
				    	  alert("截止日期应大于生效日期");
				    	  $('#endTime').attr("value","");
				    	  return;
				      }
			      }
			      
			     var cDate=new Date();
			        currentDate=formatStrToDate(cDate.getFullYear()+"-"+(cDate.getMonth()+1)+"-"+cDate.getDate());
					if(endDate.getTime()<currentDate.getTime()){
						alert("截止日期应大于当前日期");
						$('#endTime').attr("value","");
					}
			  }
		});
	});


	function formatStrToDate(dateStr){
		var year=parseInt(dateStr.split("-")[0]);
		var monStr=dateStr.split("-")[1];
		if(monStr.indexOf("0")==0){//如果是一位数应加0，否则getTime()获得的值不一致
			monStr=monStr.substring(1);
		}
      	var month=parseInt(monStr);
      	var dayStr=dateStr.split("-")[2];
      	if(dayStr.indexOf("0")==0){
      		dayStr=dayStr.substring(1);
      	}
     	var day=parseInt(dayStr);
      	return new Date(year,month-1,day);
      	
	}

	</script>
	<div class="opt-btn">
		<s:if test="state==null||state.code=='delegate.main.states.new.creating'">
			<security:authorize ifAnyGranted="wf_delegateMain_save">
				<button class="btn" onclick="validateForEntrust();submitForm();"><span><span>保存</span></span></button>
			</security:authorize>
			<security:authorize ifAnyGranted="wf_delegateMain_save">
				<button class="btn" onclick="submitAndStartForm();"><span><span>保存并启用</span></span></button>
			</security:authorize>
		</s:if>
		<security:authorize ifAnyGranted="wf_delegateMain">
			<button class="btn" onclick="setPageState();ajaxSubmit('defaultForm','${wfCtx}/engine/delegate-main.htm','delegatemainlist');"><span><span >返回</span></span></button>
		</security:authorize>
	</div>
	<div id="opt-content">
		<div id="message" style="display: none" ><s:actionmessage theme="mytheme" /></div>
		<form action="${wfCtx}/engine/delegate-main!save.htm" name="delegateSaveForm" id="delegateSaveForm" method="post">
			<input type="hidden" id="id" name="id" value="${id}"/> 
			<input type="hidden" id="needStart" name="needStart" value="false"/>
			<table class="Table" >
		 		<tr style="height: 30px;">
					<td style="width: 80px;">
						受&nbsp;托&nbsp;人：
					</td>
					<td  style="width: 380px;">
						<input id="trusteeName" type="text" name="trusteeName" value="${trusteeName}" readonly="readonly"/>
						<input id="trustee" type="hidden" name="trustee" value="${trustee}" />
						  <a href="#" onclick='selectUser("selectBtn")' title="选择"  class="small-btn" id="selectBtn"><span><span>选择</span></span></a> 	<span class="required">*</span>
						 <!-- <a href="#" onclick='selectUser("selectBtn")' title="追加"  class="small-btn" id="selectBtn"><span><span>追加</span></span></a> 	<span class="required">*</span>-->
						 <!-- <a href="#" onclick='removeOption()' title="移除"  class="small-btn" id="selectBtn"><span><span>移除</span></span></a> 	<span class="required">*</span> -->
					</td>
					<td>
					
					 </td>
			  	</tr>
		 		<tr style="height: 30px;">
					<td >
						生效日期：
					</td>
					<td  >
						<input value="<s:date name="beginTime"  format="yyyy-MM-dd" />" id="beginTime" name="beginTime" readonly="readonly" /><span class="required">*</span>
					</td>
					<td>
						
					 </td>
			  	</tr>
		 		<tr style="height: 30px;">
					<td >
						截止日期：
					</td>
					<td  >
						<input value="<s:date name="endTime"  format="yyyy-MM-dd" />" id="endTime" name="endTime" readonly="readonly"/><span class="required">*</span>
					</td>
					<td>
					 	
					 </td>
			  	</tr>
		 		<tr style="height: 30px;">
					<td >
						委托形式：
					</td>
					<td  >	
					<select name="style" id="styleSelect" onchange="change(this.value);" class="styleRequired">
								<option value="0">请选择</option>
								<s:if test="style==1">
										<option value="1"  selected="selected">指定流程</option>
								</s:if>
								<s:else>
									<option value="1">指定流程</option>
								</s:else>
								<s:if test="style==2">
										<option value="2"  selected="selected">所有流程</option>
								</s:if>
								<s:else>
									<option value="2">所有流程</option>
								</s:else>
								<s:if test="style==3">
										<option value="3"  selected="selected">委托权限</option>
								</s:if>
								<s:else>
									<option value="3">委托权限</option>
								</s:else>
						</select>
						<input id="inputForValidate1" name="inputForValidate1" type="hidden"/><span class="required">*</span>	
					</td>
					<td>	
						
					 </td>
			  	</tr>
		 		<tr id="chooseP" style="height: 30px;display :none;">
					<td >
						选择流程：
					</td>
					<td  >
					<select id="processId" name="processId" id="flowIdSelect" onchange="changeFlow(this.value);" class="flowRequired">
								<option value="0">请选择流程</option>
								<s:iterator value="workflowDefinitions" var="wdf">
									<s:if test="processDefinitionId==#wdf.processId">
										<option value="${wdf.processId }" selected="selected">${name}(${version })</option>
									</s:if>
									<s:else>
										<option value="${wdf.processId }">${name }(${version })</option>
									</s:else>
								</s:iterator>
							</select>
							
							<input id="inputForValidate2" name="inputForValidate2" type="hidden"/><span class="required">*</span>
					</td>
					<td>
						
					</td>
			  	</tr>
			 		<tr id="chooseT" style="height: 30px;display :none;">
						<td style="vertical-align: top;">
							选择环节：
						</td>
						<td>
						<aa:zone name="taskNamesSelect">
							<a href="#" onclick='selectTache();' title="选择"  class="small-btn" id="selectBtn"><span><span>选择</span></span></a>
							<a href="#" onclick="$('#activityName').attr('value','');" title="选择"  class="small-btn" id="selectBtn"><span><span>清空</span></span></a>  
							<br/>委托第一环节在提交前是不起作用的 <span class="required">*</span><br/>
							<textarea id="activityName" name="activityName" cols="20" rows="5"  readonly="readonly" class="tacheRequired">${activityName}</textarea>
						 	<input id="inputForValidate3" name="inputForValidate3" type="hidden"/>
						</aa:zone>
						</td>
						<td>
							
						 </td>
				  	</tr>
			  	<tr style="display: none;">
			  		<td >
			  			拥有权限：
			  	  	</td>
			  	  <td id="rolesId"  >
			  	  </td>
			  	  <td><span class="required">*</span></td>
			  	</tr>
		 		<tr style="height: 30px;">
					<td  >
		 			  	 说&nbsp;&nbsp; 明：
					</td>
					<td  >
		 			  <textarea id="contextArea" name="remark" cols="50" rows="5" 
		 			   style="overflow: auto;"
		 			  >${remark}</textarea>
		 			</td>
		 			<td>
		 				
		 			</td>
			  	</tr>
			</table>
		</form>
		<form name="defaultForm" id="defaultForm" action="" method="post"></form>
	</div>
</aa:zone>
</div>
</body>
</html>
