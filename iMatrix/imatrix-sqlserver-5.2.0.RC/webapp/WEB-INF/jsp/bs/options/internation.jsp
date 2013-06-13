<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>国际化设置</title>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	
	<script type="text/javascript">
	//弹出页面
	function openPage(url,titles,opt){
		if(opt=="create"){
			$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:titles});
		}else if(opt=="update"){
			var boxes = jQuery("#InterInfoId").jqGrid("getGridParam",'selarrrow');
			if(boxes.length<=0){
				alert("请选择记录");
				return;
			}else if(boxes.length>1){
				alert("只能选择一条记录");
				return;
			}else{
				$.colorbox({href:url+"?id="+boxes[0],iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:titles});
			}
		}
	}

	//删除
	function deleteInfo(){
		var boxes = jQuery("#InterInfoId").jqGrid("getGridParam",'selarrrow');
		if(boxes.length<=0){
			alert("请选择需要删除的记录！");
			return;
		}else{
			if(confirm("确认删除吗？")){
				setPageState();
				ajaxSubmit('pageForm','${settingCtx}/options/internation-delete.htm?ids='+boxes.join(','),'groups_main');
			}
		}
	}

	function backPage(){
		setPageState();
		ajaxSubmit('pageForm','${settingCtx}/options/internation.htm','groups_main');
	}

	function updateCache(){
		ajaxSubmit('pageForm','${settingCtx}/options/internation-update-cache.htm','groups_main');
		window.location.reload(true);
	}
	</script>
</head>
<body>
	<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="groups_main">
			<div class="opt-btn">
				<a class="btn" href="#" onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></a>
				<a class="btn" href="#" onclick="openPage('${settingCtx}/options/internation-input.htm','新建','create');"><span><span>新建</span></span></a>
				<a class="btn" href="#" onclick="openPage('${settingCtx}/options/internation-input.htm','修改','update');"><span><span>修改</span></span></a>
				<a class="btn" href="#" onclick="updateCache();"><span><span>更新国际化缓存</span></span></a>
				<a class="btn" href="#" onclick="deleteInfo();"><span><span >删除</span></span></a>
			</div>
			<div id="opt-content" >
				<script type="text/javascript">setTimeout('$("#message").hide("show");',3000);</script>
				<div id="message"><s:actionmessage theme="mytheme" /></div>
				<form action="" name="pageForm" id="pageForm" method="post">
					<view:jqGrid url="${settingCtx}/options/internation.htm" subGrid="childId" code="BS_INTERNATION" pageName="pages" gridId="InterInfoId"></view:jqGrid>
					<div style="height: 8px;"></div>
					<view:subGrid gridId="childId" url="${settingCtx}/options/internation-chiledList.htm" code="BS_INTERNATION_OPTION" pageName="interOptions"></view:subGrid>
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