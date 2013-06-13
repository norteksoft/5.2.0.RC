<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>定时设置</title>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	
	<script type="text/javascript">
		//通用ajaxAnywhere提交
		function ajaxSubmit(form, url, zoons, ajaxCallback){
			var formId = "#"+form;
			if(url != ""){
				$(formId).attr("action", url);
			}
			ajaxAnywhere.formName = form;
			ajaxAnywhere.getZonesToReload = function() {
				return zoons;
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				if(typeof(ajaxCallback) == "function"){
					ajaxCallback();
				}
			};
			ajaxAnywhere.submitAJAX();
		}
		
		//子表点击事件
		//列表管理/字段信息/格式设置列编辑时的文本框的onclick事件
		//obj:{rowid:id,currentInputId:id_formatSetting}
		function cornClick(obj){
			$('#'+obj.currentInputId).timepicker({
				timeOnlyTitle: '时间'
			});
		}

		//子表下拉框改变事件
		function typeEnumChange(obj){
			//$("#"+obj.rowid+"_dateTime").attr("disabled","disabled");
		}

		

		//修改
		function updateJobInfo(){
			var boxes = jQuery("#jobInfoId").jqGrid("getGridParam",'selarrrow');
			if(boxes==null||boxes==""||boxes.length>1){
				alert("请选择一条记录！");
			}else{
				ajaxSubmit('defaultForm','${settingCtx}/options/job-info-input.htm?id='+boxes,'groups_main');
			}
			
		}
		
		//删除
		function deleteJobInfo(){
			var boxes = jQuery("#jobInfoId").jqGrid("getGridParam",'selarrrow');
			if(boxes==null||boxes==""){
				alert("请选择一条记录！");
			}else{
				if(confirm("确认删除吗？")){
					setPageState();
					ajaxSubmit('defaultForm','${settingCtx}/options/job-info-delete.htm?ids='+boxes,'groups_main');
				}
			}
			
		}
		
		//查看页面
		function viewJobInfo(ts1,cellval,opts,rwdat,_act){
			var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"viewJobInfoInput("+opts.id+");\">" + ts1 + "</a>";
			return v;
		}

		//查看页面
		function viewJobInfoInput(id){
			ajaxSubmit('defaultForm','${settingCtx}/options/job-info-view.htm?id='+id,'groups_main');
		}

		//弹出页面
		function openPage(url,titles){
			$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:titles});
		}

		function backPage(){
			setPageState();
			ajaxSubmit('defaultForm','${settingCtx}/options/job-info.htm','groups_main');
		}

		//增加定时
		function addCornInfo(url,titles){
			var boxes = jQuery("#jobInfoId").jqGrid("getGridParam",'selarrrow');
			if(boxes==null||boxes==""||boxes.length>1){
				alert("请选择一条记录！");
			}else{
				openPage(url+"&id="+boxes,titles);
			}
			
		}

		//删除定时
		function deleteCornInfo(){
			var boxes = jQuery("#childId").jqGrid("getGridParam",'selarrrow');
			if(boxes==null||boxes==""){
				alert("请选择一条记录！");
			}else{
				if(confirm("确认删除吗？")){
					setPageState();
					ajaxSubmit('defaultForm','${settingCtx}/options/job-info-deleteCornInfo.htm?ids='+boxes,'groups_main');
				}
			}
			
		}

		// 设置状态
		function setStateCornInfo(state){
			var boxes = jQuery("#jobInfoId").jqGrid("getGridParam",'selarrrow');
			if(boxes==null||boxes==""){
				alert("请选择一条记录！");
			}else{
				setPageState();
				ajaxSubmit('defaultForm','${settingCtx}/options/job-info-setState.htm?ids='+boxes+'&dataState='+state,'groups_main');
			}
			
		}
		//obj{rowid:id,currentInputId:rowid_runAsUserName}
		function runAsUserNameClick(obj){
			popTree({ title :'选择',
				innerWidth:'400',
				treeType:'MAN_DEPARTMENT_TREE',
				defaultTreeValue:'id',
				leafPage:'false',
				treeTypeJson:null,
				multiple:'false',
				hiddenInputId:obj.rowid+"_runAsUser",
				showInputId:obj.currentInputId,
				loginNameId:'',
				acsSystemUrl:imatrixRoot,
				isAppend:"false",
				callBack:function(){
				runAsUserNameClickCallback(obj);
				}});
		}

		function runAsUserNameClickCallback(obj){
			$("#"+obj.rowid+"_runAsUser").attr("value",jstree.getLoginName());
		}

	</script>
</head>
<body>
	<div class="ui-layout-center">
	<div class="opt-body">
		<form name="defaultForm" id="defaultForm">
			<input type="hidden" name="systemId" value="${systemId}"/>
		</form>
		<aa:zone name="groups_main">
			<div class="opt-btn">
				<a class="btn" href="#" onclick="openPage('${settingCtx}/options/job-info-input.htm?systemId=${systemId}','新建定时设置');"><span><span>新建</span></span></a>
				<a class="btn" href="#" onclick="deleteJobInfo();"><span><span >删除</span></span></a>
				<a class="btn" href="#" onclick="addCornInfo('${settingCtx}/options/job-info-input.htm?systemId=${systemId}','增加定时');"><span><span>增加定时</span></span></a>
				<a class="btn" href="#" onclick="deleteCornInfo();"><span><span >删除定时</span></span></a>
				<a class="btn" href="#" onclick="setStateCornInfo('ENABLE');"><span><span >启用</span></span></a>
				<a class="btn" href="#" onclick="setStateCornInfo('DISABLE');"><span><span >禁用</span></span></a>
			</div>
			<div id="opt-content" >
				<script type="text/javascript">setTimeout('$("#message").hide("show");',3000);</script>
				<div id="message"><s:actionmessage theme="mytheme" /></div>
				<view:jqGrid url="${settingCtx}/options/job-info.htm?systemId=${systemId}" subGrid="childId" code="BS_JOBINFO" pageName="pages" gridId="jobInfoId"></view:jqGrid>
				<div style="height: 8px;"></div>
				<view:subGrid gridId="childId" url="${settingCtx}/options/job-info-chiledList.htm" code="BS_CORNINFO" pageName="cornInfos"></view:subGrid>
			</div>
		</aa:zone>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>