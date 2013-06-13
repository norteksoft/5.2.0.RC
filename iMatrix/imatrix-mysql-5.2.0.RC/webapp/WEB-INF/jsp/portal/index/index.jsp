<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/portal-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head>
	<title>${webpage.name}</title>
	<%@ include file="/common/portal-meta.jsp"%>
	<script src="${resourcesCtx}/js/jquery.timers-1.2.js" type="text/javascript"></script>
	<script src="${portalCtx}/js/index.js" type="text/javascript"></script>
	<script src="${portalCtx}/js/layout.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/myMessage.js" type="text/javascript"></script>
	<style type="text/css">
		#header-resizer img.addpage{ float: left;  margin: 4px 6px;cursor: pointer;}
		#header-resizer img.addpage:hover{ background-color: transparent; border: none;}
		#header-resizer img.editpage{ float: left; margin: 10px 2px 2px 6px; cursor: pointer; display: inline;}
		#header-resizer img.editpage:hover{ background-color: transparent; border: none;}
		#header-resizer ul li span span a{ float: left; }
		.palace-left-c2{ width: 40%; }
		.palace-right-c2{ width: 59%; }
		.palace-widget{ width: 100%; }
		.leadTable th,.leadTable td{white-space:nowrap;}
		.ui-layout-center{overflow:auto;*padding-right:16px;overflow-x:hidden;}
		
		
		.div-tb{float:left;width:120px;height:20px;margin:0 5px;text-align: center;}
	</style>
	<script type="text/javascript">
	$(document).ready(function () {
		_initialWidget();
		loadWidgetContents();
	});

	</script>
</head>
<body onclick="bodyClick();$('#sysTableDiv').hide();$('#styleList').hide();">
	<form action="#" name="webpageChangeForm" id="webpageChangeForm" method="post">
	<input type="hidden" id="_webpageId" name="webpageId"/>
	</form>
	<form action="${portalCtx}/index/index!save.htm" name="aa" id="aa" method="post"></form>
	<form id="webpageForm" > <input type="hidden" id="deleteWebpageId" name="webpageId" value=""/> </form>
	<form id="taskChangeLeafForm" ><input type="hidden" id="task_detail_leaf" value=""/>  </form>
    	
	<%@ include file="/menus/header.jsp"%>
	
	<div id="tabsDiv" style="width: 100px; height: 40px; background:#FFFFFF; display: none; border: 1px solid #B7B7B7;">
		<div style="width: 100px; height: 20px; text-align: center; line-height: 20px;" onmouseover="tabsAOver(this)" onmouseout="tabsAOut(this)"><a href="#" onclick="alterWebpage(this);bodyClick();">修改页签</a></div>
		<div style="width: 100px; height: 20px; text-align: center; line-height: 20px;" onmouseover="tabsAOver(this)" onmouseout="tabsAOut(this)"><a href="#" onclick="delPage(this);">删除页签</a></div>
	</div>
	<div id="tabsDivDefault" style="width: 100px; height: 20px; background:#FFFFFF; display: none; border: 1px solid #B7B7B7;">
		<div style="width: 100px; height: 20px; text-align: center; line-height: 20px;" onmouseover="tabsAOver(this)" onmouseout="tabsAOut(this)"><a href="#" onclick="alterWebpage(this);bodyClick();">修改页签</a></div>
	</div>
	<div id="secNav">
		<ul>
			<s:iterator value="webPages" id="wp" status="st">
				<li id="webpage_${id}" class='<s:if test="#st.Last">last</s:if> <s:if test="id==webpage.id">sec-selected</s:if>' >
					<s:if test="!#wp.acquiescent">
						<span><span><a href="${portalCtx}${url}?webpageId=${id}">${name}</a>
						<a style="margin-top: 4px" class="ui-icon  ui-icon-gear editpage" onclick="tabsClick(this,'${id}');stopBubble(event);"></a>
						</span></span>
					</s:if><s:else>
						<span><span><a href="${portalCtx}${url}?webpageId=${id}">${name}</a>
						<a style="margin-top: 4px" class="ui-icon  ui-icon-gear editpage" onclick="tabsDefaultClick(this,'${id}');stopBubble(event);"></a>
						</span></span>
					</s:else>
				</li>
			</s:iterator>
		</ul>
		<img onclick="addPage();" class="addpage" alt="添加页签" src="../../images/add.png">
		<div class="sec-forms">
			<b class="pemessage"></b><a href="#"  onclick="openMessage();" >个人消息</a>
			<b class="public-setting"></b><a href="#"  onclick="baseSetting();" >个人设置</a>
			<security:authorize ifAnyGranted="show-register-widget">
			<b class="loginforms"></b><a href="#" onclick="registerWidget();">注册小窗体</a>
			</security:authorize>
			<b class="addforms addwg"></b><a class="addwg" href="#"  onclick="addWidget();">添加小窗体</a>
			<security:authorize ifAnyGranted="portal_index_add_theme">
			<b class="addforms addwg"></b><a class="addwg" href="#"  onclick="addTheme();">添加主题</a>
			</security:authorize>
		</div>
		<div title="隐藏" onclick="headerChange(this);" class="hid-header"></div>
	</div>
	
	<div class="ui-layout-center">
		<aa:zone name="webpage_zone">
		<div id="widgetDiv">
		<form id="widgetForm" action="${portalCtx}/index/index!saveWidgetToPortal.htm" method="post">
			<input type="hidden" id="webpageId" name="webpageId" value="${webpage.id}"/>
			<input type="hidden" id="widgetPosition" name="positions" value="${webpage.widgetPosition}"/>
			<input type="hidden" id="_widgetCode" name="widgetCode" value=""/>
			<input type="hidden" id="_position" name="position" value=""/>
			<input type="hidden" id="message_visible" value="${baseSetting.messageVisible }"/>
			<input type="hidden" id="refresh_time" value="${baseSetting.refreshTime }"/>
			<input type="hidden" id="portalMessage" value="portal"/>
		</form>
			<div id="widget-place-left" class="widget-place <s:if test="webpage.columns==2">palace-widget</s:if><s:else>palace-left</s:else>" style="min-height: 1000px;">
				<s:iterator value="webpage.leftWidgets" status="status" var="leftPos">
					<div class="widget movable collapsable removable  closeconfirm editable" id="identifierwidget-${id}">
						<div class="widget-header">
							<h3>${name}</h3>
						</div>
						<div class="widget-editbox"></div>
						<div class="widget-content" id="widget-content-${id}" iframeable="${iframeable }" widgetUrl = "${systemUrl }/${url }" >
								<s:if test="iframeable">
									<iframe  id="contentIFrame-${id}"  src=""  style="height:100%;" frameborder="0" allowtransparency="no" ></iframe>
								</s:if><s:else>
									<table style="width: 100%;" ><tr><td align="center"><img alt="" src="../../images/loading.gif"/>&emsp;正在加载...</td></tr></table>
								</s:else>
						</div>
						<b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
					</div>
				</s:iterator>
			</div>  
			
			<div id="widget-place-center" class="widget-place palace-center" style="min-height: 1000px;">
			   <s:iterator value="webpage.centerWidgets" status="status" var="leftPos">
					<div class="widget movable collapsable removable  closeconfirm editable" id="identifierwidget-${id}">
						<div class="widget-header">
							<h3>${name}</h3>
						</div>
						<div class="widget-editbox"></div>
						<div class="widget-content" id="widget-content-${id}" iframeable="${iframeable }" widgetUrl = "${systemUrl }/${url }">
							<s:if test="iframeable">
									<iframe  id="contentIFrame-${id}"  src=""  style="height:100%;" frameborder="0" allowtransparency="no" ></iframe>
							</s:if><s:else>
								<table style="width: 100%;" ><tr><td align="center"><img alt="" src="../../images/loading.gif"/>&emsp;正在加载...</td></tr></table>
							</s:else>
						</div>
						<b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
					</div>
				</s:iterator>
			 </div>
			 
			 <div id="widget-place-right" class="widget-place palace-right" style="min-height: 1000px;">
			   <s:iterator value="webpage.rightWidgets" status="status" var="leftPos">
					<div class="widget movable collapsable removable  closeconfirm editable" id="identifierwidget-${id}">
						<div class="widget-header">
							<h3>${name}</h3>
						</div>
						<div class="widget-editbox"></div>
						<div class="widget-content" id="widget-content-${id}" iframeable="${iframeable }" widgetUrl = "${systemUrl }/${url }">
							<s:if test="iframeable">
									<iframe  id="contentIFrame-${id}"  src=""  style="height:100%;" frameborder="0" allowtransparency="no" ></iframe>
							</s:if><s:else>
								<table style="width: 100%;" ><tr><td align="center"><img alt="" src="../../images/loading.gif"/>&emsp;正在加载...</td></tr></table>
							</s:else>
						</div>
						<b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
					</div>
				</s:iterator>
			 </div>
		</div>
		</aa:zone>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>