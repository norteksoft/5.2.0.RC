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
		$().ready(function(){
			showMessage('message');
		});
		
		//单选按钮事件
		function selectLoginTypeResponse(type){
	      if(type==1){
	    	  $('#ldap').show();
	    	  $('#rtx').hide();
	    	  $('#external').hide();
	      }else if(type==2){
	    	  $('#ldap').hide();
	    	  $('#rtx').show();
	    	  $('#external').hide();
	      }else if(type==3){
	    	  $('#ldap').hide();
	    	  $('#rtx').hide();
	    	  $('#external').show();
	      }
	    }	
	    //提交事件
	    function submitForm(type){
	        if(type=="ldapStart"){
	            var ldapUsername = $('#ldapUsername').attr("value");
	            var ldapPassword = $('#ldapPassword').attr("value");
	            var ldapUrl = $('#ldapUrl').attr("value");
	            if(ldapUsername=="" || ldapUsername==undefined 
	                    || ldapPassword=="" || ldapPassword==undefined
	                       || ldapUrl=="" || ldapUrl==undefined){
	                alert("<s:text name='server.config.error'/>");
	                return;
	            }
	            $('#ldap').show();
	        	$('#rtx').hide();
	            $('#ldapInvocation').attr("value",true);
	        }else if(type=="ldapEnd"){
	        	$('#ldap').show();
	        	$('#rtx').hide();
	        	$('#ldapInvocation').attr("value",false);
	        }else if(type=="rtxStart"){
		        var ldapRtxUrl = $("#ldapRtxUrl").val();
		        if(ldapRtxUrl==""){
	                alert("<s:text name='server.config.error'/>");
	                return;
	            }
	        	$('#ldap').hide();
	        	$('#rtx').show();
	        	$('#rtxInvocation').attr("value",true);
	        }else if(type=="rtxEnd"){
	        	$('#ldap').hide();
	        	$('#rtx').show();
	        	$('#rtxInvocation').attr("value",false);
	        }
	        $('#inputForm').submit();
	    }
	
	    //页面显示事件
	    function pageShow(){
	      var login = $('#loginType').attr("value");
	      if(login==1){
	          $('#radio2').click();
	      }else if(login==2){
	    	  $('#radio3').click();
	      }
	    }
	    function submitSave(){
	    	$('#inputForm').submit();
	    }
		function showMessage(id){
			$("#"+id).show("show");
			setTimeout('$("#'+id+'").hide("show");',3000);
		}
		function externalEnable(){
			var externalUrl = $("#externalUrl").val();
	        if(externalUrl==""){
                alert("<s:text name='server.config.error'/>");
                return;
            }
			$('#ldapInvocation').attr("value",false);
			$('#rtxInvocation').attr("value",false);
			$('#externalInvocation').attr("value",true);
			$('#inputForm').submit();
		}
		function externaldisable(){
			$('#externalInvocation').attr("value",false);
			$('#inputForm').submit();
		}
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="acs_content">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="menu_security--set">
					<button  class='btn' onclick="cancel('/syssetting/security-set.action');"><span><span><s:text name="common.cancel"/></span></span></button>
				</security:authorize>
				<script type="text/javascript">
				    function cancel(url){
				     location.href=webRoot+url;
					}
				</script>
			</div>
			<form id="ajax_from" name="ajax_from" action="" method="post">
		       <input type="hidden" id="mse" value="${mse}"/>
		    </form>
		    <div id="message"><s:actionmessage theme="mytheme"/></div>
			<div id="opt-content" >
				<form id="inputForm" name="inputForm" action="${acsCtx}/syssetting/server-config!save.action" method="post">
					  <input  type="hidden" name="id" size="40" value="${serverConfig.id}" />
					  <input type="radio" name="radio" onclick="selectLoginTypeResponse(1)"  <s:if test="serverConfig.ldapInvocation==null || serverConfig.ldapInvocation||(!serverConfig.ldapInvocation&&!serverConfig.rtxInvocation)">checked="checked"</s:if>/><s:text name="serverConfig.ldap"/>
					  <s:if test="serverConfig.ldapInvocation==true">
					  <input type="button" value="<s:text name='serverConfig.start'/>" disabled="disabled"/>
					  <input type="button" value="<s:text name='serverConfig.end'/>" onclick="submitForm('ldapEnd')" />
					  </s:if>
					  <s:else>
					  <input type="button" value="<s:text name='serverConfig.start'/>" onclick="submitForm('ldapStart')"/>
					  <input type="button" value="<s:text name='serverConfig.end'/>" disabled="disabled"/>
					  </s:else>
		              <input type="radio" name="radio" onclick="selectLoginTypeResponse(2)"  <s:if test="serverConfig.rtxInvocation">checked="checked"</s:if>/><s:text name="serverConfig.rtx"/>
		              <s:if test="serverConfig.rtxInvocation==true">
					  <input type="button" value="<s:text name='serverConfig.start'/>" disabled="disabled"/>
					  <input type="button" value="<s:text name='serverConfig.end'/>" onclick="submitForm('rtxEnd')" />
					  </s:if>
					  <s:else>
					  <input type="button" value="<s:text name='serverConfig.start'/>" onclick="submitForm('rtxStart')"/>
					  <input type="button" value="<s:text name='serverConfig.end'/>" disabled="disabled"/>
					  </s:else>
					  
					  <input type="radio" name="radio" onclick="selectLoginTypeResponse(3)"  <s:if test="serverConfig.extern">checked="checked"</s:if>/>其他方式：
					  <input type="button" value="启用" <s:if test="serverConfig.extern">disabled="disabled"</s:if><s:else>onclick="externalEnable();"</s:else> />
					  <input type="button" value="禁用" <s:if test="!serverConfig.extern">disabled="disabled"</s:if><s:else>onclick="externaldisable();"</s:else> />
					  
						<FIELDSET id="ldap" <s:if test="serverConfig.rtxInvocation || serverConfig.extern">  style="display:none"</s:if>>
						<table class="form_table1">
							<tr>
		                      <td width="40%">LDAP服务器类别</td>
		                      <td>
		                      	<select name="ldapType">
		                      		<option value="APACHE" <s:if test="serverConfig.ldapType==@com.norteksoft.acs.entity.sysSetting.LdapType@APACHE"> selected="selected" </s:if> >apache</option>
		                      		<option value="DOMINO" <s:if test="serverConfig.ldapType==@com.norteksoft.acs.entity.sysSetting.LdapType@DOMINO"> selected="selected" </s:if> >domino</option>
		                      		<option value="WINDOWS_AD" <s:if test="serverConfig.ldapType==@com.norteksoft.acs.entity.sysSetting.LdapType@WINDOWS_AD"> selected="selected" </s:if> >windowsAD</option>
		                      	</select>
		                      </td>
			               </tr>
						   <tr>
		                      <td width="40%">
		                          <s:text name="serverConfig.ldap.username"/>
		                      </td>
		                      <td>
		                          <input  type="text" id="ldapUsername" name="ldapUsername"  value="${serverConfig.ldapUsername}" style="width: 200px;"/>
		                      </td>
			               </tr>
					       <tr >
		                      <td >
		                           <s:text name="serverConfig.ldap.password"/>
		                      </td>
		                      <td>
		                           <input type="password" id="ldapPassword" name="ldapPassword"  value="${serverConfig.ldapPassword}" style="width: 200px;"/>
		                      </td>
			               </tr>
					       <tr>
				            <td >
				                 <s:text name="serverConfig.ldap.url"/>
				            </td>
				            <td>
				                 <input type="text" id="ldapUrl" name="ldapUrl"  value="${serverConfig.ldapUrl}" style="width: 200px;"/>
				            </td>
					       </tr>
						</table>
						</FIELDSET>
						<FIELDSET id="rtx" <s:if test="serverConfig.rtxInvocation!=null&&!serverConfig.rtxInvocation"> style="display:none"</s:if>>
						<table class="full edit">
						   <tr>
		                       <td >
		                           rtx地址
		                       </td>
		                       <td>
		                           <input  type="text" id="ldapRtxUrl" name="rtxUrl"  value="${serverConfig.rtxUrl}" /><span style="color: red">(192.168.1.88)</span>
		                       </td>
		                    </tr>
						</table>
						</FIELDSET>
						
						<FIELDSET id="external" <s:if test="serverConfig.extern!=null&&!serverConfig.extern"> style="display:none"</s:if>>
						<table class="full edit">
						   <tr>
						   	   <td> 方式 &nbsp; </td>
		                       <td >
		                       	<select name="externalType">
		                       		<option value="HTTP" <s:if test="serverConfig.externalType==@com.norteksoft.acs.entity.sysSetting.ExternalType@HTTP"> selected="selected" </s:if> >http</option>
		                       		<option value="RESTFUL" <s:if test="serverConfig.externalType==@com.norteksoft.acs.entity.sysSetting.ExternalType@RESTFUL"> selected="selected" </s:if> >RESTful</option>
		                       		<option value="WEBSERVICE" <s:if test="serverConfig.externalType==@com.norteksoft.acs.entity.sysSetting.ExternalType@WEBSERVICE"> selected="selected" </s:if> >webservice</option>
		                       	</select>
		                       
		                       </td>
		                    </tr>
		                    <tr>
		                       <td > 地址 &nbsp;  </td>
		                       <td> <input  type="text" id="externalUrl" name="externalUrl"  value="${serverConfig.externalUrl}" style="width: 500px;"/> </td>
		                    </tr>
						</table>
						</FIELDSET>
					<input type="hidden" id="externalInvocation" name="extern" value="${serverConfig.extern}"/>
					<input type="hidden" id="ldapInvocation" name="ldapInvocation" value="${serverConfig.ldapInvocation}"/>
					<input type="hidden" id="rtxInvocation" name="rtxInvocation" value="${serverConfig.rtxInvocation}"/>
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
