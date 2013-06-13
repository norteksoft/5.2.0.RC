
//页面验证
function  validate(){
	$.formValidator.initConfig({formid:"definitionForm",onsuccess: function() {ajax_submit_form();return false;},onerror:function(){}});
	$("#name").formValidator({onshow:'请输入流程名字',onfocus:'请输入流程名字',oncorrect:'成功'}).inputValidator({min:1,empty:{emptyerror:'必须流程名字'},onerror:'错误'});
	$("#adminName").formValidator({onshow:'请选择流程管理员',onfocus:'请选择流程管理员',oncorrect:'成功'}).inputValidator({min:1,empty:{emptyerror:'必须选择流程管理员'},onerror:'错误'});
	$("#formId").formValidator({onshow:'请选择表单',onfocus:'请选择表单',oncorrect:'成功'}).inputValidator({min:1,empty:{emptyerror:'必须选择表单'},onerror:'错误'});
 }

$(function(){
	validate();
});
function returnList(typeId){
	window.location.href = webRoot+"/engine/workflow-definition.htm?type="+typeId;
}
function save(){
	$("#definitionForm").submit();
}



function ajax_submit_form(){
	var url = webRoot+"/engine/workflow-definition!saveCustomProcess.htm";
	ajaxSubmit("definitionForm",url,"wf_definition",validate);
}