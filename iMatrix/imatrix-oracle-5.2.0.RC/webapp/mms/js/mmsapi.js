
// 执行按钮事件
function buttonExecute(btn, executer){
	var pageId = $(btn).attr('pageid');
	if(pageId==undefined){alert("此页面的【按钮设置】中没有添加此按钮的转向页面！");
		return;
	}
	$('#_pageId').attr('value', pageId);
	$('#pageId').attr('value', pageId);
	$('#____pageId').attr('value', pageId);
	if(typeof(executer.execute)=='function'){
		var needExecute = true;
		if(typeof(executer.before)=='function'){
			needExecute = executer.before();
		}
		if(needExecute){
			if(typeof(executer.after)=='function'){
				executer.execute(executer.after);
			}else{
				executer.execute();
			}
		}
	}
}

/*******  execute method   ************/
// 显示查询div
function toQuery(){
	iMatrix.showSearchDIV($("#query"));
}
// 表单的新建
function toCreateFrom(afterRefresh){
	var callBack;
	if(typeof(afterRefresh)=='function'){
		callBack = function(){ selectWorkflow();getContentHeight(); _formValidate();afterRefresh();};
	}else{
		callBack = function(){ selectWorkflow();getContentHeight();_formValidate();};
	}
	ajaxAnyWhereSubmit("default_refresh_form", webRoot+"/common/input.htm", "default_refresh_zone", callBack);
}
// 选择流程定义
function selectWorkflow(){
	var url = $('#selectWorkflowUrl').val();
	if(url == '/common/select-workflow.htm'){
		$.colorbox({href:webRoot+url+"?pageId="+$('#_pageId').val(),iframe:true, innerWidth:700, innerHeight:500,overlayClose:false,onClosed:function(){noWorkflowSelected();},title:"选择流程"});
	}
}
//选择流程定义后进入表单页面
function afterSelectWorkflow(){
	ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/input.htm", "default_refresh_zone", afterSelectWorkflowCallback);
}

function afterSelectWorkflowCallback(){
	getContentHeight();
	_formValidate();
}
// 选择流程定义页面的返回
function noWorkflowSelected(){
	$('#back').click();
	//parent.$.colorbox.close();
	if(parent.$('#_wfDefId').attr('value')==''){
		window.location.reload(true);
	}
}
// 表单保存
function saveForm(afterSave){
	$('#default_submit_form').submit();
	if($('#_is_validate_ok').attr('value')=="TRUE"){
		doSaveForm(afterSave);
	}
}
// 执行保存方法
function doSaveForm(afterSave){
	var callBack;
	if(typeof(afterSave)=='function'){
		callBack = function(){saveSuccessMsg();_formValidate();afterSave();getContentHeight();};
	}else{
		callBack = function(){saveSuccessMsg();_formValidate();getContentHeight();};
	}
	ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/save.htm", "default_refresh_zone", callBack);
}
// 返回列表页面
function toListPage(afterRefresh){
	//getPageStateAttr('page', 'default_refresh_form');
	if($("#default_common_form").attr("id")=="default_common_form"){//是否存在该form，当没有默认列表页面时会有异常信息，所以要判断
		setPageState();
		ajaxAnyWhereSubmit("default_common_form", webRoot+"/common/list.htm", "default_refresh_zone", afterRefresh);
	}else{
		alert("没有相应的列表,不需要返回");
	}
}
// 表单修改
function toUpdateForm(afterUpdate){
	if(!selectOne()) return;
	var callBack;
	if(typeof(afterUpdate)=='function'){
		callBack = function(){_formValidate();afterUpdate();getContentHeight();};
	}else{
		callBack = function(){_formValidate();getContentHeight();};
	}
	var dataId = jQuery("#main_table").getGridParam('selarrrow');
	$("#dataId").attr("value",dataId);
	ajaxAnyWhereSubmit("default_list_form", webRoot+"/common/input.htm", "default_refresh_zone", callBack);
}
// 删除
function deleteList(afterRefresh){
	if(!selectOneOrMore()) return;
	var callBack;
	if(typeof(afterRefresh)=='function'){
		callBack = function(){deleteSuccessMsg();afterRefresh();};
	}else{
		callBack = function(){setTimeout('validateDeleteMsg();','800');};
	}
	var ids = jQuery("#main_table").getGridParam('selarrrow');
	$("input[name='deleteIds']").remove();
	for(var i=0; i<ids.length; i++){
		$("#default_list_form").append("<input name='deleteIds' type='hidden' value='"+ids[i]+"'/>");
	}
	ajaxAnyWhereSubmit("default_list_form", webRoot+"/common/delete.htm", "default_refresh_zone", callBack());
	
}

function validateDeleteMsg(){
	$("#opt_message").show("show");
	setTimeout('$("#opt_message").hide("show");',3000);
}

function saveSuccessMsg(){
	__show_message('opt_message','保存成功','onSuccess');
}
function deleteSuccessMsg(){
	__show_message('opt_message',"删除成功",'onSuccess');
}
function _formValidate(){
	$("#default_submit_form").validate({
		submitHandler:function(){
			$('#_is_validate_ok').attr('value','TRUE');
		},
		rules:{}
	});
}
/*******  before or after call method   ************/
// 选且选择一项
function selectOne(){
	var selectCount = jQuery("#main_table").getGridParam('selarrrow').length;
	if(selectCount == 1){
		return true;
	}else{
		__show_message('opt_message','请选且只能选择一项','onError');
		return false;
	}
}
// 选择一项以上
function selectOneOrMore(){
	var selectCount = jQuery("#main_table").getGridParam('selarrrow').length;
	if(selectCount >= 1){
		return true;
	}else{
		__show_message('opt_message','请至少选择一项','onError');
		return false;
	}
}

function __show_message(id, msg, clazz){
	if(msg != ""){
		$("#"+id).html('<span style="color:red">'+msg+"</span>");
		$("#"+id).attr('class', clazz);
	}
	$("#"+id).show("show");
	setTimeout('$("#'+id+'").hide("show");',3000);
}


/******* 分页  ************/
function jmesaSubmit(id){
	ajaxAnyWhereSubmit("default_list_form",webRoot+"/common/list.htm","default_refresh_zone");
}

function popbox(showDivId, width, height, title){
	if($("#popbox_shade").attr('id')=='popbox_shade'){
		
	}else{
		var html = '<div id="popbox_shade" class="searchOver"></div>'+
			'<div id="popbox_box" style="display: block;" class="searchBox">'+
				'<div id="search_header" style="display: block;">'+
				'<div id="search_title">'+title+'</div><div id="search_close" onclick="closePopbox();"> </div></div>'+
				'<div class="popbox_body" style="padding: 5px;overflow: auto;height:'+(height-40)+'px;">'+$('#'+showDivId).html()+
			'</div></div>';
		//$('body').html(html);
		$('#'+showDivId).remove();
		$('body').append(html);
		$("#popbox_box").css("width",width+'px');
		$('#popbox_box').css('height',height+'px');
		$('#popbox_box').css('margin-top', -(height/2)+"px");
		$('#popbox_box').css('margin-left', -(width/2)+"px");
	}
}
function closePopbox(){
	$('#popbox_shade').remove();
	$('#popbox_box').remove();
}

