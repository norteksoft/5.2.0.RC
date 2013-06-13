<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>选项组信息维护</title>
	</head>
	<body>
	<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="groups_main">
		<div class="opt-btn">
			<button class="btn" onclick="save();"><span><span>保存</span></span></button>
			<button class="btn" onclick="setPageState();goBack();"><span><span >返回</span></span></button>
		</div>
		<div id="opt-content">
		<aa:zone name="groups_input">
			<span id="message" style="display: inline;"><s:actionmessage theme="mytheme" /></span>
			<script type="text/javascript">
				setTimeout('$("#message").hide();',3000);
			</script>
			<form name="optionGroupFrom" id="optionGroupFrom" method="post">
				<input type="hidden" id="_optionGroupId" name="optionGroupId" value="${id}"/>
				<input name="systemId" id="_systemId" type="hidden"></input>
				<div style="margin: 5px;">
				<table class="formTable marginTop">
					<tr>
						<td >
							选项组编号:
						</td>
						<td >
							<input type="text" name="code" id="optionGroupNo"  value="${code}" onblur="validateNO();"/>
							<span class="required">*</span>
						</td>
					</tr>
					<tr>
						<td >
							业务编码:
						</td>
						<td >
							<input type="text" name="workCode" id="workCode"  value="${workCode}"/>
						</td>
					</tr>
					<tr>
						<td >
							选项组名称:
						</td>
						<td >
							<input type="text" name="name" id="optionGroupName" value="${name}"/>
							<span class="required">*</span>
						</td>
					</tr>
					<tr>
						<td>
							选项组描述:
						</td>
			    		<td>
			    		</td>
			  		</tr>
			  		<tr>
			    		<td></td>
			    		<td>
			    			<textarea id="depict" name="description" style="width:650px;height:180px;" onkeyup="javascript:if(this.value.length >=120){this.value=this.value.slice(0,120);alert('字符不能超过120');return false;}">${description}</textarea>
			    		</td>
			  		</tr>
				</table>
				<p>
					选项字段信息
				</p>
				<table id="optionTable" class="marginTop fieldTable" border="0" style="left: 90px;">
					<tr>
						<th scope="col">&nbsp;选项名&nbsp;</th>
						<th scope="col">&nbsp;选项值&nbsp;</th>
						<th scope="col">&nbsp;是否默认&nbsp;</th>
						<th scope="col">&nbsp;显示顺序&nbsp;</th>
						<th scope="col">&nbsp;操作&nbsp;</th>
					</tr>
					<s:iterator value="option" var="opt" status="i">
						<tr>
							<td>
								<input name="option[${i.index}].id" type="hidden" value="${id}"/>
								<input name="option[${i.index}].name" type="text" id="name_${i.index}" value="${name}"/>
			  					<span id="name_${i.index}Tip"></span>
							</td>
							<td>
								<input name="option[${i.index}].value" type="text" id="value_${i.index}" value="${value}"/>
			  					<span id="value_${i.index}Tip"></span>
							</td>
							<td align="center">
								<select name="option[${i.index}].selected" id="isSelect_${i.index}" onchange="validateSelected(this);">
									<option value="false" <s:if test="!selected"> selected="selected"</s:if>>否</option>
									<option value="true" <s:if test="selected"> selected="selected"</s:if>>是</option>
								</select>
	  						</td>
	  						<td align="center">
	  							<select name="option[${i.index}].optionIndex" id="optionIndex_${i.index}">
	  								<s:iterator begin="0" end="100" var="defaulted">
	  									<s:if test="#defaulted == optionIndex">
	  										<option value="${optionIndex}" selected="selected">${optionIndex}</option>
	  									</s:if><s:else>
	  										<option value="${defaulted}">${defaulted}</option>
	  									</s:else>
	  								</s:iterator>
								</select>
							</td>
	  						<td>
								<a class='small-btn' name="${i.index}" href="#${i.index}" onclick='deleteOptionRow(this,${i.index},${id});'><span><span>删除</span></span></a>&nbsp;
								<a class="small-btn" href="#${i.index}" onclick='addOptionRow("optionTable",true)' ><span><span>添加</span></span></a>
							</td>							
						</tr>
					</s:iterator>
				</table>	
				</div>			
			</form>
		</aa:zone>
		</div>
		</aa:zone>
		</div>
	</div>
	</body>
</html>