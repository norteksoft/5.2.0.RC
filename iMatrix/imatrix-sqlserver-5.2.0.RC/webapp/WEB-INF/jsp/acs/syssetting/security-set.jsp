<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title>参数设置</title>
	<script  type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
	
	<script type="text/javascript">
	//ajax提交方法
	var tempPage;
	function opt(id, url_, opt) {
		var tempUrl;
		if (id == 'null') {
			tempUrl = url_;
		} else {
			tempUrl = url_ + "?id=" + id;
		}

		$("#ajax_from").attr("action", tempUrl);
		ajaxAnywhere.formName = "ajax_from";
		ajaxAnywhere.getZonesToReload = function() {
			return "acs_button,acs_content,acs_footer";
		};
		ajaxAnywhere.onAfterResponseProcessing = function() {
			if (tempPage == null) {
				tempPage = opt;
				document.getElementById(opt).setAttribute("class",
						"nv_selected");
				aaCallBack();
			} else {
				document.getElementById(tempPage).setAttribute("class", "");
				tempPage = opt;
				document.getElementById(opt).setAttribute("class",
						"nv_selected");
				aaCallBack();
			}

		};
		ajaxAnywhere.submitAJAX();
	}

	//修改页面提交方法
	function submitForm() {
		if($("#prems04").attr("checked")){//如果登录安全设置中选择【锁定用户】，则【锁定时间(分钟)】必填
			if($("#lockTime").val()==""){
				var errorlables=$("#lockTimeInput").children(".error");
				if(errorlables.length<=0){
					$("#lockTimeInput").append('<label  class="error">必填</label>');
				}
			}else{
				$("#inputForm").submit();
			}
		}else{
			$("#inputForm").submit();
		}
	}

	//密码规则验证
	function checkPasswordLength(pass) {
		var pas = pass.value;
		document.getElementById("warn3").style.display = "none";
		document.getElementById("warn2").style.display = "none";
		document.getElementById("warn").style.display = "none";
		if (pas <= 3) {
			document.getElementById("warn").style.display = "";
		}
		if (pas > 3 && pas <= 6) {
			document.getElementById("warn2").style.display = "";
		}
		if (pas > 6 && pas <= 8) {
			document.getElementById("warn3").style.display = "";
		}
	}


	$(document).ready(function(){
		var ms = $("#mse").val();
		if(ms=="ok"){
			$("#mse").attr("value","");
			alert('<s:text name="securitSet.success"/>');
		}
		getContentHeight();
	});
	function lockUserClick(val){
		if(val=="LOCK_USER"){
			$("#lockTimeTb").css("display","block");
		}else{
			$("#lockTimeTb").css("display","none");
		}
	}
</script>
</head>
<body>
<div class="ui-layout-center" style="height: 1000px;">
	<div class="opt-body">
		<aa:zone name="acs_content">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="menu_security--set">
					<button  class='btn' onclick="submitForm();"><span><span><s:text name="common.submit"/></span></span></button>
					<button  class='btn' onclick="cancel('/syssetting/security-set.action');"><span><span><s:text name="common.cancel"/></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="acs_server_config_list">
				    <button  class='btn' onclick="cancel('/syssetting/server-config.action');"><span><span><s:text name="server.config.set"/></span></span></button>
				</security:authorize>
				<script type="text/javascript">
				    function cancel(url){
				     location.href=webRoot+url;
					}
				</script>
			</div>
			<div id="message"><s:actionmessage theme="mytheme"/></div>
		    <form id="ajax_from" name="ajax_from" action="" method="post">
		      <input type="hidden" id="mse" value="${mse}"/>
		    </form>
			<div id="opt-content">
				<form id="inputForm" name="inputForm" action="${acsCtx}/syssetting/security-set!save.action" method="post">
					<FIELDSET><legend><s:text name="loginSecurity.title"/></legend>
					<table class="form_table1">
		               <tr>
		                    <td style="width: 170px;">
		                        <s:text name="securitySet.loginNo"/>
		                    </td>
		                    <td >
		                        <input  type="text" id="prems0" name="prems" onkeyup="value=value.replace(/[^\d]/g,'');value=value.length>3?value.substr(0,3):value" value="${prems[0]}" />
		                    </td>
		               </tr>
		               <tr>
		                  <td style="width: 170px;">
		                      <s:text name="over.securitySet.loginNo.transact"/>
		                  </td>
		                  <td>
		                   <s:if test="failType=='VALIDATE_CODE'">
		                       <input  type="radio" id="prems03" name="failType" value="VALIDATE_CODE" checked="checked" onclick="lockUserClick('VALIDATE_CODE');"/>显示验证码
		                   </s:if><s:else>
		                   	   <input  type="radio" id="prems03" name="failType" value="VALIDATE_CODE" onclick="lockUserClick('VALIDATE_CODE');"/>显示验证码
		                   </s:else>
		                   <s:if test="failType=='LOCK_USER'">
		                      	<input  type="radio" id="prems04" name="failType" value="LOCK_USER"  checked="checked" onclick="lockUserClick('LOCK_USER');"/>锁定用户
		                      </s:if>
		                      <s:else>
		                     	<input  type="radio" id="prems04" name="failType" value="LOCK_USER"  onclick="lockUserClick('LOCK_USER');"/>锁定用户
		                      </s:else>
		                  </td>
		               </tr>
		              </table>
		                  <s:if test="failType=='LOCK_USER'">
		                 <table class="form_table1" id="lockTimeTb">
			                  <tr >
			                    <td style="width: 170px;" >
			                     <s:text name="securitySet.lockTime"/>(<s:text name="securitySet.minute"/>)
			                     </td>
			                     <td id="lockTimeInput">
			                     <input  type="text" id="lockTime" name="lockTime" onkeyup="value=value.replace(/[^\d]/g,'');value=value.length>3?value.substr(0,3):value" maxlength="9" value="${lockTime }" />
			                    </td>
			                    </tr>
			               </table>
		                  </s:if>
		                  <s:else>
		                   <table class="form_table1" id="lockTimeTb" style="display: none;">
			                   <tr >
			                    <td style="width: 170px;">
			                    <s:text name="securitySet.lockTime"/>(<s:text name="securitySet.minute"/>)
			                    </td>
			                    <td id="lockTimeInput">
			                    <input  type="text" id="lockTime" name="lockTime" onkeyup="value=value.replace(/[^\d]/g,'');value=value.length>3?value.substr(0,3):value" maxlength="9" value="${lockTime }" />
			                     </td>
			                     </tr>
			               	</table>
		                  </s:else>
		               <table class="form_table1">
				       <tr>
			            <td style="width: 170px;">
			                 <s:text name="securitySet.remark"/>
			            </td>
			            <td>
			                 <input type="text" id="prems2" name="prems" maxlength="20" value="${prems[2]}" />
			            </td>
				       </tr>
					</table>
					</FIELDSET>
					<div style="height: 10px;"></div>
					<FIELDSET><legend><s:text name="securitySet.loginTimeOut"/></legend>
					<table class="form_table1">
					   <tr>
		                 <td style="width: 170px;">
		                     <s:text name="securitySet.loginTimeOut"/>
		                 </td>
		                 <td>
		                     <input id="prems3" name="prems" onkeyup="value=value.replace(/[^\d]/g,'');value=value.length>3?value.substr(0,3):value" value="${prems[3]}" />
		                     (<s:text name="securitySet.minute"/>)
		                 </td>
		                </tr>
		                <tr>  
		                 <td style="width: 170px;">
		                     <s:text name="securitySet.remark"/>
		                 </td>
		                 <td>
		                    <input type="text" id="prems4" maxlength="20" name="prems"  value="${prems[4]}"/>
		                 </td>
				         </tr>
					</table>
					</FIELDSET>
					<div style="height: 10px;"></div>
					<FIELDSET><legend><s:text name="securitSet.passwordOverNotice"/></legend>
					<table class="form_table1">
				    <tr>
		             <td style="width: 170px;">
		                 <s:text name="securitSet.passwordOverNotice"/>
		             </td>
		             <td >
		                 <input  type="text" id="prems5" name="prems" onkeyup="value=value.replace(/[^\d]/g,'');value=value.length>3?value.substr(0,3):value" value="${prems[5]}" />
		                 (<s:text name="securitSet.day"/>)
		             </td>
			        </tr>
			        <tr>  
		             <td style="width: 170px;">
		                 <s:text name="securitySet.remark"/>
		             </td>
		             <td>
		                 <input type="text" name="prems" value="${prems[6]}"  maxlength="20" id="remarks6"/>
		             </td>
			         </tr>
					</table>
					</FIELDSET>
					<div style="height: 10px;"></div>
					<FIELDSET><legend><s:text name="adminPassWordOverdue.title"/></legend>
					<table class="form_table1">
				    <tr>
		              <td style="width: 170px;">
		                  <s:text name="securitSet.adminPassWordOverdue"/>
		              </td>
		              <td >
		                  <input  type="text" id="prems7" name="prems" onkeyup="value=value.replace(/[^\d]/g,'');value=value.length>3?value.substr(0,3):value" value="${prems[7]}" />
		                  (<s:text name="securitSet.day"/>)
		              </td>
		             </tr>
		             <tr>
		               <td style="width: 170px;">
		                   <s:text name="securitySet.remark"/>
		               </td>
		               <td>
		                   <input type="text" name="prems" value="${prems[8]}" maxlength="20" id="remarks8"/>
		               </td>
		             </tr>
					</table>
					</FIELDSET>
					<div style="height: 10px;"></div>
					<FIELDSET><legend><s:text name="securitSet.userPassWordOverdue.title"/></legend>
					<table class="form_table1">
				     <tr>
		             <td style="width: 170px;">
		                 <s:text name="securitSet.userPassWordOverdue"/>
		             </td>
		             <td >
		                 <input  type="text" id="prems9" name="prems" onkeyup="value=value.replace(/[^\d]/g,'');value=value.length>3?value.substr(0,3):value" value="${prems[9]}" />
		                 (<s:text name="securitSet.day"/>)
		             </td>
		             </tr>
			         <tr>
		             <td style="width: 170px;">
		                 <s:text name="securitySet.remark"/>
		             </td>
		             <td>
		                 <input type="text" name="prems" value="${prems[10]}" maxlength="20" id="remarks10"/>
		             </td>
			         </tr>
					</table>
					</FIELDSET>
					<div style="height: 10px;"></div>
					<FIELDSET><legend>审计信息设置</legend>
					<table class="form_table1">
				     <tr>
		             <td style="width: 170px;">
		                	日志保留时间
		             </td>
		             <td >
		                 <input  type="text" id="logRemainTime" name="logRemainTime" onkeyup="value=value.replace(/[^\d]/g,'');value=value.length>3?value.substr(0,3):value" value="${logRemainTime}" />
		                 (<s:text name="securitSet.day"/>)
		             </td>
		             </tr>
			         <tr>
		             <td style="width: 170px;">
		                 <s:text name="securitySet.remark"/>
		             </td>
		             <td>
		                 <input type="text" name="logRemainTimeRemark" value="${logRemainTimeRemark}" maxlength="20" id="logRemainTimeRemark"/>
		             </td>
			         </tr>
					</table>
					</FIELDSET>
					<div style="height: 10px;"></div>
					<FIELDSET><legend><s:text name="passwordComplexity.title"/></legend>
					<table class="form_table1">
			        <tr>
		              <td style="width: 170px;"> <s:text name="securitSet.englishUpperCase"/></td>
		              <td >
		                <s:if test='passRule!=null&&passRule.trim().indexOf("(?=(.*[A-Z]){1,})")!=-1'>
		                     <input type="checkbox" name="passRule"  value="(?=(.*[A-Z]){1,})" checked="checked"/> 
		                </s:if>
		                <s:else>
		                    <input type="checkbox" name="passRule"  value="(?=(.*[A-Z]){1,})" /> 
		                </s:else>
		               </td>
		               <td><s:text name="securitSet.englishLowerCase"/></td>
		               <td >
		                <s:if test='passRule!=null&&passRule.trim().indexOf("(?=(.*[a-z]){1,})")!=-1'>
		                   <input type="checkbox" name="passRule"  value="(?=(.*[a-z]){1,})" checked="checked"/> 
		                </s:if>
		                <s:else>
		                   <input type="checkbox" name="passRule"  value="(?=(.*[a-z]){1,})" /> 
		                </s:else>
		               </td>
			           </tr>
			           <tr>
		                <td style="width: 170px;"><s:text name="securitSet.number"/></td>
		                <td style="width: 200px;">
		                   <s:if test='passRule!=null&&passRule.trim().indexOf("d")!=-1'>
		                       <input type="checkbox" name="passRule" value="(?=(.*\d){1,})" checked="checked"/> 
		                   </s:if>
		                   <s:else>
		                       <input type="checkbox" name="passRule" value="(?=(.*\d){1,})" /> 
		                   </s:else>
		                </td>
		                <td style="width: 250px;"><s:text name="securitSet.mark"/></td>
		                <td >
		                  <s:if test='passRule!=null&&passRule.trim().indexOf("W")!=-1'>
		                     <input type="checkbox" name="passRule" value="(?=(.*\W){1,})" checked="checked"/> 
		                  </s:if>
		                  <s:else>
		                      <input type="checkbox" name="passRule"  value="(?=(.*\W){1,})" />
		                  </s:else>
		                </td>
			           </tr>
			           <tr> 
			             <td style="width: 170px;"><s:text name="securitSet.passWordLenth"/></td>
		                 <td colspan="3">
		                    <input type="text" 
		                          name="passRule" 
		                          id="passWordLenth"
		                          onkeyup="value=value.replace(/[^\d]/g,'');value=value.length>2?value.substr(0,3):value"
		                          onblur="checkPasswordLength(this)"
		                          value='<s:if test="passWordLenth==null">0</s:if><s:else>${passWordLenth}</s:else>'/>
		                
		                	<font color="red"><s:text name="syssetting.remark"/></font>
		                </td>
			           </tr>
				       <tr>
				         <td></td>
				         <td  style="display: none" colspan="3" id="warn">
				             <font color="red"><s:text name="syssetting.prompt"/></font>
				         </td>
				       </tr>
				       <tr>
				      	 <td></td>
		                <td  style="display: none" colspan="3" id="warn2">
		                   <font color="red"><s:text name="syssetting.passwordSecondDanger"/></font>
		                </td>
				       </tr>
				       <tr>
				       	<td></td>
			             <td  style="display: none" colspan="3" id="warn3">
			                 <font color="red"><s:text name="syssetting.passwordLightDanger"/></font>
			             </td>
				       </tr>
					</table>
			    </FIELDSET>
			    <div style="height: 10px;"></div>
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
