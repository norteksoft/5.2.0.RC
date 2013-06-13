<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/task-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	
	<title>待办事宜</title>
	<%@ include file="/common/task-meta.jsp"%>
	
	<script src="${resourcesCtx}/js/jquery.timers-1.2.js" type="text/javascript"></script>
	<script src="${taskCtx}/js/task.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/staff-tree.js" type="text/javascript"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		if("${currentNodeId}"!=""&&typeof("${currentNodeId}")!="undefined"){
			tree("unhandle_bussiness_content","${currentNodeId}",true);
		}else{
			tree("unhandle_bussiness_content");
		}
	});

	function drawTask(){
		if(validateReadioTaskIds()){
			ajaxSubmit('task_form', '${taskCtx}/task/task-receive.htm', 'product_task');
		}
	}
	function onClosedFun(){}
	function assign(){
		if(validateReadio()){
			/*
			title:弹框的标题
			innerWidth:弹框的宽度
			treeType:无页签时树的类型 
			                         树的类型如下:
						         COMPANY  公司树
						         MAN_DEPARTMENT_GROUP_TREE  部门工作组人员树
						         MAN_DEPARTMENT_TREE  部门人员树
						         MAN_GROUP_TREE  工作组人员树
						         DEPARTMENT_TREE  部门树
						         GROUP_TREE 工作组树
						         DEPARTMENT_WORKGROUP_TREE 部门工作组树
			defaultTreeValue:无页签树的返回值设置; 格式:id,loginName,email,name
			leafPage: 是否需要页签 ,默认为false
			treeTypeJson: 定义页签,为json类型 :name:页签名字;type:页签显示树的类型
			multiple: boolean类型 默认false,是否是多选树    
			hiddenInputId: 隐藏域id
			showInputId: 显示框id
			callBack:回调方法
			返回值格式: id=10,loginName=dzy,email=undefined,name=董志云;
			           id=4,loginName=test1,email=undefined,name=test1;
			           或
			           departmentIds==id=1,loginName=undefined,email=undefined,name=test
			           +groupIds==id=1,loginName=undefined,email=undefined,name=ff;
			                      id=2,loginName=undefined,email=undefined,name=gfg;

			
            var json = '[';
			    json = json + '{name:"第一页签",type:"DEPARTMENT_TREE",value:""},';
			    json = json + '{name:"第二页签",type:"GROUP_TREE",value:"id,name"},';
			    json = json + '{name:"第三页签",type:"MAN_DEPARTMENT_GROUP_TREE",value:"id,loginName,email,name"},';
			    json = json + '{name:"第四页签",type:"DEPARTMENT_WORKGROUP_TREE",value:"id,loginName,email,name"}]';
          
			  popTree({ title :'选择',
						innerWidth:'400',
						treeType:'GROUP_TREE',
						defaultTreeValue:'id,loginName,email,name',
						leafPage:'true',
						treeTypeJson:json,
						multiple:'false',
						hiddenInputId:'hiddenInputId',
						showInputId:'showInputId',
						callBack:function(){onClosedFun();}});*/
			$.colorbox({href:webRoot+'/task/tree.htm',iframe:true, innerWidth:300, innerHeight:400,overlayClose:false,title:"选择"});
		}else{
			alert("请选择任务");
		}
	}
	function validateReadio(){
		var rds = jQuery("#taskTableId").getGridParam('selarrrow');
		
		for(var i=0;i<rds.length;i++){
			$("#task_id").attr("value", rds[i]);
			return true;
		}
		return false;
	}
	function validateReadioTaskIds(){
		var rds = jQuery("#taskTableId").getGridParam('selarrrow');
	    if(rds.length==0){
	    	alert("请选择一条记录!");
		    return false;
		}else{
			var taskIds = "";
			for(var i=0;i<rds.length;i++){
				taskIds+=rds[i]+",";
			}
			taskIds=taskIds.substring(0,taskIds.length-1);
			$("#task_ids").attr("value", taskIds);
		    return true;	
		}
	}
	
	function getTaskId(){
		return $("#task_id").attr("value");
	}
	
	function markColor(code){
			$("#flag").hide();
			if(validateReadioTaskIds()){
				$('#taskMark').attr('value', code.toUpperCase());
				ajaxSubmit("task_form", "${taskCtx }/task/task-mark.htm", "product_task");
			}
	}
	
	//翻页
	function changePage(page){
		$("#pageNo").attr("value", page);
		ajaxSubmit('pageForm', '${taskCtx}/task/task.htm', 'product_task');
	}
	//错误页数
	function showErrorPageNo(){
		alert('<s:text name="page.errorPageNo"></s:text>');
	}
	
	function taskList(){
		setPageState();
		$("#searchBtn").find("span").find("span").html("查询");
		var typeName = $("#typeName").val();
		var taskType = $("#taskType").val();
		var currentId=typeName;
		if(currentId=="")currentId="active_task";
		tree("unhandle_bussiness_content",currentId,true);
		ajaxSubmit('searchForm', encodeURI('${taskCtx}/task/task.htm?typeName='+typeName+'&taskType='+taskType), 'product_task');
	}
	
	function taskListAssgin(){
		var msg = "<font class=\"onSuccess\"><nobr>任务已成功指派</nobr></font>";
		$("#backMsg").html(msg);
		$("#backMsg").show();
		setTimeout('$("#backMsg").hide();',3000);
		taskList();
	}

	function changeTaskType(taskType){
		$("#typeName").attr("value","");
		$("#taskType").attr("value",taskType);
		ajaxSubmit('pageForm', '${taskCtx}/task/task.htm', 'product_task_type',changeTaskTypeCallback);
	}
	function changeTaskTypeCallback(){
		tree("unhandle_bussiness_content");
	}
	</script>
</head>

<body onclick="$('#sysTableDiv').hide();$('#styleList').hide();">
	<script type="text/javascript">
  		var secondMenu = "myTask";
  		var thirdMenu = "task-thirdMenu1";
  	</script>
  	<%@ include file="/menus/header.jsp"%>
	<%@ include file="/menus/task-sec-menu.jsp"%>
	<div class="ui-layout-west">
		<%@ include file="/menus/task-third-menu.jsp"%>
	</div>
	<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="product_task_all">
			<div class="opt-btn" >
			<input type="hidden" id="hiddenInputId"  value="">
			<input type="hidden"" id="showInputId"  value="">
				<button class="btn" onclick="$('#flag').hide();iMatrix.showSearchDIV(this);"  id="searchBtn"><span><span >查询</span></span></button>
				<button class="btn" onclick="drawTask();"><span><span>领取</span></span></button>
				<button class="btn" onclick="assign();"><span><span >指派</span></span></button>
				<button id="_task_button" class="btn" onclick="showIdentifiersDiv();"><span><span >标识为.. </span></span></button>
				<div class="btndiv" id="_flowbtn" style="*top:-2px;">
					<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentFlowBtn">
						<span class="ui-button-text">任务类型</span>
					</button>
					<button  title="更多"  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
						<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
						<span class="ui-button-text">更多</span>
					</button>
				</div>
				<div id="flowbtn" class="flag" >
					<ul >
						<s:iterator value="@com.norteksoft.task.base.enumeration.TaskType@values()" >
							<li><a href="#" onclick="changeTaskType('${code}');">${name }</a></li>
						</s:iterator>
					</ul>
				</div>
			</div>
			
			<div id="flag" onmouseover='show_moveiIdentifiersDiv();' onmouseout='hideIdentifiersDiv();'>
			<ul >
			 <s:iterator value="@com.norteksoft.task.entity.TaskMark@values()" id="acolor">
			 <li onclick="markColor('<s:property value="#acolor"/>');">
			 <span><img style="border: none;" src="${taskCtx}/images/${name}.gif"/></span>
			 <a href="#"><s:text name="%{name}"></s:text></a>
			 </li>
			 </s:iterator>
			</ul>
			</div>
			<input id="task_state" type="hidden" value="new"/>
					
			<form id="pageForm" name="pageForm" action="" method="post">
				<input name="taskType" value="${taskType }" type="hidden" id="taskType"/>
				<input name="typeName" value="${typeName }"  type="hidden" id="typeName"/>
				<input name="taskCategory" value="active"  type="hidden" id="taskCategory"/>
			</form>
		   <aa:zone name="product_task_type">
			<div id="opt-content">
				<form id="searchForm" name="searchForm" action="" method="post">
				</form>
			<aa:zone name="product_task">
				<div style="display: none;">
					<form action="#" id="task_form" name="task_form">
						<input type="hidden" id="task_id" name="id"/>
						<input type="hidden" id="task_ids" name="ids"/>
						<input id="taskMark" name="taskMarks" type="hidden"/>
						<input name="taskCategory" value="active" type="hidden"/>
						<input type="hidden" name="tasks.pageNo" value="${tasks.pageNo }">
					</form>
				</div>
				
				<span id="message" >
					<s:actionmessage theme="mytheme" />
				</span>
				<script type="text/javascript">
					initBtnGroup();
					setTimeout('$("#message").hide();',3000);
				</script>
				<span id="backMsg" style="display: none;"></span>
				<form id="searchSubmit" name="searchSubmit" action=""  method="post">
					<view:jqGrid url="${taskCtx}/task/task.htm?typeName=${typeName }&taskType=${taskType }" code="TASK_WF_TASK" gridId="taskTableId" pageName="tasks" submitForm="searchForm"></view:jqGrid>
				</form>	
			</aa:zone>
			</div>
			</aa:zone>
		</aa:zone>	
		</div>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
