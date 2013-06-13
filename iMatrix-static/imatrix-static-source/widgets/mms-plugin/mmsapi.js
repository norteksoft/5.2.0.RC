
// 执行按钮事件
function buttonExecute(btn, executer){
	var pageId = $(btn).attr('pageid');
	$('#_pageId').attr('value', pageId);
	$('#pageId').attr('value', pageId);
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
	showSearchDIV();
}
// 表单的新建
function toCreateFrom(afterRefresh){
	var callBack;
	if(typeof(afterRefresh)=='function'){
		callBack = function(){ selectWorkflow(); addformValidate();afterRefresh();};
	}else{
		callBack = function(){ selectWorkflow(); addformValidate();};
	}
	ajaxAnyWhereSubmit("default_refresh_form", webRoot+"/common/input.htm", "default_refresh_zone", callBack);
}
// 选择流程定义
function selectWorkflow(){
	var url = $('#selectWorkflowUrl').val();
	if(url == '/common/select-workflow.htm'){
		init_tb2(webRoot+url+"?pageId="+$('#_pageId').val()+"TB_iframe=true&width=400&height=500","选择流程");
	}
}
// 选择流程定义页面的返回
function noWorkflowSelected(){
	$('#back').click();
	tb_remove();
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
		callBack = function(){saveSuccessMsg();afterSave();};
	}else{
		callBack = function(){saveSuccessMsg();};
	}
	ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/save.htm", "default_refresh_zone", callBack);
}
// 返回列表页面
function toListPage(afterRefresh){
	//getPageStateAttr('page', 'default_refresh_form');
	ajaxAnyWhereSubmit("default_refresh_form", webRoot+"/common/list.htm", "default_refresh_zone", afterRefresh);
}
// 表单修改
function toUpdateForm(afterUpdate){
	if(!selectOne()) return;
	var callBack;
	if(typeof(afterUpdate)=='function'){
		callBack = function(){addformValidate();afterUpdate();};
	}else{
		callBack = function(){addformValidate();};
	}
	var ids = jQuery("#main_table").getGridParam('selarrrow');
	$("#ids").attr("value",ids);
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
	for(var i=0; i<ids.length; i++){
		$("#default_list_form").append("<input name='deleteIds' type='hidden' value='"+ids[i]+"'");
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
function addformValidate(){
	$.formValidator.initConfig({formid:"default_submit_form",onsuccess: function() {$('#_is_validate_ok').attr('value', 'TRUE');return false;},onerror:function(msg){$('#_is_validate_ok').attr('value', '');}});
	var data = $('#_validate_string').attr('value');
	$.formValidator.addFormValidate(data);
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




