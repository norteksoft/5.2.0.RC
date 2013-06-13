<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	
	<!-- 树 -->
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${ctx}/js/item.js"></script>
	
	<title>定时设置</title>
	<script type="text/javascript">
	$(document).ready(function(){
		validateTimer();
		checkBoxSelect('everyWeek');
		timeFormat('everyDate');
		dateFormat('appointTime');
	});
	function checkBoxSelect(id){
		$("#"+id).multiselect({
		 	multiple: true,
		  /*header: "Select an option",
		   noneSelectedText: "Select an Option",*/
		   header: true,
		   selectedList: 1
		});
	}

	/**
	 * 日期初始化
	 * @param id
	 * @return
	 */
	function timeFormat(id){
		$('#'+id).timepicker({
			timeOnlyTitle: '时间',
			beforeShow:function(input, inst){
				if($("#"+id).attr("value")==""||typeof ($("#"+id).attr("value"))=='undefined'){
					$("#"+id).attr("value","00:00");
				}
			}
		});
	}

	/**
	 * 日期初始化
	 * @param id
	 * @return
	 */
	function dateFormat(id){
		$('#'+id).datetimepicker({
			"dateFormat":'yy-mm-dd',
			changeMonth:true,
			changeYear:true
		});
	}

	function typeChange(value){
		if(value=="everyDate"){
			$("#tr_everyDate").show();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").hide();
		}else if(value=="everyWeek"){
			$("#tr_everyDate").show();
			$("#tr_everyWeek").show();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").hide();
		}else if(value=="everyMonth"){
			$("#tr_everyDate").show();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").show();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").hide();
		}else if(value=="appointTime"){
			$("#tr_everyDate").hide();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").show();
			$("#tr_appointSet").hide();
		}else if(value=="appointSet"){
			$("#tr_everyDate").hide();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").show();
		}
	}

	//提交
	function submitJobInfo(){
		$("#jobInfoFrom").attr("action",'${settingCtx}/options/job-info-save.htm');
		$("#jobInfoFrom").submit();
	}

		$.validator.addMethod("customRequired", function(value, element) {
			var $element = $(element);
			if($element.val()!=null&&$element.val()!=''&&typeof ($element.val())!='undefined'){
				return true;
			}
			if($("#typeEnum").val()=='everyDate'){
				if($("#everyDate").val()!=null&&$("#everyDate").val()!=''&&typeof ($("#everyDate").val())!='undefined'){
					return true;
				}
			}
			if($("#typeEnum").val()=='everyMonth'){
				if(($("#everyMonth").val()!=null&&$("#everyMonth").val()!=''&&typeof ($("#everyMonth").val())!='undefined')
						&&($("#everyDate").val()!=null&&$("#everyDate").val()!=''&&typeof ($("#everyDate").val())!='undefined')){
					return true;
				}
			}
			if($("#typeEnum").val()=='everyWeek'){
				if($("#everyDate").val()!=null&&$("#everyDate").val()!=''&&typeof ($("#everyDate").val())!='undefined'){
					return true;
				}
			}
			if($("#typeEnum").val()=='appointTime'){
				if($("#appointTime").val()!=null&&$("#appointTime").val()!=''&&typeof ($("#appointTime").val())!='undefined'){
					return true;
				}
			}
			if($("#typeEnum").val()=='appointSet'){
				if($("#appointSet").val()!=null&&$("#appointSet").val()!=''&&typeof ($("#appointSet").val())!='undefined'){
					return true;
				}
			}
		}, "必填");
		
	function validateTimer(){
		
		$("#jobInfoFrom").validate({
			submitHandler: function() {
				if($("#typeEnum").val()=='everyWeek'){//每周时
					if($("#everyWeek").val()==null||$("#everyWeek").val()==''||typeof ($("#everyWeek").val())=='undefined'){
						$("#tr_everyWeek td").append('<label  class="error">必填</label>');
					}else{
						$("#tr_everyWeek td label").remove();
						$("#jobInfoFrom").ajaxSubmit(function (id){
							$("#id").attr("value",id);
							$("#message").show();
							setTimeout('$("#message").hide("show");',3000);
							parent.backPage();
							parent.$.colorbox.close();
						});
					}
				}else{//非每周
					$("#jobInfoFrom").ajaxSubmit(function (id){
						$("#id").attr("value",id);
						$("#message").show();
						setTimeout('$("#message").hide("show");',3000);
						parent.backPage();
						parent.$.colorbox.close();
					});
				}
			
			},
			rules: {
				runAsUser: "required",
				code: "required",
				url: "required",
				typeEnum:"required"
			},
			messages: {
				runAsUser: "必填",
				code: "必填",
				url: "必填",
				typeEnum: "必填"
			}
		});
	}

	/*---------------------------------------------------------
	函数名称:selectPrincipal
	参    数:id
	功    能：负责人树
	------------------------------------------------------------*/
	function selectPrincipal(name,id){
		popTree({ title :'选择',
		innerWidth:'400',
		treeType:'MAN_DEPARTMENT_TREE',
		defaultTreeValue:'id',
		leafPage:'false',
		treeTypeJson:null,
		multiple:'false',
		hiddenInputId:id,
		showInputId:name,
		loginNameId:'',
		acsSystemUrl:imatrixRoot,
		isAppend:"false",
		callBack:function(){
			getUserInformation();
		}});
	}

	function getUserInformation(){
		$("#runAsUser").attr("value",jstree.getLoginName());
	}
	</script>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="submitJobInfo();"><span><span>提交</span></span></a>
	</div>
	<div id="opt-content" >
		<div id="message" style="display: none;"><font class='onSuccess'><nobr>保存成功！</nobr></font></div>
		<form action="" name="jobInfoFrom" id="jobInfoFrom" method="post">
			<input type="hidden" name="id" id="id" value="${id}"/>
			<input type="hidden" name="systemId" id="systemId" value="${systemId}"/>
			<table>
				<tbody>
				<s:if test="id==null">
					<tr>
						<td>
							定时运行身份：<input readonly="readonly" type="text" id="runAsUserName" name="runAsUserName" value="${runAsUserName }"/>
						<input readonly="readonly" type="hidden" id="runAsUserNameId" />
						<input type="hidden" id="runAsUser" name="runAsUser" value="${runAsUser }"/>
						<a href="#" onclick="selectPrincipal('runAsUserName','runAsUserNameId')" title="追加"  class="small-btn" id="selectBtn">
							<span><span>选择</span></span>
						</a>
						</td>
					</tr>
					<tr>
						<td>
							定时任务编号：<input type="text" name="code" id="jobCode" value="${code}" maxlength="60"/><span style="color: red;margin: 40px;">(必须是唯一的)</span>
						</td>
					</tr>
					<tr>
						<td>
							定时请求类型：<select name="applyType" id="applyType">
										<s:iterator value="@com.norteksoft.bs.options.enumeration.ApplyType@values()" var="FK">
											<option value="${FK}"><s:text name="%{code}"></s:text></option>
										</s:iterator>
									</select>
						</td>
					</tr>
					<tr>
						<td>
							定时任务地址：<input type="text" name="url" id="url" value="${url}" maxlength="60"/><span style="color: red;margin: 40px;">(如:"/rest/wf/delegate")</span>
						</td>
					</tr>
					<tr>
						<td>
							定时任务备注：<input type="text" name="description" id="urlInfo" value="${description}" maxlength="60"/>
						</td>
					</tr>
				</s:if>
					<tr>
						<td>
							定时任务方式：<select name="typeEnum" id="typeEnum" onchange="typeChange(this.value);">
											<option value="">请选择</option>
										<s:iterator value="@com.norteksoft.bs.options.enumeration.TimingType@values()" var="FK">
											<option value="${FK}"><s:text name="%{code}"></s:text></option>
										</s:iterator>
									</select>
						</td>
					</tr>
					<tr id="tr_everyMonth" style="display: none;">
						<td>
							定时任务每月：<select id="everyMonth" name="everyMonth"  class="customRequired">
										<option value="">请选择</option>
										<s:iterator value="@com.norteksoft.bs.options.enumeration.DateEnum@values()" var="FK">
											<option value="${FK}"><s:text name="%{code}"></s:text></option>
										</s:iterator>
									</select>
						</td>
					</tr>
					<tr id="tr_everyWeek" style="display: none;">
						<td>
							定时任务每周：<select id="everyWeek" name="everyWeek" multiple="multiple">
										<s:iterator value="@com.norteksoft.bs.options.enumeration.WeekEnum@values()" var="FK">
											<option value="${FK}"><s:text name="%{code}"></s:text></option>
										</s:iterator>
									</select>
						</td>
					</tr>
					<tr id="tr_everyDate" style="display: block;">
						<td>
							定时任务每天：<input id="everyDate" name="everyDate" value="" readonly="readonly" class="customRequired"/>
						</td>
					</tr>
					<tr id="tr_appointTime" style="display: none;">
						<td>
							定时指定日期：<input id="appointTime" name="appointTime" value="" readonly="readonly" class="customRequired"/>
						</td>
					</tr>
					<tr id="tr_appointSet" style="display: none;">
						<td>
							定时高级设置：<input id="appointSet" name="appointSet" value="" maxlength="30" class="customRequired"/>
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>