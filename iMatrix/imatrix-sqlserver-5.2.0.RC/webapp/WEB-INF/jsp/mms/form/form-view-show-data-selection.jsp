<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<%@ taglib uri="http://ajaxanywhere.sourceforge.net/" prefix="aa" %>
<html>
	<head>
		<title><s:text name="role.roleManager"/></title>
		<%@ include file="/common/mms-iframe-meta.jsp"%>
		<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>	
		<script type="text/javascript">
			function jmesaSubmit(){
				ajaxSubmit("pageForm",webRoot+"/show-data-selection.htm","formTable");
			}
			function addValue(obj){
				var tds = $(obj).parent().parent().children();
				var contral = $("#"+$("#controlId").attr("value"),window.parent.document);
				var data_control_ids = contral.attr("data_control");
				var data_control_id_arry = data_control_ids.split(',');
				for(var i=0;i<data_control_id_arry.length;i++){
					if(data_control_id_arry[i]=='')break;
					$("#"+data_control_id_arry[i],window.parent.document).attr("value",$(tds[i]).html());
				}
				window.parent.tb_remove();
			}
		</script>
	</head>
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
	<div class="opt-body">
		<div class="opt-btn">
			<button class="btn" onclick="$('#textForm').submit();"><span><span>确定</span></span></button>
			<button class="btn" onclick='parent.$.colorbox.close();'><span><span >取消</span></span></button>
		</div>
		<div id="opt-content">
			<form name="searchForm" action="${mmsCtx }/show-data-selection.htm">
				<input type="hidden" name="formId" value="${formId }"/>
				<input type="hidden" name="formControlId" value="${formControlId }" id="controlId"/>
				<p style="margin: 10px">
					<s:iterator value="properties" var="querys" status="in">
						<s:if test="#querys[2]==1">
							<span>${querys[1]}：</span>
							<span><input type="text" name="${querys[0]}" id="${querys[0]}"/></span>
						</s:if>
					</s:iterator>
					<span>
				 		<a class="btnStyle" href="#" onclick='ajaxSubmit("searchForm","${ctx}/show-data-selection.htm","formTable");' style="color:black;" hidefocus="true">查询</a>
					</span>
				</p>
			</form>
		
			<aa:zone name="formTable">
				<form action="" name="pageForm" id="pageForm" method="post">
				<input type="hidden" name="formId" value="${formId }"/>
				<input type="hidden" name="formControlId" value="${formControlId }"/>
				<s:if test="existTable">
					<jmesa:myTableFacade var="bean" 
										 id="datas"
										 needPage="true">
						<jmesa:htmlTable >
							<jmesa:htmlRow >
							<c:forEach items="${properties}" var="property" varStatus="state">
								<jmesa:htmlColumn property="${property[0]}"
												  title="${property[1]}"
												  filterable="false" 
												  >${bean[state.index] }</jmesa:htmlColumn>
							</c:forEach>	
								<jmesa:htmlColumn property=""
												  title="操作"
												  sortable="false"
												  filterable="false"
												  >
									<a href="#" onclick="addValue(this);">添加</a>
								</jmesa:htmlColumn>
							</jmesa:htmlRow>
						</jmesa:htmlTable>
					</jmesa:myTableFacade>	
				</s:if><s:else>
					<jmesa:myTableFacade var="bean" 
										 id="datas"
										 needPage="true">
						<jmesa:htmlTable >
							<jmesa:htmlRow >
							<c:forEach items="${properties}" var="property" varStatus="state">
								<jmesa:htmlColumn property="${property[0]}"
												  title="${property[1]}"
												  filterable="false" 
												  >${bean[property[0]] }</jmesa:htmlColumn>
							</c:forEach>	
								<jmesa:htmlColumn property=""
												  title="操作"
												  sortable="false"
												  filterable="false"
												  >
									<a href="#" onclick="addValue(this);">添加</a>
								</jmesa:htmlColumn>
							</jmesa:htmlRow>
						</jmesa:htmlTable>
					</jmesa:myTableFacade>	
				</s:else>
					
				</form>
			</aa:zone>
		</div>
	</div>
	</div>	
	</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>