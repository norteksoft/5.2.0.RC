/************************************************************
 模块名称: 流程汇编主页面对应js
 创 建 者：吴荣
 说    明: 该js文件名和jsp主页文件名相同
 ************************************************************/
//发起流程
function createInstance(){
	var ids = jQuery("#main_table").getGridParam('selarrrow');
	if(ids==''){
		alert("请先选择");
	}else if(ids.length > 1){
		alert("请选择一个流程定义");
	}else{
		selectProcess(ids);
	}
}

function selectProcess(val){
	$("#createInst").unbind();
	$('#wf_defId').attr('value', val);
	ajaxSubmit('defaultForm', webRoot+'/engine/workflow!startProcess.htm', 'myprocess');
}


function showMsg(){
	$("#backMsg").show();
	setTimeout('$("#backMsg").hide("show");',3000);
	var items =$("[name=wf_rd]");
	for(var i=0;i<items.length;i++){
		if($(items[i]).attr('value')==$('#processId').attr('value')){
			$(items[i]).attr("checked",'checked');
			if(items.length==1){
				$('#_boxHeader').attr('checked', 'checked');
			}
			break;
		}
	}
}

function removeReturnBtn(){
	viewDefinition();
	$('#backbutton').remove();
	resetJmessaPageNo("wfdPage");
}

function viewDef(ts1,cellval,opts,rwdat,_act){
	var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"_click_fun("+opts.id+");\">" + ts1 + "</a>";
	return v;
}

function _click_fun(id){
	$('#wf_defId').attr('value', id);
	ajaxSubmit('defaultForm', webRoot+'/engine/workflow-definition!view.htm', 'myprocess', viewDefinition);
}


function treeReturn(){
	var lis = $("#treeid-form").children("ul").children("li");
	setTree(lis);
}

function setTree(list){
	var selected = false;
	for(var i = 0; i < list.length; i++){
		//是否本元素
		if($(list[i]).children('a').attr('class').indexOf('clicked') != -1){
			$($(list[i]).children('a')).click();
			selected = true;
		}else{
			//是否有子元素
			var subList = $(list[i]).children('ul').children("li");
			if(subList.length == 0){
				selected = false;
			}else{
				selected = setTree(subList);
			}
		}
		if(selected) break;
	}
	return selected;
}

function viewDefinition(){
	addSWf("view");
}


function addSWf(type){
	var so = new SWFObject(imatrixRoot+"/widgets/workflowEditor/FlowChartProject.swf", "FlowChartProject", "100%", "100%", "10", "#CCCCCC");
	so.addParam("quality", "high");
	so.addParam("name", "FlowChartProject");
	so.addParam("id", "FlowChartProject");
	so.addParam("AllowScriptAccess", "always");
	so.addParam("menu", "false");
	so.addVariable("webRoot", imatrixRoot);
	so.addVariable("companyId", $("#companyId").val());
	so.addVariable("creator", $("#creator").val());
	so.addVariable("systemId", $("#systemId").val());
	if($("#localeLang").val()=="en"){
		so.addVariable("localeLanguage", "en_US");
	}else if($("#localeLang").val()=="zh"){
		so.addVariable("localeLanguage", "zh_CN");
	}
	if(type=="add"){
		so.addVariable("type", $("#type").val());
		so.addVariable("page", "addProcess");
	}else if(type=="update"){
		so.addVariable("type", $("#type").val());
		so.addVariable("wfId", $("#wfdId").val());
		so.addVariable("page", "updateProcess");
	}else if(type=="view"){
		so.addVariable("wfId", $("#wfdId").val());
		so.addVariable("page", "viewProcess");
	}
	so.write("flashcontent");
}

function changeDefTab(tabId, wfdId){
	$('#a1').removeClass('selectind');
	$('#b1').removeClass('selectind');
	$('#'+tabId).addClass('selectind');
	$('#wf_defId').attr('value', wfdId);
	$('#flashcontent').html('');
	if(tabId == 'a1'){
		$("#flashcontent").show();
		$("#htmlcontent").hide();
		viewDefinition();
	}else{
		$("#flashcontent").hide();
		$("#htmlcontent").show();
	}
}

